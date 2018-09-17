package bt.tasks.java.packaging;

import bt.api.Task;
import bt.tasks.java.compiler.CompiledCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CreateJars implements Task<Jars> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateJars.class);
  @Inject private CompiledCode compiledCode;

  @Override
  public Jars run() throws IOException, InterruptedException {
    List<Path> jars = new ArrayList<>();
    for (Path compiledCode : compiledCode.getCompiledCode().keySet()) {

      Path jar = compiledCode.getParent().resolve(Paths.get(compiledCode.getFileName() + ".jar"));

      Optional<Long> lastModified =
          Files.walk(compiledCode).map(file -> file.toFile().lastModified()).max(Long::compareTo);

      jars.add(jar);

      if (Files.exists(jar)
          && lastModified.isPresent()
          && lastModified.get() <= jar.toFile().lastModified()) {
        LOGGER.info("skipping {}, {} is unchanged", jar, compiledCode);
        continue;
      }

      ProcessBuilder command =
          new ProcessBuilder()
              .directory(jar.getParent().toFile())
              .command(
                  "jar",
                  "cf",
                  jar.getFileName().toString(),
                  "-C",
                  compiledCode.getFileName().toString(),
                  ".");

      LOGGER.info("running {},", command.command());
      Process process = command.start();

      log(process.getInputStream(), LOGGER::info);
      log(process.getErrorStream(), LOGGER::error);

      int exitValue = process.waitFor();

      LOGGER.info("exit {}", exitValue);

      if (exitValue != 0) {
        throw new IllegalStateException();
      }
    }

    return new Jars(jars);
  }

  private void log(InputStream inputStream, Consumer<String> info) {
    new Thread(
            () -> {
              try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
                in.lines().forEach(info);
              } catch (IOException e) {
                LOGGER.error("error {}", e);
              }
            })
        .start();
  }
}
