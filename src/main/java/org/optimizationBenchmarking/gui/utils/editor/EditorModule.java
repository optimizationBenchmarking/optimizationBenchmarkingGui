package org.optimizationBenchmarking.gui.utils.editor;

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
import org.optimizationBenchmarking.gui.utils.Encoder;
import org.optimizationBenchmarking.gui.utils.Loaded;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.DefinitionElement;
import org.optimizationBenchmarking.utils.parsers.LooseBooleanParser;
import org.optimizationBenchmarking.utils.parsers.LooseCharParser;
import org.optimizationBenchmarking.utils.parsers.LooseDoubleParser;
import org.optimizationBenchmarking.utils.parsers.LooseLongParser;
import org.optimizationBenchmarking.utils.parsers.NumberParser;
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

  /** the main div suffix */
  static final String DIV_MAIN_SUFFIX = "-main";//$NON-NLS-1$
  /** the inner div suffix */
  static final String DIV_INNER_SUFFIX = "-inner";//$NON-NLS-1$
  /** the up-button suffix */
  static final String BUTTON_UP_SUFFIX = "-up";//$NON-NLS-1$
  /** the down-button suffix */
  static final String BUTTON_DOWN_SUFFIX = "-down";//$NON-NLS-1$

  /** the visibility button suffix */
  static final String BUTTON_VISIBILITY_SUFFIX = "-vis";//$NON-NLS-1$
  /** the visible button value for visible */
  static final char BUTTON_VISIBILITY_VISIBLE = 0x25cf;
  /** the visible button value for hidden */
  static final char BUTTON_VISIBILITY_HIDDEN = 0x25cb;
  /** the button value for the up button */
  private static final char BUTTON_UP_VALUE = 0x21e7;
  /** the button value for the down button */
  private static final char BUTTON_DOWN_VALUE = 0x21e9;
  /** the button value for the delete button */
  private static final String BUTTON_DELETE_VALUE = "delete";//$NON-NLS-1$
  /** the button value for the copy button */
  private static final String BUTTON_COPY_VALUE = "copy";//$NON-NLS-1$

  /** please select an option */
  public static final String PLEASE_SELECT_OPTION = "Please select an option.";//$NON-NLS-1$

  /** the start of the configuration table */
  private static final char[] CONFIG_TABLE_START = { '<', 't', 'a', 'b',
      'l', 'e', ' ', 'c', 'l', 'a', 's', 's', '=', '"', 'i', 'n', 'v',
      'i', 's', 'i', 'b', 'l', 'e', '"', '>', };
  /** the start of the configuration table */
  private static final char[] CONFIG_TABLE_END = { '<', '/', 't', 'a',
      'b', 'l', 'e', '>', };
  /** the start of the configuration table row */
  static final char[] CONFIG_ROW_SPACER = { '<', 't', 'r', ' ', 'c', 'l',
      'a', 's', 's', '=', '"', 'c', 'o', 'n', 'f', 'i', 'g', 'S', 'p',
      'a', 'c', 'e', 'r', '"', '>', '<', 't', 'd', ' ', 'c', 'o', 'l',
      's', 'p', 'a', 'n', '=', '"', '3', '"', ' ', 'c', 'l', 'a', 's',
      's', '=', '"', 'c', 'o', 'n', 'f', 'i', 'g', 'S', 'p', 'a', 'c',
      'e', 'r', '"', '>', '&', 'n', 'b', 's', 'p', ';', '<', '/', 't',
      'd', '>', '<', '/', 't', 'r', '>' };

  /** a string field starter */
  static final char[] STRING_FIELD = { '<', 'i', 'n', 'p', 'u', 't', ' ',
      't', 'y', 'p', 'e', '=', '"', 't', 'e', 'x', 't', '"', ' ', 's',
      'i', 'z', 'e', '=', '"', '6', '0', };

  /** the enabler suffix */
  public static final String BUTTON_ENABLE_SUFFIX = "-enabled"; //$NON-NLS-1$
  /** the prefix parameter */
  public static final String PARAMETER_PREFIX = "prefix"; //$NON-NLS-1$
  /** the field row suffix */
  public static final String TABLE_FIELD_ROW_SUFFIX = "-field"; //$NON-NLS-1$
  /** the desc row suffix */
  public static final String TABLE_DESC_ROW_SUFFIX = "-desc"; //$NON-NLS-1$
  /** the choice row suffix */
  public static final String TABLE_CHOICE_ROW_SUFFIX = "-choice"; //$NON-NLS-1$
  /** the choice row suffix */
  public static final String TABLE_CHOICE_CELL_SUFFIX = "-choiced"; //$NON-NLS-1$

  /** the start of the configuration table row */
  static final char[] CONFIG_ROW_START_1 = { '<', 't', 'r', ' ', 'c', 'l',
      'a', 's', 's', '=', '"', 'c', 'o', 'n', 'f', 'i', 'g', 'F', 'i',
      'e', 'l', 'd', 'R', 'o', 'w', '"', ' ', 'i', 'd', '=', '"' };
  /** the start of the configuration table row */
  static final char[] CONFIG_ROW_START_2 = { '"', '>', '<', 't', 'h', ' ',
      'c', 'l', 'a', 's', 's', '=', '"', 'c', 'o', 'n', 'f', 'i', 'g',
      'N', 'a', 'm', 'e', '"', '>' };
  /** the start of the configuration field */
  static final char[] CONFIG_NAME_END = { '<', '/', 't', 'h', '>', '<',
      't', 'd', ' ', 'c', 'l', 'a', 's', 's', '=', '"', 'c', 'o', 'n',
      'f', 'i', 'g', 'F', 'i', 'e', 'l', 'd', '"', };
  /** the start of the configuration field */
  private static final char[] CONFIG_NAME_COLSPAN = { ' ', 'c', 'o', 'l',
      's', 'p', 'a', 'n', '=', '"', '2', '"', '>' };

  /** the end of the configuration field */
  static final char[] CONFIG_FIELD_END = { '<', '/', 't', 'd', '>', '<',
      't', 'd', ' ', 'c', 'l', 'a', 's', 's', '=', '"', 'c', 'o', 'n',
      'f', 'i', 'g', 'E', 'n', 'a', 'b', 'l', 'e', 'd', '"', '>', '<',
      'i', 'n', 'p', 'u', 't', ' ', 't', 'y', 'p', 'e', '=', '"', 'c',
      'h', 'e', 'c', 'k', 'b', 'o', 'x', '"', ' ', 'n', 'a', 'm', 'e',
      '=', '"', };

  /** the end of the configuration field */
  static final char[] CONFIG_ROW_END = { '"', '/', '>', '<', '/', 't',
      'd', '>', '<', '/', 't', 'r', '>' };

  /** the start of the configuration table row */
  private static final char[] CONFIG_DESC_ROW_1 = { '<', 't', 'r', ' ',
      'c', 'l', 'a', 's', 's', '=', '"', 'c', 'o', 'n', 'f', 'i', 'g',
      'D', 'e', 's', 'c', 'R', 'o', 'w', '"', ' ', 'i', 'd', '=', '"' };

  /** the start of the configuration table row */
  private static final char[] CONFIG_DESC_ROW_2 = { '"', '>', '<', 't',
      'd', ' ', 'c', 'l', 'a', 's', 's', '=', '"', 'c', 'o', 'n', 'f',
      'i', 'g', 'D', 'e', 's', 'c', '"', ' ', 'c', 'o', 'l', 's', 'p',
      'a', 'n', '=', '"', '3', '"', '>', };

  /** the start of the choice row */
  private static final char[] CONFIG_CHOICE_ROW_1 = { '<', '/', 't', 'd',
      '>', '<', '/', 't', 'r', '>', '<', 't', 'r', ' ', 'c', 'l', 'a',
      's', 's', '=', '"', 'c', 'o', 'n', 'f', 'i', 'g', 'C', 'h', 'o',
      'i', 'c', 'e', 'R', 'o', 'w', '"', ' ', 'i', 'd', '=', '"' };

  /** the start of the configuration table row */
  private static final char[] CONFIG_CHOICE_ROW_2 = { '"', '>', '<', 't',
      'd', ' ', 'c', 'l', 'a', 's', 's', '=', '"', 'c', 'o', 'n', 'f',
      'i', 'g', 'C', 'h', 'o', 'i', 'c', 'e', '"', ' ', 'c', 'o', 'l',
      's', 'p', 'a', 'n', '=', '"', '3', '"', ' ', 'i', 'd', '=', '"', };
  /** the start of the configuration table row */
  private static final char[] CONFIG_CHOICE_ROW_3 = { '"', '/', '>', '<',
      '/', 't', 'r', '>' };

  /** new field name */
  static final String NEW_FIELD_NAME = "_new_field_name"; //$NON-NLS-1$
  /** the id of the add-field-row */
  static final String ADD_FIELD_ROW_ID = "_add_field_row"; //$NON-NLS-1$

  /** the current selection */
  public static final String CURRENT_SELECTION = "<em>Current Selection:</em>&nbsp;"; //$NON-NLS-1$

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
    encoded = page.getHTMLEncoded();
    out.write("<input type=\"hidden\" name=\""); //$NON-NLS-1$
    out.write(ControllerUtils.PARAMETER_SELECTION);
    out.write("\" value=\""); //$NON-NLS-1$
    encoded.append(data.getRelativePath());
    out.write("\"/><input type=\"hidden\" name=\"");//$NON-NLS-1$
    out.write(EditorModule.PARAMETER_PREFIX);
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
              EditorModule.PARAMETER_PREFIX));

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

  /**
   * Write the default option selector.
   *
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected final void formSelectionDefaultOption(final Page page)
      throws IOException {
    final JspWriter out;

    out = page.getOut();
    out.write(//
    "<option value=\"\" selected disabled>");//$NON-NLS-1$
    out.write(EditorModule.PLEASE_SELECT_OPTION);
    out.write("</option>");//$NON-NLS-1$
  }

  /**
   * Put a selection drop-down menu
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param choices
   *          the values to choose from
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected final void formPutSelection(final String id,
      final String value,
      final ArrayListView<? extends DefinitionElement> choices,
      final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    final String js;
    String current;

    out = page.getOut();
    encoded = page.getHTMLEncoded();

    out.write("<select name=\"");//$NON-NLS-1$
    encoded.append(id);
    out.write("\" id=\"");//$NON-NLS-1$
    encoded.append(id);

    js = (((((page.getFunction(new _ChoiceUpdateFunctionRenderer(choices)) + '(') + '\'') + //
    Encoder.htmlEncode(id)) + '\'') + ')');
    page.onLoad(js);
    out.write("\" onchange=\"");//$NON-NLS-1$
    out.write(js);
    out.write("\">"); //$NON-NLS-1$

    if (value == null) {
      this.formSelectionDefaultOption(page);
    }

    for (final DefinitionElement cde : choices) {
      current = cde.getName();
      if (current.equalsIgnoreCase(value)) {
        out.write("<option selected>");//$NON-NLS-1$
      } else {
        out.write("<option>");//$NON-NLS-1$
      }
      encoded.append(current);
      out.write("</option>");//$NON-NLS-1$
    }

    out.write("</select>");//$NON-NLS-1$
  }

  /**
   * Put a selection drop-down menu
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param choices
   *          the values to choose from
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  protected final void formPutSelection(final String id,
      final Object value,
      final ArrayListView<? extends DefinitionElement> choices,
      final Page page) throws IOException {
    final String valStr;
    if (value != null) {
      valStr = TextUtils.prepare(String.valueOf(value));
    } else {
      valStr = null;
    }
    this.formPutSelection(id, valStr, choices, page);
  }

  /**
   * Close a field
   *
   * @param out
   *          the JspWriter
   * @param encoded
   *          the encoded writer
   * @param id
   *          the field id
   * @throws IOException
   *           if i/o fails
   */
  private static final void __closeField(final JspWriter out,
      final ITextOutput encoded, final String id) throws IOException {
    out.write("\" name=\"");//$NON-NLS-1$
    encoded.append(id);
    out.write("\" id=\"");//$NON-NLS-1$
    encoded.append(id);
    out.write("\">"); //$NON-NLS-1$
  }

  /**
   * Put an integer field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param min
   *          the minimum value
   * @param max
   *          the maximum value
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected final void formPutInteger(final String id, final Number value,
      final long min, final long max, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getHTMLEncoded();

    out.write("<input type=\"number\" placeholder=\"Please enter number.");//$NON-NLS-1$
    if (min > Long.MIN_VALUE) {
      out.write("\" min=\"");//$NON-NLS-1$
      encoded.append(min);
    }
    if (max < Long.MAX_VALUE) {
      out.write("\" max=\"");//$NON-NLS-1$
      encoded.append(max);
    }

    if (value != null) {
      out.write("\" value=\"");//$NON-NLS-1$
      encoded.append(value.longValue());
    }
    out.write("\" step=\"1");//$NON-NLS-1$
    EditorModule.__closeField(out, encoded, id);
  }

  /**
   * Put an integer field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param numpar
   *          the parser to parse the object with, or {@code null} for
   *          default
   * @param min
   *          the minimum value
   * @param max
   *          the maximum value
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  protected final void formPutInteger(final String id, final Object value,
      final NumberParser<?> numpar, final long min, final long max,
      final Page page) throws IOException {
    Number lng;

    if (value != null) {
      try {
        if (numpar != null) {
          lng = numpar.parseObject(value);
        } else {
          lng = LooseLongParser.INSTANCE.parseObject(value);
        }
      } catch (final Throwable error) {
        lng = null;
      }
    } else {
      lng = null;
    }

    this.formPutInteger(id, lng, min, max, page);
  }

  /**
   * Put an floating point field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param min
   *          the minimum value
   * @param max
   *          the maximum value
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected final void formPutFloat(final String id, final Number value,
      final double min, final double max, final Page page)
      throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    final double d;

    out = page.getOut();
    encoded = page.getHTMLEncoded();

    out.write("<input type=\"number\" placeholder=\"Please enter number.");//$NON-NLS-1$
    if (min > Double.NEGATIVE_INFINITY) {
      out.write("\" min=\"");//$NON-NLS-1$
      encoded.append(min);
    }
    if (max < Double.NEGATIVE_INFINITY) {
      out.write("\" max=\"");//$NON-NLS-1$
      encoded.append(max);
    }

    if (value != null) {
      d = value.doubleValue();
      if (d == d) {
        out.write("\" value=\"");//$NON-NLS-1$
        encoded.append(value.doubleValue());
      }
    }

    out.write("\" step=\"any");//$NON-NLS-1$
    EditorModule.__closeField(out, encoded, id);
  }

  /**
   * Put an floating point field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param numpar
   *          the parser to parse the object with, or {@code null} for
   * @param min
   *          the minimum value
   * @param max
   *          the maximum value
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  protected final void formPutFloat(final String id, final Object value,
      final NumberParser<?> numpar, final double min, final double max,
      final Page page) throws IOException {
    Number dbl;

    if (value != null) {
      try {
        if (numpar != null) {
          dbl = numpar.parseObject(value);
        } else {
          dbl = LooseDoubleParser.INSTANCE.parseObject(value);
        }
      } catch (final Throwable error) {
        dbl = null;
      }
    } else {
      dbl = null;
    }

    this.formPutFloat(id, dbl, min, max, page);
  }

  /**
   * Put a Boolean field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected final void formPutBoolean(final String id,
      final Boolean value, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getHTMLEncoded();
    out.write("<input type=\"checkbox\"");//$NON-NLS-1$
    if ((value != null) && (value.booleanValue())) {
      out.write("\" checked");//$NON-NLS-1$
    }
    EditorModule.__closeField(out, encoded, id);
  }

  /**
   * Put a Boolean field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  protected final void formPutBoolean(final String id, final Object value,
      final Page page) throws IOException {
    Boolean bool;

    if (value != null) {
      try {
        bool = LooseBooleanParser.INSTANCE.parseObject(value);
      } catch (final Throwable error) {
        bool = null;
      }
    } else {
      bool = null;
    }

    this.formPutBoolean(id, bool, page);
  }

  /**
   * Put a character field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected final void formPutChar(final String id, final Character value,
      final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getHTMLEncoded();

    out.write("<input type=\"text\" size=\"1");//$NON-NLS-1$
    if (value != null) {
      out.write("\" value=\"");//$NON-NLS-1$
      encoded.append(value.charValue());
    }
    EditorModule.__closeField(out, encoded, id);
  }

  /**
   * Put a Character field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  protected final void formPutChar(final String id, final Object value,
      final Page page) throws IOException {
    Character chr;

    if (value != null) {
      try {
        chr = LooseCharParser.INSTANCE.parseObject(value);
      } catch (final Throwable error) {
        chr = null;
      }
    } else {
      chr = null;
    }

    this.formPutChar(id, chr, page);
  }

  /**
   * Put a String field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected final void formPutString(final String id, final String value,
      final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getHTMLEncoded();

    out.write(EditorModule.STRING_FIELD);
    if (value != null) {
      out.write("\" value=\"");//$NON-NLS-1$
      encoded.append(value);
    }
    EditorModule.__closeField(out, encoded, id);
  }

  /**
   * Put a String field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  protected final void formPutString(final String id, final Object value,
      final Page page) throws IOException {
    String chr;

    if (value != null) {
      try {
        chr = String.valueOf(value);
      } catch (final Throwable error) {
        chr = null;
      }
    } else {
      chr = null;
    }

    this.formPutString(id, chr, page);
  }

  /**
   * Put a text field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected final void formPutText(final String id, final String value,
      final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getHTMLEncoded();

    out.write("<textarea class=\"editor\" rows=\"4\" cols=\"60\" name=\"");//$NON-NLS-1$
    encoded.append(id);
    out.write("\" id=\"");//$NON-NLS-1$
    encoded.append(id);
    out.write("\" wrap=\"off\"");//$NON-NLS-1$

    if (value != null) {
      out.write('>');
      page.printLines(value, false, false);
      out.write("</textarea>"); //$NON-NLS-1$
    } else {
      out.write('/');
      out.write('>');
    }
  }

  /**
   * Put a text field
   *
   * @param id
   *          the id of the field
   * @param value
   *          the current value, or {@code null} if none is provided
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  protected final void formPutText(final String id, final Object value,
      final Page page) throws IOException {
    String chr;

    if (value != null) {
      try {
        chr = String.valueOf(value);
      } catch (final Throwable error) {
        chr = null;
      }
    } else {
      chr = null;
    }

    this.formPutText(id, chr, page);
  }

  /**
   * Describe the controls
   *
   * @param canMove
   *          can we move components?
   * @param canDelete
   *          can we delete components?
   * @param page
   *          the page
   * @throws IOException
   *           if I/o fails
   */
  @SuppressWarnings("resource")
  protected void formPutComponentButtonHelp(final boolean canMove,
      final boolean canDelete, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    final String componentType;

    out = page.getOut();
    encoded = page.getHTMLEncoded();

    componentType = this.getComponentTypeName();

    out.write('A');
    out.write(' ');
    encoded.append(componentType);
    out.write(" shown/expanded by pressing the <code>");//$NON-NLS-1$
    encoded.append(EditorModule.BUTTON_VISIBILITY_HIDDEN);
    out.write("</code> button and minimized by pressing <code>");//$NON-NLS-1$
    encoded.append(EditorModule.BUTTON_VISIBILITY_VISIBLE);
    out.write("</code>.");//$NON-NLS-1$
    if (canMove) {
      out.write(" The <code>");//$NON-NLS-1$
      encoded.append(EditorModule.BUTTON_UP_VALUE);
      out.write("</code> button moves a ");//$NON-NLS-1$
      encoded.append(componentType);
      out.write(" up, The <code>");//$NON-NLS-1$
      encoded.append(EditorModule.BUTTON_DOWN_VALUE);
      out.write("</code> button moves it down.");//$NON-NLS-1$
    }
    if (canDelete) {
      out.write(" The <code>");//$NON-NLS-1$
      encoded.append(EditorModule.BUTTON_DELETE_VALUE);
      out.write("</code> button deletes a ");//$NON-NLS-1$
      encoded.append(componentType);
      out.write(". There is no undo!"); //$NON-NLS-1$
    }
  }

  /**
   * Get the name of the component type
   *
   * @return the name of the component type
   */
  protected String getComponentTypeName() {
    return "component";//$NON-NLS-1$
  }

  /**
   * Put the help for the component buttons. This should add a paragraph of
   * text, including a call to
   * {@link #formPutComponentButtonHelp(boolean, boolean, Page)}. It should
   * be called before any form is constructed.
   *
   * @param page
   *          the page
   * @throws IOException
   *           if I/O fails
   */
  @SuppressWarnings("resource")
  public void formPutComponentButtonHelp(final Page page)
      throws IOException {
    final JspWriter out;

    out = page.getOut();
    out.write("<p class=\"buttonHelp\">");//$NON-NLS-1$
    this.formPutComponentButtonHelp(true, true, page);
    out.write("</p>");//$NON-NLS-1$
  }

  /**
   * Put the head of a component
   *
   * @param name
   *          the name of the component
   * @param description
   *          the component's description, or {@code null} if none is
   *          defined
   * @param globalPrefix
   *          the overall prefix of the parent form
   * @param componentPrefix
   *          the component prefix
   * @param page
   *          the page to write to
   * @param canMove
   *          can the component be moved up and down?
   * @param canDelete
   *          can the component be deleted?
   * @param canCopy
   *          can we copy the given element?
   * @param initiallyVisible
   *          are the contents of the component initially visible
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected void formPutComponentHead(final String name,
      final String description, final String globalPrefix,
      final String componentPrefix, final boolean canMove,
      final boolean canDelete, final boolean canCopy,
      final boolean initiallyVisible, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getHTMLEncoded();

    out.write("<div class=\"componentMain\" id=\""); //$NON-NLS-1$
    out.write(componentPrefix);
    out.write(EditorModule.DIV_MAIN_SUFFIX);
    out.append("\"><table class=\"invisible\"><tr class=\"moduleHead\"><td class=\"moduleHead\"><h3 style=\"margin-top:0px;margin-bottom:0px\">"); //$NON-NLS-1$
    encoded.append(name);

    out.write("</td><td class=\"moduleCtrls\">");//$NON-NLS-1$

    out.write("<input type=\"button\" id=\"");//$NON-NLS-1$
    encoded.append(componentPrefix);
    out.write(EditorModule.BUTTON_VISIBILITY_SUFFIX);
    out.write("\" class=\"moduleCtrl\" value=\"");//$NON-NLS-1$
    encoded.append(//
        initiallyVisible ? EditorModule.BUTTON_VISIBILITY_VISIBLE
            : EditorModule.BUTTON_VISIBILITY_HIDDEN);
    out.write("\" onclick=\"");//$NON-NLS-1$
    encoded.append(page.getFunction(//
        _ComponentToggleVisibilityFunctionRenderer.INSTANCE));
    out.write('(');
    out.write('\'');
    encoded.append(componentPrefix);
    out.write("')\"/>");//$NON-NLS-1$

    if (canMove) {
      out.write(' ');

      out.write("<input type=\"button\" id=\"");//$NON-NLS-1$
      encoded.append(componentPrefix);
      out.write(EditorModule.BUTTON_UP_SUFFIX);
      out.write("\" class=\"moduleCtrl\" value=\"");//$NON-NLS-1$
      encoded.append(EditorModule.BUTTON_UP_VALUE);
      out.write("\" onclick=\"");//$NON-NLS-1$
      out.write(page.getFunction(_ComponentUpFunctionRenderer.INSTANCE));
      out.write("('");//$NON-NLS-1$
      encoded.append(componentPrefix);
      out.write("')\"/> <input type=\"button\" id=\"");//$NON-NLS-1$
      encoded.append(componentPrefix);
      out.write(EditorModule.BUTTON_DOWN_SUFFIX);
      out.write("\" class=\"moduleCtrl\" value=\"");//$NON-NLS-1$
      encoded.append(EditorModule.BUTTON_DOWN_VALUE);
      out.write("\" onclick=\"");//$NON-NLS-1$
      out.write(page.getFunction(_ComponentDownFunctionRenderer.INSTANCE));
      out.write("('");//$NON-NLS-1$
      encoded.append(componentPrefix);
      out.write("')\"/>");//$NON-NLS-1$

      page.onLoad(page.getFunction(//
          _CheckMoveableFunctionRenderer.INSTANCE) + '(' + '\''
          + Encoder.htmlEncode(componentPrefix) + '\'' + ')' + ';');
    }

    if (canCopy) {
      out.write(' ');
      out.write("<input type=\"button\" value=\"");//$NON-NLS-1$
      out.write(EditorModule.BUTTON_DELETE_VALUE);
      out.write("\" class=\"moduleCtrl\" onclick=\"this.parentElement.parentElement.parentElement.remove()\"/>");//$NON-NLS-1$
    }
    if (canDelete) {
      out.write(' ');
      out.write("<input type=\"button\" value=\"");//$NON-NLS-1$
      out.write(EditorModule.BUTTON_COPY_VALUE);
      out.write("\" class=\"moduleCtrl\" onclick=\"");//$NON-NLS-1$
      out.write(page.getFunction(_ComponentCopyFunctionRenderer.INSTANCE));
      out.write('(');
      out.write('\'');
      encoded.append(globalPrefix);
      out.write('\'');
      out.write(',');
      out.write('\'');
      encoded.append(componentPrefix);
      out.write("')\"/>");//$NON-NLS-1$
    }

    out.write("</td></tr></table></h3><div class=\"componentInner\" id=\"");//$NON-NLS-1$
    encoded.append(componentPrefix);
    out.write(EditorModule.DIV_INNER_SUFFIX);
    out.write('"');
    if (!initiallyVisible) {
      out.write(" style=\"display:none\"");//$NON-NLS-1$
    }
    out.write('>');
    if (description != null) {
      out.write("<p class=\"componentDesc\">");//$NON-NLS-1$
      page.printLines(description, true, true);
      out.write("</p>");//$NON-NLS-1$
    }
  }

  /**
   * Put the foot of a component
   *
   * @param name
   *          the name of the component
   * @param page
   *          the page to write to
   * @param componentPrefix
   *          the component prefix
   * @param canMove
   *          can the component be moved up and down?
   * @param canDelete
   *          can the component be deleted?
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected void formPutComponentFoot(final String name,
      final String componentPrefix, final boolean canMove,
      final boolean canDelete, final Page page) throws IOException {
    final JspWriter out;

    out = page.getOut();
    out.write("</div></div>"); //$NON-NLS-1$
  }

  /**
   * Put an add-field button
   *
   * @param prefix
   *          the form prefix
   * @param name
   *          the button's name
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected final void formPutAddField(final String prefix,
      final String name, final Page page) throws IOException {

    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getHTMLEncoded();

    out.write(//
    "<tr class=\"configAddFieldRow\" id=\""); //$NON-NLS-1$

    encoded.append(Page.fieldNameFromPrefixAndName(prefix,
        EditorModule.ADD_FIELD_ROW_ID));
    out.write(//
    "\"><td class=\"configAddFieldButtonCell\"><input type=\"button\" onclick=\""); //$NON-NLS-1$
    out.write(page.getFunction(_AddFieldFunctionRenderer.INSTANCE));
    out.write('(');
    out.write('\'');
    encoded.append(prefix);
    out.write("')\" value=\"");//$NON-NLS-1$
    encoded.append(name);
    out.write("\"/>&nbsp;</td><td colspan=\"2\" class=\"configAddNameCell\">&nbsp;<input type=\"text\" size=\"60\" id=\"");//$NON-NLS-1$
    encoded.append(//
        Page.fieldNameFromPrefixAndName(prefix,
            EditorModule.NEW_FIELD_NAME));
    out.write(//
    "\"/></td></tr>");//$NON-NLS-1$
  }

  /**
   * begin a new form table
   *
   * @param page
   *          the page to write
   * @throws IOException
   *           if i/o fails
   */
  protected void formTableBegin(final Page page) throws IOException {
    page.getOut().write(EditorModule.CONFIG_TABLE_START);
  }

  /**
   * End a form table
   *
   * @param page
   *          the page to write to
   * @throws IOException
   *           if i/o fails
   */
  protected void formTableEnd(final Page page) throws IOException {
    page.getOut().write(EditorModule.CONFIG_TABLE_END);
  }

  /**
   * Put a spacer into a form
   *
   * @param page
   *          the page to write to
   * @throws IOException
   *           if i/o fails
   */
  protected void formTableSpacer(final Page page) throws IOException {
    page.getOut().write(EditorModule.CONFIG_ROW_SPACER);
  }

  /**
   * Begin a new field row.
   *
   * @param id
   *          the id of the field
   * @param name
   *          the name of the field
   * @param hasEnableButton
   *          will there be an enable button on the right end of the row?
   * @param page
   *          the page to write to
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected void formTableFieldRowBegin(final String id,
      final String name, final boolean hasEnableButton, final Page page)
      throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getHTMLEncoded();
    out.write(EditorModule.CONFIG_ROW_START_1);
    encoded.append(id);
    out.write(EditorModule.TABLE_FIELD_ROW_SUFFIX);
    out.write(EditorModule.CONFIG_ROW_START_2);
    encoded.append(name);
    out.write(EditorModule.CONFIG_NAME_END);
    if (hasEnableButton) {
      out.write('>');
    } else {
      out.write(EditorModule.CONFIG_NAME_COLSPAN);
    }
  }

  /**
   * End a new field row.
   *
   * @param id
   *          the id of the field
   * @param hasEnableButton
   *          will there be an enable button on the right end of the row?
   * @param isEnabled
   *          is the row enabled (only considered when
   *          {@code hasEnableButton==true})
   * @param page
   *          the page to write to
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected void formTableFieldRowEndDescRowBegin(final String id,
      final boolean hasEnableButton, final boolean isEnabled,
      final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    final String js;

    out = page.getOut();
    encoded = page.getHTMLEncoded();
    if (hasEnableButton) {
      out.write(EditorModule.CONFIG_FIELD_END);
      out.write(id);
      out.write(EditorModule.BUTTON_ENABLE_SUFFIX);
      out.write("\" id=\"");//$NON-NLS-1$
      encoded.append(id);
      out.write(EditorModule.BUTTON_ENABLE_SUFFIX);
      out.write('"');
      if (isEnabled) {
        out.write(" checked");//$NON-NLS-1$
      }

      js = (((((page.getFunction(//
          _FieldEnableUpdateFunctionRenderer.INSTANCE) //
      + '(') + '\'') + id) + '\'') + ')');
      page.onLoad(js);
      out.write(" onclick=\"");//$NON-NLS-1$
      encoded.append(js);
      out.write(EditorModule.CONFIG_ROW_END);
    } else {
      out.write(EditorModule.CONFIG_ROW_END, 3,
          (EditorModule.CONFIG_ROW_END.length - 3));
    }
    out.write(EditorModule.CONFIG_DESC_ROW_1);
    encoded.append(id);
    out.write(EditorModule.TABLE_DESC_ROW_SUFFIX);
    out.write(EditorModule.CONFIG_DESC_ROW_2);
  }

  /**
   * End the description row and, potentially, put a choice description
   * field
   *
   * @param id
   *          the id of the current field
   * @param hasChoices
   *          are there choices to be updated?
   * @param page
   *          the page to write to
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("resource")
  protected void formTableDescRowEnd(final String id,
      final boolean hasChoices, final Page page) throws IOException {
    final JspWriter out;

    out = page.getOut();

    if (hasChoices) {
      out.write(EditorModule.CONFIG_CHOICE_ROW_1);
      out.write(id);
      out.write(EditorModule.TABLE_CHOICE_ROW_SUFFIX);
      out.write(EditorModule.CONFIG_CHOICE_ROW_2);
      out.write(id);
      out.write(EditorModule.TABLE_CHOICE_CELL_SUFFIX);
      out.write(EditorModule.CONFIG_CHOICE_ROW_3);
    } else {
      out.write(EditorModule.CONFIG_ROW_END, 3,
          (EditorModule.CONFIG_ROW_END.length - 3));
    }
  }
}