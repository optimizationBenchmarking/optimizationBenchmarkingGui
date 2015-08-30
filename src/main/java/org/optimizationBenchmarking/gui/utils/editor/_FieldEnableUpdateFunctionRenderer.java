package org.optimizationBenchmarking.gui.utils.editor;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/** The toggle field function renderer */
final class _FieldEnableUpdateFunctionRenderer extends FunctionRenderer {

  /** the shared instance */
  static final _FieldEnableUpdateFunctionRenderer INSTANCE = new _FieldEnableUpdateFunctionRenderer();

  /**
   * create the choice function renderer
   */
  private _FieldEnableUpdateFunctionRenderer() {
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
    out.write(EditorModule.BUTTON_ENABLE_SUFFIX);
    out.write(//
    "');if(box!=null){var value=box.checked;var control=document.getElementById(id);if(control!=null){control.disabled=!value;}var row=document.getElementById(id+'");//$NON-NLS-1$
    out.write(EditorModule.TABLE_FIELD_ROW_SUFFIX);
    out.write(//
    "');if(row!=null){if(value){row.style.background='transparent';}else{row.style.background='#bbbbbb';}}row=document.getElementById(id+'");//$NON-NLS-1$
    out.write(EditorModule.TABLE_DESC_ROW_SUFFIX);
    out.write(//
    "');if(row!=null){if(value){row.style.display='table-row';}else{row.style.display='none';}}row=document.getElementById(id+'");//$NON-NLS-1$
    out.write(EditorModule.TABLE_CHOICE_ROW_SUFFIX);
    out.write(//
    "');if(row!=null){if(value){row.style.display='table-row';}else{row.style.display='none';}}}}"); //$NON-NLS-1$
  }
}
