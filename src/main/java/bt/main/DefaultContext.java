package bt.main;

import bt.api.Context;
import bt.api.EventBus;
import bt.api.Subscribe;
import bt.api.events.Finished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;

public class DefaultContext implements Context, EventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContext.class);
    private final ThreadPoolExecutor executor;
    private final List<Object> beans = new ArrayList<>();

    public DefaultContext(int maximumPoolSize) {
        executor =
                new ThreadPoolExecutor(
                        maximumPoolSize, maximumPoolSize, 10L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    }

    public void register(Object bean) {
        LOGGER.debug("registered {}", bean);
        inject(bean);
        beans.add(bean);
    }

    @Override
    public void inject(Object target) {
        Arrays.stream(target.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Inject.class) != null)
                .filter(
                        field -> {
                            field.setAccessible(true);
                            try {
                                return field.get(target) == null;
                            } catch (IllegalAccessException e) {
                                throw new IllegalStateException(e);
                            }
                        })
                .forEach(
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
        LOGGER.info("{}", event);
        if (executor.isShutdown()) {
            return;
        }
        new ArrayList<>(beans)
                .forEach(
                        task ->
                                Arrays.stream(task.getClass().getMethods())
                                        .filter(method -> method.getAnnotation(Subscribe.class) != null)
                                        .filter(
                                                method -> method.getParameterTypes()[0].isAssignableFrom(event.getClass()))
                                        .forEach(
                                                method -> {
                                                    executor.submit(
                                                            () -> {
                                                                LOGGER.debug("{} {}", method, task);
                                                                long start = currentTimeMillis();
                                                                try {
                                                                    method.invoke(task, event);
                                                                    LOGGER.debug("{} took {}ms", task, currentTimeMillis() - start);
                                                                } catch (Exception e) {
                                                                    LOGGER.error("failed to dispatch " + event + " to " + task, e);

                                                                    throw new IllegalStateException(e);
                                                                }
                                                            });
                                                }));
    }

    public void awaitTermination() throws InterruptedException {
        while (executor.getTaskCount() != executor.getCompletedTaskCount()) {
            Thread.sleep(500L);
        }
        emit(new Finished());
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
