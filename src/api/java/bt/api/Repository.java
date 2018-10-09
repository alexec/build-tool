package bt.api;

import java.nio.file.Path;
import java.util.List;

public interface Repository {
  void addModule(Module module);

  void resolve(Dependency dependency);

  Path getPath(Dependency dependency);

  List<Dependency> getDependencies(Artifact artifact);

  List<Dependency> getDependencies(Module module);

  String getClassPath(Module module);
}
