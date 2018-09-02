package bt.tasks.java.compiler;

import lombok.Data;

@Data
class MavenDependency {
  private String groupId;
  private String artifactId;
  private String version;

  @Override
  public String toString() {
    return groupId + ":" + artifactId + ":" + version;
  }
}
