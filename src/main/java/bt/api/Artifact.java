package bt.api;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Artifact {
  private static final Pattern PATTERN = Pattern.compile("(.*):(.*):(.*)");
  private final String groupId;
  private final String artifactId;
  private final String version;
  private final String type;

  private Artifact(String groupId, String artifactId, String version, String type) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.type = type;
  }

  /** Creates an artifact. */
  static Artifact valueOf(String text) {
    Matcher matcher = PATTERN.matcher(text);
    if (!matcher.find()) {
      throw new IllegalArgumentException(text);
    }

    return new Artifact(matcher.group(1), matcher.group(2), matcher.group(3), "jar");
  }

  /** The path for the artifact. */
  Path toPath() {
    return Paths.get(
        System.getProperty("user.home"),
        ".m2",
        "repository",
        groupId.replaceAll("\\.", "/"),
        artifactId,
        version,
        artifactId + "-" + version + "." + type);
  }

  @Override
  public String toString() {
    return groupId + ":" + artifactId + ":" + version + ":" + type;
  }
}
