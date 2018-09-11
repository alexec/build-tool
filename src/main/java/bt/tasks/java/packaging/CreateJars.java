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
import java.nio.file.Path;
import java.util.function.Consumer;

public class CreateJars implements Task<Void> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateJars.class);
  @Inject private CompiledCode compiledCode;

  @Override
  public Void run() throws IOException, InterruptedException {

    for (Path path : compiledCode.getCompiledCode().keySet()) {

      ProcessBuilder command =
          new ProcessBuilder()
              .directory(path.getParent().toFile())
              .command(
                  "jar",
                  "cf",
                  path.getFileName() + ".jar",
                  "-C",
                  path.getFileName().toString(),
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

    return null;
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
