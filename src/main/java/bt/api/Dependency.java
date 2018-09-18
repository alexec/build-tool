package bt.api;

import java.nio.file.Path;

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

  static class ModuleDependency extends Dependency {
    private final String artifactId;

    private ModuleDependency(String artifactId) {
      this.artifactId = artifactId;
    }

    String getArtifactId() {
      return artifactId;
    }

    @Override
    public String toString() {
      return ":" + artifactId;
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
    return text.startsWith(":")
        ? new ModuleDependency(text.substring(1))
        : text.contains(":")
            ? new ArtifactDependency(Artifact.valueOf(text))
            : new ModuleDependency(text);
  }
}
