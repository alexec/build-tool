package bt.api;

public interface Context {
  void inject(Object target);

  void register(Object task);
}
