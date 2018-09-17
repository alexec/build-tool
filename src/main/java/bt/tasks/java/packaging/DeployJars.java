package bt.tasks.java.packaging;

import bt.api.Artifact;
import bt.api.Dependency;
import bt.api.Project;
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
  @Inject private Project project;
  @Inject private Jars jars;
  @Inject private Repository repository;

  @Override
  public Void run() throws Exception {

    for (Path jar : this.jars.getJars()) {
      Artifact artifact = project.getArtifact();
      Path target =
          repository.get(
              Dependency.valueOf(
                  artifact.getGroupId()
                      + ":"
                      + artifact.getArtifactId()
                      + ":jar:"
                      + artifact.getVersion()
                      + ":"
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
