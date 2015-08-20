package org.optimizationBenchmarking.gui.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.Evaluator;
import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.config.ConfigurationBuilder;
import org.optimizationBenchmarking.utils.config.ConfigurationXMLInput;
import org.optimizationBenchmarking.utils.config.ConfigurationXMLOutput;
import org.optimizationBenchmarking.utils.config.Definition;
import org.optimizationBenchmarking.utils.config.DefinitionElement;
import org.optimizationBenchmarking.utils.config.DefinitionXMLInput;
import org.optimizationBenchmarking.utils.config.Dump;
import org.optimizationBenchmarking.utils.config.InstanceParameter;
import org.optimizationBenchmarking.utils.config.Parameter;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.parsers.CharParser;
import org.optimizationBenchmarking.utils.parsers.LooseBooleanParser;
import org.optimizationBenchmarking.utils.parsers.LooseDoubleParser;
import org.optimizationBenchmarking.utils.parsers.LooseLongParser;
import org.optimizationBenchmarking.utils.parsers.NumberParser;
import org.optimizationBenchmarking.utils.parsers.Parser;
import org.optimizationBenchmarking.utils.reflection.EPrimitiveType;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.AbstractTextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.tokenizers.LineIterator;

/** Some support for configuration I/O */
public final class ConfigIO {

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

  /** the prefix separator */
  static final char PREFIX_SEPARATOR = '_';

  /** the start of the configuration table */
  private static final char[] CONFIG_TABLE_START = { '<', 't', 'a', 'b',
      'l', 'e', ' ', 'c', 'l', 'a', 's', 's', '=', '"', 'i', 'n', 'v',
      'i', 's', 'i', 'b', 'l', 'e', '"', '>', };
  /** the start of the configuration table */
  private static final char[] CONFIG_TABLE_END = { '<', '/', 't', 'a',
      'b', 'l', 'e', '>', };
  /** the start of the configuration table row */
  private static final char[] CONFIG_ROW_SPACER = { '<', 't', 'r', ' ',
      'c', 'l', 'a', 's', 's', '=', '"', 'c', 'o', 'n', 'f', 'i', 'g',
      'S', 'p', 'a', 'c', 'e', 'r', '"', '>', '<', 't', 'd', ' ', 'c',
      'o', 'l', 's', 'p', 'a', 'n', '=', '"', '3', '"', ' ', 'c', 'l',
      'a', 's', 's', '=', '"', 'c', 'o', 'n', 'f', 'i', 'g', 'S', 'p',
      'a', 'c', 'e', 'r', '"', '>', '&', 'n', 'b', 's', 'p', ';', '<',
      '/', 't', 'd', '>', '<', '/', 't', 'r', '>' };
  /** the start of the configuration table row */
  private static final char[] CONFIG_ROW_START_1 = { '<', 't', 'r', ' ',
      'c', 'l', 'a', 's', 's', '=', '"', 'c', 'o', 'n', 'f', 'i', 'g',
      'F', 'i', 'e', 'l', 'd', 'R', 'o', 'w', '"', ' ', 'i', 'd', '=', '"' };
  /** the start of the configuration table row */
  private static final char[] CONFIG_ROW_START_2 = { '"', '>', '<', 't',
      'h', ' ', 'c', 'l', 'a', 's', 's', '=', '"', 'c', 'o', 'n', 'f',
      'i', 'g', 'N', 'a', 'm', 'e', '"', '>' };
  /** the start of the configuration field */
  private static final char[] CONFIG_NAME_END = { '<', '/', 't', 'h', '>',
      '<', 't', 'd', ' ', 'c', 'l', 'a', 's', 's', '=', '"', 'c', 'o',
      'n', 'f', 'i', 'g', 'F', 'i', 'e', 'l', 'd', '"', '>', };

  /** the end of the configuration field */
  private static final char[] CONFIG_FIELD_END = { '<', '/', 't', 'd',
      '>', '<', 't', 'd', ' ', 'c', 'l', 'a', 's', 's', '=', '"', 'c',
      'o', 'n', 'f', 'i', 'g', 'E', 'n', 'a', 'b', 'l', 'e', 'd', '"',
      '>', '<', 'i', 'n', 'p', 'u', 't', ' ', 't', 'y', 'p', 'e', '=',
      '"', 'c', 'h', 'e', 'c', 'k', 'b', 'o', 'x', '"', ' ', 'n', 'a',
      'm', 'e', '=', '"', };

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

