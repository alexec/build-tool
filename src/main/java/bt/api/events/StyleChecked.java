package bt.api.events;

import java.nio.file.Path;

public class StyleChecked {
  private final Path sourceSet;

  public StyleChecked(Path sourceSet) {
    this.sourceSet = sourceSet;
  }

  @Override
  public String toString() {
    return "StyleChecked(" + sourceSet + ')';
  }
}
