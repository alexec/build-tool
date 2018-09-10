package bt.tasks.java.test;

import bt.api.Task;
import bt.tasks.java.compiler.CompiledCode;
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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RunTests implements Task<TestResult> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RunTests.class);
  @Inject private CompiledCode compiledCode;

  @Override
  public TestResult run() throws IOException, InterruptedException {

    for (Map.Entry<Path, List<Path>> entry : compiledCode.getCompiledCode().entrySet()) {
      Path dir = entry.getKey();
      String classPath =
          dir
              + ":"
              + entry.getValue().stream().map(String::valueOf).collect(Collectors.joining(":"));

      if (!Files.exists(dir.resolve(Paths.get("META-INF/tests")))) {
        LOGGER.info("skipping {}, no tests", dir);
        continue;
      }

      LOGGER.info("running tests in {} with -cp {}", dir, classPath);

      Process process =
          new ProcessBuilder()
              .command("java", "-cp", classPath, "bt.tasks.java.test.EmbeddedTestRunner")
              .start();

      log(process.getInputStream(), LOGGER::info);
      log(process.getErrorStream(), LOGGER::error);

      int exitValue = process.waitFor();

      LOGGER.info("exit {}", exitValue);
    }

    return new TestResult();
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
