package bt.api;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Project {
  private Artifact artifact;
  private List<Path> modules;

  void setArtifact(Artifact artifact) {
    this.artifact = artifact;
  }

  public Artifact getArtifact() {
    return artifact;
  }

  public List<Path> getModules() {
    return modules;
  }

  public void setModules(List<Path> modules) {
    this.modules = modules;
  }
}
