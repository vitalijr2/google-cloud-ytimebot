package io.github.vitalijr2.ytimebot.youtube;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.regex.Pattern;

/**
 * Combination of properties of snippet and status.
 *
 * @param title         localized or original title
 * @param description   localized or original description
 * @param channelTitle  channel title
 * @param thumbnail     default image: URL, height and width
 * @param preview       standard image: URL, height and width
 * @param uploadStatus  upload status
 * @param privacyStatus privacy status
 * @param errorReason   error reason code, see <em>Error detail</em> on <a
 *                      href="https://developers.google.com/youtube/v3/docs/videos/list#errors">YouTube
 *                      Data API Reference: Video list, Errors</a> and <a
 *                      href="https://developers.google.com/youtube/v3/docs/errors#general-errors">YouTube
 *                      Data API Reference: General errors</a>
 * @see <a href="https://developers.google.com/youtube/v3/docs/videos/list#response">YouTube Data
 * API Reference: Video list, Response</a>
 * @see <a href="https://any-api.com/googleapis_com/youtube/docs/videos/youtube_videos_list">Any
 * API: YouTube Data, Video list</a>
 */
public record VideoData(String title, String description, String channelTitle, Thumbnail thumbnail,
                        Thumbnail preview, UploadStatus uploadStatus, PrivacyStatus privacyStatus,
                        String errorReason) {

  public VideoData(String title, String description, String channelTitle, Thumbnail thumbnail,
      Thumbnail preview, UploadStatus uploadStatus, PrivacyStatus privacyStatus) {
    this(title, description, channelTitle, thumbnail, preview, uploadStatus, privacyStatus, null);
  }

  public VideoData(String errorReason) {
    this(null, null, null, null, null, null, null, errorReason);
  }

  /**
   * Privacy status.
   */
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
        getLogger(PrivacyStatus.class).warn("Unknown status: {}", status);

        return null;
      }
    }

  }

  /**
   * Upload status.
   */
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
        getLogger(UploadStatus.class).warn("Unknown status: {}", status);

        return null;
      }
    }

  }

  /**
   * Thumbnail.
   *
   * @param url image URL
   * @param height height of the thumbnail image, optional
   * @param width width of the thumbnail image, optional
   */
  public record Thumbnail(String url, Integer height, Integer width) {

  }

}
