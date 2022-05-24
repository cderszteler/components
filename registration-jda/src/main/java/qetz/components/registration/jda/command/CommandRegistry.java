package qetz.components.registration.jda.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import qetz.components.ComponentScanning;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class CommandRegistry {
  public static CommandRegistry empty(
    RegisterCommand registerCommand,
    ComponentScanning components,
    JDA jda
  ) {
    Preconditions.checkNotNull(registerCommand, "registerCommand");
    Preconditions.checkNotNull(components, "components");
    Preconditions.checkNotNull(jda, "jda");
    Map<CommandIdentifier, Command<? super GenericCommandInteractionEvent>> commands = Maps.newHashMap();
    return new CommandRegistry(
      commands,
      registerCommand.withCommands(commands),
      components,
      jda
    );
  }

  private final Map<CommandIdentifier, Command<? super GenericCommandInteractionEvent>> commands;
  private final RegisterCommand registerCommand;
  private final ComponentScanning components;
  private final JDA jda;

  CommandRegistry(
    Map<CommandIdentifier, Command<? super GenericCommandInteractionEvent>> commands,
    RegisterCommand registerCommand,
    ComponentScanning components,
    JDA jda
  ) {
    this.commands = commands;
    this.registerCommand = registerCommand;
    this.components = components;
    this.jda = jda;
  }

  public Optional<Command<? super GenericCommandInteractionEvent>> findByNameAndType(
    String name,
    Type type
  ) {
    Preconditions.checkNotNull(name, "name");
    Preconditions.checkNotNull(type, "type");
    return Optional.ofNullable(commands.get(CommandIdentifier.with(name, type)));
  }

  public void scanAndRegister() {
    var updateAction = jda.updateCommands();
    components.classes()
      .findSuperType(Command.class)
      .forEach(command -> registerCommand
        .withUpdateAction(updateAction)
        .withCommand(command)
        .register()
      );
    updateAction.queue();
  }

  public static final class CommandIdentifier {
    public static CommandIdentifier with(String name, Type type) {
      Preconditions.checkNotNull(name, "name");
      Preconditions.checkNotNull(type, "type");
      return new CommandIdentifier(name.toLowerCase(), type);
    }

    private final String name;
    private final Type type;

    private CommandIdentifier(String name, Type type) {
      this.name = name;
      this.type = type;
    }

    private static final String toStringFormat = "CommandIdentifier{name=%s, type=%s}";

    @Override
    public String toString() {
      return String.format(toStringFormat, name, type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object object) {
      if (object == this) {
        return true;
      }
      if (!(object instanceof CommandIdentifier)) {
        return false;
      }
      var identifier = (CommandIdentifier) object;
      return identifier.name.equals(name) && identifier.type.equals(type);
    }
  }
}