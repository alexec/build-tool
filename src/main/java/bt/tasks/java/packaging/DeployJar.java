package bt.tasks.java.packaging;

import bt.api.Artifact;
import bt.api.Dependency;
import bt.api.EventBus;
import bt.api.Project;
import bt.api.Repository;
import bt.api.Subscribe;
import bt.api.Task;
import bt.api.events.JarCreated;
import bt.api.events.JarDeployed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DeployJar implements Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeployJar.class);
  @Inject private Project project;
  @Inject private Repository repository;
  @Inject private EventBus eventBus;

  @Subscribe
  public void jarCreated(JarCreated event) throws Exception {
    Path jar = event.getPath();
    Artifact artifact = project.getArtifact();
    Path target =
        repository.getPath(
            Dependency.valueOf(
                artifact.getGroupId()
                    + ":"
                    + jar.getFileName().toString().replaceFirst("\\..*", "")
                    + ":"
                    + artifact.getVersion()));

    if (Files.exists(target) && target.toFile().lastModified() >= jar.toFile().lastModified()) {
      LOGGER.debug("skipping {}, {} is unchanged", jar, target);
    } else {

      LOGGER.debug("deploying {} to {}", jar, target);

      Files.createDirectories(target.getParent());

      Files.copy(jar, target, StandardCopyOption.REPLACE_EXISTING);
    }

    eventBus.emit(new JarDeployed(event.getModule(), target));
  }
}
