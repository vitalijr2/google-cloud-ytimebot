package io.github.vitalijr2.ytimebot.youtube;

public record VideoData(String title, String description, String channelTitle, String thumbnail,
                        String preview, String uploadStatus, String privacyStatus,
                        String errorReason) {

  public VideoData(String title, String description, String channelTitle, String thumbnail,
      String preview, String uploadStatus, String privacyStatus) {
    this(title, description, channelTitle, thumbnail, preview, uploadStatus, privacyStatus, null);
  }

  public VideoData(String errorReason) {
    this(null, null, null, null, null, null, null, errorReason);
  }

}
