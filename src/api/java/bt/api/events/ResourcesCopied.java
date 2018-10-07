package bt.api.events;

import bt.api.Module;

import java.nio.file.Path;

public class ResourcesCopied {
  private final Module module;
  private final Path target;

  public ResourcesCopied(Module module, Path target) {
    this.module = module;
    this.target = target;
  }

  public Module getModule() {
    return module;
  }

  @Override
  public String toString() {
    return "ResourcesCopied(" + target + ')';
  }
}
