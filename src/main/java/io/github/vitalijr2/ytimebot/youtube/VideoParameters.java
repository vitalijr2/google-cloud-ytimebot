package io.github.vitalijr2.ytimebot.youtube;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Request parameters: hl, video id, parts.
 *
 * @param id   video ID
 * @param hl   host language
 * @param part list of parts, defaults to {@code snippet} and {@code status}
 * @see <a href="https://developers.google.com/youtube/v3/docs/videos/list#parameters">YouTube Data
 * API Reference: Video list, Parameters</a>
 * @see <a href="https://developers.google.com/youtube/v3/docs/videos/list">YouTube Data API
 * Reference: Video list</a>
 */
public record VideoParameters(String id, HostLanguage hl, PartNames... part) {

  public VideoParameters {
    if (0 == part.length) {
      part = new PartNames[]{PartNames.Snippet, PartNames.Status};
    }
  }

  /**
   * Google host language {@code hl}.
   *
   * @see <a
   * href="https://developers.google.com/custom-search/docs/xml_results_appendices#interfaceLanguages">
   * Programmable Search Engine Guide: Supported Interface Languages</a>
   */
  public enum HostLanguage {

    Afrikaans("af"), Albanian("sq"), Amharic("am"), Arabic("ar"), Armenian("hy"), Azerbaijani(
        "az"), Bengali("bn"), Bulgarian("bg"), Burmese("my"), Catalan("ca"), Chinese(
        "zh"), ChineseSimplified("zh-CN"), ChineseTraditional("zh-TW"), Croatian("hr"), Czech(
        "cs"), Danish("da"), Dutch("nl"), English("en"), EnglishUK("en-GB"), Estonian(
        "et"), Filipino("fil"), Finnish("fi"), French("fr"), FrenchCanadian("fr-CA"), Georgian(
        "ka"), German("de"), Greek("el"), Gujarati("gu"), Hebrew("iw"), Hindi("hi"), Hungarian(
        "hu"), Icelandic("is"), Indonesian("id"), Italian("it"), Japanese("ja"), Kannada(
        "kn"), Kazakh("kk"), Khmer("km"), Korean("ko"), Kyrgyz("ky"), Laothian("lo"), Latvian(
        "lv"), Lithuanian("lt"), Macedonian("mk"), Malay("ms"), Malayam("ml"), Marathi(
        "mr"), Mongolian("mn"), Nepali("Nepali"), Norwegian("no"), Persian("fa"), Polish(
        "pl"), Portuguese("pt"), PortugueseBrazil("pt-BR"), PortuguesePortugal("pt-PT"), Punjabi(
        "pa"), Romanian("ro"), Russian("ru"), Serbian("sr"), SerbianLatin("sr-Latn"), Sinhalese(
        "si"), Slovak("sk"), Slovenian("sl"), Spanish("es"), SpanishLatinAmerica("es-419"), Swahili(
        "sw"), Swedish("sv"), Tamil("ta"), Telugu("te"), Thai("th"), Turkish("tr"), Ukrainian(
        "uk"), Urdu("ur"), Uzbek("uz"), Vietnamese("vi"), Welsh("cy");


    private static final Map<String, HostLanguage> REVERSE_LOOKUP_MAP = Arrays.stream(values())
        .collect(Collectors.toMap(value -> value.toString().toLowerCase(), Function.identity()));
    final String hl;

    HostLanguage(String hl) {
      this.hl = hl;
    }

    static HostLanguage lookup(String hl) {
      HostLanguage result = null;

      if (null != hl) {
        result = REVERSE_LOOKUP_MAP.get(hl.trim().toLowerCase());

        if (null == result && hl.contains("-")) {
          result = lookup(hl.split("-")[0]);
        }
      }

      return result;
    }

    @Override
    public String toString() {
      return hl;
    }

  }

  /**
   * Part names
   *
   * @see <a href="https://developers.google.com/youtube/v3/docs/videos/list#parameters">YouTube
   * Data API Reference: Video list, Parameters</a>
   */
  public enum PartNames {

    ContentDetails("contentDetails"), FileDetails("fileDetails"), Identifier(
        "id"), LiveStreamingDetails("liveStreamingDetails"), Localizations("localizations"), Player(
        "player"), ProcessingDetails("processingDetails"), RecordingDetails(
        "recordingDetails"), Snippet("snippet"), Statistics("statistics"), Status(
        "status"), Suggestions("suggestions"), TopicDetails("topicDetails");

    final String part;

    PartNames(String part) {
      this.part = part;
    }

    @Override
    public String toString() {
      return part;
    }

  }

}
