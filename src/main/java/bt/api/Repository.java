package bt.api;

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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repository {
  private static final Logger LOGGER = LoggerFactory.getLogger(Repository.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private Map<String, Module> modules = new HashMap<>();

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
  public Path get(Dependency dependency) {
    return dependency instanceof Dependency.ArtifactDependency
        ? get((Dependency.ArtifactDependency) dependency)
        : get((Dependency.ModuleDependency) dependency);
  }

  public List<Dependency> getDependencies(Path sourceSet) {

    List<Dependency> artifacts = new ArrayList<>();
    for (Path path :
        new Path[] {
          Paths.get("dependencies.json"), Paths.get("../dependencies.json"),
        }) {
      Path dependenciesFile = sourceSet.resolve(path);
      if (Files.exists(dependenciesFile)) {
        LOGGER.debug("reading {}", dependenciesFile);
        Map<String, Map> tree;
        try {
          tree =
              OBJECT_MAPPER.readValue(
                  dependenciesFile.toFile(), new TypeReference<Map<String, Map>>() {});
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }

        artifacts.addAll(flatten(tree, new ArrayList<>()));
      }
    }
    LOGGER.debug(
        "{} depends on {} ",
        sourceSet,
        artifacts.stream().map(String::valueOf).collect(Collectors.joining(":")));
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

  public String getClassPath(Path sourceSet) {
    return getDependencies(sourceSet)
        .stream()
        .map(this::get)
        .map(String::valueOf)
        .collect(Collectors.joining(":"));
  }
}
