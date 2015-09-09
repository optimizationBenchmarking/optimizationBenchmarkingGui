package org.optimizationBenchmarking.gui.controller;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/** the select/de-select all button function */
public final class SelButtonFunctionRenderer extends FunctionRenderer {

  /** the selection button function renderer */
  public static SelButtonFunctionRenderer INSTANCE = new SelButtonFunctionRenderer();

  /**
   * Create the select/de-select all function renderer
   */
  private SelButtonFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;

    out = page.getOut();
    out.write("(formId,value){var form=document.getElementById(formId);if(form!=null){var inputs=form.getElementsByTagName('input');if(inputs!=null){for(i=inputs.length;(--i)>=0;){var input=inputs[i];if(input.type=='checkbox'){if(input.name=='");//$NON-NLS-1$
    out.write(ControllerUtils.PARAMETER_SELECTION);
    out.write("'){input.checked=value;}}}}}}"); //$NON-NLS-1$
  }
}
