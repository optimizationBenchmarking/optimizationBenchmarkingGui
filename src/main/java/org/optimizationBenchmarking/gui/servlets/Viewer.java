package org.optimizationBenchmarking.gui.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.Header;
import org.optimizationBenchmarking.utils.document.impl.xhtml10.XHTML;
import org.optimizationBenchmarking.utils.io.FileTypeRegistry;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** A java servlet for viewing an element. */
public final class Viewer extends HttpServlet {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** the XHTML mime types */
  private static final String[] XHTML_MIME_TYPES = new String[] {//
  XHTML.XHTML_1_0.getMIMEType(), "application/xhtml+xml"//$NON-NLS-1$
  };
  /** the HTML mime types */
  private static final String[] HTML_MIME_TYPES = new String[] {//
  "text/html" //$NON-NLS-1$
  };

  /** create */
  public Viewer() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected final void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    Viewer.__process(req, resp);
  }

  /** {@inheritDoc} */
  @Override
  protected final void doPost(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    Viewer.__process(req, resp);
  }

  /**
   * check if a given type is a mime type
   *
   * @param mime
   *          the mime type
   * @param types
   *          the type list
   * @return {@code true} if it is a HTML mime type, {@code false}
   *         otherwise
   */
  private static final boolean __is(final String mime, final String[] types) {
    if (mime == null) {
      return false;
    }
    for (final String cmp : types) {
      if (cmp.equalsIgnoreCase(mime)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Resolve the given url
   *
   * @param base
   *          the base url
   * @param current
   *          the current url
   * @param handle
   *          the handle
   * @return the resolved one, or {@code null} if resolution is not
   *         necessary
   */
  private static final String __resolve(final String base,
      final String current, final Handle handle) {
    URI currentURI;
    String currentURL;
    int length;

    if ((current == null) || (current.length() <= 0)) {
      return null;
    }

    if (current.charAt(0) == '#') {
      return null;
    }
    if ((handle != null) && (handle.isLoggable(Level.FINEST))) {
      handle.finest("Now resolving URL '" + current + '\'' + '.'); //$NON-NLS-1$
    }

    try {
      currentURI = new URI(current).normalize();
      if (!(currentURI.isAbsolute())) {
        currentURL = currentURI.toString();
        if ((currentURL == null) || ((length = currentURL.length()) <= 0)//
            || ((length == 1) && (currentURL.charAt(length) == '/'))) {
          return base;
        }

        currentURL = (base + currentURL
            .substring((currentURL.charAt(0) == '/') ? 1 : 0));
        return new URI(currentURL).normalize().toString();
      }

    } catch (final Throwable error) {
      if ((handle != null) && (handle.isLoggable(Level.WARNING))) {
        handle.log(Level.WARNING,//
            ("Error when resolving URL '" + current + '\'' + '.'), //$NON-NLS-1$
            error);
      }
    }
    return null;
  }

  /**
   * Serve a HTML file
   *
   * @param input
   *          the input path
   * @param root
   *          the root path
   * @param view
   *          the file to view
   * @param os
   *          the output stream
   * @param isXHTML
   *          is the stuff XHTML?
   * @param handle
   *          the handle
   * @throws IOException
   *           if i/o fails
   */
  private static final void __serveHTML(final Path input,
      final String root, final String view, final boolean isXHTML,
      final OutputStream os, final Handle handle) throws IOException {
    final int rootStart;
    final OutputSettings output;
    int rootLength;
    String base, currentURL;
    Document doc;

    if ((handle != null) && (handle.isLoggable(Level.FINE))) {
      handle.fine("Now serving path '" + view + //$NON-NLS-1$
          (isXHTML ? "' as XHTML." : "' as HTML.")); //$NON-NLS-1$//$NON-NLS-2$
    }

    // find the base url
    base = "/viewer?view=";//$NON-NLS-1$
    if ((root != null) && ((rootLength = root.length()) > 0)) {
      if (root.charAt(0) == '/') {
        rootStart = 1;
      } else {
        rootStart = 0;
      }
      if (rootStart < rootLength) {
        base += root.substring(rootStart);
        if (root.charAt(rootLength - 1) != '/') {
          base += '/';
        }
      }
    }

    if ((handle != null) && (handle.isLoggable(Level.FINER))) {
      handle.finer("Now loading '" + view + //$NON-NLS-1$
          "' using via Jsoup.");//$NON-NLS-1$
    }

    // load the data
    try (final InputStream is = PathUtils.openInputStream(input)) {
      doc = Jsoup.parse(is, "UTF-8", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if ((handle != null) && (handle.isLoggable(Level.FINER))) {
      handle.finer("Done loading '" + view + //$NON-NLS-1$
          "' using via Jsoup, now resolving URLs against base '"//$NON-NLS-1$
          + base + '\'' + '.');
    }

    // resolve the urls in '<a>' and '<link>' tags
    for (final String tag : new String[] { "a", "link" }) {//$NON-NLS-1$ //$NON-NLS-2$
      for (final Element element : doc.select(tag)) {
        currentURL = Viewer.__resolve(base, element.attr("href"),//$NON-NLS-1$
            handle);
        if (currentURL != null) {
          element.attr("href", currentURL);//$NON-NLS-1$
        }
      }
    }

    // resolve the urls in '<img>' and '<script>'
    for (final String tag : new String[] { "img", "script" }) {//$NON-NLS-1$ //$NON-NLS-2$
      for (final Element element : doc.select(tag)) {
        currentURL = Viewer.__resolve(base, element.attr("src"),//$NON-NLS-1$
            handle);
        if (currentURL != null) {
          element.attr("src", currentURL);//$NON-NLS-1$
        }
      }
    }

    // resolve background urls in '<body>' tags
    for (final Element element : doc.select("body")) {//$NON-NLS-1$
      currentURL = Viewer.__resolve(base, element.attr("background"),//$NON-NLS-1$
          handle);
      if (currentURL != null) {
        element.attr("background", currentURL);//$NON-NLS-1$
      }
    }

    if ((handle != null) && (handle.isLoggable(Level.FINER))) {
      handle.finer("Done resolving URLs against base '"//$NON-NLS-1$
          + base + "' for document '" + view + //$NON-NLS-1$
          "', now serializing the modified document using via Jsoup.");//$NON-NLS-1$
    }

    output = doc.outputSettings();

    output.syntax(isXHTML ? Syntax.xml : Syntax.html);
    output.escapeMode(EscapeMode.xhtml);
    output.indentAmount(0);
    output.prettyPrint(false);

    try (final OutputStreamWriter osw = new OutputStreamWriter(os)) {
      osw.write(doc.outerHtml());
    }
  }

  /**
   * Process a request
   *
   * @param req
   *          the request
   * @param resp
   *          the response
   * @throws ServletException
   *           the error
   * @throws IOException
   *           the io error
   */
  private static final void __process(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    final Controller controller;
    final String view;
    final Path path;
    final String type;
    final boolean isHTML, isXHTML;
    String root;

    controller = ControllerUtils.getController(req);
    if (controller != null) {
      try (final Handle handle = controller.createServletHandle()) {
        view = req.getParameter("view"); //$NON-NLS-1$
        if (view != null) {
          path = controller.resolve(handle, view, null);
          if (path != null) {
            try {
              if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                type = FileTypeRegistry.getInstance().getMimeTypeForPath(
                    path);
                resp.setContentType(type);
                Header.defaultHeader(resp);

                isHTML = Viewer.__is(type, Viewer.HTML_MIME_TYPES);
                isXHTML = Viewer.__is(type, Viewer.XHTML_MIME_TYPES);

                if (isHTML || isXHTML) {
                  if ((handle != null) && (handle.isLoggable(Level.INFO))) {
                    handle.info("Parsing and refining (X)HTML document."); //$NON-NLS-1$
                  }

                  root = controller.getRootDir().relativize(//
                      path.getParent()).toString();
                  if (File.separatorChar == '\\') {
                    root = root.replace(File.separatorChar, '/');
                  }
                  Viewer.__serveHTML(path, root, view, //
                      isXHTML, resp.getOutputStream(), handle);
                } else {
                  Files.copy(path, resp.getOutputStream());
                }

                handle.success("Successfully provided file '" + //$NON-NLS-1$
                    view + '\'' + '.');
                return;
              }
              handle.failure("Path '" + view + //$NON-NLS-1$
                  "' is not a file.");//$NON-NLS-1$
            } catch (final Throwable error) {
              handle.failure("Error while providing file '" + //$NON-NLS-1$
                  view + '\'' + '.', error);
            }
          }
        } else {
          handle.failure("Cannot view file, since no file specified.");//$NON-NLS-1$
        }
      }
    }

    resp.sendRedirect("/controller.jsp");//$NON-NLS-1$
  }
}
