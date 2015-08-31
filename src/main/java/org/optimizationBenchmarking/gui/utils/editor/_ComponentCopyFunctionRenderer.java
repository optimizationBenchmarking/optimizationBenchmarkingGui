package org.optimizationBenchmarking.gui.utils.editor;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/** Make a copy of a component */
final class _ComponentCopyFunctionRenderer extends FunctionRenderer {

  /** the shared instance */
  static final _ComponentCopyFunctionRenderer INSTANCE = new _ComponentCopyFunctionRenderer();

  /**
   * create the copy component function renderer
   */
  private _ComponentCopyFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;

    out = page.getOut();
    out.write("(globalPrefix,innerPrefix){div=document.getElementById(innerPrefix+'");//$NON-NLS-1$
    out.write(EditorModule.DIV_MAIN_SUFFIX);
    out.write("');if(div!=null){temp=document.createElement('div');var copy=div.cloneNode(true);copy.style.display='block';temp.appendChild(copy);temp.innerHTML=temp.innerHTML.replace(new RegExp(innerPrefix,'g'),");//$NON-NLS-1$
    page.jsCreateUniqueId(false, "globalPrefix", false);//$NON-NLS-1$
    out.write(");div.parentNode.insertBefore(temp.firstChild,div);}}");//$NON-NLS-1$
  }
}
