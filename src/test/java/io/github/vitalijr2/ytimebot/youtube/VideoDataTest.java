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
        "http://thumbnail.test/path.jpg", "processed", "public");

    // then
    assertAll("Good video data", () -> assertEquals("test title", goodData.title(), "title"),
        () -> assertEquals("test description", goodData.description(), "description"),
        () -> assertEquals("test channel title", goodData.channelTitle(), "channel title"),
        () -> assertEquals("http://thumbnail.test/path.jpg", goodData.thumbnail().toString(),
            "thumbnail"),
        () -> assertEquals("processed", goodData.uploadStatus(), "upload status"),
        () -> assertEquals("public", goodData.privacyStatus(), "privacy status"),
        () -> assertNull(goodData.errorReason(), "error reason"));
  }

  @DisplayName("Error")
  @Test
  void error() {
    // when
    var error = new VideoData("test error reason");

    // then
    assertAll("Good video data", () -> assertNull(error.title(), "title"),
        () -> assertNull(error.description(), "description"),
        () -> assertNull(error.channelTitle(), "channel title"),
        () -> assertNull(error.thumbnail(), "thumbnail"),
        () -> assertNull(error.uploadStatus(), "upload status"),
        () -> assertNull(error.privacyStatus(), "privacy status"),
        () -> assertEquals("test error reason", error.errorReason(), "error reason"));
  }

}