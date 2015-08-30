package org.optimizationBenchmarking.gui.utils.editor;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** The check function for the visibility button. */
final class _ComponentToggleVisibilityFunctionRenderer extends
FunctionRenderer {

  /** the shared instance */
  static final _ComponentToggleVisibilityFunctionRenderer INSTANCE = new _ComponentToggleVisibilityFunctionRenderer();

  /** create the toggle visibility function renderer */
  private _ComponentToggleVisibilityFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput js;

    out = page.getOut();

    out.write("(prefix){if(prefix!=null){var div=document.getElementById(prefix+'");//$NON-NLS-1$
    out.write(EditorModule.DIV_INNER_SUFFIX);
    out.write("');if(div!=null){var button=document.getElementById(prefix+'");//$NON-NLS-1$
    out.write(EditorModule.BUTTON_VISIBILITY_SUFFIX);
    out.write("');if(button!=null){if(button.value!='");//$NON-NLS-1$
    js = page.getJSEncoded();
    js.append(EditorModule.BUTTON_VISIBILITY_VISIBLE);
    out.write("'){button.value='");//$NON-NLS-1$
    js.append(EditorModule.BUTTON_VISIBILITY_VISIBLE);
    out.write("';div.style.display='block';}else{button.value='");//$NON-NLS-1$
    js.append(EditorModule.BUTTON_VISIBILITY_HIDDEN);
    out.write("';div.style.display='none';}}}}}");//$NON-NLS-1$
  }
}
