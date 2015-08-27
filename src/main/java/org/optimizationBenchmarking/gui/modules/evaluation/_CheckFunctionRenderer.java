package org.optimizationBenchmarking.gui.modules.evaluation;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/**
 * The check function renderer: check whether a module can be moved up or
 * down
 */
final class _CheckFunctionRenderer extends FunctionRenderer {

  /** the shared instance */
  static final _CheckFunctionRenderer INSTANCE = new _CheckFunctionRenderer();

  /**
   * create the choice function renderer
   */
  private _CheckFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;
    out = page.getOut();

    out.write("(prefix){if(prefix!=null){var div=document.getElementById(prefix+'");//$NON-NLS-1$
    out.write(EvaluationIO.MAIN_DIV_SUFFIX);
    out.write("');if(div!=null){var upDisplay='none';var downDisplay='none';var sib=div.nextElementSibling;if(sib!=null){if(sib.tagName.toUpperCase()=='DIV'){downDisplay='inline';}}sib=div.previousElementSibling;if(sib!=null){if(sib.tagName.toUpperCase()=='DIV'){upDisplay='inline';}}var e=document.getElementById(prefix+'");//$NON-NLS-1$
    out.write(EvaluationIO.UP_BUTTON_SUFFIX);
    out.write("');if(e!=null){e.style.display=upDisplay;}var e=document.getElementById(prefix+'");//$NON-NLS-1$
    out.write(EvaluationIO.DOWN_BUTTON_SUFFIX);
    out.write("');if(e!=null){e.style.display=downDisplay;}}}}");//$NON-NLS-1$
  }
}
