package bt.api;

public interface EventBus {
  void add(Object event);

  void register(Task task);
}
