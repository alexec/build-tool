package bt.api.events;

import bt.api.Module;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class JarCreated {
  private final Module module;
  private final Path path;

  public JarCreated(Module module, Path path) {
    this.module = requireNonNull(module);
    this.path = requireNonNull(path);
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
