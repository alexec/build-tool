package bt.api.events;

import java.nio.file.Path;

public class JarDeployed {
  private final Path target;

  public JarDeployed(Path target) {
    this.target = target;
  }

  @Override
  public String toString() {
    return "JarDeployed(" + target + ')';
  }
}
