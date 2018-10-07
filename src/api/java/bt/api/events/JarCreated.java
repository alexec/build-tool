package bt.api.events;

import bt.api.Module;

import java.nio.file.Path;

public class JarCreated {
  private final Module module;
  private final Path path;

  public JarCreated(Module module, Path path) {
    this.module = module;
    this.path = path;
  }

  public Module getModule() {
    return module;
  }

  public Path getPath() {
    return path;
  }

  @Override
  public String toString() {
    return "JarCreated(" + module.getArtifact() + ')';
  }
}
