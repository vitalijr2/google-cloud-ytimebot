package io.github.vitalijr2.ytimebot.youtube;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("fast")
class YouTubeTimeServiceFastTest {

  private YouTubeTimeService service;

  @BeforeEach
  void setUp() {
    service = new YouTubeTimeService("http://api.youtube.test", "qwerty-xyz");
  }

  @DisplayName("Wrong arguments")
  @ParameterizedTest
  @CsvSource({"https://google.com/,02:01,Not a YouTube video link",
      "https://youtu.be/W0xGq9KoQNc,12-01,Bad time"})
  void wrongArguments(String locator, String time, String message) {
    // when and then
    assertEquals(message, assertThrows(IllegalArgumentException.class,
        () -> service.combineLinkAndTime(locator, time)).getMessage());
  }

  @DisplayName("Combine link and time")
  @ParameterizedTest(name = "{0} at {1}")
  @CsvSource({"https://youtu.be/W0xGq9KoQNc,02:01,https://youtu.be/W0xGq9KoQNc?t=121",
      "https://www.youtube.com/watch?v=W0xGq9KoQNc,2:01,https://youtu.be/W0xGq9KoQNc?t=121",
      "https://youtu.be/W0xGq9KoQNc,15:22:34,https://youtu.be/W0xGq9KoQNc?t=55354"})
  void combineLinkAndTime(String locator, String time, String expectedResult)
      throws MalformedURLException {
    // when and then
    assertEquals(expectedResult, service.combineLinkAndTime(locator, time));
  }

  @DisplayName("Regular, share, \"in list\" and mobile YouTube video links")
  @ParameterizedTest
  @ValueSource(strings = {"https://www.youtube.com/watch?v=W0xGq9KoQNc",
      "https://youtube.com/watch?v=W0xGq9KoQNc", "https://youtu.be/W0xGq9KoQNc?si=MBpBkDSSNzW40qhg",
      "https://m.youtube.com/watch?v=W0xGq9KoQNc",
      "https://www.youtube.com/watch?v=W0xGq9KoQNc&feature=youtu.be",
      "https://www.youtube.com/watch?v=W0xGq9KoQNc&list=PLajRfiERKR8HLsUZgCePJmVjZvLfbRm6R",
      "https://www.youtube.com/watch?v=iul4XJN01tg&list=PLajRfiERKR8HLsUZgCePJmVjZvLfbRm6R&index=2",
      "https://youtu.be/iul4XJN01tg?si=XpaQ61jUN1NLLUTl",
      "https://www.youtube.com/watch?v=iul4XJN01tg", "https://youtube.com/watch/W0xGq9KoQNc",
      "https://www.youtube.com/watch/W0xGq9KoQNc", "https://youtube.com/v/W0xGq9KoQNc",
      "https://www.youtube.com/v/W0xGq9KoQNc", "https://www.youtube.com/embed/W0xGq9KoQNc",
      "https://www.youtube.com/e/W0xGq9KoQNc", "https://www.youtube.com/live/W0xGq9KoQNc"})
  void isVideoLink(String value) throws MalformedURLException {
    // when and then
    assertTrue(service.isVideoLink(value));
  }

  @DisplayName("Channel, playlist and shorts aren't YouTube video links")
  @ParameterizedTest
  @ValueSource(strings = {"https://www.youtube.com/@Vitalij_R2",
      "https://www.youtube.com/shorts/xP63GfnCpE4",
      "https://youtube.com/shorts/xP63GfnCpE4?si=NGgpfBSiGbP5OmPv",
      "https://m.youtube.com/shorts/xP63GfnCpE4", "https://www.youtube.com/@Vitalij_R2/videos",
      "https://www.youtube.com/channel/UCgY6JNQcANzIXP1sc3hqiRw",
      "https://www.youtube.com/playlist?list=PLajRfiERKR8HLsUZgCePJmVjZvLfbRm6R",
      "https://youtube.com/shorts/iul4XJN01tg?si=kQNyJ86w1CE05ByI",
      "https://www.youtube.com/shorts/iul4XJN01tg"})
  void isNotVideoLink(String value) throws MalformedURLException {
    // when and then
    assertFalse(service.isVideoLink(value));
  }

  @DisplayName("YouTube Music, YouTube Studio, Youtube and Mobile Youtube links")
  @ParameterizedTest
  @ValueSource(strings = {"https://music.youtube.com/channel/UCgY6JNQcANzIXP1sc3hqiRw",
      "https://www.youtube.com/", "https://studio.youtube.com/channel/UCgY6JNQcANzIXP1sc3hqiRw",
      "https://m.youtube.com/", "https://google.com/"})
  void nonYouTubeLinks(String value) throws MalformedURLException {
    // when and then
    assertFalse(service.isVideoLink(value));
  }

  // Just to increase coverage and avoid users' mistypings
  @DisplayName("Bad YouTube links")
  @ParameterizedTest
  @ValueSource(strings = {"https://youtube.com/watch", "https://youtube.com/watch/bad/path",
      "https://youtube.com/v/bad/path", "https://youtube.com/watch/bad$id",
      "https://youtube.com/v/bad$id", "https://youtu.be/bad/path", "https://youtu.be/bad$id",
      "https://m.youtube.com/watch?v=bad$id", "https://m.youtube.com/watch?v=bad=id",
      "https://m.youtube.com/watch?si=MBpBkDSSNzW40qhg"})
  void badYouTubeLinks(String value) throws MalformedURLException {
    // when and then
    assertFalse(service.isVideoLink(value));
  }

  @DisplayName("Not URL")
  @ParameterizedTest
  @EmptySource
  @ValueSource(strings = {"qwerty", "   "})
  void notURL(String value) {
    // when and then
    assertThrows(MalformedURLException.class, () -> service.isVideoLink(value));
  }

  @DisplayName("Time")
  @ParameterizedTest
  @ValueSource(strings = {"2:01", "02:01", "12:01", "5:12:01", "101:12:01"})
  void time(String value) {
    // when and then
    assertTrue(service.isTime(value));
  }

  @DisplayName("Not time")
  @ParameterizedTest
  @ValueSource(strings = {":01", "02 01", "12-01", "312:01", "5:2:01"})
  void notTime(String value) {
    // when and then
    assertFalse(service.isTime(value));
  }

  @DisplayName("Required arguments")
  @ParameterizedTest
  @EmptySource
  @ValueSource(strings = "   ")
  void requiredArguments(String value) {
    // when
    var locatorException = assertThrows(IllegalArgumentException.class,
        () -> new YouTubeTimeService(value, "qwerty-xyz"));
    var keyException = assertThrows(IllegalArgumentException.class,
        () -> new YouTubeTimeService(value));

    // then
    assertAll("Argument exception",
        () -> assertTrue(locatorException.getMessage().contains("locator is empty")),
        () -> assertTrue(keyException.getMessage().contains("key is empty")));
  }

}