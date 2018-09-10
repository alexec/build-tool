package bt.tasks.java.formatter;

import bt.api.Task;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/** https://github.com/google/google-java-format */
public class FormatSourceCode implements Task {
  private static final Formatter FORMATTER = new Formatter();
  @Inject private Set<Path> sourceSets;

  @Override
  public SourceCodeFormatterReport run() throws IOException {

    for (Path sourceSet : sourceSets) {
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
}
