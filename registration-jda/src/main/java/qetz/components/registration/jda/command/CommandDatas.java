package qetz.components.registration.jda.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import qetz.components.registration.jda.command.CommandProperties.CommandOptions;
import qetz.components.registration.jda.command.CommandProperties.SubCommandProperties;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;

public final class CommandDatas {
  private CommandDatas() {}

  public static CommandData fromProperties(
    CommandProperties properties,
    Type type
  ) {
    Preconditions.checkNotNull(properties, "properties");
    var data = initializeWithType(type).apply(properties.name());
    data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(
      properties.defaultPermissions()
    ));
    if (data instanceof SlashCommandData slashCommandData) {
      extendSlashCommands(properties, slashCommandData);
    }
    return data;
  }

  private static final ImmutableMap<
    Type,
    Command.Type
  > typeEvents = ImmutableMap.of(
    SlashCommandInteractionEvent.class, Command.Type.SLASH,
    UserContextInteractionEvent.class, Command.Type.USER,
    MessageContextInteractionEvent.class, Command.Type.MESSAGE
  );

  private static Function<String, ? extends CommandData> initializeWithType(
    Type commandType
   ) {
    var type = typeEvents.get(commandType);
    return switch (type) {
      case SLASH -> name -> Commands.slash(name, " ");
      case USER -> Commands::user;
      case MESSAGE -> Commands::message;
      default -> throw new IllegalStateException(
        "command type resolved by event is not supported"
      );
    };
  }

  private static void extendSlashCommands(
    CommandProperties properties,
    SlashCommandData data
  ) {
    var options = Arrays.stream(properties.options())
      .map(CommandDatas::fromOptions)
      .toArray(OptionData[]::new);
    var subCommands = Arrays.stream(properties.subCommands())
      .map(CommandDatas::fromSubProperties)
      .toArray(SubcommandData[]::new);
    data.addOptions(options);
    data.addSubcommands(subCommands);
    data.setDescription(properties.description());
  }

  private static OptionData fromOptions(CommandOptions options) {
    Preconditions.checkNotNull(options, "options");
    var option = new OptionData(
      options.type(),
      options.name(),
      options.description()
    );
    option.setRequired(options.required());
    option.setAutoComplete(options.autoComplete());
    if (options.type() == OptionType.CHANNEL) {
      option.setChannelTypes(options.channelTypes());
    }
    if (options.type().canSupportChoices() && !options.autoComplete()) {
      for (var choice : options.choices()) {
        option.addChoice(choice, choice);
      }
    }
    return option;
  }

  private static SubcommandData fromSubProperties(SubCommandProperties properties) {
    Preconditions.checkNotNull(properties, "properties");
    var data = new SubcommandData(properties.name(), properties.description());
    var options = Arrays.stream(properties.options())
      .map(CommandDatas::fromOptions)
      .toArray(OptionData[]::new);
    data.addOptions(options);
    return data;
  }
}