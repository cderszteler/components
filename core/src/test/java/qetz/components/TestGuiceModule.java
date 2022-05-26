package qetz.components;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

@Component
public final class TestGuiceModule extends AbstractModule {
  private TestGuiceModule() {}

  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named("test")).toInstance("test");
  }
}