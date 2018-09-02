package bt.tasks.java.finder;

import bt.api.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

public class FindSourceSets implements Task<Set<Path>> {

  @Override
  public Set<Path> run() throws IOException {
    return Files.find(Paths.get("src"), 1, (path, basicFileAttributes) -> path.getNameCount() == 2)
        .collect(Collectors.toSet());
  }

  @Override
  public String toString() {
    return "FindSourceSets{}";
  }
}
