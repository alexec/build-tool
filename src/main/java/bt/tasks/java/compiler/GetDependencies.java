package bt.tasks.java.compiler;

import bt.api.Task;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GetDependencies implements Task<Map<Path, List<Artifact>>> {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  @Inject private Set<Path> sourceSets;

  @Override
  public Map<Path, List<Artifact>> run() throws Exception {
    Map<Path, List<Artifact>> dependencies = new HashMap<>();
    for (Path sourceSet : sourceSets) {
      dependencies.put(
          sourceSet.resolve(Paths.get("java")),
          OBJECT_MAPPER.readValue(
              sourceSet.resolve(Paths.get("java", "dependencies.json")).toFile(),
              new TypeReference<List<Artifact>>() {}));
    }

    return dependencies;
  }

  @Override
  public String toString() {
    return "GetDependencies{}";
  }
}
