package qetz.components.registration.jda;

import com.google.common.collect.Lists;
import qetz.components.ClassScanModule;

import java.util.Collection;

public final class JdaClassScanModule implements ClassScanModule {
  @Override
  public Collection<String> recursivePackages() {
    return Lists.newArrayList(JdaClassScanModule.class.getPackageName());
  }
}