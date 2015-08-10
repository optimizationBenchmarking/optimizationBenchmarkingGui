package org.optimizationBenchmarking.gui.utils;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.optimizationBenchmarking.utils.error.ErrorUtils;

/**
 * With this class, we can set some standard stuff in the headers of the
 * {@linkplain javax.servlet.http.HttpServletResponse HTTP responses} of
 * our servlets as well as in the meta-tags of our JSPs.
 */
public final class Header {

  /** the first meta part */
  private static final char[] META_1 = { '<', 'm', 'e', 't', 'a', ' ',
      'h', 't', 't', 'p', '-', 'e', 'q', 'u', 'i', 'v', '=', '"', };
  /** the second meta part */
  private static final char[] META_2 = { '"', ' ', 'c', 'o', 'n', 't',
      'e', 'n', 't', '=', '"' };
  /** the third meta part */
  private static final char[] META_3 = { '"', '/', '>' };

  /** the default header elements */
  private static final String[][] DEFAULT = {//
  { "pragma", "no-cache" },//$NON-NLS-1$//$NON-NLS-2$
      { "expires", "0" },//$NON-NLS-1$//$NON-NLS-2$
      { "Cache-Control", "no-cache, no-store" },//$NON-NLS-1$//$NON-NLS-2$
  };

  /** the html header elements */
  private static final String[][] JSP = {//
  { "viewport",//$NON-NLS-1$
      "width=device-width, initial-scale=1, maximum-scale=1" },//$NON-NLS-1$
      { "X-UA-Compatible", "chrome=1" },//$NON-NLS-1$//$NON-NLS-2$
  };

  /**
   * Set the header
   *
   * @param response
   *          the servlet response
   * @param data
   *          the header data
   */
  private static final void __header(final HttpServletResponse response,
      final String[][] data) {
    for (final String[] element : data) {
      response.setHeader(element[0], element[1]);
    }
  }

  /**
   * Write the header to the body
   *
   * @param out
   *          the output writer
   * @param data
   *          the header data
   * @throws IOException
   *           if I/O fails
   */
  private static final void __header(final Writer out,
      final String[][] data) throws IOException {
    for (final String[] element : data) {
      out.write(Header.META_1);
      out.write(element[0]);
      out.write(Header.META_2);
      out.write(element[1]);
      out.write(Header.META_3);
    }
  }

  /**
   * Set the default header which can be used for any servlet
   *
   * @param response
   *          the servlet response
   */
  public static final void defaultHeader(final HttpServletResponse response) {
    Header.__header(response, Header.DEFAULT);
  }

  /**
   * Write the header which can be used for any servlet to the HTML body
   *
   * @param out
   *          the output writer
   * @throws IOException
   *           if I/O fails
   */
  public static final void defaultHeader(final Writer out)
      throws IOException {
    Header.__header(out, Header.DEFAULT);
  }

  /**
   * Set the default header which can be used for any JSP
   *
   * @param response
   *          the servlet response
   */
  public static final void jspHeader(final HttpServletResponse response) {
    Header.__header(response, Header.DEFAULT);
    Header.__header(response, Header.JSP);
  }

  /**
   * Write the header which can be used for any JSP to the HTML body
   *
   * @param out
   *          the output writer
   * @throws IOException
   *           if I/O fails
   */
  public static final void jspHeader(final Writer out) throws IOException {
    Header.__header(out, Header.DEFAULT);
    Header.__header(out, Header.JSP);
  }

  /** set the header */
  private Header() {
    ErrorUtils.doNotCall();
  }
}
