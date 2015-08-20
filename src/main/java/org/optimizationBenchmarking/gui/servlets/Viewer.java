package org.optimizationBenchmarking.gui.servlets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.Header;
import org.optimizationBenchmarking.utils.io.MimeTypeDetector;

/** A java servlet for viewing an element. */
public final class Viewer extends HttpServlet {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

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

    controller = ControllerUtils.getController(req);
    if (controller != null) {
      try (final Handle handle = controller.createServletHandle()) {
        view = req.getParameter("view"); //$NON-NLS-1$
        if (view != null) {
          path = controller.resolve(handle, view, null);
          if (path != null) {
            try {
              if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                resp.setContentType(MimeTypeDetector.getInstance()
                    .getMimeType(path));
                Header.defaultHeader(resp);
                Files.copy(path, resp.getOutputStream());
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
