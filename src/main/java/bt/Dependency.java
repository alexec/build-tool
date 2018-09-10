package bt;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Dependency {
  public abstract Path toPath();

  private static class PathDependency extends Dependency {
    private final Path path;

    private PathDependency(Path path) {
      this.path = path;
    }

    @Override
    public Path toPath() {
      return path;
    }

    @Override
    public String toString() {
      return String.valueOf(path);
    }
  }

  private static class ArtifactDependency extends Dependency {
    private final Artifact artifact;

    private ArtifactDependency(Artifact artifact) {
      this.artifact = artifact;
    }

    @Override
    public Path toPath() {
      return artifact.toPath();
    }

    @Override
    public String toString() {
      return String.valueOf(artifact);
    }
  }

  private Dependency() {}

  /** Create a dependency form a string. */
  public static Dependency valueOf(String text) {
    return text.contains(":")
        ? new ArtifactDependency(Artifact.valueOf(text))
        : new PathDependency(Paths.get(text));
  }
}
