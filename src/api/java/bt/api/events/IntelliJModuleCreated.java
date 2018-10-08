package bt.api.events;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class IntelliJModuleCreated {
  private final Path output;

  public IntelliJModuleCreated(Path output) {
    this.output = requireNonNull(output);
  }

  @Override
  public String toString() {
    return "IntelliJModuleCreated(" + output + ')';
  }
}
