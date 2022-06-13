package qetz.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ClassStream<Type extends Class<?>> {
  public static <Type extends Class<?>> ClassStream<Type> empty() {
    return withClasses(Lists.newArrayList());
  }

  public static <Type extends Class<?>> ClassStream<Type> withClasses(
    Collection<Type> classes
  ) {
    Preconditions.checkNotNull(classes, "classes");
    return new ClassStream<>(classes.stream());
  }

  public static <Type extends Class<?>> ClassStream<Type> withStream(
    Stream<Type> stream
  ) {
    Preconditions.checkNotNull(stream, "stream");
    return new ClassStream<>(stream);
  }

  private Stream<Type> stream;

  private ClassStream(Stream<Type> stream) {
    this.stream = stream;
  }

  public ClassStream<Type> extendWithMultiple(
    Collection<ClassStream<Type>> streams
  ) {
    var combined = stream;
    for (var stream : streams) {
      combined = Stream.concat(combined, stream.stream);
    }
    this.stream = combined.distinct();
    return this;
  }

  public ClassStream<Type> extend(ClassStream<Type> stream) {
    this.stream = Stream.concat(this.stream, stream.stream)
      .distinct();
    return this;
  }

  public ClassStream<Type> findAnnotated(Annotation annotation) {
    Preconditions.checkNotNull(annotation, "annotation");
    return findAnnotated(annotation.getClass());
  }

  public ClassStream<Type> findNonAnnotated(Annotation annotation) {
    Preconditions.checkNotNull(annotation, "annotation");
    return findNonAnnotated(annotation.getClass());
  }

  public ClassStream<Type> findAnnotated(
    Class<? extends Annotation> annotationType
  ) {
    Preconditions.checkNotNull(annotationType, "annotationType");
    stream = stream
      .filter(checked -> checked.isAnnotationPresent(annotationType));
    return this;
  }

  public ClassStream<Type> findNonAnnotated(
    Class<? extends Annotation> annotationType
  ) {
    Preconditions.checkNotNull(annotationType, "annotationType");
    stream = stream
      .filter(checked -> !checked.isAnnotationPresent(annotationType));
    return this;
  }

  @SuppressWarnings("unchecked")
  public <E extends Class<?>> ClassStream<E> findSuperType(E superType) {
    Preconditions.checkNotNull(superType, "superType");
    return ClassStream.withStream(stream
      .filter(superType::isAssignableFrom)
      .map(checked -> (E) checked)
    );
  }

  public Stream<Type> asJavaStream() {
    return stream;
  }

  public Collection<Type> all() {
    return stream.toList();
  }

  public Collection<Class<?>> allClassCasted() {
    return stream
      .map(claZ -> (Class<?>) claZ)
      .collect(Collectors.toList());
  }
}