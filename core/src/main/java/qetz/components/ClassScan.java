package qetz.components;

import com.google.common.base.Preconditions;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Stream;

public final class ClassScan extends ClassStream implements ClassStreamable {
  static ClassScan withClasses(Collection<Class<?>> scan) {
    Preconditions.checkNotNull(scan, "scan");
    return new ClassScan(scan);
  }

  private final Collection<Class<?>> scan;

  private ClassScan(Collection<Class<?>> scan) {
    super(scan);
    this.scan = scan;
  }

  @Override
  public ClassStream stream() {
    return this;
  }

  private static final String toStringFormat = "ClassScan{scan=%s}";

  @Override
  public String toString() {
    return String.format(toStringFormat, scan.toString());
  }
}