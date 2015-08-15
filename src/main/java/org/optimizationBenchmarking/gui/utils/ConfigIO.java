package org.optimizationBenchmarking.gui.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
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
import org.optimizationBenchmarking.utils.text.textOutput.AbstractTextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

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

  /** the end of the configuration table row */
  private static final char[] CONFIG_ROW_END = { '<', '/', 't', 'd', '>',
      '<', '/', 't', 'r', '>', };

  /** the start of the java script */
  private static final char[] JAVASCRIPT_START = { '<', 's', 'c', 'r',
      'i', 'p', 't', ' ', 't', 'y', 'p', 'e', '=', '"', 't', 'e', 'x',
      't', '/', 'j', 'a', 'v', 'a', 's', 'c', 'r', 'i', 'p', 't', '"', '>' };

  /** the toggle function name */
  private static final String TOGGLE_FUNCTION_NAME = "onEnableBoxClick"; //$NON-NLS-1$

  /** the end of the java script */
  private static final char[] JAVASCRIPT_END = { '<', '/', 's', 'c', 'r',
      'i', 'p', 't', '>', };

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
      handle.failure("Set of paths to load and edit cannot be null."); //$NON-NLS-1$
      return null;
    }

    i = relPaths.length;
    if (i <= 0) {
      handle.failure("Set of paths to load and edit cannot empty."); //$NON-NLS-1$
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
            result[i] = Dump.EMPTY_DUMP;
            if (handle.isLoggable(Level.INFO)) {
              handle.info("File '" + relPath + //$NON-NLS-1$
                  "' does not exist or is empty, configuration if empty.");//$NON-NLS-1$
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
  @SuppressWarnings("incomplete-switch")
  public static final void putFormFields(final String prefix,
      final Dump dump, final JspWriter out,
      final ArrayList<String> jsCollector) throws IOException {
    final ITextOutput encoded, wrapper;
    Parameter<?> param;
    NumberParser<?> numpar;
    Object value;
    String name, field, defstr, cur, js;
    Parser<?> parser;
    EPrimitiveType type;
    boolean first, integer, bool, enabled;
    long lng;
    double dbl;

    wrapper = AbstractTextOutput.wrap(out);
    encoded = XMLCharTransformer.getInstance().transform(wrapper);

    out.write(ConfigIO.CONFIG_TABLE_START);
    first = true;
    for (final Map.Entry<Parameter<?>, Object> entry : dump) {
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

      field = name;
      if (prefix != null) {
        field = prefix + field;
      }

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
          out.write("\">"); //$NON-NLS-1$

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
      encoded.append(param.getDescription());
      out.write(ConfigIO.CONFIG_ROW_END);
    }

    out.write(ConfigIO.CONFIG_TABLE_END);
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
    String prefix, name, enabled, value;

    submit = request.getParameter(ControllerUtils.INPUT_SUBMIT);
    if (submit.equalsIgnoreCase(FileIO.PARAM_SAVE)) {
      path = request.getParameter(ControllerUtils.PARAMETER_SELECTION);
      if (path != null) {
        realPath = handle.getController().resolve(handle, path, null);
        if (realPath != null) {

          prefix = request.getParameter(ConfigIO.PARAMETER_PREFIX);
          if (prefix == null) {
            prefix = ""; //$NON-NLS-1$
          }

          try {
            definition = ConfigIO.getDefinition(handle);
            if (definition != null) {
              try (final ConfigurationBuilder builder = new ConfigurationBuilder()) {
                for (final Parameter<?> param : definition) {
                  name = param.getName();
                  enabled = request.getParameter(prefix + name
                      + ConfigIO.ENABLER_SUFFIX);
                  if (enabled != null) {
                    if (LooseBooleanParser.INSTANCE.parseBoolean(enabled)) {
                      value = request.getParameter(prefix + name);
                      if (value != null) {
                        builder.put(name, value);
                      }
                    }
                  }
                }
                config = builder.getResult();
              }

              ConfigurationXMLOutput.getInstance().use().setLogger(handle)
                  .setPath(realPath).setSource(config).create().call();
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
}
