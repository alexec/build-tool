package bt.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** String utils. */
public class Strings {
  /**
   * Converts text to snake case.
   *
   * @param text some text.
   * @return text in snake case
   */
  public static String toSnakeCase(String text) {
    Pattern p = Pattern.compile("[A-Z][^A-Z]*");
    Matcher m = p.matcher(text);
    List<String> parts = new ArrayList<>();
    while (m.find()) {
      parts.add(m.group());
    }
    return parts.stream().map(String::toLowerCase).collect(Collectors.joining("-"));
  }
}
