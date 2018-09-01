package bt.api;

public interface Task<O> {

  O run() throws Exception;
}
