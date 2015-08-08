package org.optimizationBenchmarking.gui.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.FSElement;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;

/** A java servlet for downloading the remembered selected elements. */
public final class DownloadRemembered extends _FSDownloaderServlet {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** create */
  public DownloadRemembered() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    final Controller controller;
    final ArraySetView<FSElement> selected;

    controller = ControllerUtils.getController(req);
    if (controller != null) {
      try (final Handle handle = controller.createServletHandle()) {
        selected = controller.getSelected();
        if ((selected != null) && (selected.size() > 0)) {
          try {
            this._download(resp, controller.getRootDir(), selected, handle);
            handle.success("Successfully downloaded the selected files.");//$NON-NLS-1$
          } catch (final Throwable error) {
            handle.failure("Failed to download selected files.", error);//$NON-NLS-1$
          }
          return;
        }
        handle.warning("No files have been selected for download."); //$NON-NLS-1$
      }
    }
  }
}
