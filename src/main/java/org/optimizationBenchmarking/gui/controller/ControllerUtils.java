package org.optimizationBenchmarking.gui.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.optimizationBenchmarking.gui.modules.Delete;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** Some utility functions for the controller. */
public final class ControllerUtils {

  /** the name of the controller bean */
  private static final String CONTROLLER_BEAN_NAME = "controller"; //$NON-NLS-1$

  /** the name of the submit buttons */
  public static final String INPUT_SUBMIT = "submitBtn";//$NON-NLS-1$
  /** the name of the current dir parameter */
  public static final String INPUT_CURRENT_DIR = "current";//$NON-NLS-1$

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
  /** The files array parameter */
  public static final String PARAMETER_FILES = "files[]";//$NON-NLS-1$

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
  /** edit a file with the default editor */
  public static final String COMMAND_EDIT = "edit";//$NON-NLS-1$
  /** edit a text file */
  public static final String COMMAND_EDIT_AS_TEXT = "edit as plain text";//$NON-NLS-1$
  /** edit a dimensions file */
  public static final String COMMAND_EDIT_AS_DIMENSIONS = "edit as dimensions file";//$NON-NLS-1$
  /** edit an instances file */
  public static final String COMMAND_EDIT_AS_INSTANCES = "edit as instances file";//$NON-NLS-1$
  /** edit an experiment file */
  public static final String COMMAND_EDIT_AS_EXPERIMENT = "edit as experiment file";//$NON-NLS-1$
  /** edit a config file */
  public static final String COMMAND_EDIT_AS_CONFIG = "edit as config file";//$NON-NLS-1$
  /** edit a evaluation file */
  public static final String COMMAND_EDIT_AS_EVALUATION = "edit as evaluation file";//$NON-NLS-1$
  /** delete a file or path */
  public static final String COMMAND_DELETE = "delete";//$NON-NLS-1$
  /** create a new text file */
  public static final String COMMAND_NEW_FILE = "new";//$NON-NLS-1$
  /** the command for uploading the selected elements */
  public static final String COMMAND_UPLOAD = "upload";//$NON-NLS-1$
  /** save the stuff */
  public static final String PARAM_SAVE = "save"; //$NON-NLS-1$

