package bt.api;

import java.nio.file.Path;
import java.util.Objects;

public class Module {
  private final Path sourceSet, compiledCode;
  private final Artifact artifact;

  public Module(Path sourceSet, Path compiledCode, Artifact artifact) {
    this.sourceSet = sourceSet;
    this.compiledCode = compiledCode;
    this.artifact = artifact;
  }

  public Path getSourceSet() {
    return sourceSet;
  }

  public Path getCompiledCode() {
    return compiledCode;
  }

  public Artifact getArtifact() {
    return artifact;
  }

  public String getName() {
    return artifact.getArtifactId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Module module = (Module) o;
    return Objects.equals(sourceSet, module.sourceSet);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceSet);
  }

  @Override
  public String toString() {
    return artifact.toString();
  }
}
