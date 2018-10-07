package bt.api.events;

import java.nio.file.Path;

public class IntelliJProjectCreated {
  private final Path output;

  public IntelliJProjectCreated(Path output) {
    this.output = output;
  }

  @Override
  public String toString() {
    return "IntelliJProjectCreated(" + output + ')';
  }
}
