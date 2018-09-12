package bt.api;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Repository {
  private Path get(Dependency.PathDependency dependency) {
    return dependency.getPath();
  }

  private Path get(Dependency.ArtifactDependency dependency) {
    Artifact artifact = dependency.getArtifact();

    return Paths.get(
        System.getProperty("user.home"),
        ".m2",
        "repository",
        artifact.getGroupId().replaceAll("\\.", "/"),
        artifact.getArtifactId(),
        artifact.getVersion(),
        artifact.getArtifactId() + "-" + artifact.getVersion() + "." + artifact.getType());
  }

  /** Get the path of the dependency. */
  public Path get(Dependency dependency) {
    return dependency instanceof Dependency.ArtifactDependency
        ? get((Dependency.ArtifactDependency) dependency)
        : get((Dependency.PathDependency) dependency);
  }
}
