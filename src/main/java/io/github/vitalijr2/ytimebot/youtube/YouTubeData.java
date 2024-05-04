package io.github.vitalijr2.ytimebot.youtube;

import feign.Headers;
import feign.QueryMap;
import feign.RequestLine;
import org.json.JSONObject;

@Headers({"Accept: application/json"})
public interface YouTubeData {

  @RequestLine("GET /youtube/v3/videos")
  JSONObject videos(@QueryMap VideoParameters videoParameters);

}
