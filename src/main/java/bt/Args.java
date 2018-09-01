package bt;

import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Value
public class Args {

  private final Map<String, Object> options = new HashMap<>();
  private final List<String> commands = new ArrayList<>();

  private Args(String[] args) {
    String optionName = null;
    for (String arg : args) {
      if (arg.startsWith("--")) {
        optionName = arg.substring(2);
      } else if (arg.startsWith("-")) {
        optionName = arg.substring(1);
      } else if (optionName != null) {
        options.put(optionName, arg);
        optionName = null;
      } else {
        commands.add(arg);
      }
    }
  }

  public static Args parse(String[] args) {
    return new Args(args);
  }
}
