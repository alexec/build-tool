package bt;

import bt.api.Args;
import bt.api.Project;
import bt.api.Task;
import bt.api.events.Start;
import bt.main.DefaultContext;
import bt.main.DefaultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

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

    int threads =
        Integer.parseInt(
            args.getOption("-T", String.valueOf(Runtime.getRuntime().availableProcessors() / 2)));

    LOGGER.info("Threads {}", threads);
    DefaultContext context = new DefaultContext(threads);
    context.register(context);
    context.register(args);
    context.register(project);
    context.register(new DefaultRepository());

    tasks.forEach(context::register);

    context.emit(new Start());

    context.awaitTermination();

    LOGGER.info("Done: {}ms", (System.currentTimeMillis() - startTime));
  }

  private static List<Task> getTasks() {
    List<Task> tasks = new ArrayList<>();
    for (Task task : ServiceLoader.load(Task.class)) {
      tasks.add(task);
    }
    return tasks;
  }
}
