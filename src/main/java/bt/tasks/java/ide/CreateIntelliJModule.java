package bt.tasks.java.ide;

import bt.api.Dependency;
import bt.api.EventBus;
import bt.api.Repository;
import bt.api.Subscribe;
import bt.api.Task;
import bt.api.events.IntelliJModuleCreated;
import bt.api.events.ModuleFound;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class CreateIntelliJModule implements Task {

  @Inject private Repository repository;
  @Inject private EventBus eventBus;

  @Subscribe
  public void moduleFound(ModuleFound event) throws Exception {

    Path sourceSet = event.getModule().getSourceSet();

    boolean testSource =
        repository
            .getDependencies(sourceSet)
            .stream()
            .anyMatch(dependency -> dependency.toString().contains("junit"));

    String context =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<module type=\"JAVA_MODULE\" version=\"4\">\n"
            + "  <component name=\"NewModuleRootManager\">\n"
            + "    <output url=\"file://$MODULE_DIR$/../../target/java/"
            + event.getModule().getName()
            + "/classes\" />\n"
            + "    <output-test url=\"file://$MODULE_DIR$/../../target/java/"
            + event.getModule().getName()
            + "/classes\" />\n"
            + "    <exclude-output />\n"
            + "    <content url=\"file://$MODULE_DIR$\">\n"
            + "      <sourceFolder url=\"file://$MODULE_DIR$/java\" isTestSource=\""
            + testSource
            + "\" />\n"
            + "      <sourceFolder url=\"file://$MODULE_DIR$/resources\" isTestSource=\""
            + testSource
            + "\" />\n"
            + "    </content>\n"
            + "    <orderEntry type=\"inheritedJdk\" />\n"
            + "    <orderEntry type=\"sourceFolder\" forTests=\""
            + false
            + "\" />\n"
            + (repository
                .getDependencies(sourceSet)
                .stream()
                .map(
                    dependency -> {
                      if (dependency instanceof Dependency.ModuleDependency) {
                        return "    <orderEntry type=\"module\" module-name=\""
                            + ((Dependency.ModuleDependency) dependency).getArtifactId()
                            + "\" />";
                      } else {
                        return "    <orderEntry type=\""
                            + "library"
                            + "\" name=\""
                            + dependency
                            + "\" level=\"project\" />";
                      }
                    })
                .collect(Collectors.joining("\n")))
            + "\n"
            + "  </component>\n"
            + "</module>";
    Path path = sourceSet.resolve(Paths.get(sourceSet.getFileName() + ".iml"));

    Files.write(path, context.getBytes());

    eventBus.emit(new IntelliJModuleCreated(path));
  }
}
