package bt.api;

import java.nio.file.Path;
import java.util.List;

public interface Repository {
  void addModule(Module module);

  Path get(Dependency dependency);

  List<Dependency> getDependencies(Path sourceSet);

  String getClassPath(Path sourceSet);
}
