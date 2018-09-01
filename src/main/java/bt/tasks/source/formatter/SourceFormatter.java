package bt.tasks.source.formatter;

import bt.api.Task;
import bt.tasks.source.finder.SourceSets;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** https://github.com/google/google-java-format */
public class SourceFormatter implements Task {
  private static final Formatter FORMATTER = new Formatter();
  private static final Logger LOGGER = LoggerFactory.getLogger(SourceFormatter.class);
  private final SourceSets sourceSets;

  public SourceFormatter(SourceSets sourceSets) {
    this.sourceSets = sourceSets;
  }

  @Override
  public SourceCodeFormatterReport run() throws IOException {

    for (Path sourceSet : sourceSets.getSourceSets()) {
      LOGGER.info("formatting {}", sourceSet);
      Files.find(
              sourceSet,
              Integer.MAX_VALUE,
              (file, basicFileAttributes) -> file.toString().endsWith(".java"))
          .forEach(
              source -> {
                try {
                  String content = new String(Files.readAllBytes(source));
                  String formattedContent = FORMATTER.formatSource(content);
                  if (!content.equals(formattedContent)) {
                    LOGGER.info("formatted {}", source);
                    Files.write(source, formattedContent.getBytes());
                  }
                } catch (IOException e) {
                  throw new UncheckedIOException(e);
                } catch (FormatterException e) {
                  throw new IllegalStateException(e);
                }
              });
    }

    return new SourceCodeFormatterReport();
  }
}
