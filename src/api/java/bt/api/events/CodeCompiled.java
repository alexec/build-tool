package bt.api.events;

import bt.api.Module;

import static java.util.Objects.requireNonNull;

public class CodeCompiled {
  private final Module module;

  public CodeCompiled(Module module) {
    this.module = requireNonNull(module);
  }

  public Module getModule() {
    return module;
  }

  @Override
  public String toString() {
    return "CodeCompiled(" + module + ')';
  }
}
