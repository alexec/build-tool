package bt.tasks.java.packaging;

import bt.api.EventBus;
import bt.api.Task;
import bt.api.events.CodeCompiled;
import bt.api.events.JarCreated;
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
import java.util.Optional;
import java.util.function.Consumer;

public class CreateJar implements Task<CodeCompiled> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateJar.class);
  @Inject private EventBus eventBus;

  @Override
  public Class<CodeCompiled> eventType() {
    return CodeCompiled.class;
  }

  @Override
  public void consume(CodeCompiled event) throws Exception {
    Path compiledCode = event.getModule().getCompiledCode();

    Path jar =
        compiledCode
            .getParent()
            .resolve(Paths.get(event.getModule().getSourceSet().getFileName() + ".jar"));

    if (!canSkip(compiledCode, jar)) {

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

      LOGGER.debug("running {},", command.command());
      Process process = command.start();

      log(process.getInputStream(), LOGGER::info);
      log(process.getErrorStream(), LOGGER::error);

      int exitValue = process.waitFor();

      LOGGER.debug("exit {}", exitValue);

      if (exitValue != 0) {
        throw new IllegalStateException();
      }
    }
    eventBus.add(new JarCreated(jar));
  }

  private boolean canSkip(Path compiledCode, Path jar) throws IOException {
    Optional<Long> lastModified =
        Files.walk(compiledCode).map(file -> file.toFile().lastModified()).max(Long::compareTo);

    if (Files.exists(jar)
        && lastModified.isPresent()
        && lastModified.get() <= jar.toFile().lastModified()) {
      LOGGER.debug("skipping {}, {} is unchanged", jar, compiledCode);
      return true;
    }
    return false;
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
