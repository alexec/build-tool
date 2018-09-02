package bt.api;

@FunctionalInterface
public interface TaskFactory<T extends Task> {
  Class<T> get();
}
