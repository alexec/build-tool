package bt.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Artifact {
  private static final Pattern PATTERN =
      Pattern.compile(
          "(?<groupId>[^:]*):(?<artifactId>[^:]*)(:(?<type>[^:]*))?:(?<version>[^:]*)(:(?<classifier>[^:]*))?");
  private static final String JAR = "jar";
  private final String groupId;
  private final String artifactId;
  private final String type;
  private final String version;
  private final String classifier;

  private Artifact(
      String groupId, String artifactId, String type, String version, String classifier) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.type = type;
    this.version = version;
    this.classifier = classifier;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  String getType() {
    return type;
  }

  public String getVersion() {
    return version;
  }

  String getClassifier() {
    return classifier;
  }

  /** Creates an artifact. */
  public static Artifact valueOf(String text) {
    Matcher matcher = PATTERN.matcher(text);
    if (!matcher.find()) {
      throw new IllegalArgumentException(text);
    }

    return new Artifact(
        matcher.group("groupId"),
        matcher.group("artifactId"),
        matcher.group("type") != null ? matcher.group("type") : JAR,
        matcher.group("version"),
        matcher.group("classifier"));
  }

  @Override
  public String toString() {
    return groupId
        + ":"
        + artifactId
        + (!type.equals(JAR) ? ":" + type : "")
        + ":"
        + version
        + (classifier != null ? ":" + classifier : "");
  }
}
