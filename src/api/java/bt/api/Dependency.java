package bt.api;

import java.util.Objects;

public abstract class Dependency {

  Dependency() {}

  /** Create a dependency form a string. */
  @SuppressWarnings("unused")
  public static Dependency valueOf(String text) {
    return text.startsWith(":")
        ? new ModuleDependency(text.substring(1))
        : new ArtifactDependency(Artifact.valueOf(text));
  }
}
