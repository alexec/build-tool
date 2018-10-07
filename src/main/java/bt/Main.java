package bt;

import bt.api.Args;
import bt.api.Project;
import bt.api.Repository;
import bt.api.Task;
import bt.api.events.Start;
import bt.main.DefaultEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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
    LOGGER.debug("{} task(s)", totalTasks);

    List<Object> context = new ArrayList<>();
    DefaultEventBus eventBus = new DefaultEventBus();
    context.add(args);
    context.add(project);
    context.add(eventBus);
    context.add(new Repository());

    tasks.forEach(task -> inject(context, task));
    tasks.forEach(eventBus::register);

    Thread thread = new Thread(eventBus);

    eventBus.add(new Start());

    thread.run();
    thread.join();

    LOGGER.info("Done: {}ms", (System.currentTimeMillis() - startTime));
  }

  private static void inject(List<Object> context, Task task) {
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

    fields.forEach(
        field ->
            context
                .stream()
                .filter(bean -> field.getType().isAssignableFrom(bean.getClass()))
                .forEach(
                    bean -> {
                      field.setAccessible(true);
                      try {
                        field.set(task, bean);
                      } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                      }
                    }));
  }

  private static List<Task> getTasks() {
    List<Task> tasks = new ArrayList<>();
    for (Task task : ServiceLoader.load(Task.class)) {
      tasks.add(task);
    }
    return tasks;
  }
}
