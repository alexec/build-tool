package bt.tasks.java.ide;

import bt.api.EventBus;
import bt.api.Repository;
import bt.api.Task;
import bt.api.events.CodeCompiled;
import bt.api.events.ImlFileCreated;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImlCreator implements Task<CodeCompiled> {

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

        Path sourceSet = event.getSourceSet();

        boolean testSource = Files.exists(event.getCompiledCode().resolve(Paths.get("META-INF", "tests")));

        String context = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<module type=\"JAVA_MODULE\" version=\"4\">\n" +
                "  <component name=\"NewModuleRootManager\" inherit-compiler-output=\"true\">\n" +
                "    <exclude-output />\n" +
                "    <content url=\"file://$MODULE_DIR$\">\n" +
                "      <sourceFolder url=\"file://$MODULE_DIR$/java\" isTestSource=\""+testSource+"\" />\n" +
                "    </content>\n" +
                "    <orderEntry type=\"inheritedJdk\" />\n" +
                "    <orderEntry type=\"sourceFolder\" forTests=\""+testSource+"\" />\n" +
                "  </component>\n" +
                "</module>";
        Path path = sourceSet.resolve(Paths.get(sourceSet.getFileName() + ".iml"));

        Files.write(path, context.getBytes());

        eventBus.add(new ImlFileCreated(path));
    }
}
