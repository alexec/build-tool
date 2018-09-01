package bt;

import bt.api.Task;
import bt.api.TaskFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  /** Runs tool. */
  public static void main(String[] args) {
    LOGGER.info("starting");
    long startTime = System.currentTimeMillis();
    try {

      List<Class<Task>> taskTypes = getTasks();
      int totalTasks = taskTypes.size();
      LOGGER.info("{} task(s) to run", totalTasks);

      Map<Class, Object> context = new HashMap<>();

      int taskNo = 1;
      while (!taskTypes.isEmpty()) {

        Iterator<Class<Task>> it = taskTypes.iterator();
        while (it.hasNext()) {
          Class<Task> taskType = it.next();

          Constructor<?> constructor = taskType.getConstructors()[0];

          Class<?>[] parameterTypes = constructor.getParameterTypes();
          Object[] parameters =
              Arrays.stream(parameterTypes)
                  .filter(context::containsKey)
                  .map(context::get)
                  .toArray();

          if (parameters.length == parameterTypes.length) {
            try {
              Task task = (Task) constructor.newInstance(parameters);
              LOGGER.info("Task [{}/{}]: {}", taskNo, totalTasks, taskType.getSimpleName());
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

  @SuppressWarnings("unchecked")
  private static List<Class<Task>> getTasks() {
    List<Class<Task>> taskTypes = new ArrayList<>();
    for (TaskFactory taskFactory : ServiceLoader.load(TaskFactory.class)) {
      taskTypes.add(taskFactory.get());
    }
    return taskTypes;
  }
}
