package bt.tasks.java.packaging;

import bt.api.Dependency;
import bt.api.Module;
import bt.api.Repository;
import bt.api.Task;
import bt.api.events.CodeCompiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class RunnableJar implements Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(RunnableJar.class);
  private final Repository repository;

  RunnableJar(Repository repository) {
    this.repository = repository;
  }

  void codeCompiled(CodeCompiled event, Path jar) throws Exception {

    Module module = event.getModule();
    Map<String, Path> names = new HashMap<>();

    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(jar.toFile()))) {
      addDirectory(names, out, event.getModule().getCompiledCode());
      for (Dependency dependency : repository.getDependencies(module)) {
        LOGGER.debug("adding {} ", dependency);
        Path path = repository.getPath(dependency);
        File file = path.toFile();
        if (file.isFile()) {
          addJar(names, out, path, file);
        } else {
          addDirectory(names, out, path);
        }
      }
    }
  }

  private void addJar(Map<String, Path> names, ZipOutputStream out, Path path, File file)
      throws IOException {
    JarFile jar = new JarFile(file);
    Enumeration<JarEntry> entries = jar.entries();
    while (entries.hasMoreElements()) {
      JarEntry jarEntry = entries.nextElement();
      if (jarEntry.isDirectory()) {
        continue;
      }
      String name = jarEntry.getName();
      if (names.containsKey(name)) {
        LOGGER.debug("found duplicate {} in {} (using {}), skipping", name, file, names.get(name));
        continue;
      }
      names.put(name, path);
      out.putNextEntry(new ZipEntry(name));

      try (InputStream in = jar.getInputStream(jarEntry)) {
        addStuff(out, in);
      }
      out.closeEntry();
    }
  }

  private void addStuff(ZipOutputStream out, InputStream in) throws IOException {
    byte[] bytes = new byte[4096];
    int length;
    while ((length = in.read(bytes)) >= 0) {
      out.write(bytes, 0, length);
    }
  }

  private void addDirectory(Map<String, Path> names, ZipOutputStream out, Path path)
      throws IOException {
    Files.walk(path)
        .filter(jarFile -> jarFile.toFile().isFile())
        .forEach(
            jarFile -> {
              Path relativize = path.relativize(jarFile);

              String name = relativize.toString();

              names.put(name, path);
              try {
                out.putNextEntry(new ZipEntry(name));

                try (InputStream in = new FileInputStream(jarFile.toFile())) {
                  addStuff(out, in);
                }
                out.closeEntry();
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            });
  }
}
