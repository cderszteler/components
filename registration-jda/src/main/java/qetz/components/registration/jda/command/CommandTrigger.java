package qetz.components.registration.jda.command;

import com.google.inject.Inject;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import qetz.components.Component;

import java.util.logging.Logger;

@Component
public final class CommandTrigger extends ListenerAdapter {
  private static final Logger log = Logger.getLogger(CommandTrigger.class.getSimpleName());

  private final CommandRegistry registry;

  @Inject
  private CommandTrigger(CommandRegistry registry) {
    this.registry = registry;
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent slash) {
    executeCommand(slash);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public void onUserContextInteraction(
    UserContextInteractionEvent userInteraction
  ) {
    executeCommand(userInteraction);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public void onMessageContextInteraction(
    MessageContextInteractionEvent messageInteraction
  ) {
    executeCommand(messageInteraction);
  }

  private <T extends GenericCommandInteractionEvent> void executeCommand(T event) {
    if (!event.isFromGuild()) {
      return;
    }
    var name = event.getName();

    try {
      registry
        .findByNameAndType(name, event.getClass())
        .ifPresent(command -> command.execute(event));
    } catch (Throwable failure) {
      failure.printStackTrace();
      log.warning(String.format(
        "failure occurred when executing %s command: %s",
        name,
        failure.getMessage()
      ));
    }
  }
}