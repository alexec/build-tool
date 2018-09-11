package bt.tasks.java.formatter;

import bt.api.Reporter;
import bt.api.Task;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/** https://github.com/google/google-java-format */
public class FormatSourceCode implements Task<Void> {
  private static final Formatter FORMATTER = new Formatter();
  private static final Logger LOGGER = LoggerFactory.getLogger(FormatSourceCode.class);
  @Inject private Set<Path> sourceSets;
  private final Reporter<SourceCodeFormatterReport> reporter =
      Reporter.of(
          SourceCodeFormatterReport.class, () -> new SourceCodeFormatterReport(Long.MIN_VALUE));

  @Override
  public Void run() throws IOException {

    SourceCodeFormatterReport lastReport = reporter.load();

    for (Path sourceSet : sourceSets) {
      Files.find(
              sourceSet,
              Integer.MAX_VALUE,
              (file, attributes) ->
                  file.toString().endsWith(".java")
                      && attributes.lastModifiedTime().to(TimeUnit.MILLISECONDS)
                          > lastReport.getEndTime())
          .forEach(
              source -> {
                LOGGER.info("formatting {}", source);
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

    reporter.save(new SourceCodeFormatterReport(System.currentTimeMillis()));

    return null;
  }
}
