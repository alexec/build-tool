package bt.api.events;

import java.nio.file.Path;

public class CodeCompiled {
  public CodeCompiled(Path sourceSet, Path compiledCode) {
    this.sourceSet = sourceSet;
    this.compiledCode = compiledCode;
  }

  private final Path sourceSet, compiledCode;

  public Path getSourceSet() {
    return sourceSet;
  }

  public Path getCompiledCode() {
    return compiledCode;
  }

  @Override
  public String toString() {
    return "CodeCompiled(" + sourceSet + " -> " + compiledCode + ')';
  }
}
