package qetz.components.registration.spigot;

import qetz.components.ClassScanFactory;
import qetz.components.ClassScanModule;

public final class SpigotClassScanModule implements ClassScanModule {
  public static SpigotClassScanModule create() {
    return new SpigotClassScanModule();
  }

  private SpigotClassScanModule() {}

  @Override
  public void configure(ClassScanFactory factory) {
    factory.withRecursivePackageByClass(getClass());
  }
}