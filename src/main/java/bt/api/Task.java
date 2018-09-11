package bt.api;

public interface Task<O> {

  /**
   * Run the task.
   *
   * @return Output to add to the context. Maybe null.
   */
  O run() throws Exception;
}
