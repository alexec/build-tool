package bt.tasks.java.compiler;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Builder
class Artifact {
  private static final Pattern PATTERN = Pattern.compile("(.*):(.*):(.*)");
  @NonNull private final String groupId;
  @NonNull private final String artifactId;
  @NonNull private final String version;
  @NonNull private final String type;

  static Artifact valueOf(String text) {
    Matcher matcher = PATTERN.matcher(text);
    if (!matcher.find()) {
      throw new IllegalArgumentException(text);
    }

    return Artifact.builder()
        .groupId(matcher.group(1))
        .artifactId(matcher.group(2))
        .version(matcher.group(3))
        .type("jar")
        .build();
  }

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

  Artifact pom() {
    return Artifact.builder()
        .groupId(groupId)
        .artifactId(artifactId)
        .version(version)
        .type("pom")
        .build();
  }
}
