package bt.api.events;

import bt.api.Module;

import static java.util.Objects.requireNonNull;

public class ModuleFound {
  private final Module module;

  public ModuleFound(Module module) {
    this.module = requireNonNull(module);
  }

  public Module getModule() {
    return module;
  }

  @Override
  public String toString() {
    return "ModuleFound(" + module + ')';
  }
}
