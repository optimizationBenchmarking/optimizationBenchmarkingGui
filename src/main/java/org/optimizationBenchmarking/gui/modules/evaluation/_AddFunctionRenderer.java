package org.optimizationBenchmarking.gui.modules.evaluation;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescription;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescriptions;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * The add function renderer: add a new editor module, then save and
 * reload.
 */
final class _AddFunctionRenderer extends FunctionRenderer {

  /** the instance */
  private static volatile _AddFunctionRenderer s_instance;

  /** the module descriptions */
  private final ModuleDescriptions m_descriptions;

  /**
   * create the choice function renderer
   *
   * @param descriptions
   *          the descriptions
   */
  private _AddFunctionRenderer(final ModuleDescriptions descriptions) {
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
  static final _AddFunctionRenderer _getInstance(
      final ModuleDescriptions descriptions) {
    if (_AddFunctionRenderer.s_instance == null) {
      _AddFunctionRenderer.s_instance = new _AddFunctionRenderer(
          descriptions);
    }
    return _AddFunctionRenderer.s_instance;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    final String newPrefix;

    out = page.getOut();

    out.write("(prefix,button){var form=button;while((form!=null)&&(form.tagName.toUpperCase()!='FORM')){form=form.parentNode;}if(form!=null){var sel=document.getElementById(");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(false, "prefix", //$NON-NLS-1$
        false, EvaluationIO.MODULE_ADD_SELECT_ID, true);
    out.write(");if(sel!=null){var text=null;");//$NON-NLS-1$
    out.append("switch(sel.value){");//$NON-NLS-1$

    newPrefix = page.newPrefix();
    encoded = page.getHTMLEncoded();
    for (final ModuleDescription md : this.m_descriptions) {
      out.write("case '");//$NON-NLS-1$
      encoded.append(md.getName());
      out.write("':{text='<input type=\"hidden\" name=\"");//$NON-NLS-1$
      out.append(EvaluationIO.PARAMETER_MODULE);
      out.append("\" value=\"");//$NON-NLS-1$
      page.fieldNameFromPrefixAndNameJS(true, "prefix",//$NON-NLS-1$
          false, newPrefix, true);
      out.append(EvaluationIO.DEF_SEP);
      encoded.append(TextUtils.className(md.getModuleClass()));
      out.write("\"/>';break;}");//$NON-NLS-1$
    }

    out.write("default:{return;}}var dummy=document.createElement('form');dummy.innerHTML=text;var insert=dummy.firstChild;sel.parentNode.insertBefore(insert,sel);dummy.innerHTML='<input type=\"hidden\" name=\"");//$NON-NLS-1$
    out.write(ControllerUtils.INPUT_SUBMIT);
    out.write("\" value=\"");//$NON-NLS-1$
    out.write(ControllerUtils.PARAM_SAVE);
    out.write("\"/>';insert=dummy.firstChild;sel.parentNode.insertBefore(insert,sel);form.submit();}}}");//$NON-NLS-1$
  }
}
