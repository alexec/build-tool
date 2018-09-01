package bt.tasks.source.finder;

import bt.api.TaskFactory;

public class SourceSetFinderFactory implements TaskFactory<Class<SourceSetFinder>> {
  @Override
  public Class<SourceSetFinder> get() {
    return SourceSetFinder.class;
  }
}
