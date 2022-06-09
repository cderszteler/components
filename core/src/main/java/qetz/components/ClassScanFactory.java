package qetz.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.github.classgraph.ClassGraph;

import java.util.Collection;

public final class ClassScanFactory {
  public static ClassScanFactory createEmpty() {
    return new ClassScanFactory(
      Lists.newArrayList(),
      Lists.newArrayList(),
      Lists.newArrayList()
    );
  }

  private final Collection<String> recursivePackages;
  private final Collection<Class<?>> explicits;
  private final Collection<String> packages;

  private ClassScanFactory(
    Collection<String> recursivePackages,
    Collection<Class<?>> explicits,
    Collection<String> packages
  ) {
    this.recursivePackages = recursivePackages;
    this.explicits = explicits;
    this.packages = packages;
  }

  public ClassScanFactory withPackageByClass(Class<?> packageClass) {
    Preconditions.checkNotNull(packageClass, "packageClass");
    return withPackage(packageClass.getPackageName());
  }

  public ClassScanFactory withPackages(Collection<String> packages) {
    Preconditions.checkNotNull(packages, "packages");
    this.packages.addAll(packages);
    return this;
  }

  public ClassScanFactory withPackage(String packageName) {
    Preconditions.checkNotNull(packageName, "packageName");
    packages.add(packageName);
    return this;
  }

  public ClassScanFactory withRecursivePackageByClass(Class<?> packageClass) {
    Preconditions.checkNotNull(packageClass, "packageClass");
    return withRecursivePackage(packageClass.getPackageName());
  }

  public ClassScanFactory withRecursivePackages(Collection<String> packages) {
    Preconditions.checkNotNull(packages, "packages");
    recursivePackages.addAll(packages);
    return this;
  }

  public ClassScanFactory withRecursivePackage(String packageName) {
    Preconditions.checkNotNull(packageName, "packageName");
    recursivePackages.add(packageName);
    return this;
  }

  public ClassScanFactory withExplicits(Collection<Class<?>> explicits) {
    Preconditions.checkNotNull(explicits, "explicits");
    this.explicits.addAll(explicits);
    return this;
  }

  public ClassScanFactory withExplicit(Class<?> explicit) {
    Preconditions.checkNotNull(explicit, "explicit");
    explicits.add(explicit);
    return this;
  }

  public ClassScanFactory withModule(ClassScanModule module) {
    Preconditions.checkNotNull(module, "module");
    module.configure(this);
    return this;
  }

  public ClassScanFactory withExistingFactory(ClassScanFactory factory) {
    Preconditions.checkNotNull(factory, "factory");
    return withPackages(factory.packages)
      .withRecursivePackages(factory.recursivePackages)
      .withExplicits(factory.explicits);
  }

  public ClassScan scanWithExistingBuilder(ClassGraph base) {
    Preconditions.checkNotNull(base, "base");
    var builder = addAcceptancesToBuilder(base);
    try (var scan = builder.scan()) {
      return ClassScan.withClasses(scan.getAllClasses().loadClasses());
    }
  }

  private ClassGraph addAcceptancesToBuilder(ClassGraph builder) {
    return builder.acceptClasses(
        explicits.stream()
          .map(Class::getName)
          .toArray(String[]::new)
      )
      .acceptPackagesNonRecursive(packages.toArray(String[]::new))
      .acceptPackages(recursivePackages.toArray(String[]::new));
  }

  public ClassScan scan() {
    return scanWithExistingBuilder(
      new ClassGraph()
      .enableClassInfo()
      .enableAnnotationInfo()
    );
  }
}