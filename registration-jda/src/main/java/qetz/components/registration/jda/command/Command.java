package qetz.components.registration.jda.command;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;

public interface Command<Type extends GenericCommandInteractionEvent> {
  void execute(Type command);
}