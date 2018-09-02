package bt;

import bt.api.Args;
import bt.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  /** Runs tool. */
  public static void main(String[] strings) {
    LOGGER.info("starting");
    Args args = Args.parse(strings);
    long startTime = System.currentTimeMillis();
    try {

      List<Task> tasks = getTasks();
      int totalTasks = tasks.size();
      LOGGER.info("{} task(s) to run", totalTasks);

      Map<Class, Object> context = new HashMap<>();
      context.put(Args.class, args);

      int taskNo = 1;
      while (!tasks.isEmpty()) {

        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
          Task task = it.next();

          List<Field> fields =
              Arrays.stream(task.getClass().getDeclaredFields())
                  .filter(f -> f.getAnnotation(Inject.class) != null)
                  .collect(Collectors.toList());
          long count = fields.stream().map(Field::getType).filter(context::containsKey).count();

          if (count == fields.size()) {
            try {
              fields.forEach(
                  f -> {
                    f.setAccessible(true);
                    try {
                      f.set(task, context.get(f.getType()));
                    } catch (IllegalAccessException e) {
                      throw new IllegalStateException(e);
                    }
                  });

              LOGGER.info("Task [{}/{}]: {}", taskNo, totalTasks, task);
              Object output = task.run();
              if (output != null) {
                context.put(output.getClass(), output);
              }
              taskNo++;
            } catch (Exception e) {
              throw new IllegalStateException(e);
            }
            it.remove();
          }
        }
      }
    } finally {
      LOGGER.info("done - " + (System.currentTimeMillis() - startTime) + "ms");
    }
  }

  private static List<Task> getTasks() {
    List<Task> tasks = new ArrayList<>();
    for (Task task : ServiceLoader.load(Task.class)) {
      tasks.add(task);
    }
    return tasks;
  }
}
