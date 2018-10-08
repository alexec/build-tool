package bt.api.events;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class StyleChecked {
  private final Path sourceSet;

  public StyleChecked(Path sourceSet) {
    this.sourceSet = requireNonNull(sourceSet);
  }

  @Override
  public String toString() {
    return "StyleChecked(" + sourceSet + ')';
  }
}
