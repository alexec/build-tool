package bt.api;

import java.util.ArrayList;
import java.util.List;

public class Project {
  private Artifact artifact;

  void setArtifact(Artifact artifact) {
    this.artifact = artifact;
  }

  public Artifact getArtifact() {
    return artifact;
  }
}
