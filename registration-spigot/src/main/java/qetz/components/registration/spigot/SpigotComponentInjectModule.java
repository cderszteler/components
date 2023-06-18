package qetz.components.registration.spigot;

import com.google.inject.AbstractModule;
import org.bukkit.command.CommandMap;
import qetz.components.registration.spigot.command.ReflectionCommandMapProvider;

public final class SpigotComponentInjectModule extends AbstractModule {
  public static SpigotComponentInjectModule create() {
    return new SpigotComponentInjectModule();
  }

  private SpigotComponentInjectModule() {}

  @Override
  protected void configure() {
    bind(CommandMap.class).toProvider(ReflectionCommandMapProvider.class);
  }
}