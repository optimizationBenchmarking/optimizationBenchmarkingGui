package org.optimizationBenchmarking.gui.modules.config;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/** The toggle field function renderer */
final class _ToggleFunctionRenderer extends FunctionRenderer {

  /** the shared instance */
  static final _ToggleFunctionRenderer INSTANCE = new _ToggleFunctionRenderer();

  /**
   * create the choice function renderer
   */
  private _ToggleFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;

    out = page.getOut();
    out.write(//
    "(id){var box=document.getElementById(id+'");//$NON-NLS-1$
    out.write(ConfigIO.ENABLER_SUFFIX);
    out.write(//
    "');if(box!=null){var value=box.checked;var control=document.getElementById(id);if(control!=null){control.disabled=!value;}var row=document.getElementById(id+'");//$NON-NLS-1$
    out.write(ConfigIO.SUFFIX_FIELD_ROW);
    out.write(//
    "');if(row!=null){if(value){row.style.background='transparent';}else{row.style.background='#eeeeee';}}row=document.getElementById(id+'");//$NON-NLS-1$
    out.write(ConfigIO.SUFFIX_DESC_ROW);
    out.write(//
    "');if(row!=null){if(value){row.style.display='table-row';}else{row.style.display='none';}}row=document.getElementById(id+'");//$NON-NLS-1$
    out.write(ConfigIO.SUFFIX_CHOICE_ROW);
    out.write(//
    "');if(row!=null){if(value){row.style.display='table-row';}else{row.style.display='none';}}}}"); //$NON-NLS-1$
  }
}
