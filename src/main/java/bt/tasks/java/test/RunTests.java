package bt.tasks.java.test;

import bt.api.Task;

public class RunTests implements Task<TestResult> {
  @Override
  public TestResult run() {
    return new TestResult();
  }
}
