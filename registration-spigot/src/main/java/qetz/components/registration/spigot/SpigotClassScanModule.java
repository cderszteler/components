package qetz.components.registration.spigot;

import com.google.common.collect.Lists;
import qetz.components.ClassScanModule;

import java.util.Collection;

public final class SpigotClassScanModule implements ClassScanModule {
  @Override
  public Collection<String> recursivePackages() {
    return Lists.newArrayList(SpigotClassScanModule.class.getPackageName());
  }
}