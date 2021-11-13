package qetz.components;

import com.google.common.base.Preconditions;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassScanning {
  private final ClassInfoList result;

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
    return new ClassScanning(new ClassGraph()
      .enableClassInfo()
      .enableAnnotationInfo()
      .scan()
      .getAllClasses()
    );
  }

  public static ClassScanning createInPackage(String name) {
    Preconditions.checkNotNull(name, "name");
    return new ClassScanning(new ClassGraph()
      .enableClassInfo()
      .enableAnnotationInfo()
      .acceptPackagesNonRecursive(name)
      .scan()
      .getAllClasses()
    );
  }

  public static ClassScanning createInPackageRecursive(String name) {
    Preconditions.checkNotNull(name, "name");
    return new ClassScanning(new ClassGraph()
      .enableClassInfo()
      .enableAnnotationInfo()
      .acceptPackages(name)
      .scan()
      .getAllClasses()
    );

  }

  public static ClassScanning explicit(Collection<Class<?>> classes) {
    Preconditions.checkNotNull(classes, "classes");
    if (classes.size() == 0) {
      return new ClassScanning(ClassInfoList.emptyList());
    }
    return new ClassScanning(new ClassGraph()
      .enableClassInfo()
      .enableAnnotationInfo()
      .acceptClasses(classes.stream()
        .map(Class::getName)
        .toArray(String[]::new))
      .scan()
      .getAllClasses());
  }
}