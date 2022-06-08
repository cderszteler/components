package qetz.components;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class ComponentScanningTest {
  private final ComponentScanning scan;

  private ComponentScanningTest() {
    this.scan = ComponentScanning.fromScan(ClassScanFactory.createEmpty().scan());
  }

  @Test
  @BeforeAll
  public void loadComponents() {
    scan.loadComponents();
  }

  @Test
  public void findTestComponent() {
    var components = scan.classes().all().toList();

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