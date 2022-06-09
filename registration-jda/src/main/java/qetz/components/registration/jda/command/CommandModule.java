package qetz.components.registration.jda.command;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import qetz.components.Component;
import qetz.components.ComponentScan;

@Component
public final class CommandModule extends AbstractModule {
  public static CommandModule create() {
    return new CommandModule();
  }

  private CommandModule() {}

  @Singleton
  @Provides
  @Inject
  CommandRegistry bindRegistry(
    RegisterCommand registerCommand,
    ComponentScan components,
    JDA jda
  ) {
    return CommandRegistry.empty(registerCommand, components, jda);
  }
}