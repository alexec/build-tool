package bt.api.events;

import bt.api.Module;

import java.nio.file.Path;

public class JarDeployed {
  private final Module module;
  private final Path target;

  public JarDeployed(Module module, Path target) {
    this.module = module;
    this.target = target;
  }

  @Override
  public String toString() {
    return "JarDeployed(" + target + ')';
  }

  public Module getModule() {
    return module;
  }
}
