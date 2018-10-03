package bt.api.events;

import java.nio.file.Path;

public class IprFileCreated {
  private final Path output;

  public IprFileCreated(Path output) {
    this.output = output;
  }

  @Override
  public String toString() {
    return "IprFileCreated(" + output + ')';
  }
}
