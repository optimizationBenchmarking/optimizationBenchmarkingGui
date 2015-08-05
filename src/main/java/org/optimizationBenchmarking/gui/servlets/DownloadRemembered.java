package org.optimizationBenchmarking.gui.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.FSElement;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;

/** A java servlet for downloading the remembered selected elements. */
public class DownloadRemembered extends _FSDownloaderServlet {
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

    controller = ((Controller) (req.getSession()
        .getAttribute(Controller.CONTROLLER_BEAN_NAME)));
    if (controller != null) {
      selected = controller.getSelected();
      if (selected != null) {
        this._download(resp, controller.getRootDir(), selected);
        return;
      }
    }
  }
}
