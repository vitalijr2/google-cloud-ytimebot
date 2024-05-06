package io.github.vitalijr2.ytimebot.telegram;

import static io.github.vitalijr2.ytimebot.telegram.BotTools.badMethod;
import static io.github.vitalijr2.ytimebot.telegram.BotTools.internalError;
import static io.github.vitalijr2.ytimebot.telegram.BotTools.isInlineQuery;
import static io.github.vitalijr2.ytimebot.telegram.BotTools.isMessage;
import static io.github.vitalijr2.ytimebot.telegram.BotTools.ok;
import static io.github.vitalijr2.ytimebot.telegram.BotTools.okWithBody;
import static io.github.vitalijr2.ytimebot.telegram.BotTools.viaBot;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
  @NotNull
  Optional<String> processRequestBody(Reader requestBodyReader) {
    var update = new JSONObject(new JSONTokener(requestBodyReader));
    var result = Optional.<String>empty();

    if (viaBot(update)) {
      logger.trace("Ignore message of another bot");
    } else if (isInlineQuery(update)) {
      result = Optional.ofNullable(processInlineQuery(update));
    } else if (isMessage(update)) {
      result = Optional.ofNullable(processMessage(update));
    }

    return result;
  }

  @VisibleForTesting
  @Nullable
  String processInlineQuery(JSONObject update) {
    return "inline query";
  }

  @VisibleForTesting
  @Nullable
  String processMessage(JSONObject update) {
    return "message";
  }

}
