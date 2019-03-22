package bt.tasks.java.packaging;

import bt.api.Dependency;
import bt.api.EventBus;
import bt.api.Module;
import bt.api.Project;
import bt.api.Repository;
import bt.api.Subscribe;
import bt.api.Task;
import bt.api.events.JarCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

public class CreateJarWithDependencies implements Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateJarWithDependencies.class);
  @Inject private Project project;
  @Inject private Repository repository;
  @Inject private EventBus eventBus;

  @Subscribe
  public void jarCreated(JarCreated event) throws Exception {

    Module module = event.getModule();
    Path manifest =
        module.getSourceSet().resolve(Paths.get("resources", "META-INF", "MANIFEST.MF"));

    if (Files.exists(manifest)) {
      LOGGER.info("{} found, creating jar-with-dependencies", manifest);

      Path jarWithDeps =
          module
              .getBuildDir()
              .resolve(
                  Paths.get(module.getArtifact().getArtifactId() + "-jar-with-dependencies.jar"));

      Map<String, Path> names = new HashMap<>();
      try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(jarWithDeps.toFile()))) {
        for (Dependency dependency : repository.getDependencies(module)) {
          LOGGER.debug("adding {} ", dependency);
          Path path = repository.getPath(dependency);
          File file = path.toFile();
          if (file.isFile()) {
            JarFile jar = new JarFile(file);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
              JarEntry jarEntry = entries.nextElement();
              if (jarEntry.isDirectory()) {
                continue;
              }
              String name = jarEntry.getName();
              if (names.containsKey(name)) {
                LOGGER.debug(
                    "found duplicate {} in {} (using {}), skipping", name, file, names.get(name));
                continue;
              }
              names.put(name, path);
              out.putNextEntry(new ZipEntry(name));

              try (InputStream in = jar.getInputStream(jarEntry)) {
                byte[] bytes = new byte[4096];
                int length;
                while ((length = in.read(bytes)) >= 0) {
                  out.write(bytes, 0, length);
                }
              }
              out.closeEntry();
            }
          } else {
            LOGGER.info("{}", path);
          }
        }
      }
    }
  }
}
