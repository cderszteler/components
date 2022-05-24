package qetz.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.stream.Collectors;

public final class ComponentScanning {
  public static ComponentScanning fromScan(ClassScanning scan) {
    Preconditions.checkNotNull(scan, "scan");
    return new ComponentScanning(scan, Lists.newArrayList());
  }

  private final ClassScanning scan;
  private Collection<Class<?>> classes;

  private ComponentScanning(
    ClassScanning scan,
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

  public ClassScanning classes() {
    return ClassScanning.explicit(this.classes);
  }
}