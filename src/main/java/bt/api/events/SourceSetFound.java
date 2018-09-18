package bt.api.events;

import java.nio.file.Path;

public class SourceSetFound {
  private final Path path;

  public SourceSetFound(Path path) {
    this.path = path;
  }

  public Path getSourceSet() {
    return path;
  }

  @Override
  public String toString() {
    return "SourceSetFound(" + path + ')';
  }
}