  /** the remember action */
  public static final SelectionAction REMEMBER = new SelectionAction(
      ControllerUtils.COMMAND_REMEMBER,//
      "/controller.jsp",//$NON-NLS-1$
      "_self",//$NON-NLS-1$
      "post",//$NON-NLS-1$
      "Remember the selected files. The files will be listed at the bottom of the controller window. Remembering files allows you to pick files from different directories, e.g., for download, without having to choose the complete directories."//$NON-NLS-1$
  );
  /** the forget action */
  public static final SelectionAction FORGET = new SelectionAction(
      ControllerUtils.COMMAND_FORGET,//
      "/controller.jsp",//$NON-NLS-1$
      "_self",//$NON-NLS-1$
      "post",//$NON-NLS-1$
      "Forget the remembered selected files. The files will be removed from the remembered selection."//$NON-NLS-1$
  );
  /** the download action */
  public static final SelectionAction DOWNLOAD = new SelectionAction(
      ControllerUtils.COMMAND_DOWNLOAD,//
      "/download",//$NON-NLS-1$
      "_blank",//$NON-NLS-1$
      "post",//$NON-NLS-1$
      "Download the selected file(s). If one file is selected, it is sent as-is. If multiple files or folders are selected, they will be put into a <code>zip</code> archive."//$NON-NLS-1$
  );
  /** the editor action */
  public static final SelectionAction EDIT = new SelectionAction(
      ControllerUtils.COMMAND_EDIT,//
      "/editor",//$NON-NLS-1$
      "_self",//$NON-NLS-1$
      "get",//$NON-NLS-1$
      "Edit the selected file as with the default editor. For most of the file types our system works with, a default editor is available. This choice will automatically pick the right editor for the selected file (or tell you if there is no suitable editor for it)."//$NON-NLS-1$
  );
  /** the text editor action */
  public static final SelectionAction EDIT_AS_TEXT = new SelectionAction(
      ControllerUtils.COMMAND_EDIT_AS_TEXT,//
      "/textEdit.jsp",//$NON-NLS-1$
      "_self",//$NON-NLS-1$
      "get",//$NON-NLS-1$
      "Edit the selected file as text file. This assumes that you know what you are doing, as syntax and content of the file will not be verified but treated as plain text."//$NON-NLS-1$
  );
  /** the dimensions editor action */
  public static final SelectionAction EDIT_AS_DIMENSIONS = new SelectionAction(
      ControllerUtils.COMMAND_EDIT_AS_DIMENSIONS,//
      "/dimensionsEdit.jsp",//$NON-NLS-1$
      "_self",//$NON-NLS-1$
      "get",//$NON-NLS-1$
      "Edit the selected file as dimensions file. A dimensions file specifies which measurements are taken during experiments. You could, for instance, count the number of objective function evaluations, measure the objective values, and/or measure the runtime. You define this in the dimensions file."//$NON-NLS-1$
  );
  /** the instances editor action */
  public static final SelectionAction EDIT_AS_INSTANCES = new SelectionAction(
      ControllerUtils.COMMAND_EDIT_AS_INSTANCES,//
      "/instancesEdit.jsp",//$NON-NLS-1$
      "_self",//$NON-NLS-1$
      "get",//$NON-NLS-1$
      "Edit the selected file as instances file. An instances file specifies the names and features of benchmark problem instances."//$NON-NLS-1$
  );
  /** the experiment editor action */
  public static final SelectionAction EDIT_AS_EXPERIMENT = new SelectionAction(
      ControllerUtils.COMMAND_EDIT_AS_EXPERIMENT,//
      "/experimentEdit.jsp",//$NON-NLS-1$
      "_self",//$NON-NLS-1$
      "get",//$NON-NLS-1$
      "Edit the selected file as experiment file. An instances file specifies the parameter settings of one specific setup of an algorithm, which is applied to the benchmark instancs."//$NON-NLS-1$
  );
  /** the configuration editor action */
  public static final SelectionAction EDIT_AS_CONFIG = new SelectionAction(
      ControllerUtils.COMMAND_EDIT_AS_CONFIG,//
      "/configEdit.jsp",//$NON-NLS-1$
      "_self",//$NON-NLS-1$
      "get",//$NON-NLS-1$
      "Edit the selected file as configuration file. A configuration file tells the evaluator where to find the input data, where to put the output documents, which format to use for the output documents, and where it can find the list of &quot;things to do&quot;. The files must be XML files following the configuration schema.."//$NON-NLS-1$
  );
  /** the evaluation editor action */
  public static final SelectionAction EDIT_AS_EVALUATION = new SelectionAction(
      ControllerUtils.COMMAND_EDIT_AS_EVALUATION,//
      "/evaluationEdit.jsp",//$NON-NLS-1$
      "_self",//$NON-NLS-1$
      "get",//$NON-NLS-1$
      "Edit the selected file as evaluation file. An evaluation file tells the evaluation what to do with the experiment data, i.e., what stuff you want in your output report."//$NON-NLS-1$
  );
  /** the evaluation editor action */
  public static final SelectionAction EXECUTE_EVALUATOR = new SelectionAction(
      ControllerUtils.COMMAND_EXECUTE_EVALUATOR,//
      "/evaluator.jsp",//$NON-NLS-1$
      "_self",//$NON-NLS-1$
      "get",//$NON-NLS-1$
      "The selected files must be configuration files for evaluation processes. Then evaluation processes will be started. This may take some time to finish. During this time, depending on the <a href='/logLevel.jsp'>log level</a> you set, you will receive information about what's going on. While the process is running, do not close or refresh the page. If you selected multiple configuration files, they will be processed one after the other."//$NON-NLS-1$
  );
  /** the delete action */
  public static final SelectionAction DELETE = new SelectionAction(
      ControllerUtils.COMMAND_DELETE,//
      "/controller.jsp",//$NON-NLS-1$
      "_self",//$NON-NLS-1$
      "get",//$NON-NLS-1$
      "Delete the selected items. If a folder is deleted, all files and folders therein are deleted recursively. Handle with care."//$NON-NLS-1$
  );

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
                request.getParameter(ControllerUtils.INPUT_CURRENT_DIR),//
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
                case COMMAND_DELETE: {
                  Delete.delete(request.getParameterValues(//
                      ControllerUtils.PARAMETER_SELECTION), handle);
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

  /**
   * Put the form selection choice list
   *
   * @param prefix
   *          the form prefix
   * @param page
   *          the destination page
   * @param renderer
   *          the function renderer
   * @param actions
   *          the actions to list
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  public static final void putFormSelection(final String prefix,
      final Page page, final ControllerActionFunctionRenderer renderer,
      final SelectionAction... actions) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    final String update;

    out = page.getOut();
    encoded = page.getHTMLEncoded();
    out.write("<select id=\""); //$NON-NLS-1$
    out.write(prefix);
    out.write("Selection\" name=\""); //$NON-NLS-1$
    out.write(ControllerUtils.PARAMETER_WITH_SELECTED);
    out.write("\" onchange=\""); //$NON-NLS-1$
    update = (page.getFunction(renderer) + '(' + '\'' + prefix + '\'' + ')');
    page.onLoad(update);
    out.write(update);
    out.write("\">"); //$NON-NLS-1$
    for (final SelectionAction action : actions) {
      out.write("<option>"); //$NON-NLS-1$
      encoded.append(action.m_name);
      out.write("</option>"); //$NON-NLS-1$
    }
    out.write("</select>"); //$NON-NLS-1$
    renderer._addActions(actions);
  }

  /** The forbidden constructor */
  private ControllerUtils() {
    ErrorUtils.doNotCall();
  }
}
