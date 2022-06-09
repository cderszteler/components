package qetz.components.registration.jda;

import qetz.components.ClassScanFactory;
import qetz.components.ClassScanModule;

public final class JdaClassScanModule implements ClassScanModule {
  public static JdaClassScanModule create() {
    return new JdaClassScanModule();
  }

  private JdaClassScanModule() {}

  @Override
  public void configure(ClassScanFactory factory) {
    factory.withRecursivePackageByClass(getClass());
  }
}