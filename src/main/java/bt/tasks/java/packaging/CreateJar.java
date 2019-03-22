package bt.tasks.java.packaging;

import bt.api.EventBus;
import bt.api.Module;
import bt.api.Repository;
import bt.api.Subscribe;
import bt.api.Task;
import bt.api.events.CodeCompiled;
import bt.api.events.JarCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateJar implements Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateJar.class);
  @Inject private Repository repository;
  @Inject private EventBus eventBus;

  @Subscribe
  public void codeCompiled(CodeCompiled event) throws Exception {

    Module module = event.getModule();
    Path manifest =
        module.getSourceSet().resolve(Paths.get("resources", "META-INF", "MANIFEST.MF"));

    boolean jarWithDeps = Files.exists(manifest);
    Path jar =
        event
            .getModule()
            .getCompiledCode()
            .getParent()
            .resolve(Paths.get(event.getModule().getSourceSet().getFileName() + ".jar"));

    LOGGER.debug("{} -> {}", event.getModule(), jarWithDeps ? "runnable-jar" : "jar");

    if (jarWithDeps) {
      new RunnableJar(repository).codeCompiled(event, jar);
    } else {
      new StandardJar().codeCompiled(event, jar);
    }
    eventBus.emit(new JarCreated(module, jar));
  }
}
