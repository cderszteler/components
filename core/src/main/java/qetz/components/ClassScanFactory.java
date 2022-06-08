package qetz.components;

import com.google.common.base.Preconditions;
import io.github.classgraph.ClassGraph;

import java.util.Collection;

public final class ClassScanFactory {
  public static ClassScanFactory createEmpty() {
    return withGraph(
      new ClassGraph()
        .enableClassInfo()
        .enableAnnotationInfo()
    );
  }

  public static ClassScanFactory withGraph(ClassGraph graph) {
    Preconditions.checkNotNull(graph, "graph");
    return new ClassScanFactory(graph);
  }

  private final ClassGraph builder;

  private ClassScanFactory(ClassGraph builder) {
    this.builder = builder;
  }

  public ClassScanFactory withPackageByClass(Class<?> packageClass) {
    Preconditions.checkNotNull(packageClass, "packageClass");
    return withPackages(packageClass.getPackageName());
  }

  public ClassScanFactory withPackages(String... packages) {
    Preconditions.checkNotNull(packages, "packages");
    builder.acceptPackagesNonRecursive(packages);
    return this;
  }

  public ClassScanFactory withRecursivePackageByClass(Class<?> packageClass) {
    Preconditions.checkNotNull(packageClass, "packageClass");
    return withRecursivePackages(packageClass.getPackageName());
  }

  public ClassScanFactory withRecursivePackages(String... packages) {
    Preconditions.checkNotNull(packages, "packages");
    builder.acceptPackages(packages);
    return this;
  }

  public ClassScanFactory withExplicit(Class<?> explicit) {
    Preconditions.checkNotNull(explicit, "explicit");
    builder.acceptClasses(explicit.getName());
    return this;
  }

  public ClassScanFactory withExplicits(Collection<Class<?>> classes) {
    Preconditions.checkNotNull(classes, "classes");
    for (var explicit : classes) {
      builder.acceptClasses(explicit.getName());
    }
    return this;
  }

  public ClassScanFactory withModule(ClassScanModule module) {
    Preconditions.checkNotNull(module, "module");
    return withPackages(module.packages().toArray(String[]::new))
      .withRecursivePackages(module.recursivePackages().toArray(String[]::new))
      .withExplicits(module.explicits());
  }

  public ClassScan scan() {
    try (var scan = builder.scan()) {
      return ClassScan.withClasses(scan.getAllClasses().loadClasses());
    }
  }
}