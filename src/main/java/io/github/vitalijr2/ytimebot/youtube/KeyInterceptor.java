package io.github.vitalijr2.ytimebot.youtube;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Add API key to query parameters.
 *
 * @see <a href="https://developers.google.com/youtube/registering_an_application">Obtaining
 * authorization credentials</a>
 */
class KeyInterceptor implements RequestInterceptor {

  private static final String KEY = "key";

  private final String key;

  KeyInterceptor(String key) {
    this.key = key;
  }

  @Override
  public void apply(RequestTemplate template) {
    if (!template.queries().containsKey(KEY)) {
      template.query(KEY, key);
    }
  }

}
