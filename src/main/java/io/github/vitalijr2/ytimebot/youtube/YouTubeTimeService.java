package io.github.vitalijr2.ytimebot.youtube;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import feign.Client;
import feign.Feign;
import feign.Logger.Level;
import feign.Retryer;
import feign.http2client.Http2Client;
import feign.json.JsonDecoder;
import feign.slf4j.Slf4jLogger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.json.JSONPointer;

public class YouTubeTimeService {

  private static final Pattern VIDEO_ID_PATTERN = Pattern.compile("[-A-Za-z0-9_]+");
  private static final Predicate<String> VIDEO_ID_PARAMETER = parameter -> {
    var values = parameter.split("=");

    return 2 == values.length && values[0].equals("v") && VIDEO_ID_PATTERN.matcher(values[1])
        .matches();
  };

  private final YouTubeData youTubeData;

  public YouTubeTimeService(@NotNull String locator, @NotNull String key) {
    this(locator, key, new Http2Client());
  }

  @VisibleForTesting
  YouTubeTimeService(@NotNull String locator, @NotNull String key, Client client) {
    if (key.isBlank()) {
      throw new IllegalArgumentException("YouTube Data API key is empty");
    }
    if (locator.isBlank()) {
      throw new IllegalArgumentException("YouTube Data API locator is empty");
    }
    youTubeData = Feign.builder().client(client).decoder(new JsonDecoder())
        .logger(new Slf4jLogger()).logLevel(Level.BASIC).requestInterceptor(new KeyInterceptor(key))
        .retryer(Retryer.NEVER_RETRY).target(YouTubeData.class, locator);
  }

  private static Optional<String> getVideoId(@NotNull URL locator) {
    var result = Optional.<String>empty();

    if (locator.getHost().endsWith("youtube.com")) {
      if (locator.getPath().equals("/watch") && nonNull(locator.getQuery())) {
        // youtube.com/watch?v={video ID}
        result = Arrays.stream(locator.getQuery().split("&")).filter(VIDEO_ID_PARAMETER)
            .map(parameter -> parameter.split("=")[1]).findAny();
      } else if (locator.getPath().startsWith("/watch/") || locator.getPath().startsWith("/v/")
          || locator.getPath().startsWith("/e/") || locator.getPath().startsWith("/embed/")
          || locator.getPath().startsWith("/live/")) {
        // youtube.com/watch/{video ID}, youtube.com/v/{video ID},
        // youtube.com/e/{video ID}, youtube.com/embed/{video ID}
        // or youtube.com/live/{video ID}
        var values = locator.getPath().split("/");

        if (3 == values.length && VIDEO_ID_PATTERN.matcher(values[2]).matches()) {
          result = Optional.of(values[2]);
        }
      }
    } else if (locator.getHost().endsWith("youtu.be")) {
      // youtu.be/{video ID}
      var values = locator.getPath().split("/");

      if (2 == values.length && VIDEO_ID_PATTERN.matcher(values[1]).matches()) {
        result = Optional.of(values[1]);
      }
    }

    return result;
  }

  @NotNull
  public String combineLinkAndTime(@NotNull String locator, @NotNull String time)
      throws MalformedURLException {
    return combineLinkAndTime(new URL(locator), time);
  }

  @NotNull
  public String combineLinkAndTime(@NotNull URL locator, @NotNull String time)
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

  public VideoData getLinkData(@NotNull String locator, @Nullable String hostLanguage)
      throws IllegalArgumentException, MalformedURLException {
    var videoId = getVideoId(new URL(locator));
    VideoData result;

    if (videoId.isEmpty()) {
      throw new IllegalArgumentException("Not a YouTube video link");
    }

    var videos = youTubeData.videos(new VideoParameters(hostLanguage, videoId.get()));

    if (videos.has("error")) {
      var reasonPointer = new JSONPointer("/error/errors/0/reason");

      result = new VideoData((String) reasonPointer.queryFrom(videos));
    } else {
      var video = videos.getJSONArray("items").getJSONObject(0);

      var snippet = video.getJSONObject("snippet");

      var channelTitle = snippet.getString("channelTitle");
      String description;
      String title;

      if (snippet.has("localized")) {
        var localized = snippet.getJSONObject("localized");

        description = localized.getString("description");
        title = localized.getString("title");
      } else {
        description = snippet.getString("description");
        title = snippet.getString("title");
      }

      var thumbnails = snippet.getJSONObject("thumbnails");
      var preview = thumbnails.getJSONObject("standard").getString("url");
      var thumbnail = thumbnails.getJSONObject("default").getString("url");

      var status = video.getJSONObject("status");
      var privacyStatus = status.getString("privacyStatus");
      var uploadStatus = status.getString("uploadStatus");

      result = new VideoData(title, description, channelTitle, thumbnail, preview, uploadStatus,
          privacyStatus);
    }

    return result;
  }

  public boolean isVideoLink(@NotNull String locator) throws MalformedURLException {
    return isVideoLink(new URL(locator));
  }

  public boolean isVideoLink(@NotNull URL locator) {
    return getVideoId(locator).isPresent();
  }

  public boolean isTime(@NotNull String time) {
    return time.matches("((\\d+:)?\\d)?\\d:\\d\\d");
  }

}
