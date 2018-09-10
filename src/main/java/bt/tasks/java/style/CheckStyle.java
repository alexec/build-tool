package bt.tasks.java.style;

import bt.api.Task;
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
import java.util.Set;
import java.util.stream.Collectors;

public class CheckStyle implements Task<CheckStyleReport> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CheckStyle.class);

  @Inject private Set<Path> sourceSets;

  @Override
  public CheckStyleReport run() throws Exception {

    for (Path sourceSet : sourceSets) {
      Path configurationFile = sourceSet.resolve(Paths.get("java", "checkstyle.xml"));

      if (!Files.exists(configurationFile)) {
        LOGGER.info("skipping {} as {} does not exist", sourceSet, configurationFile);
        continue;
      }
      LOGGER.info("checking {}", sourceSet);

      List<File> files =
          Files.find(
                  sourceSet,
                  Integer.MAX_VALUE,
                  (path, basicFileAttributes) -> path.toString().endsWith(".java"))
              .map(Path::toFile)
              .collect(Collectors.toList());
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
            public void fileStarted(AuditEvent auditEvent) {}

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
              LOGGER.info("{}", auditEvent.getFileName());
            }
          });
      int errorCount = checker.process(files);

      LOGGER.info("{} error(s)", errorCount);

      if (errorCount > 0) {
        throw new IllegalStateException("expect zero errors, got " + errorCount);
      }
    }

    return new CheckStyleReport();
  }
}
