package org.optimizationBenchmarking.gui.utils;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A class with utility methods to include images.
 */
public final class Image {

  /**
   * Put an image
   * 
   * @param isIcon
   *          is the image an icon (or a normal image)?
   * @param name
   *          the name of the image
   * @param alt
   *          the alt text
   * @param clazz
   *          the class to use
   * @param out
   *          the output destination
   * @param encoded
   *          the html-encoded destination
   * @throws IOException
   *           if I/O fails
   */
  public static final void image(final boolean isIcon, final String name,
      final String alt, final String clazz, final JspWriter out,
      final ITextOutput encoded) throws IOException {

    out.write("<img src=\"/");//$NON-NLS-1$ 
    out.write(isIcon ? "icons/" : "images/"); //$NON-NLS-1$ //$NON-NLS-2$
    out.write(name);
    out.write(".png");//$NON-NLS-1$ 

    if (alt != null) {
      out.write("\" alt=\"");//$NON-NLS-1$ 
      encoded.append(alt);
    }

    if (clazz != null) {
      out.write("\" class=\"");//$NON-NLS-1$ 
      out.write(clazz);
    }

    out.write("\"/>");//$NON-NLS-1$ 
  }

  /** the forbidden constructor */
  private Image() {
    ErrorUtils.doNotCall();
  }

}
