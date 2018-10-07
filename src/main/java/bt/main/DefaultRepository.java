package bt.main;

import bt.api.Artifact;
import bt.api.Dependency;
import bt.api.Module;
import bt.api.Repository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultRepository implements Repository {
  private static final Logger LOGGER = LoggerFactory.getLogger(Repository.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private Map<String, Module> modules = new HashMap<>();

  @Override
  public void addModule(Module module) {
    modules.put(module.getArtifact().getArtifactId(), module);
  }

  private Path get(Dependency.ModuleDependency dependency) {
    return modules.get(dependency.getArtifactId()).getCompiledCode();
  }

  private Path get(Dependency.ArtifactDependency dependency) {
    Artifact artifact = dependency.getArtifact();

    return Paths.get(
        System.getProperty("user.home"),
        ".m2",
        "repository",
        artifact.getGroupId().replaceAll("\\.", "/"),
        artifact.getArtifactId(),
        artifact.getVersion(),
        artifact.getArtifactId()
            + "-"
            + artifact.getVersion()
            + (artifact.getClassifier() != null ? "-" + artifact.getClassifier() : "")
            + "."
            + artifact.getType());
  }

  /** Get the path of the dependency. */
  @Override
  public Path get(Dependency dependency) {
    return dependency instanceof Dependency.ArtifactDependency
        ? get((Dependency.ArtifactDependency) dependency)
        : get((Dependency.ModuleDependency) dependency);
  }

  @Override
  public List<Dependency> getDependencies(Path sourceSet) {

    List<Dependency> artifacts = new ArrayList<>();
    Path moduleFile = sourceSet.resolve(Paths.get("module.json"));
    if (Files.exists(moduleFile)) {
      LOGGER.debug("reading {}", moduleFile);
      Map<String, Map> tree;
      try {
        tree =
            OBJECT_MAPPER.readValue(moduleFile.toFile(), new TypeReference<Map<String, Map>>() {});
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }

      @SuppressWarnings("unchecked")
      Map<String, Map> dependencies = tree.get("dependencies");
      artifacts.addAll(flatten(dependencies, new ArrayList<>()));
    }

    LOGGER.debug(
        "{} depends on {} ",
        sourceSet,
        artifacts.stream().map(String::valueOf).collect(Collectors.joining(",")));
    return artifacts;
  }

  @SuppressWarnings("unchecked")
  private List<Dependency> flatten(Map<String, Map> tree, List<Dependency> dependencies) {

    tree.forEach(
        (dependency, map) -> {
          dependencies.add(Dependency.valueOf(dependency));
          flatten(map, dependencies);
        });

    return dependencies;
  }

  @Override
  public String getClassPath(Path sourceSet) {
    return getDependencies(sourceSet)
        .stream()
        .map(this::get)
        .map(String::valueOf)
        .collect(Collectors.joining(":"));
  }
}
