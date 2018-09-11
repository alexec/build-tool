package bt.tasks.java.packaging;

import bt.api.Task;
import bt.tasks.java.compiler.CompiledCode;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class CreateJars implements Task<List<Path>> {
  @Inject private CompiledCode compiledCode;

  @Override
  public List<Path> run() {
    return Collections.emptyList();
  }
}
