package bt.api;

public class Project {
  private Artifact artifact;

  void setArtifact(Artifact artifact) {
    this.artifact = artifact;
  }

  public Artifact getArtifact() {
    return artifact;
  }
}