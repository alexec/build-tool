package bt.main;

import bt.api.Artifact;
import bt.api.Dependency;
import bt.api.Module;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class DefaultRepositoryTest {

  private final DefaultRepository defaultRepository = new DefaultRepository();
  private final Dependency dependency = Dependency.valueOf("org.slf4j:slf4j-api:jar:1.6.5");

  @Test
  @Ignore("slow")
  public void download() throws Exception {

    defaultRepository.setDownload(false);

    Files.deleteIfExists(defaultRepository.getPath(dependency));

    defaultRepository.setDownload(true);

    Path path = defaultRepository.getPath(dependency);

    assertTrue(Files.exists(path));
  }

  @Test
  public void resolveDependencies() {

    defaultRepository.addModule(
        new Module(
            Paths.get("src", "api"),
            Paths.get("target", "java", "api"),
            Artifact.valueOf("bt:api:1")));

    Module module = new Module(Paths.get("src", "main"), null, null);
    List<Dependency> dependencies = defaultRepository.getDependencies(module);

    System.out.println(dependencies);

    assertEquals(27, dependencies.size());

    String classPath = defaultRepository.getClassPath(module);

    System.out.println(classPath);

    assertEquals(27, classPath.split(":").length);
  }
}
