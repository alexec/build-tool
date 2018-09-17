package bt.tasks.java.finder;

import bt.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
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
                Paths.get("."),
                Integer.MAX_VALUE,
                (path, attributes) -> path.getFileName().equals(Paths.get("src")))
            .flatMap(
                path -> {
                  try {
                    return Files.find(path, 1, (path1, attributes) -> Files.isDirectory(path1));
                  } catch (IOException e) {
                    throw new UncheckedIOException(e);
                  }
                })
            .peek(sourceSet -> LOGGER.debug("{}", sourceSet))
            .collect(Collectors.toSet());
    LOGGER.debug("found {} source set(s)", sourceSets.size());
    return sourceSets;
  }
}
