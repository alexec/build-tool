package bt.api.events;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class TestsRun {
  private final Path compiledCode;
  private final int number;

  public TestsRun(Path compiledCode, int number) {

    this.compiledCode = requireNonNull(compiledCode);
    this.number = number;
  }

  @Override
  public String toString() {
    return "TestsRun(" + compiledCode + ',' + number + ')';
  }
}
