package org.optimizationBenchmarking.gui.servlets;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.FSElement;
import org.optimizationBenchmarking.gui.controller.Handle;

/** A java servlet for downloading the selected elements. */
public final class DownloadSelected extends _FSDownloaderServlet {
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

    controller = ControllerUtils.getController(req);
    if (controller != null) {
      try (final Handle handle = controller.createServletHandle()) {
        selection = req.getParameterValues(//
            ControllerUtils.PARAMETER_SELECTION);
        if ((selection != null) && (selection.length > 0)) {
          root = controller.getRootDir();
          selected = new ArrayList<>();
          for (final String sel : selection) {//
            chosen = controller.resolve(handle, sel, null);
            if (chosen != null) {
              FSElement.addToCollection(root, root, chosen, selected,//
                  handle);
            }
          }

          if (selected.size() > 0) {
            try {
              this._download(resp, root, selected, handle);
              handle.success(//
                  "Successfully downloaded the selected files.");//$NON-NLS-1$
            } catch (final Throwable error) {
              handle.failure("Failed to download selected files.", error);//$NON-NLS-1$
            }
            return;
          }
        }
        handle.warning("Nothing to download."); //$NON-NLS-1$
      }
    }
  }
}
