package org.optimizationBenchmarking.gui.modules.evaluation;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.evaluation.impl.EvaluationModuleDescriptions;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.EvaluationModules;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.EvaluationModulesBuilder;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescription;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescriptions;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleEntry;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleEntryBuilder;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.io.EvaluationXMLInput;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.io.EvaluationXMLOutput;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.modules.config.ConfigIO;
import org.optimizationBenchmarking.gui.utils.EditorModule;
import org.optimizationBenchmarking.gui.utils.Encoder;
import org.optimizationBenchmarking.gui.utils.Loaded;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.utils.config.ConfigurationBuilder;
import org.optimizationBenchmarking.utils.config.Definition;
import org.optimizationBenchmarking.utils.config.DefinitionXMLInput;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** The editor module allows us to edit an evaluation file. */
public final class EvaluationIO extends EditorModule<EvaluationModules> {

  /** the module prefix */
  public static final String PARAMETER_EVALUATION_PREFIX = "evaluationPrefix"; //$NON-NLS-1$

  /** a module */
  static final String PARAMETER_MODULE = "module[]";//$NON-NLS-1$

  /** the select */
  static final String MODULE_ADD_SELECT_ID = "moduleAddSelect"; //$NON-NLS-1$
  /** the description paragraph */
  static final String MODULE_ADD_DESC_ID = "moduleAddDes"; //$NON-NLS-1$

  /** the main div suffix */
  static final String MAIN_DIV_SUFFIX = "-div";//$NON-NLS-1$
  /** the up-button suffix */
  static final String UP_BUTTON_SUFFIX = "-up";//$NON-NLS-1$
  /** the down-button suffix */
  static final String DOWN_BUTTON_SUFFIX = "-down";//$NON-NLS-1$

  /** the definition separator char */
  static final char DEF_SEP = ';';

  /** the globally shared instance of the evaluation I/O */
  public static final EvaluationIO INSTANCE = new EvaluationIO();

  /** the module descriptions */
  private ModuleDescriptions m_descriptions;

  /** the forbidden constructor */
  private EvaluationIO() {
    super();
  }

  /**
   * Get the evaluation module descriptions
   *
   * @param handle
   *          the handle
   * @return the evaluation module descriptions
   */
  private final ModuleDescriptions __getDescriptions(final Handle handle) {
    if (this.m_descriptions == null) {
      this.m_descriptions = EvaluationModuleDescriptions
          .getDescriptions(handle);
    }
    return this.m_descriptions;
  }

  /** {@inheritDoc} */
  @Override
  protected EvaluationModules createEmpty(final Handle handle) {
    return EvaluationModules.empty();
  }

