package bt.tasks.java.test;

import bt.api.EventBus;
import bt.api.Module;
import bt.api.Repository;
import bt.api.Subscribe;
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

public class RunTests implements Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(RunTests.class);
  @Inject private EventBus eventBus;
  @Inject private Repository repository;

  @Subscribe
  public void codeCompiled(CodeCompiled event) throws Exception {

    Module module = event.getModule();
    Path compiledCode = module.getCompiledCode();

    if (!Files.exists(compiledCode.resolve(Paths.get("META-INF/tests")))) {
      LOGGER.debug("skipping {}, no tests", compiledCode);
    } else {

      String classPath = compiledCode + ":" + repository.getClassPath(module);
      LOGGER.debug("running tests in {} with -cp {}", compiledCode, classPath);

      Process process =
          new ProcessBuilder()
              .command(
                  "java",
                  "-Xverify:none",
                  "-XX:TieredStopAtLevel=1",
                  "-cp",
                  classPath,
                  "bt.tasks.java.test.EmbeddedTestRunner")
              .start();

      final int[] number = {0};
      log(
          process.getInputStream(),
          line -> {
            LOGGER.debug(line);
            if (line.startsWith("Test started")) {
              number[0]++;
            }
          });
      log(process.getErrorStream(), LOGGER::warn);

      int exitValue = process.waitFor();

      LOGGER.debug("exit {}", exitValue);

      if (exitValue != 0) {
        throw new IllegalStateException();
      }

      eventBus.emit(new TestsRun(compiledCode, number[0]));
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
