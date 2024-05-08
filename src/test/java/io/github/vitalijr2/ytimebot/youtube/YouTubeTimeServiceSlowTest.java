package io.github.vitalijr2.ytimebot.youtube;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import feign.mock.HttpMethod;
import feign.mock.MockClient;
import io.github.vitalijr2.ytimebot.youtube.VideoData.PrivacyStatus;
import io.github.vitalijr2.ytimebot.youtube.VideoData.Thumbnail;
import io.github.vitalijr2.ytimebot.youtube.VideoData.UploadStatus;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("slow")
class YouTubeTimeServiceSlowTest {

  private MockClient mockClient;
  private YouTubeTimeService service;

  @BeforeEach
  void setUp() {
    mockClient = new MockClient();
    service = new YouTubeTimeService("http://api.youtube.test", "qwerty-xyz", mockClient);
  }

  @DisplayName("Happy path")
  @ParameterizedTest(name = "{2} - {1}")
  @CsvSource({"src/test/resources/localized_happy_path.json,Localized description,Localized title",
      "src/test/resources/original_happy_path.json,Original description,Original title"})
  void happyPath(String resourcePath, String description, String title) throws IOException {
    // given
    var responseBody = Files.readString(Path.of(resourcePath), ISO_8859_1);
    var preview = new Thumbnail("https://i.ytimg.com/vi/qwerty/sddefault.jpg", 480, 640);
    var thumbnail = new Thumbnail("https://i.ytimg.com/vi/qwerty/default.jpg", 90, 120);

    mockClient.add(HttpMethod.GET, "http://api.youtube.test/youtube/v3/videos?"
        + "hl=en&part=snippet&part=status&id=qwerty&key=qwerty-xyz", 200, responseBody);

    // when
    var videoData = service.getLinkData("https://youtu.be/qwerty", "en");

    // then
    assertAll("Localized data", () -> assertNull(videoData.errorReason(), "error reason"),
        () -> assertEquals("Channel title", videoData.channelTitle(), "channel title"),
        () -> assertEquals(description, videoData.description(), "description"),
        () -> assertEquals(preview, videoData.preview(), "preview"),
        () -> assertEquals(PrivacyStatus.Public, videoData.privacyStatus(), "privacy status"),
        () -> assertEquals(thumbnail, videoData.thumbnail(), "thumbnail"),
        () -> assertEquals(title, videoData.title(), "title"),
        () -> assertEquals(UploadStatus.Processed, videoData.uploadStatus(), "upload status"));
  }

  @DisplayName("Without host language")
  @Test
  void withoutHostLanguage() throws IOException {
    // given
    var responseBody = Files.readString(Path.of("src/test/resources/localized_happy_path.json"),
        ISO_8859_1);
    var preview = new Thumbnail("https://i.ytimg.com/vi/qwerty/sddefault.jpg", 480, 640);
    var thumbnail = new Thumbnail("https://i.ytimg.com/vi/qwerty/default.jpg", 90, 120);

    mockClient.add(HttpMethod.GET, "http://api.youtube.test/youtube/v3/videos?"
        + "part=snippet&part=status&id=qwerty&key=qwerty-xyz", 200, responseBody);

    // when
    var videoData = service.getLinkData("https://youtu.be/qwerty", null);

    // then
    assertAll("Localized data", () -> assertNull(videoData.errorReason(), "error reason"),
        () -> assertEquals("Channel title", videoData.channelTitle(), "channel title"),
        () -> assertEquals("Localized description", videoData.description(), "description"),
        () -> assertEquals(preview, videoData.preview(), "preview"),
        () -> assertEquals(PrivacyStatus.Public, videoData.privacyStatus(), "privacy status"),
        () -> assertEquals(thumbnail, videoData.thumbnail(), "thumbnail"),
        () -> assertEquals("Localized title", videoData.title(), "title"),
        () -> assertEquals(UploadStatus.Processed, videoData.uploadStatus(), "upload status"));
  }

  @DisplayName("Thumbnails without size")
  @Test
  void thumbnailsWithoutSize() throws IOException {
    // given
    var responseBody = Files.readString(Path.of("src/test/resources/thumbnails_without_size.json"),
        ISO_8859_1);
    var preview = new Thumbnail("https://i.ytimg.com/vi/qwerty/sddefault.jpg", null, null);
    var thumbnail = new Thumbnail("https://i.ytimg.com/vi/qwerty/default.jpg", null, null);

    mockClient.add(HttpMethod.GET, "http://api.youtube.test/youtube/v3/videos?"
        + "part=snippet&part=status&id=qwerty&key=qwerty-xyz", 200, responseBody);

    // when
    var videoData = service.getLinkData("https://youtu.be/qwerty", null);

    // then
    assertAll("Localized data", () -> assertNull(videoData.errorReason(), "error reason"),
        () -> assertEquals(preview, videoData.preview(), "preview"),
        () -> assertEquals(thumbnail, videoData.thumbnail(), "thumbnail"));
  }

  @DisplayName("Error")
  @Test
  void error() throws IOException {
    // given
    var responseBody = Files.readString(Path.of("src/test/resources/error.json"), ISO_8859_1);
    mockClient.add(HttpMethod.GET, "http://api.youtube.test/youtube/v3/videos?"
        + "hl=en&part=snippet&part=status&id=qwerty&key=qwerty-xyz", 200, responseBody);

    // when
    var videoData = service.getLinkData("https://youtu.be/qwerty", "en");

    // then
    assertAll("Localized data", () -> assertNull(videoData.channelTitle(), "channel title"),
        () -> assertNull(videoData.description(), "description"),
        () -> assertNull(videoData.preview(), "preview"),
        () -> assertNull(videoData.privacyStatus(), "privacy status"),
        () -> assertNull(videoData.thumbnail(), "thumbnail"),
        () -> assertNull(videoData.title(), "title"),
        () -> assertNull(videoData.uploadStatus(), "upload status"),
        () -> assertEquals("forbidden", videoData.errorReason(), "error reason"));
  }

  @DisplayName("Wrong locator")
  @Test
  void wrongLocator() {
    // when
    var exception = assertThrows(IllegalArgumentException.class,
        () -> service.getLinkData("https://youtu.be/bad$id", "en"));

    // then
    assertEquals("Not a YouTube video link", exception.getMessage());
  }

}