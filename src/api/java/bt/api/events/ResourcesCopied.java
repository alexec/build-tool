package bt.api.events;

import bt.api.Module;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class ResourcesCopied {
  private final Module module;
  private final Path target;

  public ResourcesCopied(Module module, Path target) {
    this.module = requireNonNull(module);
    this.target = requireNonNull(target);
  }

  public Module getModule() {
    return module;
  }

  @Override
  public String toString() {
    return "ResourcesCopied(" + target + ')';
  }
}
