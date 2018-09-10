package bt.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringsTest {

  @Test
  public void basicTest() {
    assertEquals("basic-test", Strings.toSnakeCase("BasicTest"));
  }
}
