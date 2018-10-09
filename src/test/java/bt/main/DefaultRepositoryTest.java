package bt.main;

import bt.api.Dependency;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

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
}