  /** {@inheritDoc} */
  @Override
  protected final EvaluationModules loadFile(final Path file,
      final Handle handle) throws IOException {
    try (final EvaluationModulesBuilder builder = new EvaluationModulesBuilder()) {
      EvaluationXMLInput.getInstance().use().setLogger(handle)
          .addPath(file).setDestination(builder).create().call();
      return builder.getResult();
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void formPutEditorFields(final String prefix,
      final EvaluationModules data, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    final ModuleDescriptions descriptions;
    ModuleDescription md;
    String modulePrefix;

    out = page.getOut();
    encoded = page.getEncoded();
    descriptions = this.__getDescriptions(null);

    for (final ModuleEntry me : data.getEntries()) {
      md = descriptions.forModule(me.getModule());
      modulePrefix = Page.fieldNameFromPrefixAndName(prefix,
          page.newPrefix());

      out.write("<div id=\""); //$NON-NLS-1$
      out.write(modulePrefix);
      out.write(EvaluationIO.MAIN_DIV_SUFFIX);
      out.append("\"><h3>"); //$NON-NLS-1$

      encoded.append(md.getName());

      out.write("<span class=\"moduleCtrls\">");//$NON-NLS-1$
      out.write(//
      "<input style=\"margin-left:0.4em;margin-right:0.4em\" type=\"button\" value=\"delete\" onclick=\"this.parentElement.parentElement.parentElement.remove()\"/>");//$NON-NLS-1$
      out.write("<input style=\"margin-left:0.4em;margin-right:0.4em\" type=\"button\" id=\"");//$NON-NLS-1$
      encoded.append(modulePrefix);
      out.write(EvaluationIO.UP_BUTTON_SUFFIX);
      out.write("\" value=\"move up\" onclick=\"");//$NON-NLS-1$
      out.write(page.getFunction(_ModuleUpFunctionRenderer.INSTANCE));
      out.write("('");//$NON-NLS-1$
      encoded.append(modulePrefix);
      out.write("')\"/><input style=\"margin-left:0.4em;margin-right:0.4em\" type=\"button\" id=\"");//$NON-NLS-1$
      encoded.append(modulePrefix);
      out.write(EvaluationIO.DOWN_BUTTON_SUFFIX);
      out.write("\" value=\"move down\" onclick=\"");//$NON-NLS-1$
      out.write(page.getFunction(_ModuleDownFunctionRenderer.INSTANCE));
      out.write("('");//$NON-NLS-1$
      encoded.append(modulePrefix);
      out.write("')\"/></span></h3>");//$NON-NLS-1$

      page.onLoad(page.getFunction(_CheckFunctionRenderer.INSTANCE) + '('
          + '\'' + Encoder.htmlEncode(modulePrefix) + '\'' + ')' + ';');

      out.append("</h3><p>");//$NON-NLS-1$
      encoded.append(md.getDescription());
      out.append("</p>");//$NON-NLS-1$
      out.append("<input type=\"hidden\" name=\"");//$NON-NLS-1$
      out.append(EvaluationIO.PARAMETER_MODULE);
      out.append("\" value=\"");//$NON-NLS-1$
      encoded.append(modulePrefix);
      out.append(EvaluationIO.DEF_SEP);
      encoded.append(TextUtils.className(me.getModule().getClass()));
      out.append("\"/>");//$NON-NLS-1$

      ConfigIO.INSTANCE.formPutEditorFields(modulePrefix, //
          md.getParameters().dump(me.getConfiguration()), page);

      out.append("</div>");//$NON-NLS-1$
    }

    modulePrefix = Page.fieldNameFromPrefixAndName(prefix,
        page.newPrefix());
    out.append("<h3>Default Values</h3><p>Here you can edit the default values for any argument not provided above.</p><input type=\"hidden\" name=\"");//$NON-NLS-1$
    out.append(EvaluationIO.PARAMETER_MODULE);
    out.append("\" value=\"");//$NON-NLS-1$
    encoded.append(modulePrefix);
    out.append(EvaluationIO.DEF_SEP);
    out.append("\"/>");//$NON-NLS-1$

    ConfigIO.INSTANCE.formPutEditorFields(
        modulePrefix, //
        descriptions.getJointParameters().dump(data.getConfiguration()),
        page);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  protected final void formPutButtons(final String prefix,
      final Loaded<EvaluationModules> data, final Page page)
      throws IOException {
    final ITextOutput encoded;
    final JspWriter out;
    final ModuleDescriptions descriptions;
    final String changeFuncName, selectId;

    out = page.getOut();
    encoded = page.getEncoded();

    out.write("<hr/><div><input type=\"button\" onclick=\""); //$NON-NLS-1$
    descriptions = this.__getDescriptions(null);
    out.write(page.getFunction(_AddFunctionRenderer
        ._getInstance(descriptions)));
    out.write('(');
    out.write('\'');
    encoded.append(prefix);
    out.write("', this)"); //$NON-NLS-1$

    out.write("\" value=\"save &amp; add module\"/>&nbsp;<select id=\"");//$NON-NLS-1$"
    selectId = Page.fieldNameFromPrefixAndName(prefix,
        EvaluationIO.MODULE_ADD_SELECT_ID);
    encoded.append(selectId);
    out.write("\" onchange=\""); //$NON-NLS-1$
    changeFuncName = page.getFunction(_ChangeFunctionRenderer
        ._getInstance(descriptions));
    out.write(changeFuncName);
    out.write('(');
    out.write('\'');
    encoded.append(prefix);
    out.write('\'');
    out.write(')');

    page.onLoad(changeFuncName + '(' + '\'' + Encoder.htmlEncode(prefix)
        + '\'' + ')');

    out.write("\">"); //$NON-NLS-1$
    for (final ModuleDescription md : descriptions) {
      out.write("<option>");//$NON-NLS-1$
      encoded.append(md.getName());
      out.write("</option>");//$NON-NLS-1$
    }
    out.write("</select>");//$NON-NLS-1$
    out.write("</div><div class=\"addModuleDesc\" id=\"");//$NON-NLS-1$
    encoded.append(Page.fieldNameFromPrefixAndName(prefix,
        EvaluationIO.MODULE_ADD_DESC_ID));
    out.write("\"></div><hr/><div>");//$NON-NLS-1$

    super.formPutButtons(prefix, data, page);
    out.write("</div>");//$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  protected final EvaluationModules loadFromRequest(final String prefix,
      final HttpServletRequest request, final Handle handle) {
    final String[] strings;
    final ModuleDescriptions descriptions;
    Definition definition;
    String modulePrefix, str, clazz;
    int i, j;

    descriptions = this.__getDescriptions(handle);

    try (final EvaluationModulesBuilder builder = new EvaluationModulesBuilder()) {
      strings = request.getParameterValues(EvaluationIO.PARAMETER_MODULE);
      if (strings != null) {

        findConfig: for (i = strings.length; (--i) >= 0;) {
          str = strings[i];
          j = str.lastIndexOf(EvaluationIO.DEF_SEP);

          if (j < 0) {
            clazz = null;
            modulePrefix = TextUtils.prepare(str);
          } else {
            clazz = TextUtils.prepare(str.substring(j + 1));
            modulePrefix = TextUtils.prepare(str.substring(0, j));
          }

          if (clazz == null) {
            strings[i] = null;
            try (final ConfigurationBuilder cb = builder
                .setConfiguration()) {
              ConfigIO.INSTANCE.loadConfigurationFromRequest(cb,
                  modulePrefix, descriptions.getJointParameters(),
                  request, handle);
            }
            break findConfig;
          }
        }

        // ok, we got the base config, now load all the modules
        for (final String string : strings) {
          if (string == null) {
            continue;
          }
          j = string.lastIndexOf(EvaluationIO.DEF_SEP);
          if (j < 0) {
            continue;
          }
          clazz = TextUtils.prepare(string.substring(j + 1));
          modulePrefix = TextUtils.prepare(string.substring(0, j));

          if ((clazz != null) && (modulePrefix != null)) {
            try (final ModuleEntryBuilder meb = builder.addModule()) {
              meb.setModule(clazz);

              definition = DefinitionXMLInput.getInstance().forClass(
                  meb.getModule().getClass(), handle);

              try (final ConfigurationBuilder cb = meb.setConfiguration()) {
                ConfigIO.INSTANCE.loadConfigurationFromRequest(cb,
                    modulePrefix, definition, request, handle);
              }
            }
          }
        }
      }
      return builder.getResult();
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final void storeToFile(final EvaluationModules data,
      final Path file, final Handle handle) throws IOException {
    EvaluationXMLOutput.getInstance().use().setLogger(handle)
        .setPath(file).setSource(data).create().call();
  }
}
