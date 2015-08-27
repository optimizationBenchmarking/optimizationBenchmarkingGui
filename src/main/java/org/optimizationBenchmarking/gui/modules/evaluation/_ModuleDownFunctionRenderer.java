package org.optimizationBenchmarking.gui.modules.evaluation;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/** The module down function renderer */
final class _ModuleDownFunctionRenderer extends FunctionRenderer {

  /** the shared instance */
  static final _ModuleDownFunctionRenderer INSTANCE = new _ModuleDownFunctionRenderer();

  /**
   * create the choice function renderer
   */
  private _ModuleDownFunctionRenderer() {
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
    out.write(EvaluationIO.MAIN_DIV_SUFFIX);
    out.write("');if(div!=null){var other=div.nextElementSibling;div.parentNode.insertBefore(other,div);");//$NON-NLS-1$
    checkFunction = page.getFunction(_CheckFunctionRenderer.INSTANCE);
    out.write(checkFunction);
    out.write("(prefix);");//$NON-NLS-1$
    out.write(checkFunction);
    out.write("(other.id.substring(0,other.id.length-");//$NON-NLS-1$
    out.print(EvaluationIO.MAIN_DIV_SUFFIX.length());
    out.write("));}}");//$NON-NLS-1$
  }
}
