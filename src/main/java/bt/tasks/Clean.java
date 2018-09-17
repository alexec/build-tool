package bt.tasks;

import bt.api.Args;
import bt.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class Clean implements Task<Void> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Clean.class);
  @Inject private Args args;

  @Override
  public Void run() throws Exception {
    if (!args.getCommands().contains("clean")) {
      LOGGER.debug("skipping, not 'clean' in args");
      return null;
    }
    //noinspection ResultOfMethodCallIgnored
    Files.walk(Paths.get("target"))
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .peek(file -> LOGGER.debug("deleting {}", file))
        .forEach(File::delete);

    return null;
  }
}
