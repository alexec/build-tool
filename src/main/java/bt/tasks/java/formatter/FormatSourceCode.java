package bt.tasks.java.formatter;

import bt.api.EventBus;
import bt.api.Reporter;
import bt.api.Subscribe;
import bt.api.Task;
import bt.api.events.ModuleFound;
import bt.api.events.SourceCodeFormatted;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/** https://github.com/google/google-java-format */
public class FormatSourceCode implements Task {
  private static final Formatter FORMATTER = new Formatter();
  private static final Logger LOGGER = LoggerFactory.getLogger(FormatSourceCode.class);

  @Inject private EventBus eventBus;

  @Subscribe
  public void moduleFound(ModuleFound event) throws Exception {

    Path sourceSet = event.getModule().getSourceSet();

    Reporter<SourceCodeFormatterReport> reporter =
        Reporter.of(
            event.getModule(),
            SourceCodeFormatterReport.class,
            () -> new SourceCodeFormatterReport(Long.MIN_VALUE));

    SourceCodeFormatterReport lastReport = reporter.load();
    Files.find(
            sourceSet,
            Integer.MAX_VALUE,
            (file, attributes) ->
                file.toString().endsWith(".java")
                    && attributes.lastModifiedTime().to(TimeUnit.MILLISECONDS)
                        > lastReport.getEndTime())
        .forEach(
            source -> {
              LOGGER.debug("formatting {}", source);
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

    reporter.save(new SourceCodeFormatterReport(System.currentTimeMillis()));

    eventBus.emit(new SourceCodeFormatted(sourceSet));
  }
}
