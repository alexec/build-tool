package bt.tasks.java.test;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EmbeddedTestRunner {

  /** Runs tests. */
  public static void main(String[] args) throws Exception {

    JUnitCore junit = new JUnitCore();
    junit.addListener(
        new RunListener() {
          @Override
          public void testRunStarted(Description description) {
            System.out.println("Test run started");
          }

          @Override
          public void testRunFinished(Result result) {
            System.out.println(
                "Test run finished successful "
                    + result.wasSuccessful()
                    + ", "
                    + result.getFailureCount()
                    + " failures, "
                    + result.getIgnoreCount()
                    + " ignored");
          }

          @Override
          public void testStarted(Description description) {
            System.out.println("Test started " + description);
          }

          @Override
          public void testFinished(Description description) {
            System.out.println("Test finished " + description);
          }

          @Override
          public void testFailure(Failure failure) {
            System.out.println("Test failed " + failure);
          }

          @Override
          public void testAssumptionFailure(Failure failure) {
            System.out.println("Test assumption failure " + failure);
          }

          @Override
          public void testIgnored(Description description) {
            System.out.println("Test ignored " + description);
          }
        });

    Result result = junit.run(testClasses().toArray(new Class[0]));

    if (!result.wasSuccessful()) {
      throw new IllegalStateException();
    }
  }

  private static List<? extends Class<?>> testClasses() throws IOException {
    try (BufferedReader in =
        new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(
                        EmbeddedTestRunner.class.getClassLoader().getResource("META-INF/tests"))
                    .openStream()))) {
      return in.lines()
          .map(EmbeddedTestRunner::toClass)
          .sorted(Comparator.comparing(Class::getName))
          .collect(Collectors.toList());
    }
  }

  private static Class<?> toClass(String s) {
    try {
      return Class.forName(s);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }
}
