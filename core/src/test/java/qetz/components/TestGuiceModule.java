package qetz.components;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public final class TestGuiceModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named("test")).toInstance("test");
  }
}