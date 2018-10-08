package bt.api.events;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class SourceCodeFormatted {
  private final Path sourceSet;

  public SourceCodeFormatted(Path sourceSet) {
    this.sourceSet = requireNonNull(sourceSet);
  }

  @Override
  public String toString() {
    return "SourceCodeFormatted(" + sourceSet + ')';
  }
}
