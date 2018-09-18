package bt.tasks.java.ide;

import bt.api.Dependency;
import bt.api.EventBus;
import bt.api.Repository;
import bt.api.Task;
import bt.api.events.ImlFileCreated;
import bt.api.events.ModuleFound;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ImlCreator implements Task<ModuleFound> {

  @Inject private Repository repository;
  @Inject private EventBus eventBus;

  @Override
  public Class<ModuleFound> eventType() {
    return ModuleFound.class;
  }

  @Override
  public void consume(ModuleFound event) throws Exception {

    Path sourceSet = event.getModule().getSourceSet();

    boolean testSource =
        repository
            .getDependencies(sourceSet)
            .stream()
            .anyMatch(dependency -> dependency.toString().contains("junit"));

    String context =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<module type=\"JAVA_MODULE\" version=\"4\">\n"
            + "  <component name=\"NewModuleRootManager\" inherit-compiler-output=\"true\">\n"
            + "    <exclude-output />\n"
            + "    <content url=\"file://$MODULE_DIR$\">\n"
            + "      <sourceFolder url=\"file://$MODULE_DIR$/java\" isTestSource=\""
            + testSource
            + "\" />\n"
            + "    </content>\n"
            + "    <orderEntry type=\"inheritedJdk\" />\n"
            + "    <orderEntry type=\"sourceFolder\" forTests=\""
            + testSource
            + "\" />\n"
            + (repository
                .getDependencies(sourceSet)
                .stream()
                .map(
                    dependency -> {
                      String library =
                          dependency instanceof Dependency.ModuleDependency ? "module" : "library";
                      String name =
                          dependency instanceof Dependency.ModuleDependency
                              ? ((Dependency.ModuleDependency) dependency).getArtifactId()
                              : dependency.toString();
                      return "    <orderEntry type=\""
                          + library
                          + "\" name=\""
                          + name
                          + "\" level=\"project\" />";
                    })
                .collect(Collectors.joining("\n")))
            + "\n"
            + "  </component>\n"
            + "</module>";
    Path path = sourceSet.resolve(Paths.get(sourceSet.getFileName() + ".iml"));

    Files.write(path, context.getBytes());

    eventBus.add(new ImlFileCreated(path));
  }
}
