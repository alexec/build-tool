package bt.api.events;

import java.nio.file.Path;

public class IntelliJModuleCreated {
  private final Path output;

  public IntelliJModuleCreated(Path output) {
    this.output = output;
  }

  @Override
  public String toString() {
    return "IntelliJModuleCreated(" + output + ')';
  }
}
