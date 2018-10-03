package bt.api.events;

import java.nio.file.Path;

public class TestsRun {
  private final Path compiledCode;
  private final int number;

  public TestsRun(Path compiledCode, int number) {

    this.compiledCode = compiledCode;
    this.number = number;
  }

  @Override
  public String toString() {
    return "TestsRun(" + compiledCode + ',' + number + ')';
  }
}
