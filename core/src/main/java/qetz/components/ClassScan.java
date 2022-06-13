package qetz.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public final class ClassScan implements ClassStreamable {
  static ClassScan withClasses(Collection<Class<?>> scan) {
    Preconditions.checkNotNull(scan, "scan");
    return new ClassScan(scan);
  }

  private final Collection<Class<?>> scan;

  private ClassScan(Collection<Class<?>> scan) {
    this.scan = scan;
  }

  @Override
  public ClassStream<?> stream() {
    return ClassStream.withClasses(scan);
  }

  private static final String toStringFormat = "ClassScan{scan=%s}";

  @Override
  public String toString() {
    return String.format(toStringFormat, scan.toString());
  }
}