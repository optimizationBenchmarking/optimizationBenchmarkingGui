package org.optimizationBenchmarking.gui.utils;

import java.net.URLEncoder;

import org.optimizationBenchmarking.utils.error.ErrorUtils;

/** Some simple methods for on-the-fly encoding */
public final class Encoder {

  /**
   * Encode a string so that it can be used in the body of a document.
   *
   * @param text
   *          the text to encode
   * @return the encoded text
   */
  public static final String htmlEncode(final String text) {
    return org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer
        .getInstance().transform(text);
  }

  /**
   * Encode a string so that it can be used in an url stored in a HTML
   * document.
   *
   * @param text
   *          the text to encode
   * @return the encoded text
   */
  public static final String urlEncode(final String text) {
    String enc;
    try {
      enc = URLEncoder.encode(text, "UTF-8"); //$NON-NLS-1$
    } catch (final Throwable error) {
      enc = text;
    }
    return Encoder.htmlEncode(enc);
  }

  /** the hidden and forbidden constructor */
  private Encoder() {
    ErrorUtils.doNotCall();
  }
}
