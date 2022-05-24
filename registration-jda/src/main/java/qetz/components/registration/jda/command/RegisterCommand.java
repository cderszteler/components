package qetz.components.registration.jda.command;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import qetz.components.registration.jda.command.CommandRegistry.CommandIdentifier;
import qetz.components.registration.jda.command.RegisterCommand_.Implicits;

import java.util.Map;

public final class RegisterCommand {
  private final Implicits implicits;

  private Map<CommandIdentifier, Command<? super GenericCommandInteractionEvent>> commands;
  private Class<? super Command<? super GenericCommandInteractionEvent>> command;
  private CommandListUpdateAction updateAction;

  @Inject
  private RegisterCommand(
    Implicits implicits
  ) {
    this.implicits = implicits;
  }

  public RegisterCommand withCommands(
    Map<CommandIdentifier, Command<? super GenericCommandInteractionEvent>> commands
  ) {
    this.commands = commands;
    return this;
  }

  public RegisterCommand withCommand(
    Class<? super Command<? super GenericCommandInteractionEvent>> command
  ) {
    this.command = command;
    return this;
  }

  public RegisterCommand withUpdateAction(CommandListUpdateAction updateAction) {
    this.updateAction = updateAction;
    return this;
  }

  public void register() {
    Preconditions.checkNotNull(commands, "commands");
    Preconditions.checkNotNull(command, "command");
    Preconditions.checkNotNull(updateAction, "updateAction");
    new RegisterCommand_(commands, command, updateAction, implicits).run();
  }

}