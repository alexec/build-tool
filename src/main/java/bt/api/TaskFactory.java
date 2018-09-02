package bt.api;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface TaskFactory<T extends Callable> {
  Class<T> get();
}
