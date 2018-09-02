package bt.tasks.source.finder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class SourceSetFinder implements Callable<SourceSets> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SourceSetFinder.class);

  @Override
  public SourceSets call() throws IOException {
    return new SourceSets(
        Files.find(Paths.get("src"), 1, (path, basicFileAttributes) -> path.getNameCount() == 2)
            .peek(path -> LOGGER.info("found {}", path))
            .collect(Collectors.toSet()));
  }
}
