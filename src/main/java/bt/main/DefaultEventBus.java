package bt.main;

import bt.api.EventBus;
import bt.api.Subscribe;
import bt.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.System.currentTimeMillis;

public class DefaultEventBus implements Runnable, EventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);
    private final List<Task> tasks = new ArrayList<>();
    private final List<Object> events = new CopyOnWriteArrayList<>();

    @Override
    public void add(Object event) {
        synchronized (events) {
            events.add(event);
        }
    }

    @Override
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
                    .forEach(
                            task -> Arrays.stream(task.getClass().getMethods())
                                    .filter(method -> method.getAnnotation(Subscribe.class) != null)
                                    .filter(method -> method.getParameterTypes()[0].isAssignableFrom(event.getClass()))
                                    .forEach(method -> {
                                        LOGGER.debug("{} {}", method, task);
                                                long start = currentTimeMillis();
                                                try {
                                                    method.invoke(task, event);
                                                    LOGGER.debug("{} took {}ms", task, currentTimeMillis() - start);
                                                } catch (Exception e) {
                                                    LOGGER.error("{}", e);
                                                    events.clear();

                                                    throw new IllegalStateException(e);
                                                }
                                            }
                                    ));
        }
    }
}
