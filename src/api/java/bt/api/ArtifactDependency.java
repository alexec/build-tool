package bt.api;

import java.util.Objects;

public class ArtifactDependency extends Dependency implements Comparable<ArtifactDependency> {
  private final Artifact artifact;

  public ArtifactDependency(Artifact artifact) {
    this.artifact = artifact;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ArtifactDependency that = (ArtifactDependency) o;
    return Objects.equals(artifact, that.artifact);
  }

  @Override
  public int hashCode() {
    return Objects.hash(artifact);
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
