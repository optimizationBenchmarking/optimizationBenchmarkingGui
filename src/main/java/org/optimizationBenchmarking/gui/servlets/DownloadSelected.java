package org.optimizationBenchmarking.gui.servlets;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.FSElement;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** A java servlet for downloading the selected elements. */
public class DownloadSelected extends _FSDownloaderServlet {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** create */
  public DownloadSelected() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    final Controller controller;
    final String[] selection;
    final Path root;
    final ArrayList<FSElement> selected;
    Path chosen;

    controller = ((Controller) (req.getSession()
        .getAttribute(Controller.CONTROLLER_BEAN_NAME)));
    if (controller != null) {
      selection = req.getParameterValues("select"); //$NON-NLS-1$
      if ((selection != null) && (selection.length > 0)) {
        root = controller.getRootDir();
        selected = new ArrayList<>();
        for (final String sel : selection) {//
          chosen = PathUtils.normalize(root.resolve(Paths.get(sel)));
          if (chosen.startsWith(root)) {
            FSElement.addToCollection(root, root, chosen, selected,//
                null);
          }
        }

        if (selected.size() > 0) {
          this._download(resp, root, selected);
          return;
        }
      }
    }
  }
}
