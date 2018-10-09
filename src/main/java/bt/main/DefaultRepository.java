package bt.main;

import bt.api.Artifact;
import bt.api.ArtifactDependency;
import bt.api.Dependency;
import bt.api.Module;
import bt.api.ModuleDependency;
import bt.api.Repository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class DefaultRepository implements Repository {
  private static final Logger LOGGER = LoggerFactory.getLogger(Repository.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final Map<String, Module> modules = new HashMap<>();
  private boolean download = true;
  private final Map<Artifact, List<Artifact>> dependencies;

  public DefaultRepository() {
    try {
      dependencies =
          OBJECT_MAPPER.readValue(
              new File("dependencies.json"), new TypeReference<Map<Artifact, List<Artifact>>>() {});
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void addModule(Module module) {
    modules.put(module.getArtifact().getArtifactId(), module);
  }

  private Path get(ModuleDependency dependency) {
    return modules.get(dependency.getArtifactId()).getCompiledCode();
  }

  void setDownload(boolean download) {
    this.download = download;
  }

  private Path get(ArtifactDependency dependency) {
    Artifact artifact = dependency.getArtifact();

    Path path =
        Paths.get(
            artifact.getGroupId().replaceAll("\\.", "/"),
            artifact.getArtifactId(),
            artifact.getVersion(),
            artifact.getArtifactId()
                + "-"
                + artifact.getVersion()
                + (artifact.getClassifier() != null ? "-" + artifact.getClassifier() : "")
                + "."
                + artifact.getType());

    Path localPath = Paths.get(System.getProperty("user.home"), ".m2", "repository").resolve(path);

    if (download && !Files.exists(localPath)) {
      try {
        URL url = new URL("http://central.maven.org/maven2/" + path);
        LOGGER.info("downloading {} to {} ", url, localPath);
        Files.createDirectories(localPath.getParent());

        try (InputStream in = url.openStream()) {
          Files.copy(in, localPath, StandardCopyOption.REPLACE_EXISTING);
        }
      } catch (IOException e) {
        try {
          Files.delete(localPath);
        } catch (IOException e1) {
          throw new UncheckedIOException(e);
        }
        throw new UncheckedIOException(e);
      }
    }

    return localPath;
  }

  /** Get the path of the dependency. */
  @Override
  public Path getPath(Dependency dependency) {
    requireNonNull(dependency);
    return dependency instanceof ArtifactDependency
        ? get((ArtifactDependency) dependency)
        : get((ModuleDependency) dependency);
  }

  @Override
  public List<Dependency> getDependencies(Artifact artifact) {
    requireNonNull(artifact);
    List<Dependency> dependencies = new ArrayList<>();

    dependencies.add(new ArtifactDependency(artifact));

    List<Artifact> artifacts = this.dependencies.get(artifact);

    requireNonNull(artifacts, artifact + " not found");

    artifacts.stream().map(ArtifactDependency::new).forEach(dependencies::add);

    return dependencies;
  }

  @Override
  public List<Dependency> getDependencies(Module module) {
    requireNonNull(module);
    Path moduleFile = module.getSourceSet().resolve(Paths.get("module.json"));

    LOGGER.debug("reading {}", moduleFile);
    Map<String, ?> tree;
    try {
      tree = OBJECT_MAPPER.readValue(moduleFile.toFile(), new TypeReference<Map<String, ?>>() {});
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    List<Dependency> dependencies = new ArrayList<>();

    //noinspection unchecked
    ((List<String>) tree.get("dependencies"))
        .stream()
        .map(Dependency::valueOf)
        .forEach(
            dependency -> {
              dependencies.add(dependency);
              dependencies.addAll(getDependencies(dependency));
            });
    return dependencies;
  }

  private List<Dependency> getDependencies(Dependency dependency) {
    return dependency instanceof ModuleDependency
        ? getDependencies((ModuleDependency) dependency)
        : getDependencies((ArtifactDependency) dependency);
  }

  private List<Dependency> getDependencies(ArtifactDependency dependency) {
    return getDependencies(dependency.getArtifact());
  }

  private List<Dependency> getDependencies(ModuleDependency dependency) {
    Module module =
        requireNonNull(modules.get(dependency.getArtifactId()), dependency + " unknown");
    return getDependencies(module);
  }

  @Override
  public String getClassPath(Module module) {
    return getDependencies(module)
        .stream()
        .map(this::getPath)
        .map(String::valueOf)
        .collect(Collectors.joining(":"));
  }
}
