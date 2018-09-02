package bt.tasks.source.formatter;

import bt.api.Task;
import bt.tasks.source.finder.SourceSets;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** https://github.com/google/google-java-format */
public class FormatSourceCode implements Task {
  private static final Formatter FORMATTER = new Formatter();
  private static final Logger LOGGER = LoggerFactory.getLogger(FormatSourceCode.class);
  @Inject private SourceSets sourceSets;

  @Override
  public SourceCodeFormatterReport run() throws IOException {

    for (Path sourceSet : sourceSets.getSourceSets()) {
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

  @Override
  public String toString() {
    return "FormatSourceCode{" + "sourceSets=" + sourceSets + '}';
  }
}