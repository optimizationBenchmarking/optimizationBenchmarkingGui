package org.optimizationBenchmarking.gui.server;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServlet;

import org.optimizationBenchmarking.utils.text.TextUtils;

/** the internal servlet class */
final class _Servlet {
  /** the path of the servlet */
  final String m_path;

  /** the servlet class */
  final Class<? extends HttpServlet> m_clazz;

  /**
   * the multi-part configuration, if any ({@code null} if the servlet is
   * not multi-part enabled)
   */
  final MultipartConfigElement m_multipart;

  /**
   * Create the servlet holder
   *
   * @param clazz
   *          the servlet class
   * @param path
   *          the path of the servlet
   * @param multipart
   *          the multi-part configuration, if any ({@code null} if the
   *          servlet is not multi-part enabled)
   */
  _Servlet(final Class<? extends HttpServlet> clazz, final String path,
      final MultipartConfigElement multipart) {
    super();

    this.m_path = TextUtils.prepare(path);
    if (this.m_path == null) {
      throw new IllegalArgumentException(//
          "Servlet path cannot be null, empty, or just consist of white spaces, but you specified '" //$NON-NLS-1$
              + path + '\'' + '.');
    }
    if (clazz == null) {
      throw new IllegalArgumentException("Servlet class cannot be null."); //$NON-NLS-1$
    }
    this.m_clazz = clazz;
    this.m_multipart = multipart;
  }
}
