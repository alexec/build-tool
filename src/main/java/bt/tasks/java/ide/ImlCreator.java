package bt.tasks.java.ide;

import bt.api.EventBus;
import bt.api.Project;
import bt.api.Repository;
import bt.api.Task;
import bt.api.events.CodeCompiled;
import bt.api.events.IprFileCreated;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IprCreator implements Task<CodeCompiled> {
    @Inject
    private Project project;
    @Inject
    private Repository repository;
    @Inject
    private EventBus eventBus;

    @Override
    public Class<CodeCompiled> eventType() {
        return CodeCompiled.class;
    }

    @Override
    public void consume(CodeCompiled event) throws Exception {

        String context = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project version=\"4\">\n" +
                "  <component name=\"ProjectModuleManager\">\n" +
                "    <modules>\n" +
                "      <module fileurl=\"file://$PROJECT_DIR$/src/main/main.iml\" filepath=\"$PROJECT_DIR$/src/main/main.iml\" />\n" +
                "      <module fileurl=\"file://$PROJECT_DIR$/src/test/test.iml\" filepath=\"$PROJECT_DIR$/src/test/test.iml\" />\n" +
                "    </modules>\n" +
                "  </component>\n" +
                "  <component name=\"ProjectRootManager\" version=\"2\" languageLevel=\"JDK_1_8\" project-jdk-name=\"1.8\" project-jdk-type=\"JavaSDK\">\n" +
                "    <output url=\"file://$PROJECT_DIR$/target/java/\" />\n" +
                "  </component>\n" +
                "</project>";
        Path path = Paths.get(project.getArtifact().getArtifactId() + ".ipr");

        Files.write(path, context.getBytes());

        eventBus.add(new IprFileCreated(path));
    }
}
