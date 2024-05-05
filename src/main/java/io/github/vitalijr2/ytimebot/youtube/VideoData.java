package io.github.vitalijr2.ytimebot.youtube;

import java.net.MalformedURLException;
import java.net.URL;

public record VideoData(String title, String description, String channelTitle, URL thumbnail,
                        String uploadStatus, String privacyStatus, String errorReason) {

  public VideoData(String title, String description, String channelTitle, String thumbnail,
      String uploadStatus, String privacyStatus) throws MalformedURLException {
    this(title, description, channelTitle, new URL(thumbnail), uploadStatus, privacyStatus);
  }

  public VideoData(String title, String description, String channelTitle, URL thumbnail,
      String uploadStatus, String privacyStatus) {
    this(title, description, channelTitle, thumbnail, uploadStatus, privacyStatus, null);
  }

  public VideoData(String errorReason) {
    this(null, null, null, null, null, null, errorReason);
  }

}
