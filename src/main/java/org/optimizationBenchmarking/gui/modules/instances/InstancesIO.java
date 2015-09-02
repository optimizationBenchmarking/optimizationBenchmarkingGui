package org.optimizationBenchmarking.gui.modules.instances;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.data.impl.abstr.BasicInstanceSet;
import org.optimizationBenchmarking.experimentation.data.impl.partial.PartialExperimentSetBuilder;
import org.optimizationBenchmarking.experimentation.data.spec.IInstance;
import org.optimizationBenchmarking.experimentation.data.spec.IInstanceSet;
import org.optimizationBenchmarking.experimentation.io.impl.edi.EDIOutput;
import org.optimizationBenchmarking.experimentation.io.impl.edi.FlatEDIInput;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.Loaded;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.gui.utils.editor.EditorModule;
import org.optimizationBenchmarking.utils.collections.iterators.IterablePlusOne;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** A form which allows you to edit instance sets. */
public final class InstancesIO extends EditorModule<IInstanceSet> {
  /** the globally shared instance of the instance i/o */
  public static final InstancesIO INSTANCE = new InstancesIO();

  /** the instance id */
  static final String INSTANCE_SET = "instance[]"; //$NON-NLS-1$
  /** the instance's name */
  private static final String INSTANCE_NAME = "name";//$NON-NLS-1$
  /** the instance's description */
  private static final String INSTANCE_DESC = "description";//$NON-NLS-1$
  /** the dimension blueprint */
  private static final String BLUEPRINT_ID = "_blueprint_";//$NON-NLS-1$

  /** create the instances */
  private InstancesIO() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  protected final IInstanceSet createEmpty(final Handle handle) {
    return new BasicInstanceSet(null,//
        ((Collection) (ArraySetView.EMPTY_SET_VIEW)));
  }

  /** {@inheritDoc} */
  @Override
  protected final IInstanceSet loadFile(final Path file,
      final Handle handle) throws IOException {
    final PartialExperimentSetBuilder builder;
    builder = new PartialExperimentSetBuilder();
    FlatEDIInput.getInstance().use().setLogger(handle).addPath(file)
        .setDestination(builder).create().call();
    return builder.getInstanceSet();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void formPutEditorFields(final String prefix,
      final IInstanceSet data, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    String instId, instPrefix, instTitle, instName, id;

    out = page.getOut();
    encoded = page.getHTMLEncoded();
    instId = Page.fieldNameFromPrefixAndName(prefix,
        InstancesIO.INSTANCE_SET);
    for (final IInstance inst : new IterablePlusOne<>(data.getData(), null)) {

      if (inst != null) {
        instPrefix = Page.fieldNameFromPrefixAndName(prefix,//
            page.newPrefix());
        instName = inst.getName();
      } else {
        instPrefix = Page.fieldNameFromPrefixAndName(prefix,
            InstancesIO.BLUEPRINT_ID);
        instName = "New Instance";//$NON-NLS-1$
      }
      instTitle = ("Instance " + instName);//$NON-NLS-1$

      this.formPutComponentHead(instTitle, null, prefix, instPrefix, true,
          true, true, (inst == null), (inst == null), page);

      out.write("<input type=\"hidden\" name=\""); //$NON-NLS-1$
      out.write(instId);
      out.write("\" value=\"");//$NON-NLS-1$
      out.write(instPrefix);
      out.write("\"/>");//$NON-NLS-1$

      this.formTableBegin(page);

      id = Page.fieldNameFromPrefixAndName(instPrefix,
          InstancesIO.INSTANCE_NAME);
      this.formTableFieldRowBegin(id, InstancesIO.INSTANCE_NAME, false,
          page);
      this.formPutString(id, instName, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append(//
          "The (short, mathematical) name of the instance, as it will appear in formulas in the report and can be used as instance in the evaluation definitions."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);

      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(instPrefix,
          InstancesIO.INSTANCE_DESC);
      this.formTableFieldRowBegin(id, InstancesIO.INSTANCE_DESC, false,
          page);
      this.formPutText(id, //
          ((inst != null) ? inst.getDescription()
              : "Enter description here."),//$NON-NLS-1$
          true, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append("A description of the instance."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);

      this.formTableEnd(page);

      this.formPutComponentFoot(instTitle, instPrefix, true, true, page);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final IInstanceSet loadFromRequest(final String prefix,
      final HttpServletRequest request, final Handle handle) {
    final PartialExperimentSetBuilder builder;
    final String[] strings;

    builder = new PartialExperimentSetBuilder();

    strings = request.getParameterValues(//
        Page.fieldNameFromPrefixAndName(prefix, InstancesIO.INSTANCE_SET));

    if (strings != null) {
      for (final String dprefix : strings) {
        if (InstancesIO.BLUEPRINT_ID.equalsIgnoreCase(//
            Page.nameFromPrefixAndFieldName(prefix, dprefix))) {
          continue;
        }

        builder.instanceBegin(true);
        builder.instanceSetName(request.getParameter(//
            Page.fieldNameFromPrefixAndName(dprefix,
                InstancesIO.INSTANCE_NAME)));
        builder.instanceSetDescription(request.getParameter(//
            Page.fieldNameFromPrefixAndName(dprefix,
                InstancesIO.INSTANCE_DESC)));
        builder.instanceEnd();
      }
    }

    return builder.getInstanceSet();
  }

  /** {@inheritDoc} */
  @Override
  protected final void formPutButtons(final String prefix,
      final Loaded<IInstanceSet> data, final Page page) throws IOException {
    this.formPutCopyButton(prefix,
        Page.fieldNameFromPrefixAndName(prefix, InstancesIO.BLUEPRINT_ID),
        "add instance", //$NON-NLS-1$
        null, page);
    page.getOut().append("&nbsp;");//$NON-NLS-1$
    super.formPutButtons(prefix, data, page);
  }

  /** {@inheritDoc} */
  @Override
  protected final void storeToFile(final IInstanceSet data,
      final Path file, final Handle handle) throws IOException {
    EDIOutput.getInstance().use().setLogger(handle).setPath(file)
        .setSource(data).create().call();
  }
}
