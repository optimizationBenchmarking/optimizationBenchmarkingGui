package org.optimizationBenchmarking.gui.modules.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.Evaluator;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.EditorModule;
import org.optimizationBenchmarking.gui.utils.Encoder;
import org.optimizationBenchmarking.gui.utils.Loaded;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.utils.config.ConfigurationBuilder;
import org.optimizationBenchmarking.utils.config.ConfigurationXMLInput;
import org.optimizationBenchmarking.utils.config.ConfigurationXMLOutput;
import org.optimizationBenchmarking.utils.config.Definition;
import org.optimizationBenchmarking.utils.config.DefinitionElement;
import org.optimizationBenchmarking.utils.config.DefinitionXMLInput;
import org.optimizationBenchmarking.utils.config.Dump;
import org.optimizationBenchmarking.utils.config.InstanceParameter;
import org.optimizationBenchmarking.utils.config.Parameter;
import org.optimizationBenchmarking.utils.parsers.CharParser;
import org.optimizationBenchmarking.utils.parsers.LooseBooleanParser;
import org.optimizationBenchmarking.utils.parsers.LooseDoubleParser;
import org.optimizationBenchmarking.utils.parsers.LooseLongParser;
import org.optimizationBenchmarking.utils.parsers.NumberParser;
import org.optimizationBenchmarking.utils.parsers.Parser;
import org.optimizationBenchmarking.utils.reflection.EPrimitiveType;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * This editor module allows us to generate a dynamic form for editing
 * evaluator configuration files.
 */
public final class ConfigIO extends EditorModule<Dump> {

  /** the enabler suffix */
  public static final String ENABLER_SUFFIX = "-enabled"; //$NON-NLS-1$

