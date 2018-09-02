package bt.tasks.source.finder;

import bt.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FindSourceSets implements Task<SourceSets> {
  private static final Logger LOGGER = LoggerFactory.getLogger(FindSourceSets.class);

  @Override
  public SourceSets run() throws IOException {
    return new SourceSets(
        Files.find(Paths.get("src"), 1, (path, basicFileAttributes) -> path.getNameCount() == 2)
            .collect(Collectors.toSet()));
  }

  @Override
  public String toString() {
    return "FindSourceSets{}";
  }
}
