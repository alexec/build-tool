package bt.api.events;

import java.nio.file.Path;

public class IntellJProjectCreated {
  private final Path output;

  public IntellJProjectCreated(Path output) {
    this.output = output;
  }

  @Override
  public String toString() {
    return "IntellJProjectCreated(" + output + ')';
  }
}
