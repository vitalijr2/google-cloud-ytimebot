package io.github.vitalijr2.ytimebot.youtube;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import feign.mock.HttpMethod;
import feign.mock.MockClient;
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
  @ParameterizedTest(name = "{6} - {2}")
  @CsvSource({"src/test/resources/localized_happy_path.json,Channel title,Localized description,"
      + "https://i.ytimg.com/vi/qwerty/sddefault.jpg,public,"
      + "https://i.ytimg.com/vi/qwerty/default.jpg,Localized title,processed",
      "src/test/resources/original_happy_path.json,Channel title,Original description,"
          + "https://i.ytimg.com/vi/qwerty/sddefault.jpg,public,"
          + "https://i.ytimg.com/vi/qwerty/default.jpg,Original title,processed"})
  void happyPath(String resourcePath, String channelTitle, String description, String preview,
      String privacyStatus, String thumbnail, String title, String uploadStatus)
      throws IOException {
    // given
    var responseBody = Files.readString(Path.of(resourcePath), ISO_8859_1);

    mockClient.add(HttpMethod.GET, "http://api.youtube.test/youtube/v3/videos?"
        + "hl=en&part=snippet&part=status&id=qwerty&key=qwerty-xyz", 200, responseBody);

    // when
    var videoData = service.getLinkData("https://youtu.be/qwerty", "en");

    // then
    assertAll("Localized data", () -> assertNull(videoData.errorReason(), "error reason"),
        () -> assertEquals(channelTitle, videoData.channelTitle(), "channel title"),
        () -> assertEquals(description, videoData.description(), "description"),
        () -> assertEquals(preview, videoData.preview(), "preview"),
        () -> assertEquals(privacyStatus, videoData.privacyStatus(), "privacy status"),
        () -> assertEquals(thumbnail, videoData.thumbnail(), "thumbnail"),
        () -> assertEquals(title, videoData.title(), "title"),
        () -> assertEquals(uploadStatus, videoData.uploadStatus(), "upload status"));
  }

  @DisplayName("Without host language")
  @Test
  void withoutHostLanguage()
      throws IOException {
    // given
    var responseBody = Files.readString(Path.of("src/test/resources/localized_happy_path.json"),
        ISO_8859_1);

    mockClient.add(HttpMethod.GET, "http://api.youtube.test/youtube/v3/videos?"
        + "part=snippet&part=status&id=qwerty&key=qwerty-xyz", 200, responseBody);

    // when
    var videoData = service.getLinkData("https://youtu.be/qwerty", null);

    // then
    assertAll("Localized data", () -> assertNull(videoData.errorReason(), "error reason"),
        () -> assertEquals("Channel title", videoData.channelTitle(), "channel title"),
        () -> assertEquals("Localized description", videoData.description(), "description"),
        () -> assertEquals("https://i.ytimg.com/vi/qwerty/sddefault.jpg", videoData.preview(),
            "preview"),
        () -> assertEquals("public", videoData.privacyStatus(), "privacy status"),
        () -> assertEquals("https://i.ytimg.com/vi/qwerty/default.jpg", videoData.thumbnail(),
            "thumbnail"),
        () -> assertEquals("Localized title", videoData.title(), "title"),
        () -> assertEquals("processed", videoData.uploadStatus(), "upload status"));
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