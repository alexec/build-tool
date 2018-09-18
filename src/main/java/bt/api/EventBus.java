package bt.api;

import bt.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.System.currentTimeMillis;

public class EventBus implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
  private final List<Task> tasks = new ArrayList<>();
  private final List<Object> events = new CopyOnWriteArrayList<>();

  public void add(Object event) {
    synchronized (events) {
      events.add(event);
    }
  }

  public void register(Task task) {
    tasks.add(task);
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized void run() {
    while (!events.isEmpty()) {
      Object event = events.remove(0);
      LOGGER.info("{}", event);
      tasks
          .stream()
          .filter(task -> task.eventType().equals(event.getClass()))
          .forEach(
              task -> {
                long start = currentTimeMillis();
                try {
                  task.consume(event);
                  LOGGER.debug("{} took {}ms", task, currentTimeMillis() - start);
                } catch (Exception e) {
                  LOGGER.error("{}", e);
                  events.clear();

                  throw new IllegalStateException(e);
                }
              });
    }
  }
}
