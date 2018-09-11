package bt.tasks.java.formatter;

@SuppressWarnings({"WeakerAccess", "unused"})
public class SourceCodeFormatterReport {
  private long endTime;

  public SourceCodeFormatterReport(long endTime) {
    this.endTime = endTime;
  }

  public SourceCodeFormatterReport() {}

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public long getEndTime() {
    return endTime;
  }
}
