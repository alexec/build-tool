package bt.tasks.source.compiler;

import java.io.OutputStream;
import java.util.function.Consumer;

class LogOutputStream extends OutputStream {
  private final Consumer<String> logger;
  private String mem = "";

  LogOutputStream(Consumer<String> logger) {
    this.logger = logger;
  }

  @Override
  public void write(int b) {
    byte[] bytes = new byte[1];
    bytes[0] = (byte) (b & 0xff);
    mem = mem + new String(bytes);

    if (mem.endsWith("\n")) {
      mem = mem.substring(0, mem.length() - 1);
      flush();
    }
  }

  @Override
  public void flush() {
    if (!mem.isEmpty()) {
      logger.accept(mem);
    }
    mem = "";
  }
}
