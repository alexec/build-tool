package bt.tasks.java.compiler;

import lombok.Data;

import java.math.BigDecimal;

@Data
class CompilationOpts {

  private BigDecimal source = BigDecimal.valueOf(1.8);
  private BigDecimal target = BigDecimal.valueOf(1.8);
}
