package org.optimizationBenchmarking.gui.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.Header;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** A java servlet for downloading the jar archive of this application. */
public final class DownloadJar extends HttpServlet {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** create */
  public DownloadJar() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected final void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    DownloadJar.__process(req, resp);
  }

  /** {@inheritDoc} */
  @Override
  protected final void doPost(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    DownloadJar.__process(req, resp);
  }

  /**
   * Get the path to the jar currently running.
   *
   * @param handle
   *          the handle
   * @return the path to the jar
   */
  public static final Path getAppPath(final Handle handle) {
    URL url;
    String str;
    int index;
    Path path;
    URI uri;

    try {
      url = DownloadJar.class.getProtectionDomain().getCodeSource()
          .getLocation();
    } catch (final Throwable error) {
      url = null;
      handle
          .log(
              Level.WARNING, //
              "Error while trying to obtain the jar location via getProtectionDomain().getCodeSource().getLocation().",//$NON-NLS-1$
              error);
    }
    if (url == null) {
      url = DownloadJar.class.getResource(DownloadJar.class
          .getSimpleName() + ".class"); //$NON-NLS-1$
    }

    if (url == null) {
      handle.failure("Could not obtain the jar location.");//$NON-NLS-1$
      return null;
    }

    str = url.toString();
    index = str.lastIndexOf(".jar");//$NON-NLS-1$
    if (index >= 0) {
      str = str.substring(0, (index + 4));
    }

    try {
      uri = new URI(str).normalize();
    } catch (final Throwable error) {
      handle.failure("Could not deal with jar path '" + //$NON-NLS-1$
          str + '\'' + '.', error);
      return null;
    }

    try {
      path = Paths.get(uri);
    } catch (final Throwable error) {
      handle.failure("Could not convert jar path URI '" + //$NON-NLS-1$
          uri + "' based on path string '" + str//$NON-NLS-1$
          + "' to a path.", error);//$NON-NLS-1$
      return null;
    }

    path = PathUtils.normalize(path);
    if (path == null) {
      handle.failure("Path URI '" + //$NON-NLS-1$
          uri + "' cannot be transformed to a normalized path.");//$NON-NLS-1$
      return null;
    }

    if (!(Files.exists(path, LinkOption.NOFOLLOW_LINKS))) {
      handle.failure("Path to jar '" + //$NON-NLS-1$
          uri + "' does not point to an existing object.");//$NON-NLS-1$
      return null;
    }
    if (!(Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))) {
      handle.failure("Path to jar '" + //$NON-NLS-1$
          uri + "' does not point to a file.");//$NON-NLS-1$
      return null;
    }

    return path;
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
    final Path path;

    find: {
      controller = ControllerUtils.getController(req);
      if (controller == null) {
        break find;
      }

      try (final Handle handle = controller.createServletHandle()) {
        path = DownloadJar.getAppPath(handle);
        if (path != null) {
          resp.setContentType("application/java-archive");//$NON-NLS-1$
          Header.defaultHeader(resp);
          resp.setHeader("Content-Disposition", //$NON-NLS-1$
              "attachment; filename=\"" + //$NON-NLS-1$
                  PathUtils.getName(path) + '"');
          Files.copy(path, resp.getOutputStream());

          handle.success("Jar file successfuly provided.");//$NON-NLS-1$
          return;
        }
      }
    }

    resp.sendRedirect("/controller.jsp");//$NON-NLS-1$
  }
}
