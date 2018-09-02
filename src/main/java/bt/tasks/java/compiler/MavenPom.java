package bt.tasks.java.compiler;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
class MavenPom {
  private List<MavenDependency> dependencies = Collections.emptyList();
}
