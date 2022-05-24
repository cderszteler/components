package qetz.components.registration.jda.listener;

import com.google.inject.Injector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.EventListener;
import qetz.components.ComponentScanning;

import java.util.logging.Logger;

public final class ListenerRegistration {
  private static final Logger log = Logger.getLogger(ListenerRegistration.class.getSimpleName());

  private final ComponentScanning components;
  private final Injector injector;
  private final JDA jda;

  private ListenerRegistration(
    ComponentScanning components,
    Injector injector,
    JDA jda
  ) {
    this.components = components;
    this.injector = injector;
    this.jda = jda;
  }

  public void scanAndRegister() {
    components.classes()
      .findSuperType(EventListener.class)
      .peek(listener -> log.fine(String.format(
        "Registered %s",
        listener.getSimpleName()
      )))
      .forEach(listener -> jda.addEventListener(injector.getInstance(listener)));
  }
}