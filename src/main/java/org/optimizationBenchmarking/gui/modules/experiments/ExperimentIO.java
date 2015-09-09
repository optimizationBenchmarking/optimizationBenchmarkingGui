package org.optimizationBenchmarking.gui.modules.experiments;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.data.impl.partial.PartialExperimentSetBuilder;
import org.optimizationBenchmarking.experimentation.data.spec.IExperiment;
import org.optimizationBenchmarking.experimentation.data.spec.IParameterValue;
import org.optimizationBenchmarking.experimentation.io.impl.edi.EDI;
import org.optimizationBenchmarking.experimentation.io.impl.edi.EDIOutput;
import org.optimizationBenchmarking.experimentation.io.impl.edi.FlatEDIInput;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.gui.utils.editor.EditorModule;
import org.optimizationBenchmarking.gui.utils.files.Loaded;
import org.optimizationBenchmarking.utils.collections.iterators.IterablePlusOne;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** A form which allows you to edit experiment sets. */
public final class ExperimentIO extends EditorModule<IExperiment> {
  /** the globally shared instance of the experiment i/o */
  public static final ExperimentIO INSTANCE = new ExperimentIO();

  /** the experiment's name */
  private static final String EXPERIMENT_NAME = "name";//$NON-NLS-1$
  /** the experiment's description */
  private static final String EXPERIMENT_DESC = "description";//$NON-NLS-1$
  /** the parameter value */
  private static final String PARAMETER_VALUE = "value";//$NON-NLS-1$
  /** the parameter id */
  private static final String PARAM_ID = "param[]";//$NON-NLS-1$

  /** the experiment blueprint */
  private static final String BLUEPRINT_ID = "_blueprint_";//$NON-NLS-1$

  /** create the experiments */
  private ExperimentIO() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected final IExperiment createEmpty(final Handle handle) {
    PartialExperimentSetBuilder builder;

    builder = new PartialExperimentSetBuilder();
    builder.experimentBegin(true);
    builder.experimentEnd();
    return builder.getExperimentSet().getData().get(0);
  }

