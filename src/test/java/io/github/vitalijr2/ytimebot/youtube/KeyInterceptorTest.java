package io.github.vitalijr2.ytimebot.youtube;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.jupiter.api.Assertions.assertAll;

import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class KeyInterceptorTest {

  private KeyInterceptor interceptor;
  private RequestTemplate template;

  @BeforeEach
  void setUp() {
    interceptor = new KeyInterceptor("qwerty");
    template = new RequestTemplate();
  }

  @DisplayName("Add an API key to a request template")
  @Test
  void happyPath() {
    // when
    interceptor.apply(template);

    // then
    assertAll("API key",
        () -> assertThat("HTTP query with API key", template.queries(), hasKey("key")),
        () -> assertThat("API key value", template.queries().get("key"), contains("qwerty")));
  }

  @DisplayName("An API key exists")
  @Test
  void keyExists() {
    // given
    template.query("key", "xyz");

    // when
    interceptor.apply(template);

    // then
    assertAll("API key",
        () -> assertThat("HTTP query with API key", template.queries(), hasKey("key")),
        () -> assertThat("API key value", template.queries().get("key"), contains("xyz")));
  }

}