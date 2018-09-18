package bt.api.events;

import java.nio.file.Path;

public class JarCreated {
  private final Path path;

  public JarCreated(Path path) {

    this.path = path;
  }

  public Path getPath() {
    return path;
  }

  @Override
  public String toString() {
    return "JarCreated(" + path + ')';
  }
}
