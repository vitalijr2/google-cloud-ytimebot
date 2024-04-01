package io.github.vitalijr2.ytimebot;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("slow")
class TelegramBotFunctionSlowTest {

  @Mock
  private HttpRequest httpRequest;
  @Mock
  private HttpResponse httpResponse;
  @Mock
  private BufferedWriter writer;

  private TelegramBotFunction bot;

  @BeforeEach
  void setUp() {
    bot = new TelegramBotFunction();
  }

  @DisplayName("Webhook")
  @Test
  void webhook() throws IOException {
    // given
    var reader = new CharArrayReader("{\"a\":\"b\"}".toCharArray());

    bot = spy(bot);

    when(httpRequest.getMethod()).thenReturn("POST");
    when(httpRequest.getReader()).thenReturn(new BufferedReader(reader));
    doNothing().when(bot).processRequestBody(isA(Reader.class));

    // when
    assertDoesNotThrow(() -> bot.service(httpRequest, httpResponse));

    // then
    verify(httpResponse).setStatusCode(200, "OK");
    verify(httpResponse).appendHeader(eq("Server"), anyString());
    verifyNoMoreInteractions(httpResponse);
  }

  @DisplayName("HTTP method not allowed")
  @ParameterizedTest
  @ValueSource(strings = {"GET", "HEAD", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH"})
  void methodNotAllowed(String methodName) throws IOException {
    // given
    var headers = new HashMap<String, List<String>>();

    when(httpRequest.getMethod()).thenReturn(methodName);
    when(httpRequest.getFirstHeader(anyString())).thenReturn(Optional.of("1.2.3.4"));
    when(httpResponse.getHeaders()).thenReturn(headers);
    when(httpResponse.getWriter()).thenReturn(writer);

    // when
    assertDoesNotThrow(() -> bot.service(httpRequest, httpResponse));

    // then
    verify(httpResponse).setStatusCode(405, "Method Not Allowed");
    verify(httpResponse).appendHeader(eq("Server"), anyString());
    verify(httpResponse).getWriter();
    verifyNoMoreInteractions(httpResponse);
    verify(writer).write(startsWith("<!doctype html>"));

    assertThat("", headers, hasEntry(equalTo("Allow"), contains("POST")));
  }

  @DisplayName("Bad request body")
  @Test
  void requestBody() throws IOException {
    // given
    var reader = new CharArrayReader("{\"a\":\"b\"}".toCharArray());

    bot = spy(bot);

    when(httpRequest.getMethod()).thenReturn("POST");
    when(httpRequest.getReader()).thenReturn(new BufferedReader(reader));
    doThrow(new JSONException("test exception")).when(bot).processRequestBody(isA(Reader.class));

    // when
    assertDoesNotThrow(() -> bot.service(httpRequest, httpResponse));

    // then
    verify(httpResponse).setStatusCode(500, "Internal Server Error");
    verify(httpResponse).appendHeader(eq("Server"), anyString());
    verifyNoMoreInteractions(httpResponse);
  }

}