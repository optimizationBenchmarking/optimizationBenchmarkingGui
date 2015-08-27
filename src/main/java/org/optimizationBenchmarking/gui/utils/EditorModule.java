package org.optimizationBenchmarking.gui.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.modules.config.ConfigIO;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * This class provides a classical editor module.
 *
 * @param <T>
 *          the data type
 */
public abstract class EditorModule<T> {

  /** only one file at a time, dude */
  private static final String ONLY_ONE = //
  "You can only edit one file at a time, the other specified files are ignored.";//$NON-NLS-1$

  /** successfully loaded the configuration file */
  private static final String LOAD_SUCCESS = //
  "Successfully loaded file "; //$NON-NLS-1$

  /** create the editor module */
  protected EditorModule() {
    super();
  }

  /**
   * Create an empty data element
   *
   * @param handle
   *          the handle
   * @return the empty data element
   */
  protected abstract T createEmpty(final Handle handle);

  /**
   * Load the given file
   *
   * @param file
   *          the file
   * @param handle
   *          the handle
   * @return the result
   * @throws IOException
   *           if i/o fails
   */
  protected abstract T loadFile(final Path file, final Handle handle)
      throws IOException;

  /**
   * Load the data element from the given request
   *
   * @param request
   *          the request
   * @param handle
   *          the handle
   * @return the dumps
   */
  public final Loaded<T> executeAndLoad(final HttpServletRequest request,
      final Handle handle) {
    final String submit;

    submit = request.getParameter(ControllerUtils.INPUT_SUBMIT);
    if (ControllerUtils.BUTTON_OK.equalsIgnoreCase(submit)) {
      return this.__loadFromFile(null,//
          request.getParameterValues(ControllerUtils.PARAMETER_SELECTION),//
          handle);
    }

    if (ControllerUtils.COMMAND_NEW_FILE.equalsIgnoreCase(submit)) {
      return this.__loadFromFile(request.getParameter(//
          ControllerUtils.INPUT_CURRENT_DIR), new String[] { //
          request.getParameter(ControllerUtils.PARAMETER_CD_PATH) },//
          handle);
    }

    if (ControllerUtils.PARAM_SAVE.equalsIgnoreCase(submit)) {
      this.store(handle, request);
      return this.__loadFromFile(null, new String[] {//
          request.getParameter(ControllerUtils.PARAMETER_SELECTION) }, //
          handle);
    }

    handle.unknownSubmit(submit);
    return null;
  }

  /**
   * Load the data element from the given paths relative to the base path.
   *
   * @param basePath
   *          the base path
   * @param relPaths
   *          the paths
   * @param handle
   *          the handle
   * @return the dumps
   */
  private final Loaded<T> __loadFromFile(final String basePath,
      final String[] relPaths, final Handle handle) {
    final Controller controller;
    Loaded<T> result;
    BasicFileAttributes bfa;
    Path root, path;
    String relPath;
    int i;

    if (relPaths == null) {
      handle.failure("The set of paths to load cannot be null."); //$NON-NLS-1$
      return null;
    }

    i = relPaths.length;
    if (i <= 0) {
      handle.failure(//
          "The set of paths to load cannot be empty."); //$NON-NLS-1$
      return null;
    }

    controller = handle.getController();
    root = controller.getRootDir();
    if (basePath != null) {
      root = controller.resolve(handle, basePath, root);
    }

    for (i = 0; i < relPaths.length; i++) {
      relPath = relPaths[i];
      try {
        path = handle.getController().resolve(handle, relPath, root);
        if (path != null) {
          try {
            bfa = Files.readAttributes(path, BasicFileAttributes.class,
                LinkOption.NOFOLLOW_LINKS);
          } catch (final Throwable xyz) {
            bfa = null;
          }

          if ((bfa != null) && bfa.isRegularFile() && (bfa.size() > 0L)) {
            result = new Loaded<>(path, root, this.loadFile(path, handle));

            if (i < (relPaths.length - 1)) {
              handle.warning(EditorModule.ONLY_ONE);
            }
            handle.success(EditorModule.LOAD_SUCCESS
                + result.getRelativePath() + '.');
            return result;
          }

          if ((bfa == null) || (bfa.isRegularFile() && (bfa.size() <= 0L))) {
            if (bfa == null) {
              Files.createFile(path);
              handle
                  .info("File '" + relPath + //$NON-NLS-1$
                      "' did not exist yet (we just created it), so the form will be empty.");//$NON-NLS-1$
            } else {
              handle.info("File '" + relPath + //$NON-NLS-1$
                  "' is empty, so the form will be empty.");//$NON-NLS-1$
            }

            result = new Loaded<>(path, root, this.createEmpty(handle));

            if (i < (relPaths.length - 1)) {
              handle.warning(EditorModule.ONLY_ONE);
            }
            handle.success(EditorModule.LOAD_SUCCESS
                + result.getRelativePath() + '.');
            return result;
          }

          handle.failure("Path '" + relPath + //$NON-NLS-1$
              "' does not identify a file."); //$NON-NLS-1$
        }
      } catch (final Throwable error) {
        handle.failure("Failed to load path '" + //$NON-NLS-1$
            relPath + '\'' + '.', error);
      }
    }

    return null;
  }

