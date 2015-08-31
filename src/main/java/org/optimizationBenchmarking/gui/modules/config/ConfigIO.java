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
import org.optimizationBenchmarking.gui.utils.Loaded;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.gui.utils.editor.EditorModule;
import org.optimizationBenchmarking.utils.config.ConfigurationBuilder;
import org.optimizationBenchmarking.utils.config.ConfigurationXMLInput;
import org.optimizationBenchmarking.utils.config.ConfigurationXMLOutput;
import org.optimizationBenchmarking.utils.config.Definition;
import org.optimizationBenchmarking.utils.config.DefinitionXMLInput;
import org.optimizationBenchmarking.utils.config.Dump;
import org.optimizationBenchmarking.utils.config.InstanceParameter;
import org.optimizationBenchmarking.utils.config.Parameter;
import org.optimizationBenchmarking.utils.parsers.LooseBooleanParser;
import org.optimizationBenchmarking.utils.parsers.NumberParser;
import org.optimizationBenchmarking.utils.parsers.Parser;
import org.optimizationBenchmarking.utils.reflection.EPrimitiveType;

/**
 * This editor module allows us to generate a dynamic form for editing
 * evaluator configuration files.
 */
public final class ConfigIO extends EditorModule<Dump> {

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
  @SuppressWarnings({ "rawtypes" })
  @Override
  public final void formPutEditorFields(final String prefix,
      final Dump data, final Page page) throws IOException {
    Parameter<?> param;
    NumberParser<?> numpar;
    Object valueObject;
    String name, field;
    Parser<?> parser;
    EPrimitiveType type;
    boolean first, hasChoices;

    this.formTableBegin(page);
    first = true;
    for (final Map.Entry<Parameter<?>, Object> entry : data) {
      param = entry.getKey();
      name = param.getName();

      valueObject = entry.getValue();

      field = Page.fieldNameFromPrefixAndName(prefix, name);

      if (first) {
        first = false;
      } else {
        this.formTableSpacer(page);
      }

      this.formTableFieldRowBegin(field, name, true, page);
      hasChoices = false;

      printParam: {
        if (param instanceof InstanceParameter) {
          this.formPutSelection(field, valueObject,
              ((InstanceParameter) param).getChoices(), page);
          hasChoices = true;
          break printParam;
        }

        parser = param.getParser();
        type = EPrimitiveType.getPrimitiveType(parser.getOutputClass());

        if (type != null) {
          switch (type) {
            case BYTE:
            case SHORT:
            case INT:
            case LONG: {
              numpar = ((NumberParser<?>) parser);
              this.formPutInteger(field, valueObject, numpar, page);
              break printParam;
            }

            case FLOAT:
            case DOUBLE: {
              numpar = ((NumberParser<?>) parser);
              this.formPutFloat(field, valueObject, numpar, page);
              break printParam;
            }

            case CHAR: {
              this.formPutChar(field, valueObject, page);
              break printParam;
            }

            case BOOLEAN: {
              this.formPutBoolean(field, valueObject, null, page);
              break printParam;
            }
            default: {
              // fall through
            }
          }
        }

        this.formPutString(field, valueObject, page);
      }

      this.formTableFieldRowEndDescRowBegin(field, true,
          (valueObject != null), page);
      page.printLines(param.getDescription(), true, true);
      this.formTableDescRowEnd(field, hasChoices, page);
    }

    if (data.allowsMore()) {
      this.formTableSpacer(page);
      this.formPutAddField(prefix, "add parameter", page); //$NON-NLS-1$
    }

    this.formTableEnd(page);
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
      temp = (field + EditorModule.BUTTON_ENABLE_SUFFIX);
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
            temp = (field + EditorModule.BUTTON_ENABLE_SUFFIX);
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
