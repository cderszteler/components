package qetz.components;

import com.google.common.base.Preconditions;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Stream;

public final class ClassScanning {
  private final ClassInfoList result;

  private ClassScanning(ClassInfoList result) {
    this.result = result;
  }

  public Stream<Class<?>> findAnnotated(Annotation annotation) {
    Preconditions.checkNotNull(annotation, "annotation");
    return result
      .filter(classInfo -> classInfo
        .hasAnnotation(
          annotation.getClass().getName()
        )
      )
      .loadClasses()
      .stream();
  }

  public Stream<Class<?>> findNonAnnotated(Annotation annotation) {
    Preconditions.checkNotNull(annotation, "annotation");
    return result
      .filter(classInfo -> !classInfo
        .hasAnnotation(
          annotation.getClass().getName()
        )
      )
      .loadClasses()
      .stream();
  }

  public Stream<Class<?>> findAnnotated(Class<? extends Annotation> annotationType) {
    Preconditions.checkNotNull(annotationType, "annotationType");
    return result
      .filter(classInfo -> classInfo.hasAnnotation(annotationType.getName()))
      .loadClasses()
      .stream();
  }

  public Stream<Class<?>> findNonAnnotated(Class<? extends Annotation> annotationType) {
    Preconditions.checkNotNull(annotationType, "annotationType");
    return result
      .filter(classInfo -> !classInfo.hasAnnotation(annotationType.getName()))
      .loadClasses()
      .stream();
  }

  public <E> Stream<Class<E>> findInterfaces(Class<E> superType) {
    Preconditions.checkNotNull(superType, "superType");
    return result
      .filter(classInfo -> classInfo.implementsInterface(superType.getName()))
      .stream()
      .map(this::loadAndCast);
  }

  public <E> Stream<Class<E>> findSubTypes(Class<E> superType) {
    Preconditions.checkNotNull(superType, "superType");
    return result
      .filter(classInfo -> classInfo.extendsSuperclass(superType.getName()))
      .stream()
      .map(this::loadAndCast);
  }

  @SuppressWarnings("unchecked")
  private <E> Class<E> loadAndCast(ClassInfo classInfo) {
    return (Class<E>) classInfo.loadClass();
  }

  public Stream<Class<?>> classes() {
    return result.loadClasses().stream();
  }

  private static final String toStringFormat = "ClassScanning{result=%s}";

  @Override
  public String toString() {
    return String.format(toStringFormat, result.toString());
  }

  public static ClassScanning create() {
    try (var scan = builder().scan()) {
      return new ClassScanning(scan.getAllClasses());
    }
  }

  public static ClassScanning createInPackage(String name) {
    Preconditions.checkNotNull(name, "name");
    try (var scan = builder().acceptPackagesNonRecursive(name).scan()) {
      return new ClassScanning(scan.getAllClasses());
    }
  }

  public static ClassScanning createInPackageRecursive(String name) {
    Preconditions.checkNotNull(name, "name");
    try (var scan = builder().acceptPackages(name).scan()) {
      return new ClassScanning(scan.getAllClasses());
    }
  }

  public static ClassScanning explicit(Collection<Class<?>> classes) {
    Preconditions.checkNotNull(classes, "classes");
    if (classes.size() == 0) {
      return new ClassScanning(ClassInfoList.emptyList());
    }
    try (var scan = builder()
      .acceptClasses(classes.stream()
        .map(Class::getName)
        .toArray(String[]::new)
      )
      .scan()
    ) {
      return new ClassScanning(scan.getAllClasses());
    }
  }

  private static ClassGraph builder() {
    return new ClassGraph()
      .enableClassInfo()
      .enableAnnotationInfo();
  }
}