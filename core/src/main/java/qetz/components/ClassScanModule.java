package qetz.components;

import com.google.common.collect.Lists;

import java.util.Collection;

public interface ClassScanModule {
  default Collection<String> packages() {
    return Lists.newArrayList();
  }

  default Collection<String> recursivePackages() {
    return Lists.newArrayList();
  }

  default Collection<Class<?>> explicits() {
    return Lists.newArrayList();
  }
}