package io.github.vitalijr2.ytimebot;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class YouTubeTimeService {

  private static final Pattern VIDEO_ID_PATTERN = Pattern.compile("[-A-Za-z0-9_]+");
  private static final Predicate<String> VIDEO_ID_PARAMETER = parameter -> {
    var values = parameter.split("=");

    return 2 == values.length && values[0].equals("v") && VIDEO_ID_PATTERN.matcher(values[1])
        .matches();
  };


  @NotNull
  String combineLinkAndTime(@NotNull String locator, @NotNull String time)
      throws MalformedURLException {
    return combineLinkAndTime(new URL(locator), time);
  }

  @NotNull
  String combineLinkAndTime(@NotNull URL locator, @NotNull String time)
      throws IllegalArgumentException {
    if (!isVideoLink(locator)) {
      throw new IllegalArgumentException("Not a YouTube video link");
    }
    if (!isTime(time)) {
      throw new IllegalArgumentException("Bad time");
    }

    var videoId = new AtomicReference<String>();

    if (nonNull(locator.getQuery())) {
      Arrays.stream(locator.getQuery().split("&")).filter(VIDEO_ID_PARAMETER).findAny()
          .ifPresent(parameter -> videoId.set(parameter.split("=")[1]));
    }
    if (isNull(videoId.get())) {
      var parameters = locator.getPath().split("/");

      videoId.set(parameters[parameters.length - 1]);
    }

    var start = 0;
    var timeNumbers = Arrays.stream(time.split(":")).mapToInt(Integer::parseInt).toArray();

    for (int index = timeNumbers.length - 1, multiplier = 1; 0 <= index;
        index--, multiplier *= 60) {
      start += timeNumbers[index] * multiplier;
    }

    return "https://youtu.be/" + videoId + "?t=" + start;
  }

  boolean isVideoLink(@NotNull String locator) throws MalformedURLException {
    return isVideoLink(new URL(locator));
  }

  boolean isVideoLink(@NotNull URL locator) {
    boolean result = false;

    if (locator.getHost().endsWith("youtube.com")) {
      if (locator.getPath().equals("/watch") && !locator.getQuery().isEmpty()) {
        // youtube.com/watch?v={video ID}
        result = Arrays.stream(locator.getQuery().split("&")).anyMatch(VIDEO_ID_PARAMETER);
      } else if (locator.getPath().startsWith("/watch/") || locator.getPath().startsWith("/v/")
          || locator.getPath().startsWith("/e/") || locator.getPath().startsWith("/embed/")
          || locator.getPath().startsWith("/live/")) {
        // youtube.com/watch/{video ID}, youtube.com/v/{video ID},
        // youtube.com/e/{video ID}, youtube.com/embed/{video ID}
        // or youtube.com/live/{video ID}
        var values = locator.getPath().split("/");

        result = 3 == values.length && VIDEO_ID_PATTERN.matcher(values[2]).matches();
      }
    } else if (locator.getHost().endsWith("youtu.be")) {
      // youtu.be/{video ID}
      var values = locator.getPath().split("/");

      result = 2 == values.length && VIDEO_ID_PATTERN.matcher(values[1]).matches();
    }

    return result;
  }

  boolean isTime(@NotNull String time) {
    return time.matches("((\\d+:)?\\d)?\\d:\\d\\d");
  }

}