  /** the prefix parameter */
  public static final String PARAMETER_PREFIX = "prefix"; //$NON-NLS-1$
  /** the field row suffix */
  public static final String SUFFIX_FIELD_ROW = "-field"; //$NON-NLS-1$
  /** the desc row suffix */
  public static final String SUFFIX_DESC_ROW = "-desc"; //$NON-NLS-1$
  /** the choice row suffix */
  public static final String SUFFIX_CHOICE_ROW = "-choice"; //$NON-NLS-1$
  /** the choice row suffix */
  public static final String SUFFIX_CHOICE_CELL = "-choicetd"; //$NON-NLS-1$

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
      'f', 'i', 'g', 'F', 'i', 'e', 'l', 'd', '"', '>', };

  /** the end of the configuration field */
  static final char[] CONFIG_FIELD_END = { '<', '/', 't', 'd', '>', '<',
      't', 'd', ' ', 'c', 'l', 'a', 's', 's', '=', '"', 'c', 'o', 'n',
      'f', 'i', 'g', 'E', 'n', 'a', 'b', 'l', 'e', 'd', '"', '>', '<',
      'i', 'n', 'p', 'u', 't', ' ', 't', 'y', 'p', 'e', '=', '"', 'c',
      'h', 'e', 'c', 'k', 'b', 'o', 'x', '"', ' ', 'n', 'a', 'm', 'e',
      '=', '"', };

  /** the start of the configuration table row */
  private static final char[] CONFIG_DESC_ROW_1 = { '/', '>', '<', '/',
      't', 'd', '>', '<', '/', 't', 'r', '>', '<', 't', 'r', ' ', 'c',
      'l', 'a', 's', 's', '=', '"', 'c', 'o', 'n', 'f', 'i', 'g', 'D',
      'e', 's', 'c', 'R', 'o', 'w', '"', ' ', 'i', 'd', '=', '"' };

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

  /** the end of the configuration table row */
  private static final char[] CONFIG_ROW_END = { '<', '/', 't', 'd', '>',
      '<', '/', 't', 'r', '>', };

  /** the toggle function name */
  static final String TOGGLE_FUNCTION_NAME = "enabledChange"; //$NON-NLS-1$
  /** the choice function name */
  static final String CHOICE_FUNCTION_NAME = "choiceChange"; //$NON-NLS-1$
  /** the add field function name */
  static final String ADD_FIELD_FUNCTION_NAME = "addField"; //$NON-NLS-1$
  /** new field name */
  static final String NEW_FIELD_NAME = "_new_field_name"; //$NON-NLS-1$
  /** the id of the add-field-row */
  static final String ADD_FIELD_ROW_ID = "_add_field_row"; //$NON-NLS-1$

  /** the current selection */
  public static final String CURRENT_SELECTION = "<em>Current Selection:</em>&nbsp;"; //$NON-NLS-1$

  /** the globally shared instance of the configuration i/o */
  public static final ConfigIO INSTANCE = new ConfigIO();

  /** the definition */
  private Definition m_definition;

  /** the forbidden constructor */
  private ConfigIO() {
    super();
  }

  /**
   * The definition
   *
   * @param handle
   *          the handle
   * @return the definition
   */
  private final Definition __getDefinition(final Handle handle) {
    if (this.m_definition == null) {
      this.m_definition = DefinitionXMLInput.getInstance().forClass(
          Evaluator.class, handle);
    }
    return this.m_definition;
  }

  /** {@inheritDoc} */
  @Override
  protected Dump createEmpty(final Handle handle) {
    return Dump.emptyDumpForDefinition(this.__getDefinition(handle));
  }

  /** {@inheritDoc} */
  @Override
  protected final Dump loadFile(final Path file, final Handle handle)
      throws IOException {
    try (final ConfigurationBuilder builder = new ConfigurationBuilder()) {
      ConfigurationXMLInput.getInstance().use().setLogger(handle)
          .addPath(file).setDestination(builder).create().call();
      return this.__getDefinition(handle).dump(builder.getResult());
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void formPutEditorFields(final String prefix,
      final Dump data, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    Parameter<?> param;
    InstanceParameter<?> instp;
    NumberParser<?> numpar;
    Object value;
    String name, field, defstr, cur, js;
    Parser<?> parser;
    EPrimitiveType type;
    boolean first, integer, bool, enabled, isChoice;
    long lng;
    double dbl;

    out = page.getOut();
    encoded = page.getEncoded();

    out.write(ConfigIO.CONFIG_TABLE_START);
    first = true;
    for (final Map.Entry<Parameter<?>, Object> entry : data) {
      isChoice = false;
      param = entry.getKey();
      name = param.getName();

      value = entry.getValue();
      if (value instanceof String) {
        value = TextUtils.prepare((String) value);
      }
      if (value == null) {
        enabled = false;
        value = param.getDefault();
        if (value instanceof String) {
          value = TextUtils.prepare((String) value);
        }
      } else {
        enabled = true;
      }

      field = Page.fieldNameFromPrefixAndName(prefix, name);

      if (first) {
        first = false;
      } else {
        out.write(ConfigIO.CONFIG_ROW_SPACER);
      }
      out.write(ConfigIO.CONFIG_ROW_START_1);
      encoded.append(field);
      out.write(ConfigIO.SUFFIX_FIELD_ROW);
      out.write(ConfigIO.CONFIG_ROW_START_2);
      encoded.append(name);
      out.write(ConfigIO.CONFIG_NAME_END);

      printParam: {

        if (param instanceof InstanceParameter) {
          out.write("<select name=\"");//$NON-NLS-1$
          encoded.append(field);
          out.write("\" id=\"");//$NON-NLS-1$
          encoded.append(field);

          instp = ((InstanceParameter<?>) param);
          js = (((((page.getFunction(new _ChoiceFunctionRenderer(instp)) + '(') + '\'') + //
          Encoder.htmlEncode(field)) + '\'') + ')');
          page.onLoad(js);
          out.write("\" onchange=\"");//$NON-NLS-1$
          out.write(js);
          out.write("\">"); //$NON-NLS-1$
          isChoice = true;

          if (!enabled) {
            out.write(//
            "<option value=\"\" selected disabled>Please select an option.</option>");//$NON-NLS-1$
          }

          if (value != null) {
            defstr = TextUtils.prepare(String.valueOf(value));
          } else {
            defstr = null;
          }

          for (final DefinitionElement cde : ((InstanceParameter<?>) param)
              .getChoices()) {
            cur = cde.getName();
            if (enabled && cur.equalsIgnoreCase(defstr)) {
              out.write("<option selected>");//$NON-NLS-1$
            } else {
              out.write("<option>");//$NON-NLS-1$
            }
            encoded.append(cur);
            out.write("</option>");//$NON-NLS-1$
          }

          out.write("</select>");//$NON-NLS-1$
          break printParam;
        }

        parser = param.getParser();
        integer = false;
        type = EPrimitiveType.getPrimitiveType(parser.getOutputClass());
        doBasicParam: {
          if (type != null) {
            switch (type) {
              case BYTE:
              case SHORT:
              case INT:
              case LONG: {
                integer = true;
              }
              //$FALL-THROUGH$
              case FLOAT:
              case DOUBLE: {
                numpar = ((NumberParser<?>) parser);
                out.write("<input type=\"number");//$NON-NLS-1$
                if (integer) {
                  out.write("\" min=\"");//$NON-NLS-1$
                  encoded.append(numpar.getLowerBoundLong());
                  out.write("\" max=\"");//$NON-NLS-1$
                  encoded.append(numpar.getUpperBoundLong());
                } else {
                  dbl = numpar.getLowerBoundDouble();
                  if ((dbl > Double.NEGATIVE_INFINITY)
                      && (dbl < Double.POSITIVE_INFINITY)) {
                    out.write("\" min=\"");//$NON-NLS-1$
                    encoded.append(dbl);
                  }
                  dbl = numpar.getUpperBoundDouble();
                  if ((dbl > Double.NEGATIVE_INFINITY)
                      && (dbl < Double.POSITIVE_INFINITY)) {
                    out.write("\" max=\"");//$NON-NLS-1$
                    encoded.append(dbl);
                  }
                  out.write("\" step=\"any");//$NON-NLS-1$
                }
                if (value != null) {
                  writeDef: {
                    if (integer) {
                      try {
                        lng = LooseLongParser.INSTANCE.parseObject(value)
                            .longValue();
                      } catch (final Throwable error) {
                        break writeDef;
                      }
                      out.write("\" value=\"");//$NON-NLS-1$
                      encoded.append(lng);
                      break writeDef;
                    }

                    try {
                      dbl = LooseDoubleParser.INSTANCE.parseObject(value)
                          .doubleValue();
                    } catch (final Throwable error) {
                      break writeDef;
                    }
                    out.write("\" value=\"");//$NON-NLS-1$
                    encoded.append(dbl);
                  }
                }
                out.write("\" name=\"");//$NON-NLS-1$
                encoded.append(field);
                out.write("\" id=\"");//$NON-NLS-1$
                encoded.append(field);
                out.write("\">"); //$NON-NLS-1$

                break doBasicParam;
              }

              case BOOLEAN: {
                out.write("<input type=\"checkbox");//$NON-NLS-1$
                if (value != null) {
                  writeDef: {
                    try {
                      bool = LooseBooleanParser.INSTANCE
                          .parseObject(value).booleanValue();
                    } catch (final Throwable error) {
                      break writeDef;
                    }
                    if (bool) {
                      out.write(" checked");//$NON-NLS-1$
                    }
                  }
                }
                out.write("\" name=\"");//$NON-NLS-1$
                encoded.append(field);
                out.write("\" id=\"");//$NON-NLS-1$
                encoded.append(field);
                out.write("\">"); //$NON-NLS-1$
                break doBasicParam;
              }
              default: {
                // fall through
              }
            }
          }

          out.write("<input type=\"text\" size=\"");//$NON-NLS-1$
          if (parser instanceof CharParser) {
            out.append('1');
          } else {
            out.append('6');
            out.append('0');
          }
          if (value != null) {
            out.write("\" value=\"");//$NON-NLS-1$
            encoded.append(String.valueOf(value));
          }
          out.write("\" name=\"");//$NON-NLS-1$
          encoded.append(field);
          out.write("\" id=\"");//$NON-NLS-1$
          encoded.append(field);
          out.write("\">"); //$NON-NLS-1$
        }
      }

      out.write(ConfigIO.CONFIG_FIELD_END);
      out.write(field);
      out.write(ConfigIO.ENABLER_SUFFIX);
      out.write("\" id=\"");//$NON-NLS-1$
      out.write(field);
      out.write(ConfigIO.ENABLER_SUFFIX);
      out.write('"');
      if (enabled) {
        out.write(" checked");//$NON-NLS-1$
      }

      js = (((((page.getFunction(_ToggleFunctionRenderer.INSTANCE) + '(') + '\'') + field) + '\'') + ')');
      page.onLoad(js);
      out.write(" onclick=\"");//$NON-NLS-1$
      out.write(js);
      out.write('"');
      out.write(ConfigIO.CONFIG_DESC_ROW_1);
      out.write(field);
      out.write(ConfigIO.SUFFIX_DESC_ROW);
      out.write(ConfigIO.CONFIG_DESC_ROW_2);
      page.printLines(param.getDescription(), true, true);

      if (isChoice) {
        out.write(ConfigIO.CONFIG_CHOICE_ROW_1);
        out.write(field);
        out.write(ConfigIO.SUFFIX_CHOICE_ROW);
        out.write(ConfigIO.CONFIG_CHOICE_ROW_2);
        out.write(field);
        out.write(ConfigIO.SUFFIX_CHOICE_CELL);
        out.write(ConfigIO.CONFIG_CHOICE_ROW_3);
      } else {
        out.write(ConfigIO.CONFIG_ROW_END);
      }
    }

    if (data.allowsMore()) {
      out.write(ConfigIO.CONFIG_ROW_SPACER);
      out.write(//
      "<tr class=\"configAddFieldRow\" id=\""); //$NON-NLS-1$

      encoded.append(Page.fieldNameFromPrefixAndName(prefix,
          ConfigIO.ADD_FIELD_ROW_ID));
      out.write(//
      "\"><td class=\"configAddFieldButtonCell\"><input type=\"button\" onclick=\""); //$NON-NLS-1$
      out.write(page.getFunction(_AddFieldFunctionRenderer.INSTANCE));
      out.write('(');
      out.write('\'');
      encoded.append(prefix);
      out.write(//
      "')\" value=\"add parameter\"/>&nbsp;:</td><td colspan=\"2\" class=\"configAddFieldButtonCell\"><input type=\"text\" size=\"60\" id=\"");//$NON-NLS-1$
      encoded.append(Page.fieldNameFromPrefixAndName(prefix,
          ConfigIO.NEW_FIELD_NAME));
      out.write(//
      "\"/></td></tr>");//$NON-NLS-1$
    }

    out.write(ConfigIO.CONFIG_TABLE_END);
  }

  /**
   * Load a configuration from a request
   *
   * @param builder
   *          the destination builder
   * @param prefix
   *          the prefix
   * @param definition
   *          the definition
   * @param request
   *          the request
   * @param handle
   *          the handle
   */
  public final void loadConfigurationFromRequest(
      final ConfigurationBuilder builder, final String prefix,
      final Definition definition, final HttpServletRequest request,
      final Handle handle) {
    final HashSet<String> done;
    String name, field, enabled, temp, value;

    if (definition.allowsMore()) {
      done = new HashSet<>();
    } else {
      done = null;
    }

    for (final Parameter<?> param : definition) {
      name = param.getName();

      field = Page.fieldNameFromPrefixAndName(prefix, name);
      temp = (field + ConfigIO.ENABLER_SUFFIX);
      if (done != null) {
        done.add(field);
        done.add(temp);
      }

      enabled = request.getParameter(temp);
      if (enabled != null) {
        if (LooseBooleanParser.INSTANCE.parseBoolean(enabled)) {
          value = request.getParameter(field);
          if (value != null) {
            builder.put(name, value);
          }
        }
      }
    }

    if (done != null) {
      for (final Map.Entry<String, String[]> entry : request
          .getParameterMap().entrySet()) {
        field = entry.getKey();
        if (!(done.contains(field))) {
          name = Page.nameFromPrefixAndFieldName(prefix, field);
          if (name != null) {
            temp = (field + ConfigIO.ENABLER_SUFFIX);
            enabled = request.getParameter(temp);
            if (enabled != null) {
              if (done.add(temp) && done.add(field)) {
                if (LooseBooleanParser.INSTANCE.parseBoolean(enabled)) {
                  value = request.getParameter(field);
                  if (value != null) {
                    builder.put(name, value);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final Dump loadFromRequest(final String prefix,
      final HttpServletRequest request, final Handle handle) {
    final Definition definition;

    definition = this.__getDefinition(handle);
    if (definition != null) {
      try (final ConfigurationBuilder builder = new ConfigurationBuilder()) {
        this.loadConfigurationFromRequest(builder, prefix, definition,
            request, handle);
        return definition.dump(builder.getResult());
      }
    }

    handle.failure(//
        "Cannot load configuration if configuration definition cannot be loaded."); //$NON-NLS-1$
    return null;
  }

  /** {@inheritDoc} */
  @Override
  protected final void storeToFile(final Dump data, final Path file,
      final Handle handle) throws IOException {
    ConfigurationXMLOutput.getInstance().use().setLogger(handle)
        .setPath(file).setSource(data).create().call();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  protected final void formPutButtons(final String prefix,
      final Loaded<Dump> data, final Page page) throws IOException {
    final JspWriter out;

    super.formPutButtons(prefix, data, page);

    out = page.getOut();
    out.write("&nbsp;<input type=\"submit\" name=\"");//$NON-NLS-1$
    out.write(ControllerUtils.INPUT_SUBMIT);
    out.write("\" value=\"");//$NON-NLS-1$
    out.write(ControllerUtils.COMMAND_EXECUTE_EVALUATOR);
    out.write("\" formmethod=\"get\" formaction=\"/evaluator.jsp\"/>"); //$NON-NLS-1$
  }
}
