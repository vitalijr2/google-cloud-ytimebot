package io.github.vitalijr2.ytimebot;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.jetbrains.annotations.VisibleForTesting;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YTimeBot implements HttpFunction {

  private static final String FULL_VERSION_STRING;
  private static final String HTTP_BAD_METHOD_RESPONSE;
  private static final int HTTP_BAD_METHOD_CODE = 405;
  private static final String HTTP_BAD_METHOD_MESSAGE = "Method Not Allowed";
  private static final int HTTP_INTERNAL_ERROR_CODE = 500;
  private static final String HTTP_INTERNAL_ERROR_MESSAGE = "Internal Server Error";
  private static final String HTTP_POST_METHOD = "POST";
  private static final String SERVER_HEADER = "Server";

  private final Logger logger = LoggerFactory.getLogger(getClass());

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

  @Override
  public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
    if (HTTP_POST_METHOD.equals(httpRequest.getMethod())) {
      try {
        var jsonTokener = new JSONTokener(httpRequest.getReader());

        processRequest(jsonTokener);
        jsonTokener.close();
      } catch (JSONException exception) {
        logger.warn("Could not parse request body: {}", exception.getMessage());
        httpResponse.setStatusCode(HTTP_INTERNAL_ERROR_CODE, HTTP_INTERNAL_ERROR_MESSAGE);
        httpResponse.appendHeader(SERVER_HEADER, FULL_VERSION_STRING);
      }
    } else {
      logger.warn("Method {} isn't implemented: {}", httpRequest.getMethod(),
          httpRequest.getFirstHeader("X-Forwarded-For").orElse("address not known"));
      httpResponse.setStatusCode(HTTP_BAD_METHOD_CODE, HTTP_BAD_METHOD_MESSAGE);
      httpResponse.appendHeader("Allow", HTTP_POST_METHOD);
      httpResponse.appendHeader(SERVER_HEADER, FULL_VERSION_STRING);
      httpResponse.getWriter().write(HTTP_BAD_METHOD_RESPONSE);
    }
  }

  @VisibleForTesting
  void processRequest(JSONTokener jsonTokener) {
    var jsonObject = new JSONObject(jsonTokener);
  }

}