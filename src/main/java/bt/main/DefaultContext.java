package bt.main;


import bt.api.Context;
import bt.api.EventBus;
import bt.api.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

public class DefaultContext implements Context,EventBus, Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContext.class);
    private final List<Object> events = new CopyOnWriteArrayList<>();

    private final List<Object> beans = new ArrayList<>();

    public void register(Object bean) {
        LOGGER.debug("registered {}", bean);
        inject(bean);
        beans.add(bean);
    }

    @Override
    public void inject(Object target) {

        List<Field> fields =
                Arrays.stream(target.getClass().getDeclaredFields())
                        .filter(f -> f.getAnnotation(Inject.class) != null)
                        .filter(
                                f -> {
                                    f.setAccessible(true);
                                    try {
                                        return f.get(target) == null;
                                    } catch (IllegalAccessException e) {
                                        throw new IllegalStateException(e);
                                    }
                                })
                        .collect(Collectors.toList());

        fields.forEach(
                field ->
                        beans
                                .stream()
                                .filter(bean -> field.getType().isAssignableFrom(bean.getClass()))
                                .forEach(
                                        bean -> {
                                            field.setAccessible(true);
                                            try {
                                                field.set(target, bean);
                                            } catch (IllegalAccessException e) {
                                                throw new IllegalStateException(e);
                                            }
                                        }));
    }

    @Override
    public void emit(Object event) {
        synchronized (events) {
            events.add(event);
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public synchronized void run() {
        while (!events.isEmpty()) {
            Object event = events.remove(0);
            LOGGER.info("{}", event);
            new ArrayList<>(beans)
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