package bt.tasks.java.finder;

import bt.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

public class FindSourceSets implements Task<Set<Path>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FindSourceSets.class);

  @Override
  public Set<Path> run() throws IOException {
    Set<Path> sourceSets =
        Files.find(
                Paths.get("src"),
                1,
                (path, basicFileAttributes) -> Files.isDirectory(path) && path.getNameCount() == 2)
            .peek(sourceSet -> LOGGER.debug("{}", sourceSet))
            .collect(Collectors.toSet());
    LOGGER.debug("found {} source set(s)", sourceSets.size());
    return sourceSets;
  }
}
