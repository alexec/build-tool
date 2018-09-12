package bt.api;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Dependency {

  static class PathDependency extends Dependency {
    private final Path path;

    private PathDependency(Path path) {
      this.path = path;
    }

    public Path getPath() {
      return path;
    }

    @Override
    public String toString() {
      return String.valueOf(path);
    }
  }

  static class ArtifactDependency extends Dependency {
    private final Artifact artifact;

    private ArtifactDependency(Artifact artifact) {
      this.artifact = artifact;
    }

    @Override
    public String toString() {
      return String.valueOf(artifact);
    }

    Artifact getArtifact() {
      return artifact;
    }
  }

  private Dependency() {}

  /** Create a dependency form a string. */
  @SuppressWarnings("unused")
  public static Dependency valueOf(String text) {
    return text.contains(":")
        ? new ArtifactDependency(Artifact.valueOf(text))
        : new PathDependency(Paths.get(text));
  }
}
