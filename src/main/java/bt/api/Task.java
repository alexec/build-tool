package bt.api;

public interface Task<E> {

  Class<E> eventType();

  /** Run the task. */
  void consume(E event) throws Exception;
}
