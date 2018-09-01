package bt.api;

@FunctionalInterface
public interface TaskFactory<T extends Class<? extends Task>> {
  T get();
}
