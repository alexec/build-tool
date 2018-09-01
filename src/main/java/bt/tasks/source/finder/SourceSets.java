package bt.tasks.source.finder;

import lombok.NonNull;
import lombok.Value;

import java.nio.file.Path;
import java.util.Set;

@Value
public class SourceSets {
    @NonNull
    private final Set<Path> sourceSets;
}
