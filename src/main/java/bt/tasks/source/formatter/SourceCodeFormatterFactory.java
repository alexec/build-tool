package bt.tasks.source.formatter;

import bt.api.TaskFactory;

public class SourceCodeFormatterFactory implements TaskFactory<Class<SourceFormatter>> {
    @Override
    public Class<SourceFormatter> get() {
        return SourceFormatter.class;
    }
}
