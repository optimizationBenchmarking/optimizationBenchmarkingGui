package org.optimizationBenchmarking.gui.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;

/** Some utility functions for the controller. */
public final class ControllerUtils {

  /** the name of the controller bean */
  private static final String CONTROLLER_BEAN_NAME = "controller"; //$NON-NLS-1$

  /** the name of the submit buttons */
  public static final String INPUT_SUBMIT = "submit";//$NON-NLS-1$

  /** change to an absolute directory path */
  public static final String COMMAND_CD_ABSOLUTE = "cda";//$NON-NLS-1$

  /** change to an relative directory path */
  public static final String COMMAND_CD_RELATIVE = "cd";//$NON-NLS-1$

  /** the path to cd to */
  public static final String PARAMETER_CD_PATH = "cd";//$NON-NLS-1$

  /** The choice what to do with the selected elements */
  public static final String PARAMETER_WITH_SELECTED = "withSelected";//$NON-NLS-1$

  /** The selection array parameter */
  public static final String PARAMETER_SELECTION = "select";//$NON-NLS-1$

  /** the name of the ok button */
  public static final String BUTTON_OK = "OK";//$NON-NLS-1$

  /** the command for remembering the selected elements */
  public static final String COMMAND_REMEMBER = "remember";//$NON-NLS-1$
  /** the command for forgetting the selected elements */
  public static final String COMMAND_FORGET = "forget";//$NON-NLS-1$
  /** the command for downloading the selected elements */
  public static final String COMMAND_DOWNLOAD = "download";//$NON-NLS-1$
  /** execute the evaluator */
  public static final String COMMAND_EXECUTE_EVALUATOR = "evaluate";//$NON-NLS-1$
  /** edit a file */
  public static final String COMMAND_EDIT = "edit as plain text";//$NON-NLS-1$

  /**
   * Get the controller
   *
   * @param request
   *          the request
   * @return the controller instance
   */
  public static final Controller getController(
      final HttpServletRequest request) {
    final HttpSession session;
    final Object controller;

    session = request.getSession();
    if (session == null) {
      return null;
    }
    controller = session
        .getAttribute(ControllerUtils.CONTROLLER_BEAN_NAME);
    if (controller instanceof Controller) {
      return ((Controller) controller);
    }
    return null;
  }

  /**
   * Perform the request given to the controller
   *
   * @param request
   *          the request
   * @param context
   *          the page context
   * @return the controller state
   */
  public static final ControllerState performRequest(
      final HttpServletRequest request, final PageContext context) {
    final Controller controller;
    final String submit;

    controller = ControllerUtils.getController(request);
    if (controller == null) {
      return null;
    }
    try (final Handle handle = controller.createJspHandle(context)) {
      submit = request.getParameter(ControllerUtils.INPUT_SUBMIT);
      if (submit != null) {
        sub: switch (TextUtils.prepare(submit)) {
          case COMMAND_CD_ABSOLUTE: { // cd
            controller.cdAbsolute(handle,
                request.getParameter(ControllerUtils.PARAMETER_CD_PATH));
            break sub;
          }
          case COMMAND_CD_RELATIVE: { // relative cd
            controller.cdRelative(handle,
                request.getParameter(ControllerUtils.PARAMETER_CD_PATH));
            break sub;
          }
          case BUTTON_OK: { // select
            final String selectionValue = request
                .getParameter(ControllerUtils.PARAMETER_WITH_SELECTED);
            if (selectionValue != null) {
              switch (TextUtils.toLowerCase(selectionValue)) {
                case COMMAND_REMEMBER: {
                  controller.select(true, handle,//
                      request.getParameterValues(//
                          ControllerUtils.PARAMETER_SELECTION));
                  break sub;
                }
                case COMMAND_FORGET: {
                  controller.select(false, handle,//
                      request.getParameterValues(//
                          ControllerUtils.PARAMETER_SELECTION));
                  break sub;
                }
                default: {
                  handle.failure("Unknown selection command '" //$NON-NLS-1$
                      + selectionValue + '\'' + '.');
                  break sub;
                }
              }
            }

            handle.warning(ControllerUtils.BUTTON_OK
                + " button pressed, but nothing to do."); //$NON-NLS-1$
            break sub;
          }

          default: {
            handle.unknownSubmit(submit);
          }
        }
      }
      return controller.getState(handle);
    }
  }

  /** The forbidden constructor */
  private ControllerUtils() {
    ErrorUtils.doNotCall();
  }
}
