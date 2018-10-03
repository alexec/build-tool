package bt.tasks.java.finder;

import bt.api.Artifact;
import bt.api.EventBus;
import bt.api.Module;
import bt.api.Project;
import bt.api.Repository;
import bt.api.Task;
import bt.api.events.ModuleFound;
import bt.api.events.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FindModules implements Task<Start> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FindModules.class);
  @Inject private Project project;
  @Inject private EventBus eventBus;
  @Inject private Repository repository;

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
        .sorted()
        .map(
            sourceSet ->
                new Module(
                    sourceSet,
                    outputDirectory(sourceSet),
                    Artifact.valueOf(
                        project.getArtifact().getGroupId()
                            + ":"
                            + sourceSet.getFileName()
                            + ":"
                            + project.getArtifact().getVersion())))
        .forEach(
            module -> {
              LOGGER.debug("{}", module);
              repository.addModule(module);
              eventBus.add(new ModuleFound(module));
            });
  }

  private Path outputDirectory(Path sourceSet) {
    return sourceSet
        .resolve(Paths.get("..", "..", "target", "java", sourceSet.getFileName().toString()))
        .normalize();
  }
}
