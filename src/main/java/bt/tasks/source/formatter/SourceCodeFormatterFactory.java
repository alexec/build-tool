package bt.tasks.source.formatter;

import bt.api.TaskFactory;

public class SourceCodeFormatterFactory implements TaskFactory<SourceFormatter> {
  @Override
  public Class<SourceFormatter> get() {
    return SourceFormatter.class;
  }
}
