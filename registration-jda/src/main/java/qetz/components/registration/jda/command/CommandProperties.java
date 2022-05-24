package qetz.components.registration.jda.command;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandProperties {
  String name();
  String description();
  boolean defaultPermissions() default true;
  CommandOptions[] options() default {};
  SubCommandProperties[] subCommands() default {};

  @interface CommandOptions {
    String name();
    OptionType type();
    String description();
    boolean required() default true;
    boolean autoComplete() default false;
    ChannelType[] channelTypes() default {};
  }

  @interface SubCommandProperties {
    String name();
    String description();
    CommandOptions[] options() default {};
  }
}