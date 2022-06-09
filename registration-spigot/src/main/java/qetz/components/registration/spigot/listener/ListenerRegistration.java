package qetz.components.registration.spigot.listener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import qetz.components.ComponentScan;

public final class ListenerRegistration {
  private final ComponentScan components;
  private final Injector injector;
  private final Plugin plugin;

  @Inject
  private ListenerRegistration(
    ComponentScan components,
    Injector injector,
    Plugin plugin
  ) {
    this.components = components;
    this.injector = injector;
    this.plugin = plugin;
  }

  public void scanAndRegister() {
    components.stream()
      .findSuperType(Listener.class)
      .forEach(this::registerType);
  }

  private void registerType(Class<? extends Listener> listenerType) {
    var listener = injector.getInstance(listenerType);
    registerListener(listener);
  }

  private static final PluginManager registration = Bukkit.getPluginManager();

  private void registerListener(Listener listener) {
    registration.registerEvents(listener, plugin);
  }
}