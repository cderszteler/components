package qetz.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.logging.Level.FINER;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InjectorFactory {
  private static final Logger log = Logger.getLogger(
    InjectorFactory.class.getSimpleName()
  );

  private final Collection<Class<? extends Module>> ignoredModules;
  private final Collection<Module> manual;
  private final ComponentScanning scan;

  private Collection<Module> findModules() {
    return scan.classes()
      .findSubTypes(Module.class)
      .filter(module -> !ignoredModules.contains(module))
      .map(this::instantiateModule)
      .filter(Objects::nonNull)
      .peek(module -> log.log(FINER, String
        .format(
          "loaded injection module: %s",
          module.getClass().getSimpleName()
        )
      ))
      .collect(Collectors.toList());
  }

  @Nullable
  private Module instantiateModule(Class<? extends Module> module) {
    try {
      var constructor = module.getDeclaredConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (
      InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException instantiateFailure
    ) {
      log.severe("could not instantiate injector module");
      log.severe("the application will still load");
      log.severe(instantiateFailure.getMessage());
      return null;
    }
  }

  public Injector createInjector() {
    var modules = findModules();
    modules.addAll(manual);
    return Guice.createInjector(modules);
  }

  public static InjectorFactory createWithIgnoredAndManualModules(
    Set<Class<? extends Module>> ignored,
    Set<Module> manual,
    ComponentScanning scan
  ) {
    Preconditions.checkNotNull(ignored, "ignored");
    Preconditions.checkNotNull(manual, "manual");
    Preconditions.checkNotNull(scan, "scan");
    return new InjectorFactory(ignored, manual, scan);
  }

  public static InjectorFactory createWithManualModels(
    Set<Module> manual,
    ComponentScanning scan
  ) {

    Preconditions.checkNotNull(manual, "manual");
    Preconditions.checkNotNull(scan, "scan");
    return new InjectorFactory(
      Lists.newArrayList(),
      manual,
      scan
    );
  }

  public static InjectorFactory create(
    ComponentScanning scan
  ) {
    Preconditions.checkNotNull(scan, "scan");
    return new InjectorFactory(
      Lists.newArrayList(),
      Lists.newArrayList(),
      scan
    );
  }
}