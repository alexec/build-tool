package bt.tasks.java.ide;

import bt.api.Dependency;
import bt.api.EventBus;
import bt.api.Project;
import bt.api.Repository;
import bt.api.Subscribe;
import bt.api.Task;
import bt.api.events.IntelliJProjectCreated;
import bt.api.events.ModuleFound;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateIntelliJProject implements Task {
  @Inject private Project project;
  @Inject private Repository repository;
  @Inject private EventBus eventBus;
  private final List<Path> sourcesSets = new ArrayList<>();

  @Subscribe
  public void consume(ModuleFound event) throws Exception {

    sourcesSets.add(event.getModule().getSourceSet());

    String context =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<project version=\"4\">\n"
            + "  <component name=\"InspectionProjectProfileManager\">\n"
            + "    <profile version=\"1.0\">\n"
            + "      <option name=\"myName\" value=\"Project Default\" />\n"
            + "    </profile>\n"
            + "    <version value=\"1.0\" />\n"
            + "  </component>\n"
            + "  <component name=\"ProjectModuleManager\">\n"
            + "    <modules>\n"
            + (sourcesSets
                .stream()
                .map(sourceSet -> sourceSet.resolve(sourceSet.getFileName() + ".iml"))
                .map(
                    iml ->
                        "      <module fileurl=\"file://$PROJECT_DIR$/"
                            + iml
                            + "\" filepath=\"$PROJECT_DIR$/"
                            + iml
                            + "\" />")
                .collect(Collectors.joining("\n")))
            + "\n"
            + "    </modules>\n"
            + "  </component>\n"
            + "  <component name=\"ProjectRootManager\" version=\"2\" languageLevel=\"JDK_1_8\" project-jdk-name=\"1.8\" project-jdk-type=\"JavaSDK\"/>\n"
            + "  <component name=\"VcsDirectoryMappings\">\n"
            + "    <mapping directory=\"$PROJECT_DIR$\" vcs=\"Git\" />\n"
            + "  </component>\n"
            + "\n"
            + "  <component name=\"libraryTable\">\n"
            + (sourcesSets
                .stream()
                .flatMap(sourceSet -> repository.getDependencies(sourceSet).stream())
                .filter(dependency -> dependency instanceof Dependency.ArtifactDependency)
                .sorted()
                .distinct()
                .map(
                    dependency ->
                        "    <library name=\""
                            + dependency
                            + "\" type=\"repository\">\n"
                            + "      <properties maven-id=\""
                            + dependency
                            + "\" />\n"
                            + "      <CLASSES>\n"
                            + "        <root url=\"jar://"
                            + repository.get(dependency)
                            + "!/\" />\n"
                            + "      </CLASSES>\n"
                            + "      <JAVADOC />\n"
                            + "      <SOURCES />\n"
                            + "    </library>\n")
                .collect(Collectors.joining("")))
            + "\n"
            + "  </component>"
            + "</project>";
    Path path = Paths.get(project.getArtifact().getArtifactId() + ".ipr");

    Files.write(path, context.getBytes());

    eventBus.emit(new IntelliJProjectCreated(path));
  }
}
