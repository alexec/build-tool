package bt.api;

import java.util.Objects;

public class ModuleDependency extends Dependency implements Comparable<ModuleDependency> {
  private final String artifactId;

  public ModuleDependency(String artifactId) {
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ModuleDependency that = (ModuleDependency) o;
    return Objects.equals(artifactId, that.artifactId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(artifactId);
  }

  @Override
  public String toString() {
    return ":" + artifactId;
  }
}
