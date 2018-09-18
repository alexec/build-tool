package bt.tasks.java.finder;

import bt.api.EventBus;
import bt.api.Task;
import bt.api.events.SourceSetFound;
import bt.api.events.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FindSourceSets implements Task<Start> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FindSourceSets.class);
  @Inject private EventBus eventBus;

  @Override
  public Class<Start> eventType() {
    return Start.class;
  }

  @Override
  public void consume(Start event) throws Exception {
    // TODO - needs to be better
    Files.find(
            Paths.get("src"),
            1,
            (path, attributes) -> Files.isDirectory(path) && path.getNameCount() == 2)
        .peek(sourceSet -> LOGGER.debug("{}", sourceSet))
        .forEach(sourceSet -> eventBus.add(new SourceSetFound(sourceSet)));
  }
}
