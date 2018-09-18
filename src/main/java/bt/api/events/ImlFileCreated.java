package bt.api.events;

import java.nio.file.Path;

public class ImlFileCreated {
  private final Path output;

  public ImlFileCreated(Path output) {
    this.output = output;
  }

  @Override
  public String toString() {
    return "ImlFileCreated(" + output + ')';
  }
}
