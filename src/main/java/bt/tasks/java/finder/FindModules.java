package bt.tasks.java.finder;

import bt.api.Artifact;
import bt.api.EventBus;
import bt.api.Module;
import bt.api.Project;
import bt.api.Repository;
import bt.api.Subscribe;
import bt.api.Task;
import bt.api.events.ModuleFound;
import bt.api.events.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class FindModules implements Task {

  private static final Logger LOGGER = LoggerFactory.getLogger(FindModules.class);
  @Inject private Project project;
  @Inject private EventBus eventBus;
  @Inject private Repository repository;

  @Subscribe
  public void consume(Start event) throws Exception {
    // TODO - needs to be better
    project
        .getModules()
        .stream()
        .map(
            sourceSet ->
                new Module(
                    sourceSet,
                    buildDir(sourceSet),
                    Artifact.valueOf(
                        project.getArtifact().getGroupId()
                            + ":"
                            + sourceSet.getFileName()
                            + ":"
                            + project.getArtifact().getVersion())))
        .sorted(Comparator.comparing(Module::toString))
        .forEach(
            module -> {
              LOGGER.debug("{}", module);
              repository.addModule(module);
              eventBus.emit(new ModuleFound(module));
            });
  }

  private Path buildDir(Path sourceSet) {
    return sourceSet
        .resolve(Paths.get("..", "..", "target", "java", sourceSet.getFileName().toString()))
        .normalize();
  }
}
