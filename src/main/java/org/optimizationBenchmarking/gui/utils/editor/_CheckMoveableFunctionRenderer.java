package org.optimizationBenchmarking.gui.utils.editor;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/**
 * The check function renderer: check whether a module can be moved up or
 * down
 */
final class _CheckMoveableFunctionRenderer extends FunctionRenderer {

  /** the shared instance */
  static final _CheckMoveableFunctionRenderer INSTANCE = new _CheckMoveableFunctionRenderer();

  /** create the choice function renderer */
  private _CheckMoveableFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;
    out = page.getOut();

    out.write("(prefix){if(prefix!=null){var div=document.getElementById(prefix+'");//$NON-NLS-1$
    out.write(EditorModule.DIV_MAIN_SUFFIX);
    out.write("');if(div!=null){var upDisplay='none';var downDisplay='none';var sib=div.nextElementSibling;if(sib!=null){if(sib.tagName.toUpperCase()=='DIV'){if(document.getElementById(sib.id.substring(0,sib.id.length-");//$NON-NLS-1$
    out.print(EditorModule.DIV_MAIN_SUFFIX.length());
    out.write(")+'");//$NON-NLS-1$
    out.write(EditorModule.BUTTON_DOWN_SUFFIX);
    out.write("')!=null){downDisplay='inline';}}}sib=div.previousElementSibling;if(sib!=null){if(sib.tagName.toUpperCase()=='DIV'){if(document.getElementById(sib.id.substring(0,sib.id.length-");//$NON-NLS-1$
    out.print(EditorModule.DIV_MAIN_SUFFIX.length());
    out.write(")+'");//$NON-NLS-1$
    out.write(EditorModule.BUTTON_DOWN_SUFFIX);
    out.write("')!=null){upDisplay='inline';}}}var e=document.getElementById(prefix+'");//$NON-NLS-1$
    out.write(EditorModule.BUTTON_UP_SUFFIX);
    out.write("');if(e!=null){e.style.display=upDisplay;}var e=document.getElementById(prefix+'");//$NON-NLS-1$
    out.write(EditorModule.BUTTON_DOWN_SUFFIX);
    out.write("');if(e!=null){e.style.display=downDisplay;}}}}");//$NON-NLS-1$
  }
}
