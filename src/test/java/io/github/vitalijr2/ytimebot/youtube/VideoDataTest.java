package io.github.vitalijr2.ytimebot.youtube;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.MalformedURLException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class VideoDataTest {

  @DisplayName("Good data")
  @Test
  void goodData() throws MalformedURLException {
    // when
    var goodData = new VideoData("test title", "test description", "test channel title",
        "http://thumbnail.test/path.jpg", "http://preview.test/path.jpg", "processed", "public");

    // then
    assertAll("Good video data", () -> assertNull(goodData.errorReason(), "error reason"),
        () -> assertEquals("test channel title", goodData.channelTitle(), "channel title"),
        () -> assertEquals("test description", goodData.description(), "description"),
        () -> assertEquals("http://preview.test/path.jpg", goodData.preview(), "preview"),
        () -> assertEquals("public", goodData.privacyStatus(), "privacy status"),
        () -> assertEquals("http://thumbnail.test/path.jpg", goodData.thumbnail(), "thumbnail"),
        () -> assertEquals("test title", goodData.title(), "title"),
        () -> assertEquals("processed", goodData.uploadStatus(), "upload status"));
  }

  @DisplayName("Error")
  @Test
  void error() {
    // when
    var error = new VideoData("test error reason");

    // then
    assertAll("Good video data", () -> assertNull(error.channelTitle(), "channel title"),
        () -> assertNull(error.description(), "description"),
        () -> assertNull(error.preview(), "preview"),
        () -> assertNull(error.privacyStatus(), "privacy status"),
        () -> assertNull(error.thumbnail(), "thumbnail"), () -> assertNull(error.title(), "title"),
        () -> assertNull(error.uploadStatus(), "upload status"),
        () -> assertEquals("test error reason", error.errorReason(), "error reason"));
  }

}