package io.vitalijr2.ytimebot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
class YTimeBotTest {

  @Mock
  private HttpRequest httpRequest;
  @Mock
  private HttpResponse httpResponse;

  private YTimeBot bot;

  @BeforeEach
  void setUp() {
    bot = new YTimeBot();
  }

  @Test
  void service() {
    assertDoesNotThrow(() -> bot.service(httpRequest, httpResponse));
  }

}