  /**
   * Write the form fields corresponding to the given data element
   *
   * @param prefix
   *          the id field prefix
   * @param data
   *          the data element to build the form for
   * @param page
   *          the page to write to
   * @throws IOException
   *           if i/o fails
   */
  public abstract void formPutEditorFields(final String prefix,
      final T data, final Page page) throws IOException;

  /**
   * Put the default fields of the form.
   *
   * @param prefix
   *          the id field prefix
   * @param data
   *          the data element to build the form for
   * @param page
   *          the page to write to
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected void formPutHiddenFields(final String prefix,
      final Loaded<T> data, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getEncoded();
    out.write("<input type=\"hidden\" name=\""); //$NON-NLS-1$
    out.write(ControllerUtils.PARAMETER_SELECTION);
    out.write("\" value=\""); //$NON-NLS-1$
    encoded.append(data.getRelativePath());
    out.write("\"/><input type=\"hidden\" name=\"");//$NON-NLS-1$
    out.write(ConfigIO.PARAMETER_PREFIX);
    out.write("\" value=\"");//$NON-NLS-1$
    encoded.append(prefix);
    out.write("\"/>");//$NON-NLS-1$
  }

  /**
   * Put the buttons of the form.
   *
   * @param prefix
   *          the id field prefix
   * @param data
   *          the data element to build the form for
   * @param page
   *          the page to write to
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected void formPutButtons(final String prefix, final Loaded<T> data,
      final Page page) throws IOException {
    final JspWriter out;
    out = page.getOut();
    out.write("<input type=\"submit\" name=\"");//$NON-NLS-1$
    out.write(ControllerUtils.INPUT_SUBMIT);
    out.write("\" value=\"");//$NON-NLS-1$
    out.write(ControllerUtils.PARAM_SAVE);
    out.write("\"/>&nbsp;<input type=\"submit\" name=\"");//$NON-NLS-1$
    out.write(ControllerUtils.INPUT_SUBMIT);
    out.write("\" value=\"");//$NON-NLS-1$
    out.write(ControllerUtils.COMMAND_DOWNLOAD);
    out.write("\" formtarget=\"_blank\" formmethod=\"post\" formaction=\"/download\"/>"); //$NON-NLS-1$
  }

  /**
   * Put the buttons of the form.
   *
   * @param prefix
   *          the id field prefix
   * @param data
   *          the data element to build the form for
   * @param page
   *          the page to write to
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  public void formFinalize(final String prefix, final Loaded<T> data,
      final Page page) throws IOException {
    final JspWriter out;

    out = page.getOut();
    this.formPutHiddenFields(prefix, data, page);
    out.write("<div class=\"controllerActions\">");//$NON-NLS-1$
    this.formPutButtons(prefix, data, page);
    out.write("</div>"); //$NON-NLS-1$
  }

  /**
   * Load the data from the given request
   *
   * @param prefix
   *          the prefix
   * @param request
   *          the servlet request
   * @param handle
   *          the hadle
   * @return the data
   */
  protected abstract T loadFromRequest(final String prefix,
      final HttpServletRequest request, final Handle handle);

  /**
   * Store the data element to a given path
   *
   * @param data
   *          the data element to be stored
   * @param file
   *          the path to store to
   * @param handle
   *          the handle
   * @throws IOException
   *           if i/o fails
   */
  protected abstract void storeToFile(final T data, final Path file,
      final Handle handle) throws IOException;

  /**
   * Store the contents into a file.
   *
   * @param handle
   *          the handle
   * @param request
   *          the request
   */
  public final void store(final Handle handle,
      final HttpServletRequest request) {
    final Controller controller;
    final String path, pathName, submit;
    final Path realPath;
    final String prefix;
    final T data;

    submit = request.getParameter(ControllerUtils.INPUT_SUBMIT);
    if (submit.equalsIgnoreCase(ControllerUtils.PARAM_SAVE)) {
      path = request.getParameter(ControllerUtils.PARAMETER_SELECTION);
      if (path != null) {
        controller = handle.getController();
        realPath = controller.resolve(handle, path, null);
        if (realPath != null) {

          prefix = TextUtils.prepare(request.getParameter(//
              ConfigIO.PARAMETER_PREFIX));

          if (handle.isLoggable(Level.FINE)) {
            handle.fine("Now loading data for prefix '" + prefix//$NON-NLS-1$
                + "' from request."); //$NON-NLS-1$
          }
          data = this.loadFromRequest(prefix, request, handle);
          pathName = String.valueOf(controller.getRootDir().relativize(
              realPath));
          if (handle.isLoggable(Level.FINE)) {
            handle.fine("Done loading data for prefix '" + prefix//$NON-NLS-1$
                + "' from request, now storing it to path '" + //$NON-NLS-1$
                pathName);
          }

          try {
            this.storeToFile(data, realPath, handle);
            handle.success("Successfully stored configuration file '" + //$NON-NLS-1$
                pathName + '\'' + '.');
          } catch (final Throwable error) {
            handle.failure("Failed to store configuration file '" //$NON-NLS-1$
                + pathName + '\'' + '.', error);
          }
        }
      } else {
        handle.failure("No path provided."); //$NON-NLS-1$
      }
    } else {
      handle.unknownSubmit(submit);
    }
  }
}