  /** {@inheritDoc} */
  @Override
  protected final IExperiment loadFile(final Path file, final Handle handle)
      throws IOException {
    final PartialExperimentSetBuilder builder;
    final ArrayListView<? extends IExperiment> list;

    builder = new PartialExperimentSetBuilder();
    FlatEDIInput.getInstance().use().setLogger(handle).addPath(file)
        .setDestination(builder).create().call();
    list = builder.getExperimentSet().getData();
    if ((list == null) || (list.isEmpty())) {
      handle.failure("No experiment found."); //$NON-NLS-1$
      return null;
    }
    return list.get(0);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void formPutEditorFields(final String prefix,
      final IExperiment data, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    String id, paramId, paramPrefix, title, name;

    out = page.getOut();
    encoded = page.getHTMLEncoded();

    this.formTableBegin(page);

    id = Page.fieldNameFromPrefixAndName(prefix,
        ExperimentIO.EXPERIMENT_NAME);
    this.formTableFieldRowBegin(id, ExperimentIO.EXPERIMENT_NAME, false,
        page);
    this.formPutString(id, data.getName(), page);
    this.formTableFieldRowEndDescRowBegin(id, false, true, page);
    encoded.append(//
        "The (short, mathematical) name of the experiment."); //$NON-NLS-1$
    this.formTableDescRowEnd(id, false, page);

    this.formTableSpacer(page);
    id = Page.fieldNameFromPrefixAndName(prefix,
        ExperimentIO.EXPERIMENT_DESC);
    this.formTableFieldRowBegin(id, ExperimentIO.EXPERIMENT_DESC, false,
        page);
    this.formPutText(id, data.getDescription(), true, page);
    this.formTableFieldRowEndDescRowBegin(id, false, true, page);
    encoded.append("The description of the experiment."); //$NON-NLS-1$
    this.formTableDescRowEnd(id, false, page);
    this.formTableEnd(page);

    paramId = Page.fieldNameFromPrefixAndName(prefix,
        ExperimentIO.PARAM_ID);

    for (final IParameterValue value : new IterablePlusOne<>(
        data.getParameterSetting(), null)) {
      if ((value != null) && (value.isUnspecified())) {
        continue;
      }

      paramPrefix = Page
          .fieldNameFromPrefixAndName(
              prefix,//
              ((value != null) ? page.newPrefix()
                  : ExperimentIO.BLUEPRINT_ID));
      name = ((value != null) ? value.getOwner().getName() : null);
      title = ((name != null) ? ("Parameter " + name)//$NON-NLS-1$
          : "New Parameter");//$NON-NLS-1$

      this.formPutComponentHead(title, null, prefix, paramPrefix, false,
          true, true, (value == null), (value == null), page);

      out.write("<input type=\"hidden\" name=\""); //$NON-NLS-1$
      out.write(paramId);
      out.write("\" value=\"");//$NON-NLS-1$
      out.write(paramPrefix);
      out.write("\"/>");//$NON-NLS-1$

      this.formTableBegin(page);

      id = Page.fieldNameFromPrefixAndName(paramPrefix,
          ExperimentIO.EXPERIMENT_NAME);
      this.formTableFieldRowBegin(id, ExperimentIO.EXPERIMENT_NAME, false,
          page);
      this.formPutString(id, name, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append(//
          "The (short, mathematical) name of the parameter."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);

      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(paramPrefix,
          ExperimentIO.EXPERIMENT_DESC);
      this.formTableFieldRowBegin(id, ExperimentIO.EXPERIMENT_DESC, false,
          page);
      this.formPutText(id, ((value != null) ? //
      value.getOwner().getDescription()
          : null), true, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append("The description of the parameter."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);

      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(paramPrefix,
          ExperimentIO.PARAMETER_VALUE);
      this.formTableFieldRowBegin(id, ExperimentIO.PARAMETER_VALUE, false,
          page);
      this.formPutString(id, ((value != null) ? value.getValue() : null),
          page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append(//
          "The value of the parameter."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);
      this.formTableEnd(page);

      this.formPutComponentFoot(title, paramPrefix, false, true, page);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final void formPutButtons(final String prefix,
      final Loaded<IExperiment> data, final Page page) throws IOException {
    this.formPutCopyButton(
        prefix,
        Page.fieldNameFromPrefixAndName(prefix, ExperimentIO.BLUEPRINT_ID),
        "add parameter", //$NON-NLS-1$
        null, page);
    page.getOut().append("&nbsp;");//$NON-NLS-1$
    super.formPutButtons(prefix, data, page);
  }

  /** {@inheritDoc} */
  @Override
  protected final IExperiment loadFromRequest(final String prefix,
      final HttpServletRequest request, final Handle handle) {
    final PartialExperimentSetBuilder builder;
    final String[] strings;
    String ignore, field, paramName, paramDesc, paramValue;

    builder = new PartialExperimentSetBuilder();
    builder.experimentBegin(true);

    field = Page.fieldNameFromPrefixAndName(prefix,
        ExperimentIO.EXPERIMENT_NAME);
    builder.experimentSetName(request.getParameter(field));

    field = Page.fieldNameFromPrefixAndName(prefix,
        ExperimentIO.EXPERIMENT_DESC);
    builder.experimentSetDescription(request.getParameter(field));

    field = Page.fieldNameFromPrefixAndName(prefix, ExperimentIO.PARAM_ID);
    strings = request.getParameterValues(field);

    ignore = Page.fieldNameFromPrefixAndName(prefix,
        ExperimentIO.BLUEPRINT_ID);

    if (strings != null) {
      for (final String param : strings) {
        if (ignore.equalsIgnoreCase(param)) {
          continue;
        }

        paramName = TextUtils.prepare(request.getParameter(//
            Page.fieldNameFromPrefixAndName(param,
                ExperimentIO.EXPERIMENT_NAME)));
        if (paramName != null) {
          paramValue = TextUtils.prepare(request.getParameter(//
              Page.fieldNameFromPrefixAndName(param,
                  ExperimentIO.PARAMETER_VALUE)));
          if (paramValue != null) {
            paramDesc = TextUtils.prepare(request.getParameter(//
                Page.fieldNameFromPrefixAndName(param,
                    ExperimentIO.EXPERIMENT_DESC)));

            builder.experimentSetParameterValue(paramName, paramDesc,
                paramValue, null);

          }
        }
      }
    }

    builder.experimentEnd();

    return builder.getExperimentSet().getData().get(0);
  }

  /** {@inheritDoc} */
  @Override
  protected final void storeToFile(final IExperiment data,
      final Path file, final Handle handle) throws IOException {
    EDIOutput.getInstance().use().setLogger(handle).setPath(file)
        .setSource(data).create().call();
  }

  /** {@inheritDoc} */
  @Override
  protected final void formDoPutComponentButtonHelp(final Page page)
      throws IOException {
    this.formPutComponentDefaultButtonHelp(false, true, true, page);
  }

  /** {@inheritDoc} */
  @Override
  protected final String getComponentTypeName() {
    return "parameter"; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  protected final String getDefaultFileSuffix() {
    return EDI.EDI_XML.getDefaultSuffix();
  }
}
