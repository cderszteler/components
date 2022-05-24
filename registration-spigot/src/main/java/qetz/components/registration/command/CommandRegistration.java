package qetz.components.registration.command;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.bukkit.command.CommandExecutor;
import qetz.components.ComponentScanning;

public final class CommandRegistration {
  private final ComponentScanning scanning;
  private final RegisterCommand register;

  @Inject
  private CommandRegistration(
    ComponentScanning scanning,
    RegisterCommand register
  ) {
    this.scanning = scanning;
    this.register = register;
  }

  public void scanAndRegister(String fallbackPrefix) {
    Preconditions.checkNotNull(fallbackPrefix, "fallbackPrefix");
    scanning.classes()
      .findInterfaces(CommandExecutor.class)
      .forEach(command -> register
        .withFallbackPrefix(fallbackPrefix)
        .withType(command)
        .register()
      );
  }
}