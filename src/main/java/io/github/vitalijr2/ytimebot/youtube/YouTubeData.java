package io.github.vitalijr2.ytimebot.youtube;

import feign.Headers;
import feign.QueryMap;
import feign.RequestLine;
import org.json.JSONObject;

/**
 * YouTube Data API
 *
 * @see <a href="https://developers.google.com/youtube/v3">YouTube Data API</a>
 */
@Headers({"Accept: application/json"})
interface YouTubeData {

  /**
   * Get video list.
   *
   * @param videoParameters request parameters
   * @return video data
   * @see <a href="https://developers.google.com/youtube/v3/docs/videos/list">YouTube Data API:
   * Video list</a>
   */
  @RequestLine("GET /youtube/v3/videos")
  JSONObject videos(@QueryMap VideoParameters videoParameters);

}
