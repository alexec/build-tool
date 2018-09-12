package bt.tasks.java.compiler;

import bt.api.Dependency;
import bt.api.Task;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GetDependencies implements Task<Map<Path, List<Dependency>>> {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Logger LOGGER = LoggerFactory.getLogger(GetDependencies.class);
  @Inject private Set<Path> sourceSets;

  @Override
  public Map<Path, List<Dependency>> run() throws Exception {
    Map<Path, List<Dependency>> dependencies = new HashMap<>();
    for (Path sourceSet : sourceSets) {

      List<Dependency> artifacts = new ArrayList<>();
      for (Path path :
          new Path[] {
            Paths.get("java", "dependencies.json"),
            Paths.get("dependencies.json"),
            Paths.get("../dependencies.json"),
            Paths.get("../../dependencies.json"),
          }) {
        Path dependenciesFile = sourceSet.resolve(path);
        if (Files.exists(dependenciesFile)) {
          LOGGER.info("reading {}", dependenciesFile);
          artifacts.addAll(
              OBJECT_MAPPER.readValue(
                  dependenciesFile.toFile(), new TypeReference<List<Dependency>>() {}));
        }
      }
      LOGGER.info(
          "{} depends on {} ",
          sourceSet,
          artifacts.stream().map(String::valueOf).collect(Collectors.joining(":")));
      dependencies.put(sourceSet, artifacts);
    }

    return dependencies;
  }
}
