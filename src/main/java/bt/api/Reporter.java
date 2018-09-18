package bt.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class Reporter<R> {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final Class<R> reportType;
  private final Path path;
  private final Supplier<R> defaultValueSupplier;

  private Reporter(Path sourceSet, Class<R> reportType, Supplier<R> defaultValueSupplier) {
    this.reportType = reportType;
    path =
        sourceSet.resolve(
            Paths.get("..", "..", "target", "reports", reportType.getName() + ".json"));
    this.defaultValueSupplier = defaultValueSupplier;
  }

  public static <R> Reporter<R> of(
      Path sourceSet, Class<R> reportType, Supplier<R> defaultValueSupplier) {
    return new Reporter<>(sourceSet, reportType, defaultValueSupplier);
  }

  /**
   * Read the report.
   *
   * @return The report. Can be null if the default supplier return null.
   */
  public R load() throws IOException {
    if (Files.exists(path)) {
      return OBJECT_MAPPER.readValue(path.toFile(), reportType);
    } else {
      return defaultValueSupplier.get();
    }
  }

  /** Save the report. */
  public void save(R report) throws IOException {
    if (!Files.isDirectory(path.getParent())) {
      Files.createDirectory(path.getParent());
    }
    OBJECT_MAPPER.writeValue(path.toFile(), report);
  }
}