  /** the start of the java script */
  static final char[] JAVASCRIPT_START = { '<', 's', 'c', 'r', 'i', 'p',
      't', ' ', 't', 'y', 'p', 'e', '=', '"', 't', 'e', 'x', 't', '/',
      'j', 'a', 'v', 'a', 's', 'c', 'r', 'i', 'p', 't', '"', '>' };

  /** the toggle function name */
  private static final String TOGGLE_FUNCTION_NAME = "enabledChange"; //$NON-NLS-1$
  /** the choice function name */
  private static final String CHOICE_FUNCTION_NAME = "choiceChange"; //$NON-NLS-1$
  /** the add field function name */
  private static final String ADD_FIELD_FUNCTION_NAME = "addField"; //$NON-NLS-1$
  /** new field name */
  private static final String NEW_FIELD_NAME = "_new_field_name"; //$NON-NLS-1$
  /** the id of the add-field-row */
  private static final String ADD_FIELD_ROW_ID = "_add_field_row"; //$NON-NLS-1$

  /** the end of the java script */
  static final char[] JAVASCRIPT_END = { '<', '/', 's', 'c', 'r', 'i',
      'p', 't', '>', };

  /** the forbidden constructor */
  private ConfigIO() {
    ErrorUtils.doNotCall();
  }

  /**
   * The definition
   *
   * @param handle
   *          the handle
   * @return the definition
   */
  public static final Definition getDefinition(final Handle handle) {
    return DefinitionXMLInput.getInstance().forClass(Evaluator.class,
        handle);
  }

