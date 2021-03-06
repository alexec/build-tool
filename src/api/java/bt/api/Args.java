package bt.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Args {

  private final Map<String, String> options = new HashMap<>();
  private final List<String> commands = new ArrayList<>();

  private Args(String[] args) {
    String optionName = null;
    for (String arg : args) {
      if (arg.startsWith("-")) {
        optionName = arg;
      } else if (optionName != null) {
        options.put(optionName, arg);
        optionName = null;
      } else {
        commands.add(arg);
      }
    }
  }

  public List<String> getCommands() {
    return commands;
  }

  public static Args parse(String[] args) {
    return new Args(args);
  }

  public String getOption(String key, String defaultValue) {
    return options.getOrDefault(key, defaultValue);
  }

  @Override
  public String toString() {
    return options + " " + commands;
  }
}
