package io.github.vitalijr2.ytimebot.youtube;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class KeyInterceptor implements RequestInterceptor {

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
