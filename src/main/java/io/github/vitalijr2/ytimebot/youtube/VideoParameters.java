package io.github.vitalijr2.ytimebot.youtube;

import static java.util.Objects.isNull;

import java.util.List;

public record VideoParameters(String hl, String id, List<String> part) {

  public VideoParameters {
    if (isNull(part)) {
      part = List.of("snippet", "status");
    }
  }

  public VideoParameters(String hl, String id) {
    this(hl, id, null);
  }

}
