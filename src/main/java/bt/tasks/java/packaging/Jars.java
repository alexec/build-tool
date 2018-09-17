package bt.tasks.java.packaging;

import java.nio.file.Path;
import java.util.List;

class Jars {
  private final List<Path> jars;

  Jars(List<Path> jars) {
    this.jars = jars;
  }

  List<Path> getJars() {
    return jars;
  }
}
