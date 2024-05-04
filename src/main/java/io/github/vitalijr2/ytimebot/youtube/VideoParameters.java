package io.github.vitalijr2.ytimebot.youtube;

import java.util.List;

public record VideoParameters(String hl, String id, List<String> part) {

  public VideoParameters(String hl, String id) {
    this(hl, id, List.of("snippet", "status"));
  }

}
