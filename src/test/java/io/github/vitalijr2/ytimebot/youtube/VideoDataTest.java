package io.github.vitalijr2.ytimebot.youtube;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

import io.github.vitalijr2.ytimebot.youtube.VideoData.PrivacyStatus;
import io.github.vitalijr2.ytimebot.youtube.VideoData.Thumbnail;
import io.github.vitalijr2.ytimebot.youtube.VideoData.UploadStatus;
import java.net.MalformedURLException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
@Tag("fast")
class VideoDataTest {

  @Mock
  private Thumbnail preview;
  @Mock
  private Thumbnail thumbnail;

  @DisplayName("Good data")
  @Test
  void goodData() throws MalformedURLException {
    // when
    var goodData = new VideoData("test title", "test description", "test channel title", thumbnail,
        preview, UploadStatus.Processed, PrivacyStatus.Public);

    // then
    assertAll("Good video data", () -> assertNull(goodData.errorReason(), "error reason"),
        () -> assertEquals("test channel title", goodData.channelTitle(), "channel title"),
        () -> assertEquals("test description", goodData.description(), "description"),
        () -> assertEquals(preview, goodData.preview(), "preview"),
        () -> assertEquals(PrivacyStatus.Public, goodData.privacyStatus(), "privacy status"),
        () -> assertEquals(thumbnail, goodData.thumbnail(), "thumbnail"),
        () -> assertEquals("test title", goodData.title(), "title"),
        () -> assertEquals(UploadStatus.Processed, goodData.uploadStatus(), "upload status"));
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

  @DisplayName("Privacy statuses")
  @ParameterizedTest(name = "<{0}>")
  @CsvSource(value = {"N/A,N/A", ",N/A", "   ,N/A", "Unknown,N/A", "  private ,Private",
      "public,Public", "UNLISTED,Unlisted",
      "unlisted_new,UnlistedNew"}, nullValues = "N/A", ignoreLeadingAndTrailingWhitespace = false)
  void privacyStatuses(String caseInsensitiveNameOfStatus, PrivacyStatus expectedStatus) {
    // when
    var privacyStatus = assertDoesNotThrow(
        () -> PrivacyStatus.fromString(caseInsensitiveNameOfStatus));

    // then
    if (null != expectedStatus) {
      assertEquals(expectedStatus, privacyStatus);
    } else {
      assertNull(privacyStatus);
    }

  }

  @DisplayName("Unknown privacy status")
  @Test
  void unknownPrivacyStatus() {
    // given
    var logger = LoggerFactory.getLogger(PrivacyStatus.class);

    clearInvocations(logger);

    // when and then
    assertNull(assertDoesNotThrow(() -> PrivacyStatus.fromString("Unknown")));

    verify(logger).warn("Unknown status: {}", "Unknown");
  }

  @DisplayName("Upload statuses")
  @ParameterizedTest(name = "<{0}>")
  @CsvSource(value = {"N/A,N/A", ",N/A", "   ,N/A", "  deleted ,Deleted", "failed,Failed",
      "PROCESSED,Processed", "ReJeCTeD,Rejected",
      "upLoaded,Uploaded"}, nullValues = "N/A", ignoreLeadingAndTrailingWhitespace = false)
  void uploadStatuses(String caseInsensitiveNameOfStatus, UploadStatus expectedStatus) {
    // when
    var uploadStatus = assertDoesNotThrow(
        () -> UploadStatus.fromString(caseInsensitiveNameOfStatus));

    // then
    if (null != expectedStatus) {
      assertEquals(expectedStatus, uploadStatus);
    } else {
      assertNull(uploadStatus);
    }
  }

  @DisplayName("Unknown upload status")
  @Test
  void unknownUploadStatus() {
    // given
    var logger = LoggerFactory.getLogger(UploadStatus.class);

    clearInvocations(logger);

    // when and then
    assertNull(assertDoesNotThrow(() -> UploadStatus.fromString("Unknown")));

    verify(logger).warn("Unknown status: {}", "Unknown");
  }

}