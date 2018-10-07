package bt.tasks.java.style;

import bt.api.EventBus;
import bt.api.Module;
import bt.api.Reporter;
import bt.api.Subscribe;
import bt.api.Task;
import bt.api.events.ModuleFound;
import bt.api.events.StyleChecked;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CheckStyle implements Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(CheckStyle.class);

  @Inject private EventBus eventBus;

  @Subscribe
  public void consume(ModuleFound event) throws Exception {
    Module module = event.getModule();
    Path sourceSet = module.getSourceSet();
    Reporter<CheckStyleReport> reporter =
        Reporter.of(module, CheckStyleReport.class, () -> new CheckStyleReport(Long.MIN_VALUE));

    CheckStyleReport lastReport = reporter.load();

    Path configurationFile = sourceSet.resolve(Paths.get("java", "checkstyle.xml"));

    if (!Files.exists(configurationFile)) {
      LOGGER.debug("skipping {}, {} does not exist", sourceSet, configurationFile);
      return;
    }

    List<File> files =
        Files.find(
                sourceSet,
                Integer.MAX_VALUE,
                (path, attributes) ->
                    path.toString().endsWith(".java")
                        && attributes.lastModifiedTime().to(TimeUnit.MILLISECONDS)
                            > lastReport.getEndTime())
            .map(Path::toFile)
            .collect(Collectors.toList());

    if (files.isEmpty()) {
      LOGGER.debug("skipping {}, no changes since last check", sourceSet);
    } else {

      LOGGER.debug("checking {}", sourceSet);

      Checker checker = new Checker();
      checker.setBasedir(sourceSet.toAbsolutePath() + "/java");
      checker.setModuleClassLoader(getClass().getClassLoader());
      checker.configure(
          ConfigurationLoader.loadConfiguration(
              configurationFile.toAbsolutePath().toString(), null));
      checker.addListener(
          new AuditListener() {
            @Override
            public void auditStarted(AuditEvent auditEvent) {}

            @Override
            public void auditFinished(AuditEvent auditEvent) {}

            @Override
            public void fileStarted(AuditEvent auditEvent) {
              LOGGER.info("checking {}", auditEvent.getFileName());
            }

            @Override
            public void fileFinished(AuditEvent auditEvent) {}

            @Override
            public void addError(AuditEvent auditEvent) {

              if (auditEvent.getSeverityLevel().equals(SeverityLevel.ERROR)) {
                LOGGER.warn(
                    "{}:{}:{} {}",
                    auditEvent.getFileName(),
                    auditEvent.getLine(),
                    auditEvent.getColumn(),
                    auditEvent.getMessage());
              } else {
                LOGGER.error(
                    "{}:{}:{} {}",
                    auditEvent.getFileName(),
                    auditEvent.getLine(),
                    auditEvent.getColumn(),
                    auditEvent.getMessage());
              }
            }

            @Override
            public void addException(AuditEvent auditEvent, Throwable throwable) {
              LOGGER.debug("{}", auditEvent.getFileName());
            }
          });
      int errorCount = checker.process(files);

      LOGGER.debug("{} error(s)", errorCount);

      if (errorCount > 0) {
        throw new IllegalStateException("expect zero errors, got " + errorCount);
      }
      reporter.save(new CheckStyleReport(System.currentTimeMillis()));
    }

    eventBus.emit(new StyleChecked(sourceSet));
  }
}
