package bt.tasks.java.compiler;

import java.math.BigDecimal;

public class CompilationOpts {
  private BigDecimal source = BigDecimal.valueOf(1.8);
  private BigDecimal target = BigDecimal.valueOf(1.8);

  public BigDecimal getSource() {
    return source;
  }

  public void setSource(BigDecimal source) {
    this.source = source;
  }

  public BigDecimal getTarget() {
    return target;
  }

  public void setTarget(BigDecimal target) {
    this.target = target;
  }
}
