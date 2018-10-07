package bt.tasks.java.resources;

import bt.api.EventBus;
import bt.api.Subscribe;
import bt.api.Task;
import bt.api.events.ModuleFound;
import bt.api.events.ResourcesCopied;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CopyResources implements Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(CopyResources.class);
  @Inject private EventBus eventBus;

  @Subscribe
  public void consume(ModuleFound event) throws Exception {
    Path resources = event.getModule().getSourceSet().resolve("resources");
    Path target = event.getModule().getCompiledCode();

    if (!Files.exists(resources)) {
      LOGGER.debug("skipping {}, does not exist", resources);
    } else {
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
    eventBus.emit(new ResourcesCopied(event.getModule(), target));
  }
}
