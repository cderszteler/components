package qetz.components;

import com.google.common.base.Preconditions;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Stream;

public final class ClassScan {
  static ClassScan withClasses(Collection<Class<?>> scan) {
    Preconditions.checkNotNull(scan, "scan");
    return new ClassScan(scan);
  }

  private final Collection<Class<?>> scan;

  private ClassScan(Collection<Class<?>> scan) {
    this.scan = scan;
  }

  public Stream<Class<?>> findAnnotated(Annotation annotation) {
    Preconditions.checkNotNull(annotation, "annotation");
    return findAnnotated(annotation.getClass());
  }

  public Stream<Class<?>> findNonAnnotated(Annotation annotation) {
    Preconditions.checkNotNull(annotation, "annotation");
    return findNonAnnotated(annotation.getClass());
  }

  public Stream<Class<?>> findAnnotated(Class<? extends Annotation> annotationType) {
    Preconditions.checkNotNull(annotationType, "annotationType");
    return scan.stream()
      .filter(checked -> checked.isAnnotationPresent(annotationType));
  }

  public Stream<Class<?>> findNonAnnotated(Class<? extends Annotation> annotationType) {
    Preconditions.checkNotNull(annotationType, "annotationType");
    return scan.stream()
      .filter(checked -> !checked.isAnnotationPresent(annotationType));
  }

  @SuppressWarnings("unchecked")
  public <E> Stream<Class<E>> findSuperType(Class<E> superType) {
    Preconditions.checkNotNull(superType, "superType");
    return scan.stream()
      .filter(superType::isAssignableFrom)
      .map(checked -> (Class<E>) checked);
  }

  public Stream<Class<?>> all() {
    return scan.stream();
  }

  private static final String toStringFormat = "ClassScan{scan=%s}";

  @Override
  public String toString() {
    return String.format(toStringFormat, scan.toString());
  }
}