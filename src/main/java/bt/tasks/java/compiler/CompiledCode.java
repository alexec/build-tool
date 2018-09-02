package bt.tasks.java.compiler;

import lombok.NonNull;
import lombok.Value;

import java.nio.file.Path;
import java.util.Set;

@Value
class CompiledCode {
  @NonNull private final Set<Path> paths;
}
