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
import org.optimizationBenchmarking.utils.io.MimeTypeDetector;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** A java servlet for viewing an element. */
public class Viewer extends _FSDownloaderServlet {
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
      view = req.getParameter("view"); //$NON-NLS-1$
      if (view != null) {
        root = controller.getRootDir();
        path = PathUtils.normalize(root.resolve(Paths.get(view)));
        if (path.startsWith(root)) {
          if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
            resp.setContentType(MimeTypeDetector.getInstance()
                .getMimeType(path));
            Files.copy(path, resp.getOutputStream());
            return;
          }
        }
      }
    }
  }
}
