package bt.tasks.java.resources;

import bt.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

public class CopyResources implements Task<Void> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CopyResources.class);
  @Inject private Set<Path> sourceSets;

  @Override
  public Void run() throws Exception {
    for (Path sourceSet : sourceSets) {
      Path resources = sourceSet.resolve("resources");
      Path target = sourceSet.resolve("../../target/java/" + sourceSet.getFileName());

      if (!Files.exists(resources)) {
        LOGGER.debug("skipping {}, does not exist", resources);
        continue;
      }

      LOGGER.debug("coping {} to {}", resources, target);
      Files.walk(resources)
          .filter(path -> Files.isRegularFile(path))
          .forEach(
              sourcePath -> {
                Path dest = target.resolve(resources.relativize(sourcePath));
                if (Files.exists(dest)
                    && dest.toFile().lastModified() >= sourcePath.toFile().lastModified()) {
                  return;
                }
                LOGGER.debug("coping {} to {}", sourcePath, dest);
                try {
                  if (!Files.exists(dest.getParent())) {
                    Files.createDirectories(dest.getParent());
                  }

                  Files.copy(sourcePath, dest, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                  throw new UncheckedIOException(e);
                }
              });
    }
    return null;
  }
}
