package org.optimizationBenchmarking.gui.utils;

import java.net.URLEncoder;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.text.textOutput.AbstractTextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

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

  /**
   * HTML-encode a given text output device
   *
   * @param out
   *          the output device
   * @return the encoded device
   */
  public static final ITextOutput htmlEncode(final ITextOutput out) {
    return XMLCharTransformer.getInstance().transform(out);
  }

  /**
   * HTML-encode a given jsp writer
   *
   * @param out
   *          the output device
   * @return the encoded writer
   */
  public static final ITextOutput htmlEncode(final JspWriter out) {
    return Encoder.htmlEncode(AbstractTextOutput.wrap(out));
  }

  /** the hidden and forbidden constructor */
  private Encoder() {
    ErrorUtils.doNotCall();
  }
}
