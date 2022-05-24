package qetz.components.registration.jda.command;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import qetz.components.registration.jda.command.CommandRegistry.CommandIdentifier;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static qetz.components.registration.jda.command.CommandDatas.fromProperties;

final class RegisterCommand_ {
  private static final Logger log = Logger.getLogger(RegisterCommand_.class.getSimpleName());

  static final class Implicits {
    private final Injector injector;

    @Inject
    private Implicits(Injector injector) {
      this.injector = injector;
    }
  }

  private final Map<CommandIdentifier, Command<? super GenericCommandInteractionEvent>> commands;
  private final Class<? super Command<? super GenericCommandInteractionEvent>> command;
  private final CommandListUpdateAction updateAction;
  private final Implicits __;

  private CommandProperties properties;
  private Type type;

  RegisterCommand_(
    Map<CommandIdentifier, Command<? super GenericCommandInteractionEvent>> commands,
    Class<? super Command<? super GenericCommandInteractionEvent>> command,
    CommandListUpdateAction updateAction,
    Implicits __
  ) {
    this.commands = commands;
    this.command = command;
    this.updateAction = updateAction;
    this.__ = __;
  }

  public void run() {
    type = resolveCommandType();
    if (!hasAllowedEvent()) {
      throw new IllegalStateException(String.format(
        "event type for %s command is not valid",
        command.getSimpleName()
      ));
    }
    properties = resolveProperties();
    registerCommand();
  }

  @SuppressWarnings({"UnstableApiUsage"})
  private Type resolveCommandType() {
    for (var genericInterface : command.getGenericInterfaces()) {
      var interfaceType = TypeToken.of(genericInterface).getRawType();
      if (!Command.class.isAssignableFrom(interfaceType)) {
        continue;
      }
      if (genericInterface instanceof ParameterizedType parameterized) {
        return parameterized.getActualTypeArguments()[0];
      }
    }
    throw new IllegalStateException(String.format(
      "could not find command type for %s command",
      command.getSimpleName()
    ));
  }

  private static final ImmutableSet<Class<? extends GenericCommandInteractionEvent>>
    allowedEvents = ImmutableSet.of(
    SlashCommandInteractionEvent.class,
    UserContextInteractionEvent.class,
    MessageContextInteractionEvent.class
  );

  @SuppressWarnings({"SuspiciousMethodCalls"})
  private boolean hasAllowedEvent() {
    return allowedEvents.contains(type);
  }

  private static final Method executeMethod = Command.class.getDeclaredMethods()[0];

  private CommandProperties resolveProperties() {
    try {
      var method = command.getDeclaredMethod(
        executeMethod.getName(),
        executeMethod.getParameterTypes()
      );
      return Optional.ofNullable(method.getAnnotation(CommandProperties.class))
        .orElseThrow(() -> new IllegalStateException(String.format(
          "could not find required properties annotation for %s command",
          command.getSimpleName()
        )));
    } catch (NoSuchMethodException couldNotFindMethod) {
      throw new IllegalStateException(String.format(
        "could not find executing method for %s command",
        command.getSimpleName()
      ));
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void registerCommand() {
    var identifier = CommandIdentifier.with(properties.name(), type);
    if (commands.containsKey(identifier)) {
      throw new IllegalStateException(String.format(
        "could not register %s command, because it has equal identifier with another command",
        command.getDeclaringClass().getSimpleName()
      ));
    }
    commands.put(identifier, instantiateCommand(command));
    updateAction.addCommands(fromProperties(properties, type));
    log.fine(String.format(
      "registered command %s",
      command.getSimpleName()
    ));
  }

  @SuppressWarnings("unchecked")
  private Command<? super GenericCommandInteractionEvent> instantiateCommand(
    Class<? super Command<? super GenericCommandInteractionEvent>> command
  ) {
    return (Command<? super GenericCommandInteractionEvent>) __.injector.getInstance(
      command
    );
  }

}