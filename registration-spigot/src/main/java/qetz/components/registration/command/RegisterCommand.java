package qetz.components.registration.command;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.bukkit.command.CommandExecutor;

import static qetz.components.registration.command.RegisterCommand_.Implicits;

public final class RegisterCommand {
  private final Implicits implicits;

  private Class<? extends CommandExecutor> type;
  private String fallbackPrefix;

  @Inject
  private RegisterCommand(Implicits implicits) {
    this.implicits = implicits;
  }

  public RegisterCommand withType(
    Class<? extends CommandExecutor> type
  ) {
    this.type = type;
    return this;
  }

  private static final String emptyFallbackPrefix = "";

  public RegisterCommand withEmptyFallbackPrefix() {
    return withFallbackPrefix(emptyFallbackPrefix);
  }

  public RegisterCommand withFallbackPrefix(
    String fallbackPrefix
  ) {
    this.fallbackPrefix = fallbackPrefix;
    return this;
  }

  public void register() {
    Preconditions.checkNotNull(type, "type");
    Preconditions.checkNotNull(fallbackPrefix, "fallbackPrefix");
    new RegisterCommand_(type, fallbackPrefix, implicits).run();
  }
}