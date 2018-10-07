package bt.api;

@FunctionalInterface
public interface EventBus {
  void emit(Object event);
}
