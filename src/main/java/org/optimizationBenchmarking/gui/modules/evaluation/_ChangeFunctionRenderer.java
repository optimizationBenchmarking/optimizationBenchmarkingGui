package org.optimizationBenchmarking.gui.modules.evaluation;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescription;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescriptions;
import org.optimizationBenchmarking.gui.modules.config.ConfigIO;
import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** The change is invoked when the selected module for addition changes */
final class _ChangeFunctionRenderer extends FunctionRenderer {

  /** the instance */
  private static volatile _ChangeFunctionRenderer s_instance;

  /** the module descriptions */
  private final ModuleDescriptions m_descriptions;

  /**
   * create the choice function renderer
   *
   * @param descriptions
   *          the descriptions
   */
  private _ChangeFunctionRenderer(final ModuleDescriptions descriptions) {
    super();
    this.m_descriptions = descriptions;
  }

  /**
   * get the choice function renderer
   *
   * @param descriptions
   *          the descriptions
   * @return the renderer
   */
  static final _ChangeFunctionRenderer _getInstance(
      final ModuleDescriptions descriptions) {
    if (_ChangeFunctionRenderer.s_instance == null) {
      _ChangeFunctionRenderer.s_instance = new _ChangeFunctionRenderer(
          descriptions);
    }
    return _ChangeFunctionRenderer.s_instance;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getEncoded();

    out.write("(prefix){var par=document.getElementById(");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(false, "prefix", false,//$NON-NLS-1$
        EvaluationIO.MODULE_ADD_DESC_ID, true);
    out.write(");if(par!=null){var sel=document.getElementById(");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(false, "prefix", false,//$NON-NLS-1$
        EvaluationIO.MODULE_ADD_SELECT_ID, true);
    out.write(");if(sel!=null){switch(String(sel.value)){");//$NON-NLS-1$
    for (final ModuleDescription md : this.m_descriptions) {
      out.write("case '");//$NON-NLS-1$
      encoded.append(md.getName());
      out.write("':{par.innerHTML='");//$NON-NLS-1$
      out.write(ConfigIO.CURRENT_SELECTION);
      page.printLines(md.getDescription(), true, true);
      out.write("';break;}");//$NON-NLS-1$
    }
    out.write("default:{par.innerHTML='';}}}}}");//$NON-NLS-1$
  }
}
