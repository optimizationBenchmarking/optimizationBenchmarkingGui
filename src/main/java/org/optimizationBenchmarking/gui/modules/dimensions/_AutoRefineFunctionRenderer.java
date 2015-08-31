package org.optimizationBenchmarking.gui.modules.dimensions;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.gui.utils.editor.EditorModule;

/** The dimension integer-ness function renderer */
final class _AutoRefineFunctionRenderer extends FunctionRenderer {

  /** the globally shared instance */
  static final _AutoRefineFunctionRenderer INSTANCE = new _AutoRefineFunctionRenderer();

  /** create the update function renderer */
  _AutoRefineFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;

    out = page.getOut();
    out.write("(typeId,lowerId,upperId){var select=document.getElementById(typeId);switch(select.value){case 'byte':case 'short':case 'int':case 'long':{if(document.getElementById(lowerId+'");//$NON-NLS-1$
    out.write(EditorModule.BUTTON_ENABLE_SUFFIX);
    out.write("').value){var lower=document.getElementById(lowerId).value;if(lower!=null){lower=String(lower);if(lower.length>0){lower=parseFloat(lower);if(!(isNaN(lower))){if(document.getElementById(upperId+'");//$NON-NLS-1$
    out.write(EditorModule.BUTTON_ENABLE_SUFFIX);
    out.write("').value){var upper=document.getElementById(upperId).value;if(upper!=null){upper=String(upper);if(upper.length>0){upper=parseFloat(upper);if(!(isNaN(upper))){");//$NON-NLS-1$

    out.write("if(lower>upper){alert('Error: lower bound (' + lower + ') is greater than upper bound (' + upper + ').');}else{");//$NON-NLS-1$
    out.write("if(lower>=upper){alert('Warning: lower bound (' + lower + ') is equal to upper bound (' + upper + ').');}}");//$NON-NLS-1$

    out.write("if((lower>=");//$NON-NLS-1$
    out.print(Byte.MIN_VALUE);
    out.write(")&&(upper<=");//$NON-NLS-1$
    out.print(Byte.MAX_VALUE);
    out.write(")){select.value='byte';select.onchange();return;}");//$NON-NLS-1$

    out.write("if((lower>=");//$NON-NLS-1$
    out.print(Short.MIN_VALUE);
    out.write(")&&(upper<=");//$NON-NLS-1$
    out.print(Short.MAX_VALUE);
    out.write(")){select.value='short';select.onchange();return;}");//$NON-NLS-1$

    out.write("if((lower>=");//$NON-NLS-1$
    out.print(Integer.MIN_VALUE);
    out.write(")&&(upper<=");//$NON-NLS-1$
    out.print(Integer.MAX_VALUE);
    out.write(")){select.value='int';select.onchange();return;}");//$NON-NLS-1$

    out.write("}}}}}}}}select.value='long';select.onchange();}}}");//$NON-NLS-1$
  }
}
