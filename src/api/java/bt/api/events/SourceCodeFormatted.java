package bt.api.events;

import java.nio.file.Path;

public class SourceCodeFormatted {
  private final Path sourceSet;

  public SourceCodeFormatted(Path sourceSet) {
    this.sourceSet = sourceSet;
  }

  @Override
  public String toString() {
    return "SourceCodeFormatted(" + sourceSet + ')';
  }
}
