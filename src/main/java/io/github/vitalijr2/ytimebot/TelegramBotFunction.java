package io.github.vitalijr2.ytimebot;

import static io.github.vitalijr2.ytimebot.BotTools.badMethod;
import static io.github.vitalijr2.ytimebot.BotTools.internalError;
import static io.github.vitalijr2.ytimebot.BotTools.ok;
import static io.github.vitalijr2.ytimebot.BotTools.okWithBody;
import static io.github.vitalijr2.ytimebot.BotTools.viaBot;
import static java.util.Optional.empty;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import org.jetbrains.annotations.VisibleForTesting;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelegramBotFunction implements HttpFunction {

  private static final String HTTP_POST_METHOD = "POST";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
    if (HTTP_POST_METHOD.equals(httpRequest.getMethod())) {
      try {
        processRequestBody(httpRequest.getReader()).ifPresentOrElse(
            body -> okWithBody(httpResponse, body), () -> ok(httpResponse));
      } catch (IOException | JSONException exception) {
        logger.warn("Could not parse request body: {}", exception.getMessage());
        internalError(httpResponse);
      }
    } else {
      logger.warn("Method {} isn't implemented: {}", httpRequest.getMethod(),
          httpRequest.getFirstHeader("X-Forwarded-For").orElse("address not known"));
      badMethod(httpResponse, "POST");
    }
  }

  @VisibleForTesting
  Optional<String> processRequestBody(Reader requestBodyReader) {
    var telegramRequest = new JSONObject(new JSONTokener(requestBodyReader));

    if (viaBot(telegramRequest)) {
      return empty();
    }

    return Optional.of("test");
  }

}