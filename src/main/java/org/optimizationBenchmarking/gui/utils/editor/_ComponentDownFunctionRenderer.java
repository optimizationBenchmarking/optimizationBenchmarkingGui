package org.optimizationBenchmarking.gui.utils.editor;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/** The module down function renderer */
final class _ComponentDownFunctionRenderer extends FunctionRenderer {

  /** the shared instance */
  static final _ComponentDownFunctionRenderer INSTANCE = new _ComponentDownFunctionRenderer();

  /**
   * create the move component down function renderer
   */
  private _ComponentDownFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;
    final String checkFunction;

    out = page.getOut();
    out.write("(prefix){var div=document.getElementById(prefix+'");//$NON-NLS-1$
    out.write(EditorModule.DIV_MAIN_SUFFIX);
    out.write("');if(div!=null){var other=div.nextElementSibling;div.parentNode.insertBefore(other,div);");//$NON-NLS-1$
    checkFunction = page
        .getFunction(_CheckMoveableFunctionRenderer.INSTANCE);
    out.write(checkFunction);
    out.write("(prefix);");//$NON-NLS-1$
    out.write(checkFunction);
    out.write("(other.id.substring(0,other.id.length-");//$NON-NLS-1$
    out.print(EditorModule.DIV_MAIN_SUFFIX.length());
    out.write("));}}");//$NON-NLS-1$
  }
}
