package bt.api.events;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class IntelliJProjectCreated {
  private final Path output;

  public IntelliJProjectCreated(Path output) {
    this.output = requireNonNull(output);
  }

  @Override
  public String toString() {
    return "IntelliJProjectCreated(" + output + ')';
  }
}
