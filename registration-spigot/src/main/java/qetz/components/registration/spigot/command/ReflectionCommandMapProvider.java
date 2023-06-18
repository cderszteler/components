package qetz.components.registration.spigot.command;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionCommandMapProvider implements Provider<CommandMap> {
  @Inject
  private ReflectionCommandMapProvider() {}

  @Override
  public CommandMap get() {
    var method = findMethod();
    var server = Bukkit.getServer();
    return invokeMethod(method, server);
  }

  private CommandMap invokeMethod(Method method, Server instance) {
    try {
      method.setAccessible(true);
      var map = method.invoke(instance);
      return (CommandMap) map;
    } catch (IllegalAccessException
      | InvocationTargetException methodInvokeFailure
    ) {
      throw new IllegalStateException(
        "could not invoke necessary method to receive CommandMap",
        methodInvokeFailure
      );
    }
  }

  private static final Class<Server> methodClass = Server.class;
  private static final String methodName = "getCommandMap";

  @SuppressWarnings("JavaReflectionMemberAccess")
  private Method findMethod() {
    try {
      return methodClass.getDeclaredMethod(methodName);
    } catch (NoSuchMethodException methodNotFound) {
      throw new IllegalStateException(
        "could not find necessary method to receive CommandMap",
        methodNotFound
      );
    }
  }
}