package bt.tasks.java.compiler;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class CompiledCode {
  CompiledCode(Map<Path, List<Path>> compiledCode) {
    this.compiledCode = compiledCode;
  }

  private final Map<Path, List<Path>> compiledCode;

  public Map<Path, List<Path>> getCompiledCode() {
    return compiledCode;
  }
}
