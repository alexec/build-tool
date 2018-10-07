package bt.api.events;

import bt.api.Module;

import java.nio.file.Path;

public class CodeCompiled {
  private final Module module;

  public CodeCompiled(Module module) {
    this.module = module;
  }

  public Module getModule() {
    return module;
  }

  @Override
  public String toString() {
    return "CodeCompiled(" + module + ')';
  }
}
