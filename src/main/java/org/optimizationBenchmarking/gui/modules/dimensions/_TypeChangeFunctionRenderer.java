package org.optimizationBenchmarking.gui.modules.dimensions;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.gui.utils.editor.EditorModule;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** Update the boundaries when their type changes. */
final class _TypeChangeFunctionRenderer extends FunctionRenderer {

  /** the globally shared instance */
  static final _TypeChangeFunctionRenderer INSTANCE = new _TypeChangeFunctionRenderer();

  /** create the update function renderer */
  _TypeChangeFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput enc;

    out = page.getOut();
    enc = page.getHTMLEncoded();

    out.write("(typeId,lowerId,upperId){var toInt;var newpattern;switch(document.getElementById(typeId).value){case 'byte':case 'short':case 'int':case 'long':{toInt=true;newpattern='");//$NON-NLS-1$
    enc.append(EditorModule.PATTERN_INT);
    out.write("';break;}default:{toInt=false;newpattern='");//$NON-NLS-1$
    enc.append(EditorModule.PATTERN_FLOAT);
    out.write("'}}"); //$NON-NLS-1$

    out.write("var lfield=document.getElementById(lowerId);if(lfield.pattern!=newpattern){if(toInt){var lower=lfield.value;if(lower!=null){lower=String(lower);if(lower.length>0){lfield.value=String(parseInt(lower));}}}lfield.pattern=newpattern;}");//$NON-NLS-1$
    out.write("var ufield=document.getElementById(upperId);if(ufield.pattern!=newpattern){if(toInt){var upper=ufield.value;if(upper!=null){upper=String(upper);if(upper.length>0){ufield.value=String(parseInt(upper));}}}ufield.pattern=newpattern;}");//$NON-NLS-1$

    out.write("}");//$NON-NLS-1$
  }
}
