package qetz.components.registration.spigot.command;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

final class RegisterCommand_ {
  private static final Logger log = Logger.getLogger(RegisterCommand_.class.getSimpleName());

  static final class Implicits {
    private final CommandMap commandMap;
    private final Injector injector;

    @Inject
    private Implicits(
      CommandMap commandMap,
      Injector injector
    ) {
      this.commandMap = commandMap;
      this.injector = injector;
    }
  }

  private final Class<? extends CommandExecutor> type;
  private final String fallbackPrefix;
  private final Implicits __;

  private CommandLabel label;

  RegisterCommand_(
    Class<? extends CommandExecutor> type,
    String fallbackPrefix,
    Implicits __
  ) {
    this.type = type;
    this.fallbackPrefix = fallbackPrefix;
    this.__ = __;
  }

  public void run() {
    label = findCommandLabel(type);
    if(label == null) {
      log.warning(String.format(
        "could not find command label for class %s. command is ignored",
        type.getSimpleName()
      ));
      return;
    }
    register(__.injector.getInstance(type));
  }

  private CommandLabel findCommandLabel(
    Class<? extends CommandExecutor> commandClass
  ) {
    return commandClass.getDeclaredAnnotation(CommandLabel.class);
  }

  private void register(CommandExecutor executor) {
    __.commandMap.register(fallbackPrefix, createCommand(executor));
  }

  @SuppressWarnings("NullableProblems")
  private Command createCommand(CommandExecutor executor) {
    var command = new Command(label.value()) {
      @Override
      public boolean execute(
        CommandSender sender,
        String label,
        String[] options
      ) {
        return executor.onCommand(sender, this, label, options);
      }
    };
    command.setAliases(Lists.newArrayList(label.aliases()));
    return command;
  }
}