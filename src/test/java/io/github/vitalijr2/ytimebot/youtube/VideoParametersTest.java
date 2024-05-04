package io.github.vitalijr2.ytimebot.youtube;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fast")
class VideoParametersTest {

  @DisplayName("Snippet and status are default parts")
  @Test
  void defaultParts() {
    // when
    var videoParameters = new VideoParameters("pl", "8nKt3LOx7nA");

    // then
    assertAll("Video parameters", () -> assertEquals("pl", videoParameters.hl()),
        () -> assertEquals("8nKt3LOx7nA", videoParameters.id()),
        () -> assertThat(videoParameters.part(), containsInAnyOrder("status", "snippet")));
  }

}