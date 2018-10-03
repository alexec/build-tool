package bt.api;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Dependency implements Comparable<Dependency> {

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

  public static class ModuleDependency extends Dependency {
    private final String artifactId;

    private ModuleDependency(String artifactId) {
      this.artifactId = artifactId;
    }

    public String getArtifactId() {
      return artifactId;
    }

    @Override
    public String toString() {
      return ":" + artifactId;
    }
  }

  public static class ArtifactDependency extends Dependency {
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

  @Override
  public int compareTo(Dependency o) {
    return toString().compareTo(o.toString());
  }

  /** Create a dependency form a string. */
  @SuppressWarnings("unused")
  public static Dependency valueOf(String text) {
    return text.startsWith(":")
        ? new ModuleDependency(text.substring(1))
        : text.contains(":")
            ? new ArtifactDependency(Artifact.valueOf(text))
            : new PathDependency(Paths.get(text));
  }
}
