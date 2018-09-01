package bt.tasks.source.style;

import bt.api.TaskFactory;

public class StyleCheckerFactory implements TaskFactory<CheckStyle> {
  @Override
  public Class<CheckStyle> get() {
    return CheckStyle.class;
  }
}
