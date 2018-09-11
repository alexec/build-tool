package bt.tasks.java.style;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CheckStyleReport {
  private long endTime;

  public CheckStyleReport(long endTime) {
    this.endTime = endTime;
  }

  public CheckStyleReport() {}

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public long getEndTime() {
    return endTime;
  }
}
