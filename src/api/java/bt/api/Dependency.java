package bt.api;

public abstract class Dependency {

  public static class ModuleDependency extends Dependency implements Comparable<ModuleDependency> {
    private final String artifactId;

    private ModuleDependency(String artifactId) {
      this.artifactId = artifactId;
    }

    public String getArtifactId() {
      return artifactId;
    }

    @Override
    public int compareTo(ModuleDependency o) {
      return artifactId.compareTo(o.artifactId);
    }

    @Override
    public String toString() {
      return ":" + artifactId;
    }
  }

  public static class ArtifactDependency extends Dependency
      implements Comparable<ArtifactDependency> {
    private final Artifact artifact;

    private ArtifactDependency(Artifact artifact) {
      this.artifact = artifact;
    }

    @Override
    public String toString() {
      return String.valueOf(artifact);
    }

    public Artifact getArtifact() {
      return artifact;
    }

    @Override
    public int compareTo(ArtifactDependency o) {
      return artifact.compareTo(o.artifact);
    }
  }

  private Dependency() {}

  /** Create a dependency form a string. */
  @SuppressWarnings("unused")
  public static Dependency valueOf(String text) {
    return text.startsWith(":")
        ? new ModuleDependency(text.substring(1))
        : new ArtifactDependency(Artifact.valueOf(text));
  }
}
