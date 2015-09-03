package org.optimizationBenchmarking.gui.utils.editor;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/**
 * The delete function renderer, which deletes an element according to
 * http://stackoverflow.com/a/14887752
 */
final class _DeleteFunctionRenderer extends FunctionRenderer {

  /** the shared instance */
  static final _DeleteFunctionRenderer INSTANCE = new _DeleteFunctionRenderer();

  /** create the delete function renderer */
  private _DeleteFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;

    out = page.getOut();
    out.write(//
    "(d){if(d!=null){var a=d.attributes,i,l,n;if(a){for(i=a.length;(--i)>=0;){n=a[i].name;if(typeof d[n]==='function'){d[n]=null;}}}a=d.childNodes;if(a){l=a.length;for(i=0;i<l;i+=1){");//$NON-NLS-1$
    out.write(page.getFunction(this));
    out.write("(d.childNodes[i]);}}if(d.parentElement!=null){d.parentElement.removeChild(d);}}}");//$NON-NLS-1$
  }
}
