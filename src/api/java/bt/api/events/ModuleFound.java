package bt.api.events;

import bt.api.Module;

public class ModuleFound {
  private final Module module;

  public ModuleFound(Module module) {
    this.module = module;
  }

  public Module getModule() {
    return module;
  }

  @Override
  public String toString() {
    return "ModuleFound(" + module + ')';
  }
}
