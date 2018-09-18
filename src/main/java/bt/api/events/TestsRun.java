package bt.api.events;

import java.nio.file.Path;

public class TestsRun {
  private final Path compiledCode;

  public TestsRun(Path compiledCode) {

    this.compiledCode = compiledCode;
  }

  @Override
  public String toString() {
    return "TestsRun(" + compiledCode + ')';
  }
}
