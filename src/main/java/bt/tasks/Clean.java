package bt.tasks;

import bt.api.Args;
import bt.api.Task;
import bt.api.events.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class Clean implements Task<Start> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Clean.class);
  @Inject private Args args;

  @Override
  public Class<Start> eventType() {
    return Start.class;
  }

  @Override
  public void consume(Start event) throws Exception {
    if (!args.getCommands().contains("clean")) {
      LOGGER.debug("skipping, not 'clean' in args");
      return;
    }
    //noinspection ResultOfMethodCallIgnored
    Files.walk(Paths.get("target"))
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .peek(file -> LOGGER.debug("deleting {}", file))
        .forEach(File::delete);
  }

  @Override
  public String toString() {
    return "clean()";
  }
}
