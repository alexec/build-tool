package bt.tasks.java.test;

import bt.api.EventBus;
import bt.api.Repository;
import bt.api.Task;
import bt.api.events.CodeCompiled;
import bt.api.events.TestsRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class RunTests implements Task<CodeCompiled> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RunTests.class);
  @Inject private EventBus eventBus;
  @Inject private Repository repository;

  @Override
  public Class<CodeCompiled> eventType() {
    return CodeCompiled.class;
  }

  @Override
  public void consume(CodeCompiled event) throws Exception {

    Path compiledCode = event.getModule().getCompiledCode();

    if (!Files.exists(compiledCode.resolve(Paths.get("META-INF/tests")))) {
      LOGGER.debug("skipping {}, no tests", compiledCode);
    } else {

      String classPath =
          compiledCode + ":" + repository.getClassPath(event.getModule().getSourceSet());
      LOGGER.debug("running tests in {} with -cp {}", compiledCode, classPath);

      Process process =
          new ProcessBuilder()
              .command("java", "-cp", classPath, "bt.tasks.java.test.EmbeddedTestRunner")
              .start();

      log(process.getInputStream(), LOGGER::debug);
      log(process.getErrorStream(), LOGGER::warn);

      int exitValue = process.waitFor();

      LOGGER.debug("exit {}", exitValue);

      if (exitValue != 0) {
        throw new IllegalStateException();
      }

      eventBus.add(new TestsRun(compiledCode));
    }
  }

  private void log(InputStream inputStream, Consumer<String> info) {
    new Thread(
            () -> {
              try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
                in.lines().forEach(info);
              } catch (IOException e) {
                LOGGER.error("error {}", e);
              }
            })
        .start();
  }
}
