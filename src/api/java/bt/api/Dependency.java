package bt.api;

public abstract class Dependency implements Comparable<Dependency> {

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

    public Artifact getArtifact() {
      return artifact;
    }
  }

  private Dependency() {}

  @Override
  public int compareTo(Dependency o) {
    return toString().compareTo(o.toString());
  }

  @Override
  public boolean equals(Object obj) {
    return this.toString().equals(obj.toString());
  }

  /** Create a dependency form a string. */
  @SuppressWarnings("unused")
  public static Dependency valueOf(String text) {
    return text.startsWith(":")
        ? new ModuleDependency(text.substring(1))
        : new ArtifactDependency(Artifact.valueOf(text));
  }
}
