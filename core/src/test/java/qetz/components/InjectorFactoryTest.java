package qetz.components;

import com.google.inject.Key;
import com.google.inject.name.Names;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class InjectorFactoryTest {
  private final InjectorFactory factory;

  private InjectorFactoryTest() {
    var components = ComponentScan.fromScan(ClassScanFactory
      .createEmpty()
      .withRecursivePackageByClass(getClass())
      .scan()
    );
    this.factory = InjectorFactory.create(components);
  }

  @Test
  public void findBoundTest() {
    var injector = factory.createInjector();

    Assertions.assertDoesNotThrow(
      () -> injector
        .getBinding(Key.get(String.class).withAnnotation(Names.named("test"))
      ),
      "bound string must be found"
    );
  }
}