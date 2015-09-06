package org.optimizationBenchmarking.gui.servlets;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.files.EFSElementType;
import org.optimizationBenchmarking.gui.utils.files.FSElement;

/** A java servlet which acts as a gateway for the different editors. */
public final class Editor extends HttpServlet {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** create */
  public Editor() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected final void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    Editor.__process(req, resp);
  }

  /** {@inheritDoc} */
  @Override
  protected final void doPost(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    Editor.__process(req, resp);
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
    final String submit, basePath;
    final String[] params;
    String forward;
    Path root, base;
    FSElement fse;
    EFSElementType type;

    controller = ControllerUtils.getController(req);
    if (controller == null) {
      return;
    }

    try (final Handle handle = controller.createServletHandle()) {

      submit = req.getParameter(ControllerUtils.INPUT_SUBMIT);
      if (ControllerUtils.BUTTON_OK.equalsIgnoreCase(submit)) {
        basePath = null;
        params = req
            .getParameterValues(ControllerUtils.PARAMETER_SELECTION);
      } else {
        if (ControllerUtils.COMMAND_NEW_FILE.equalsIgnoreCase(submit)) {
          basePath = req.getParameter(ControllerUtils.INPUT_CURRENT_DIR);
          params = new String[] { req
              .getParameter(ControllerUtils.PARAMETER_CD_PATH) };
        } else {
          if (ControllerUtils.PARAM_SAVE.equalsIgnoreCase(submit)) {
            basePath = null;
            params = new String[] { req
                .getParameter(ControllerUtils.PARAMETER_SELECTION) };
          } else {
            handle.unknownSubmit(submit);
            Editor.__exit(resp);
            return;
          }
        }
      }

      if (params == null) {
        handle.failure("The set of paths to load cannot be null."); //$NON-NLS-1$
        Editor.__exit(resp);
        return;
      }

      if (params.length <= 0) {
        handle.failure(//
            "The set of paths to load cannot be empty."); //$NON-NLS-1$
        Editor.__exit(resp);
        return;
      }

      root = controller.getRootDir();
      if (basePath != null) {
        base = controller.resolve(handle, basePath, root);
      } else {
        base = root;
      }

      forward = null;

      looper: for (final String str : params) {
        fse = FSElement.fromPath(root, base, base.resolve(str), null,
            handle);
        if (fse != null) {
          switch (type = fse.getType()) {
            case EDI_EXPERIMENT: {
              forward = "/experimentEdit.jsp";//$NON-NLS-1$
              break looper;
            }
            case EDI_DIMENSIONS: {
              forward = "/dimensionsEdit.jsp";//$NON-NLS-1$
              break looper;
            }
            case EDI_INSTANCES: {
              forward = "/instancesEdit.jsp";//$NON-NLS-1$
              break looper;
            }
            case EVALUATION: {
              forward = "/evaluationEdit.jsp";//$NON-NLS-1$
              break looper;
            }
            case CONFIGURATION: {
              forward = "/configEdit.jsp";//$NON-NLS-1$
              break looper;
            }
            case TEXT:
            case CSV:
            case XHTML:
            case TEX: {
              forward = "/textEdit.jsp";//$NON-NLS-1$
              break looper;
            }
            default: {
              handle.warning(//
                  "There is no suitable editor for files of type '"//$NON-NLS-1$
                      + type + "', such as '" + //$NON-NLS-1$
                      str + '\'' + '.');

            }
          }
        }
      }

      if (forward == null) {
        handle.failure("Could not find suitable editor module."); //$NON-NLS-1$
        Editor.__exit(resp);
        return;
      }

      req.getRequestDispatcher(forward).forward(req, resp);
    }
  }

  /**
   * exit to the controller
   *
   * @param resp
   *          the servlet response
   * @throws IOException
   *           if i/o fails
   */
  private static final void __exit(final HttpServletResponse resp)
      throws IOException {
    resp.sendRedirect("/controller.jsp"); //$NON-NLS-1$
  }
}
