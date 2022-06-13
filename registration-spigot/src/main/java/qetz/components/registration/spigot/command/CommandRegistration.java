package qetz.components.registration.spigot.command;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.bukkit.command.CommandExecutor;
import qetz.components.ComponentScan;

public final class CommandRegistration {
  private final ComponentScan scan;
  private final RegisterCommand register;

  @Inject
  private CommandRegistration(
    ComponentScan scan,
    RegisterCommand register
  ) {
    this.scan = scan;
    this.register = register;
  }

  public void scanAndRegister(String fallbackPrefix) {
    Preconditions.checkNotNull(fallbackPrefix, "fallbackPrefix");
    scan.stream()
      .findSuperType(CommandExecutor.class)
      .asJavaStream()
      .forEach(command -> register
        .withFallbackPrefix(fallbackPrefix)
        .withType(command)
        .register()
      );
  }
}