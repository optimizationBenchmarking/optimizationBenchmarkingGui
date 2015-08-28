package org.optimizationBenchmarking.gui.modules.dimensions;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.data.impl.abstr.BasicDimensionSet;
import org.optimizationBenchmarking.experimentation.data.spec.IDimension;
import org.optimizationBenchmarking.experimentation.data.spec.IDimensionSet;
import org.optimizationBenchmarking.experimentation.io.impl.edi.EDIOutput;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.data.DimensionInput;
import org.optimizationBenchmarking.gui.data.DimensionsBuilder;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.gui.utils.editor.EditorModule;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;

/**
 * This editor module allows us to generate a dynamic form for editing
 * dimension files.
 */
public final class DimensionsIO extends EditorModule<IDimensionSet> {
  /** the dimension id */
  static final String DIMENSION_SET = "dimension[]"; //$NON-NLS-1$

  /** the globally shared instance of the dimension i/o */
  public static final DimensionsIO INSTANCE = new DimensionsIO();

  /** the forbidden constructor */
  private DimensionsIO() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  protected final IDimensionSet createEmpty(final Handle handle) {
    return new BasicDimensionSet(null,
        ((ArrayListView) (ArraySetView.EMPTY_SET_VIEW)));
  }

  /** {@inheritDoc} */
  @Override
  protected final IDimensionSet loadFile(final Path file,
      final Handle handle) throws IOException {
    final DimensionsBuilder builder;
    builder = new DimensionsBuilder();
    DimensionInput.getInstance().use().setLogger(handle).addPath(file)
        .setDestination(builder).create().call();
    return builder.getDimensionSet();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void formPutEditorFields(final String prefix,
      final IDimensionSet data, final Page page) throws IOException {
    final JspWriter out;
    String dimPrefix, dimTitle, dimName;

    out = page.getOut();

    for (final IDimension dim : data.getData()) {
      dimPrefix = Page.fieldNameFromPrefixAndName(prefix,//
          page.newPrefix());

      dimName = dim.getName();
      dimTitle = ("Dimension " + dimName);//$NON-NLS-1$

      this.formPutComponentHead(dimName, null, dimPrefix, true, true,
          true, page);

      out.write("<input type=\"hidden\" name=\""); //$NON-NLS-1$
      out.write(DimensionsIO.DIMENSION_SET);
      out.write("\" value=\"");//$NON-NLS-1$
      out.write(dimPrefix);
      out.write("\"/>");//$NON-NLS-1$

      this.formPutComponentFoot(dimTitle, dimPrefix, true, true, page);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final IDimensionSet loadFromRequest(final String prefix,
      final HttpServletRequest request, final Handle handle) {
    // TODO Auto-generated method stub
    return null;
  }

  /** {@inheritDoc} */
  @Override
  protected final void storeToFile(final IDimensionSet data,
      final Path file, final Handle handle) throws IOException {
    EDIOutput.getInstance().use().setLogger(handle).setPath(file)
        .setSource(data).create().call();
  }

  /** {@inheritDoc} */
  @Override
  protected final String getComponentTypeName() {
    return "dimension";//$NON-NLS-1$
  }

}
