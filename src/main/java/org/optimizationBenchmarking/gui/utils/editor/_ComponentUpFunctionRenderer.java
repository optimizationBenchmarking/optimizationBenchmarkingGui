package org.optimizationBenchmarking.gui.utils.editor;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/** The module up function renderer */
final class _ComponentUpFunctionRenderer extends FunctionRenderer {

  /** the shared instance */
  static final _ComponentUpFunctionRenderer INSTANCE = new _ComponentUpFunctionRenderer();

  /**
   * create the move component up function renderer
   */
  private _ComponentUpFunctionRenderer() {
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
    out.write("');if(div!=null){var other=div.previousElementSibling;div.parentNode.insertBefore(div,other);");//$NON-NLS-1$
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
