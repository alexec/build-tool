package bt.tasks.source.compiler;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
class CompilationOpts {
  private List<String> dependencies = new ArrayList<>();
  private BigDecimal source = BigDecimal.valueOf(1.8);
  private BigDecimal target = BigDecimal.valueOf(1.8);
}
