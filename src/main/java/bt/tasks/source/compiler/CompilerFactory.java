package bt.tasks.source.compiler;

import bt.api.TaskFactory;

public class CompilerFactory implements TaskFactory<Class<Compile>> {

  @Override
  public Class<Compile> get() {
    return Compile.class;
  }
}
