package bt.tasks.java.test;

import bt.api.Task;
import bt.tasks.java.compiler.CompiledCode;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class RunTests implements Task<TestResult> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RunTests.class);
  private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();
  @Inject private CompiledCode compiledCode;

  @Override
  public TestResult run() throws IOException {

    JUnitCore junit = new JUnitCore();
    junit.addListener(
        new RunListener() {
          @Override
          public void testRunStarted(Description description) {
            LOGGER.info("Test run started {}", description);
          }

          @Override
          public void testRunFinished(Result result) {
            LOGGER.info("Test run finished {}", result);
          }

          @Override
          public void testStarted(Description description) {
            LOGGER.info("Test started {}", description);
          }

          @Override
          public void testFinished(Description description) {
            LOGGER.info("Test finished {}", description);
          }

          @Override
          public void testFailure(Failure failure) {
            LOGGER.info("Test failed {}", failure);
          }

          @Override
          public void testAssumptionFailure(Failure failure) {
            LOGGER.info("Test assumption failure {}", failure);
          }

          @Override
          public void testIgnored(Description description) {
            LOGGER.info("Test ignored {}", description);
          }
        });

    for (Map.Entry<Path, List<Path>> entry : compiledCode.getCompiledCode().entrySet()) {

      List<Path> classPath = entry.getValue();

      boolean isJunit = classPath.stream().anyMatch(path -> path.toString().contains("junit"));

      if (!isJunit) {
        continue;
      }

      Path directory = entry.getKey();
      LOGGER.info("running tests in {}", directory);

      Files.find(
              directory,
              Integer.MAX_VALUE,
              (path, basicFileAttributes) ->
                  Files.isRegularFile(path) && path.getFileName().endsWith("Test.class"))
          .forEach(
              path -> {
                LOGGER.info("running {}", path);
              });
    }

    return new TestResult();
  }
}
