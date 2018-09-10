package bt.tasks.java.compiler;

import lombok.NonNull;
import lombok.Value;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Value
public class CompiledCode {
  @NonNull private final Map<Path, List<Path>> compiledCode;
}
