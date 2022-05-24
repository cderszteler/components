package qetz.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Stream;

public final class ClassScanning {
  private final Collection<Class<?>> result;

  private ClassScanning(Collection<Class<?>> result) {
    this.result = result;
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
    return result.stream()
      .filter(checked -> checked.isAnnotationPresent(annotationType));
  }

  public Stream<Class<?>> findNonAnnotated(Class<? extends Annotation> annotationType) {
    Preconditions.checkNotNull(annotationType, "annotationType");
    return result.stream()
      .filter(checked -> !checked.isAnnotationPresent(annotationType));
  }

  @SuppressWarnings("unchecked")
  public <E> Stream<Class<E>> findSuperType(Class<E> superType) {
    Preconditions.checkNotNull(superType, "superType");
    return result.stream()
      .filter(checked -> superType.isAssignableFrom(superType))
      .map(checked -> (Class<E>) checked);
  }

  public Stream<Class<?>> classes() {
    return result.stream();
  }

  private static final String toStringFormat = "ClassScanning{result=%s}";

  @Override
  public String toString() {
    return String.format(toStringFormat, result.toString());
  }

  public static ClassScanning create() {
    try (var scan = builder().scan()) {
      return new ClassScanning(scan.getAllClasses().loadClasses());
    }
  }

  public static ClassScanning createInPackage(String name) {
    Preconditions.checkNotNull(name, "name");
    try (var scan = builder().acceptPackagesNonRecursive(name).scan()) {
      return new ClassScanning(scan.getAllClasses().loadClasses());
    }
  }

  public static ClassScanning createInPackageRecursive(String name) {
    Preconditions.checkNotNull(name, "name");
    try (var scan = builder().acceptPackages(name).scan()) {
      return new ClassScanning(scan.getAllClasses().loadClasses());
    }
  }

  public static ClassScanning explicit(Collection<Class<?>> classes) {
    Preconditions.checkNotNull(classes, "classes");
    if (classes.size() == 0) {
      return new ClassScanning(Lists.newArrayList());
    }
    try (var scan = builder()
      .acceptClasses(classes.stream()
        .map(Class::getName)
        .toArray(String[]::new)
      )
      .scan()
    ) {
      return new ClassScanning(scan.getAllClasses().loadClasses());
    }
  }

  private static ClassGraph builder() {
    return new ClassGraph()
      .enableClassInfo()
      .enableAnnotationInfo();
  }
}