package org.optimizationBenchmarking.gui.servlets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.io.MimeTypeDetector;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** A java servlet for viewing an element. */
public final class Viewer extends _FSDownloaderServlet {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** create */
  public Viewer() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    final Controller controller;
    final String view;
    final Path path, root;

    controller = ((Controller) (req.getSession()
        .getAttribute(Controller.CONTROLLER_BEAN_NAME)));
    if (controller != null) {
      try (final Handle handle = controller.createServletHandle()) {
        view = req.getParameter("view"); //$NON-NLS-1$
        if (view != null) {
          root = controller.getRootDir();
          path = PathUtils.normalize(root.resolve(Paths.get(view)));
          if (path.startsWith(root)) {
            try {
              if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                resp.setContentType(MimeTypeDetector.getInstance()
                    .getMimeType(path));
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
          } else {
            handle.failure("File '" + view + //$NON-NLS-1$
                "' to view is outside of permitted root path.");//$NON-NLS-1$
          }
        } else {
          handle.failure("Cannot view file, since no file specified.");//$NON-NLS-1$
        }
      }
    }
  }
}