  /**
   * Get the configuration dumps corresponding to the given paths.
   *
   * @param basePath
   *          the base path
   * @param relPaths
   *          the paths
   * @param handle
   *          the handle
   * @return the dumps
   */
  public static final Dump[] load(final String basePath,
      final String[] relPaths, final Handle handle) {
    final Definition definition;
    final Controller controller;
    final Dump[] result;
    BasicFileAttributes bfa;
    Path root, path;
    String relPath;
    int i;

    if (relPaths == null) {
      handle.failure(//
          "Set of paths to load and edit configuration file cannot be null."); //$NON-NLS-1$
      return null;
    }

    i = relPaths.length;
    if (i <= 0) {
      handle.failure(//
          "Set of paths to load and edit configuration file cannot empty."); //$NON-NLS-1$
      return null;
    }

    try {
      definition = DefinitionXMLInput.getInstance().forClass(
          Evaluator.class, handle);
    } catch (final Throwable error) {
      handle.failure(
          "Could not load configuration definition for Evaluator class.",//$NON-NLS-1$
          error);
      return null;
    }

    controller = handle.getController();
    root = controller.getRootDir();
    if (basePath != null) {
      root = controller.resolve(handle, basePath, root);
    }

    result = new Dump[relPaths.length];
    for (i = 0; i < result.length; i++) {
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

            try (final ConfigurationBuilder builder = new ConfigurationBuilder()) {
              ConfigurationXMLInput.getInstance().use().setLogger(handle)
                  .addPath(path).setDestination(builder).create().call();
              result[i] = definition.dump(builder.getResult());
            }

            continue;
          }

          if ((bfa == null) || (bfa.isRegularFile() && (bfa.size() <= 0L))) {
            result[i] = Dump.emptyDumpForDefinition(definition);
            if (handle.isLoggable(Level.INFO)) {
              handle.info("Configuration file '" + relPath + //$NON-NLS-1$
                  "' does not exist or is empty, configuration is empty.");//$NON-NLS-1$
            }
            continue;
          }

          handle.failure("Path '" + relPath + //$NON-NLS-1$
              "' does not identify a file."); //$NON-NLS-1$
        }
      } catch (final Throwable error) {
        handle.failure("Failed to load path '" + //$NON-NLS-1$
            relPath + '\'' + '.', error);
      }
    }

    return result;
  }

  /**
   * Write the form fields corresponding to the given configuration dump
   *
   * @param prefix
   *          the id field prefix
   * @param dump
   *          the dump
   * @param out
   *          the the output destination
   * @param jsCollector
   *          the java script collector
   * @throws IOException
   *           if i/o fails
   */
  public static final void putFormFields(final String prefix,
      final Dump dump, final JspWriter out,
      final ArrayList<String> jsCollector) throws IOException {
    final ITextOutput encoded, wrapper;

    wrapper = AbstractTextOutput.wrap(out);
    encoded = Encoder.htmlEncode(wrapper);

    ConfigIO._putFormFields(prefix, dump, out, jsCollector, wrapper,
        encoded);
  }

  /**
   * Write the form fields corresponding to the given configuration dump
   *
   * @param prefix
   *          the id field prefix
   * @param dump
   *          the dump
   * @param out
   *          the the output destination
   * @param jsCollector
   *          the java script collector
   * @param wrapper
   *          the wrapper
   * @param encoded
   *          the encoder
   * @throws IOException
   *           if i/o fails
   */
  @SuppressWarnings("incomplete-switch")
  static final void _putFormFields(final String prefix, final Dump dump,
      final JspWriter out, final ArrayList<String> jsCollector,
      final ITextOutput wrapper, final ITextOutput encoded)
      throws IOException {
    Parameter<?> param;
    NumberParser<?> numpar;
    Object value;
    String name, field, defstr, cur, js;
    Parser<?> parser;
    EPrimitiveType type;
    boolean first, integer, bool, enabled, isChoice, needsChoices;
    long lng;
    double dbl;

    out.write(ConfigIO.CONFIG_TABLE_START);
    first = true;
    needsChoices = false;
    for (final Map.Entry<Parameter<?>, Object> entry : dump) {
      isChoice = false;
      param = entry.getKey();
      name = param.getName();

      value = entry.getValue();
      if ((value == null) || (value == "")) { //$NON-NLS-1$
        enabled = false;
        value = param.getDefault();
        if (value == "") {//$NON-NLS-1$
          value = null;
        }
      } else {
        enabled = true;
      }

      field = ConfigIO.__fieldNameFromPrefixAndName(prefix, name);

      if (first) {
        first = false;
      } else {
        out.write(ConfigIO.CONFIG_ROW_SPACER);
      }
      out.write(ConfigIO.CONFIG_ROW_START_1);
      out.write(field);
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

          js = (ConfigIO.__choiceFuncName(prefix, name) + '(' + ')');
          jsCollector.add(js);
          out.write("\" onchange=\"");//$NON-NLS-1$
          out.write(js);

          out.write("\">"); //$NON-NLS-1$
          isChoice = true;
          needsChoices = true;

          if (value != null) {
            defstr = String.valueOf(value);
          } else {
            defstr = null;
          }

          for (final DefinitionElement cde : ((InstanceParameter<?>) param)
              .getChoices()) {
            cur = cde.getName();
            if (cur.equalsIgnoreCase(defstr)) {
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
                out.write("<input type=\"checkbox\"");//$NON-NLS-1$
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
      js = (((((ConfigIO.TOGGLE_FUNCTION_NAME + '(') + '\'') + field) + '\'') + ')');
      jsCollector.add(js);
      out.write(" onclick=\"");//$NON-NLS-1$
      out.write(js);
      out.write('"');
      out.write(ConfigIO.CONFIG_DESC_ROW_1);
      out.write(field);
      out.write(ConfigIO.SUFFIX_DESC_ROW);
      out.write(ConfigIO.CONFIG_DESC_ROW_2);
      ConfigIO.printText(param.getDescription(), out, encoded);

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

    if (dump.allowsMore()) {
      out.write(ConfigIO.CONFIG_ROW_SPACER);
      out.write(//
      "<tr class=\"configAddFieldRow\" id=\""); //$NON-NLS-1$
      out.write(ConfigIO.__fieldNameFromPrefixAndName(prefix,
          ConfigIO.ADD_FIELD_ROW_ID));
      out.write(//
      "\"><td class=\"configAddFieldButtonCell\"><input type=\"button\" onclick=\""); //$NON-NLS-1$

      out.write(ConfigIO.__addFieldFuncName(prefix));
      out.write(//
      "()\" value=\"add parameter\"/>&nbsp;:</td><td colspan=\"2\" class=\"configAddFieldButtonCell\"><input type=\"text\" size=\"60\" id=\"");//$NON-NLS-1$
      out.write(ConfigIO.__fieldNameFromPrefixAndName(prefix,
          ConfigIO.NEW_FIELD_NAME));
      out.write(//
      "\"/></td></tr>");//$NON-NLS-1$
    }

    out.write(ConfigIO.CONFIG_TABLE_END);
    if (needsChoices) {
      ConfigIO.__putScripts(prefix, dump, out, encoded);
    }
  }

  /**
   * Get the choice function name
   *
   * @param prefix
   *          the prefix
   * @param name
   *          the name
   * @return the choice function name
   */
  private static final String __choiceFuncName(final String prefix,
      final String name) {
    return ('f' + ConfigIO.__fieldNameFromPrefixAndName(prefix,
        (ConfigIO.CHOICE_FUNCTION_NAME + name)));
  }

  /**
   * Get the add button function name
   *
   * @param prefix
   *          the prefix
   * @return the add button function name
   */
  private static final String __addFieldFuncName(final String prefix) {
    return ('f' + ConfigIO.__fieldNameFromPrefixAndName(prefix,
        ConfigIO.ADD_FIELD_FUNCTION_NAME));
  }

  /**
   * Get the field prefix for a given name and prefix
   *
   * @param prefix
   *          the prefix
   * @return the field name
   */
  private static final String __fieldPrefixFromPrefix(final String prefix) {
    if (prefix == null) {
      return ""; //$NON-NLS-1$
    }
    return (prefix + '_');
  }

  /**
   * Get the field name for a given name and prefix
   *
   * @param prefix
   *          the prefix
   * @param name
   *          the name
   * @return the field name
   */
  private static final String __fieldNameFromPrefixAndName(
      final String prefix, final String name) {
    if (prefix == null) {
      return name;
    }
    return (prefix + '_' + name);
  }

  /**
   * Get the parameter name from a given prefix and field name
   *
   * @param prefix
   *          the prefix
   * @param field
   *          the field name
   * @return the parameter name, or {@code null} if it does not match to
   *         the prefix/field name
   */
  private static final String __nameFromPrefixAndFieldName(
      final String prefix, final String field) {
    final int prefixLen, fieldLen;

    if ((prefix == null) || (field == null)) {
      return field;
    }
    fieldLen = field.length();
    prefixLen = prefix.length();

    if (fieldLen <= (prefixLen + 1)) {
      return null;
    }
    if (field.startsWith(prefix)) {
      if (field.charAt(prefixLen) == '_') {
        return TextUtils.prepare(field.substring(prefixLen + 1));
      }
    }

    return null;
  }

  /**
   * Put the documentation scripts.
   *
   * @param prefix
   *          the id field prefix
   * @param dump
   *          the dump
   * @param out
   *          the the output destination
   * @param encoded
   *          the encoded text output
   * @throws IOException
   *           if i/o fails
   */
  private static final void __putScripts(final String prefix,
      final Dump dump, final JspWriter out, final ITextOutput encoded)
      throws IOException {
    InstanceParameter<?> instparam;
    Parameter<?> param;
    String field;

    out.write(ConfigIO.JAVASCRIPT_START);

    for (final Map.Entry<Parameter<?>, Object> entry : dump) {
      param = entry.getKey();

      if (param instanceof InstanceParameter) {
        instparam = ((InstanceParameter<?>) param);

        out.write("function "); //$NON-NLS-1$
        out.write(ConfigIO.__choiceFuncName(prefix, param.getName()));
        out.write("(){var text=\"\"; switch(document.getElementById('");//$NON-NLS-1$
        field = ConfigIO.__fieldNameFromPrefixAndName(prefix,
            param.getName());
        encoded.append(field);
        out.write("').value){");//$NON-NLS-1$
        for (final DefinitionElement de : instparam.getChoices()) {
          out.write("case '");//$NON-NLS-1$
          encoded.append(de.getName());
          out.write("':{text='");//$NON-NLS-1$
          ConfigIO.printText(de.getDescription(), out, encoded);
          out.write("';break;}");//$NON-NLS-1$
        }
        out.write("}document.getElementById('");//$NON-NLS-1$
        encoded.append(field);
        out.write(ConfigIO.SUFFIX_CHOICE_CELL);
        out.write("').innerHTML='<em>Current Selection:</em>&nbsp;'+text;}");//$NON-NLS-1$
      }
    }

    if (dump.allowsMore()) {
      out.write("function "); //$NON-NLS-1$
      out.write(ConfigIO.__addFieldFuncName(prefix));
      out.write("(){var name=document.getElementById('");//$NON-NLS-1$
      out.write(ConfigIO.__fieldNameFromPrefixAndName(prefix,
          ConfigIO.NEW_FIELD_NAME));
      out.write("').value;if((name!=null)&&(name.length>0)){var newbody='");//$NON-NLS-1$

      out.write(ConfigIO.CONFIG_ROW_START_1);
      field = ConfigIO.__fieldPrefixFromPrefix(prefix);
      encoded.append(field);
      out.write("'+name+'");//$NON-NLS-1$
      out.write(ConfigIO.SUFFIX_FIELD_ROW);
      out.write(ConfigIO.CONFIG_ROW_START_2);
      out.write("'+name+'");//$NON-NLS-1$
      out.write(ConfigIO.CONFIG_NAME_END);

      out.write("<input type=\"text\" size=\"60\" name=\"");//$NON-NLS-1$
      encoded.append(field);
      out.write("'+name+'");//$NON-NLS-1$
      out.write("\" id=\"");//$NON-NLS-1$
      encoded.append(field);
      out.write("'+name+'");//$NON-NLS-1$
      out.write("\">"); //$NON-NLS-1$

      out.write(ConfigIO.CONFIG_FIELD_END);
      encoded.append(field);
      out.write("'+name+'");//$NON-NLS-1$
      out.write(ConfigIO.ENABLER_SUFFIX);
      out.write("\" id=\"");//$NON-NLS-1$
      encoded.append(field);
      out.write("'+name+'");//$NON-NLS-1$
      out.write(ConfigIO.ENABLER_SUFFIX);
      out.write("\" checked onclick=\"");//$NON-NLS-1$
      out.write(ConfigIO.TOGGLE_FUNCTION_NAME);
      out.write("(\\'");//$NON-NLS-1$
      encoded.append(field);
      out.write("'+name+'");//$NON-NLS-1$
      out.write("\\')\"/></td></tr>';var rowToInsertBefore=document.getElementById('");//$NON-NLS-1$
      out.write(ConfigIO.__fieldNameFromPrefixAndName(prefix,
          ConfigIO.ADD_FIELD_ROW_ID));
      out.write("');var dummy=document.createElement('table');dummy.innerHTML=newbody;var insert=dummy.firstChild;if(insert.tagName.toUpperCase()=='TBODY'){insert=insert.firstChild;}rowToInsertBefore.parentNode.insertBefore(insert,rowToInsertBefore);dummy.innerHTML='");//$NON-NLS-1$
      out.write(ConfigIO.CONFIG_ROW_SPACER);
      out.write("';insert=dummy.firstChild;if(insert.tagName.toUpperCase()=='TBODY'){insert=insert.firstChild;}rowToInsertBefore.parentNode.insertBefore(insert,rowToInsertBefore);}}");//$NON-NLS-1$
    }

    out.write(ConfigIO.JAVASCRIPT_END);
  }

  /**
   * Put a java script for keeping the elements nice.
   *
   * @param out
   *          the the output destination
   * @param jsCollector
   *          the java script collector
   * @throws IOException
   *           if i/o fails
   */
  public static final void putJavaScript(final JspWriter out,
      final ArrayList<String> jsCollector) throws IOException {
    out.write(ConfigIO.JAVASCRIPT_START);

    out.write("function ");//$NON-NLS-1$
    out.write(ConfigIO.TOGGLE_FUNCTION_NAME);
    out.write(//
    "(id){var box=document.getElementById(id+\"");//$NON-NLS-1$
    out.write(ConfigIO.ENABLER_SUFFIX);
    out.write(//
    "\");if(box!=null){var value=box.checked;var control=document.getElementById(id);if(control!=null){control.disabled=!value;}var row=document.getElementById(id+\"");//$NON-NLS-1$
    out.write(ConfigIO.SUFFIX_FIELD_ROW);
    out.write(//
    "\");if(row!=null){if(value){row.style.background=\"transparent\";}else{row.style.background=\"#eeeeee\";}}row=document.getElementById(id+\"");//$NON-NLS-1$
    out.write(ConfigIO.SUFFIX_DESC_ROW);
    out.write(//
    "\");if(row!=null){if(value){row.style.background=\"transparent\";}else{row.style.background=\"#eeeeee\";}}row=document.getElementById(id+\"");//$NON-NLS-1$
    out.write(ConfigIO.SUFFIX_CHOICE_ROW);
    out.write(//
    "\");if(row!=null){if(value){row.style.background=\"transparent\";}else{row.style.background=\"#eeeeee\";}}}}"); //$NON-NLS-1$

    out.write("window.onload=function(){");//$NON-NLS-1$
    for (final String str : jsCollector) {
      out.write(str);
      if (str.charAt(str.length() - 1) != ';') {
        out.write(';');
      }
    }
    out.write('}');

    out.write(ConfigIO.JAVASCRIPT_END);
  }

  /**
   * Load a configuration from a request
   *
   * @param request
   *          the request with all the parameters
   * @param definition
   *          the parameter definition
   * @param prefix
   *          the prefix
   * @param handle
   *          the handle
   * @param builder
   *          the configuration builder
   */
  static final void _loadConfigFromRequest(final String prefix,
      final HttpServletRequest request, final Definition definition,
      final Handle handle, final ConfigurationBuilder builder) {
    final HashSet<String> done;
    String name, field, enabled, temp, value;

    if (definition.allowsMore()) {
      done = new HashSet<>();
    } else {
      done = null;
    }

    for (final Parameter<?> param : definition) {
      name = param.getName();

      field = ConfigIO.__fieldNameFromPrefixAndName(prefix, name);
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
          name = ConfigIO.__nameFromPrefixAndFieldName(prefix, field);
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

  /**
   * Store the contents into a file.
   *
   * @param handle
   *          the handle
   * @param request
   *          the request
   */
  public static final void store(final Handle handle,
      final HttpServletRequest request) {
    final Definition definition;
    final Configuration config;
    final String path, submit;
    final Path realPath;
    final String prefix;

    submit = request.getParameter(ControllerUtils.INPUT_SUBMIT);
    if (submit.equalsIgnoreCase(FileIO.PARAM_SAVE)) {
      path = request.getParameter(ControllerUtils.PARAMETER_SELECTION);
      if (path != null) {
        realPath = handle.getController().resolve(handle, path, null);
        if (realPath != null) {

          prefix = TextUtils.prepare(request
              .getParameter(ConfigIO.PARAMETER_PREFIX));

          try {
            definition = ConfigIO.getDefinition(handle);
            if (definition != null) {

              try (final ConfigurationBuilder builder = new ConfigurationBuilder()) {
                ConfigIO._loadConfigFromRequest(prefix, request,
                    definition, handle, builder);
                config = builder.getResult();
              }

              ConfigurationXMLOutput.getInstance().use().setLogger(handle)
                  .setPath(realPath).setSource(config).create().call();
              handle.success("Successfully stored configuration file '" + //$NON-NLS-1$
                  path + '\'' + '.');
            }
          } catch (final Throwable error) {
            handle.failure("Failed to store configuration file '" //$NON-NLS-1$
                + path + '\'' + '.', error);
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
   * Append a text to an encoded output.
   *
   * @param text
   *          the text
   * @param out
   *          the writer
   * @param encoded
   *          the destination
   * @throws IOException
   *           if i/o fails
   */
  public static final void printText(final String text,
      final JspWriter out, final ITextOutput encoded) throws IOException {
    boolean first;

    first = true;
    for (final String line : new LineIterator(text)) {
      if (first) {
        first = false;
      } else {
        out.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;"); //$NON-NLS-1$
      }
      encoded.append(line);
    }
  }
}
