package io.github.vitalijr2.ytimebot.youtube;

import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;

public record VideoData(String title, String description, String channelTitle, String thumbnail,
                        String preview, UploadStatus uploadStatus, PrivacyStatus privacyStatus,
                        String errorReason) {

  public VideoData(String title, String description, String channelTitle, String thumbnail,
      String preview, UploadStatus uploadStatus, PrivacyStatus privacyStatus) {
    this(title, description, channelTitle, thumbnail, preview, uploadStatus, privacyStatus, null);
  }

  public VideoData(String errorReason) {
    this(null, null, null, null, null, null, null, errorReason);
  }

  public enum PrivacyStatus {

    Private, Public, Unlisted, UnlistedNew;

    static PrivacyStatus fromString(String status) {
      if (null == status || status.isBlank()) {
        return null;
      }

      try {
        return PrivacyStatus.valueOf(
            Pattern.compile("\\b.").matcher(status.trim().toLowerCase().replaceAll("_", " "))
                .replaceAll(m -> m.group().toUpperCase()).replaceAll("\\s", ""));
      } catch (IllegalArgumentException exception) {
        LoggerFactory.getLogger(PrivacyStatus.class).warn("Unknown status: {}", status);

        return null;
      }
    }

  }

  public enum UploadStatus {

    Deleted, Failed, Processed, Rejected, Uploaded;

    static UploadStatus fromString(String status) {
      if (null == status || status.isBlank()) {
        return null;
      }

      try {
        return UploadStatus.valueOf(Pattern.compile("^.").matcher(status.trim().toLowerCase())
            .replaceFirst(m -> m.group().toUpperCase()));
      } catch (IllegalArgumentException exception) {
        LoggerFactory.getLogger(UploadStatus.class).warn("Unknown status: {}", status);

        return null;
      }
    }

  }

}
