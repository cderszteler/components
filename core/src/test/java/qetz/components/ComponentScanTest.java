package qetz.components;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class ComponentScanTest {
  private final ComponentScan scan;

  private ComponentScanTest() {
    this.scan = ComponentScan.fromScan(ClassScanFactory
      .createEmpty()
      .withRecursivePackageByClass(getClass())
      .scan()
    );
  }

  @Test
  public void findTestComponent() {
    var components = scan.stream().all();

    Assertions.assertTrue(
      components.size() > 1,
      "size must be equal or larger than one"
    );
    Assertions.assertTrue(
      components.contains(TestComponent.class),
      "searched component must be included"
    );
  }
}