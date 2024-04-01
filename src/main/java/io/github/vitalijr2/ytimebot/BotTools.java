package io.github.vitalijr2.ytimebot;

import static java.util.Objects.nonNull;

import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BotTools {

  private static final String FULL_VERSION_STRING;
  private static final String HTTP_BAD_METHOD_RESPONSE;
  private static final Logger LOGGER = LoggerFactory.getLogger(BotTools.class);
  private static final String SERVER_HEADER = "Server";

  static {
    var body = "";
    var name = "unknown";
    var version = "unknown";

    try (InputStream versionPropsStream = YTimeBot.class.getResourceAsStream(
        "/ytimebot.properties")) {
      var properties = new Properties();

      properties.load(versionPropsStream);
      body = properties.getProperty("http.bad-method");
      name = properties.getProperty("bot.name");
      version = properties.getProperty("bot.version");
    } catch (Exception exception) {
      LoggerFactory.getLogger(YTimeBot.class)
          .error("Could not initialize the bot: {}", exception.getMessage());
      System.exit(1);
    }
    FULL_VERSION_STRING = name + " - " + version;
    HTTP_BAD_METHOD_RESPONSE = body;
  }

  private BotTools() {
  }

  static HttpResponse doResponse(@NotNull HttpResponse httpResponse, int statusCode,
      @NotNull String statusMessage, @Nullable String body) throws IOException {
    httpResponse.setStatusCode(statusCode, statusMessage);
    httpResponse.appendHeader(SERVER_HEADER, FULL_VERSION_STRING);
    if (nonNull(body)) {
      httpResponse.getWriter().write(body);
    }

    return httpResponse;
  }

  static void badMethod(@NotNull HttpResponse httpResponse, String... allowedMethods) {
    try {
      doResponse(httpResponse, 405, "Method Not Allowed", HTTP_BAD_METHOD_RESPONSE).getHeaders()
          .put("Allow", List.of(allowedMethods));
    } catch (IOException exception) {
      LOGGER.warn("Could not make HTTP 405 response: {}", exception.getMessage());
    }
  }

  static void internalError(@NotNull HttpResponse httpResponse) {
    try {
      doResponse(httpResponse, 500, "Internal Server Error", null);
    } catch (IOException exception) {
      LOGGER.warn("Could not make HTTP 500 response: {}", exception.getMessage());
    }
  }

  static boolean viaBot(JSONObject message) {
    return message.has("via_bot");
  }

}