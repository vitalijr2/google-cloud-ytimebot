package io.github.vitalijr2.ytimebot.youtube;

import static java.util.Objects.isNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArray.array;
import static org.hamcrest.object.HasToString.hasToString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.vitalijr2.ytimebot.youtube.VideoParameters.HostLanguage;
import io.github.vitalijr2.ytimebot.youtube.VideoParameters.PartNames;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Tag("fast")
class VideoParametersTest {

  @DisplayName("Snippet and status are default parts")
  @Test
  void defaultParts() {
    // when
    var videoParameters = new VideoParameters("8nKt3LOx7nA", HostLanguage.Polish);

    // then
    assertAll("Video parameters", () -> assertThat(videoParameters.hl(), hasToString("pl")),
        () -> assertEquals("8nKt3LOx7nA", videoParameters.id()),
        () -> assertThat(videoParameters.part(),
            array(hasToString("snippet"), hasToString("status"))));
  }

  @DisplayName("Regular parts")
  @Test
  void regularParts() {
    // when
    var videoParameters = new VideoParameters("8nKt3LOx7nA", HostLanguage.Romanian,
        PartNames.Identificator);

    // then
    assertAll("Video parameters", () -> assertThat(videoParameters.hl(), hasToString("ro")),
        () -> assertEquals("8nKt3LOx7nA", videoParameters.id()),
        () -> assertThat(videoParameters.part(), array(hasToString("id"))));
  }

  @DisplayName("Lookup a host language")
  @ParameterizedTest(name = "{0}: {1}")
  @CsvSource(value = {"en-CA,English", "en-GB,EnglishUK", "en-gb,EnglishUK", "pl,Polish",
      "ro,Romanian", "xx,N/A", "N/A,N/A"}, nullValues = "N/A")
  void lookupHostLanguage(String hl, HostLanguage expectedValue) {
    // when
    var hostLanguage = HostLanguage.lookup(hl);

    // then
    if (isNull(expectedValue)) {
      assertNull(hostLanguage);
    } else {
      assertEquals(expectedValue, hostLanguage);
    }
  }

  @DisplayName("Host languages: 74 + Chinese and Portuguese")
  @Test
  void hostLanguages() {
    // when and then
    assertAll("Host languages", () -> assertEquals(74 + 2, HostLanguage.values().length, "length"),
        () -> Arrays.stream(HostLanguage.values())
            .forEach(value -> assertEquals(value, HostLanguage.lookup(value.hl), "lookupable")));
  }

}