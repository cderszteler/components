package qetz.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.stream.Collectors;

public final class ComponentScanning {
  public static ComponentScanning fromScan(ClassScan scan) {
    Preconditions.checkNotNull(scan, "scan");
    return new ComponentScanning(scan, Lists.newArrayList());
  }

  private final ClassScan scan;
  private Collection<Class<?>> classes;

  private ComponentScanning(
    ClassScan scan,
    Collection<Class<?>> classes
  ) {
    this.scan = scan;
    this.classes = classes;
  }

  public void loadComponents() {
    this.classes = scan
      .findAnnotated(Component.class)
      .collect(Collectors.toList());
  }

  public ClassScan classes() {
    return ClassScanFactory
      .createEmpty()
      .withExplicits(this.classes)
      .scan();
  }
}