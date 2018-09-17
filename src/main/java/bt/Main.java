package bt;

import bt.api.Args;
import bt.api.Project;
import bt.api.Repository;
import bt.api.Task;
import bt.util.Strings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /** Runs tool. */
  public static void main(String[] strings) throws Exception {
    Args args = Args.parse(strings);

    LOGGER.debug("starting");
    LOGGER.debug("{}", args);
    long startTime = System.currentTimeMillis();

    Project project = OBJECT_MAPPER.readValue(new File("project.json"), Project.class);

    LOGGER.info("Project: {}", project.getArtifact());

    List<Task> tasks = getTasks();
    int totalTasks = tasks.size();
    LOGGER.debug("{} task(s) to run", totalTasks);

    List<Object> context = new ArrayList<>();
    context.add(args);
    context.add(project);
    context.add(new Repository());

    int taskNo = 1;
    while (!tasks.isEmpty()) {

      Iterator<Task> it = tasks.iterator();
      while (it.hasNext()) {
        Task task = it.next();

        if (inject(context, task)) {
          try {
            LOGGER.info(
                "Task {}/{}: {}",
                taskNo,
                totalTasks,
                Strings.toSnakeCase(task.getClass().getSimpleName()));
            long taskStartTime = System.currentTimeMillis();
            Object output = task.run();
            LOGGER.debug("took {}ms", System.currentTimeMillis() - taskStartTime);
            if (output != null) {
              context.add(output);
            }
            taskNo++;
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
          it.remove();
        }
      }
    }

    LOGGER.info("Done: {}ms", (System.currentTimeMillis() - startTime));
  }

  private static boolean inject(List<Object> context, Task task) {
    List<Field> fields =
        Arrays.stream(task.getClass().getDeclaredFields())
            .filter(f -> f.getAnnotation(Inject.class) != null)
            .filter(
                f -> {
                  f.setAccessible(true);
                  try {
                    return f.get(task) == null;
                  } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                  }
                })
            .collect(Collectors.toList());

    long count =
        fields
            .stream()
            .filter(
                field ->
                    context
                        .stream()
                        .filter(bean -> field.getType().isAssignableFrom(bean.getClass()))
                        .peek(
                            bean -> {
                              field.setAccessible(true);
                              try {
                                field.set(task, bean);
                              } catch (IllegalAccessException e) {
                                throw new IllegalStateException(e);
                              }
                            })
                        .findFirst()
                        .isPresent())
            .count();

    return count == fields.size();
  }

  private static List<Task> getTasks() {
    List<Task> tasks = new ArrayList<>();
    for (Task task : ServiceLoader.load(Task.class)) {
      tasks.add(task);
    }
    return tasks;
  }
}
