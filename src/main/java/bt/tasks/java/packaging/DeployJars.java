package bt.tasks.java.packaging;

import bt.api.Dependency;
import bt.api.Repository;
import bt.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DeployJars implements Task<Void> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeployJars.class);
  @Inject private Jars jars;
  @Inject private Repository repository;

  @Override
  public Void run() throws Exception {

    for (Path jar : this.jars.getJars()) {
      Path target =
          repository.get(
              Dependency.valueOf(
                  "bt:bt:jar:1.0.0-SNAPSHOT:"
                      + jar.getFileName().toString().replaceFirst("\\..*", "")));

      if (Files.exists(target) && target.toFile().lastModified() >= jar.toFile().lastModified()) {
        LOGGER.debug("skipping {}, {} is unchanged", jar, target);
        continue;
      }

      LOGGER.debug("deploying {} to {}", jar, target);

      Files.createDirectories(target.getParent());

      Files.copy(jar, target, StandardCopyOption.REPLACE_EXISTING);
    }

    return null;
  }
}
