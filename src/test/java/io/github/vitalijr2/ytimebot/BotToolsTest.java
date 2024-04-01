package io.github.vitalijr2.ytimebot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
class BotToolsTest {

  @Mock
  private HttpResponse httpResponse;

  private static Logger logger;

  @BeforeAll
  static void setUpClass() {
    logger = LoggerFactory.getLogger(BotTools.class);
  }

  @AfterEach
  void tearDown() {
    clearInvocations(logger);
  }

  @DisplayName("Bad method: IOException")
  @Test
  void badMethodException() {
    try (var botTools = mockStatic(BotTools.class)) {
      // given
      botTools.when(
              () -> BotTools.doResponse(isA(HttpResponse.class), anyInt(), anyString(), any()))
          .thenThrow(new IOException("test exception"));
      botTools.when(() -> BotTools.badMethod(isA(HttpResponse.class), anyString()))
          .thenCallRealMethod();

      // when
      assertDoesNotThrow(() -> BotTools.badMethod(httpResponse, "QWERTY"));

      // then
      verify(logger).warn("Could not make HTTP 405 response: {}", "test exception");
    }
  }

  @DisplayName("Internal error: IOException")
  @Test
  void internalErrorException() {
    try (var botTools = mockStatic(BotTools.class)) {
      // given
      botTools.when(
              () -> BotTools.doResponse(isA(HttpResponse.class), anyInt(), anyString(), any()))
          .thenThrow(new IOException("test exception"));
      botTools.when(() -> BotTools.internalError(isA(HttpResponse.class))).thenCallRealMethod();

      // when
      assertDoesNotThrow(() -> BotTools.internalError(httpResponse));

      // then
      verify(logger).warn("Could not make HTTP 500 response: {}", "test exception");
    }
  }

  @Test
  void viaBot() {
  }

}