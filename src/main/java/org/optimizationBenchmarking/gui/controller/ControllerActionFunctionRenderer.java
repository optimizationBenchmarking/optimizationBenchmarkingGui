package org.optimizationBenchmarking.gui.controller;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** the selection action function renderer */
public final class ControllerActionFunctionRenderer extends
    FunctionRenderer {

  /** the selected actions */
  private final HashSet<SelectionAction> m_actions;

  /** the prefix text */
  private final String m_prefix;

  /**
   * Create the selection action function renderer
   *
   * @param prefix
   *          the prefix for the description
   */
  public ControllerActionFunctionRenderer(final String prefix) {
    super();
    this.m_actions = new HashSet<>();
    this.m_prefix = prefix;
  }

  /**
   * Add the selection actions
   *
   * @param actions
   *          the actions to add
   */
  final void _addActions(final SelectionAction[] actions) {
    if (actions != null) {
      for (final SelectionAction action : actions) {
        this.m_actions.add(action);
      }
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getJSEncoded();

    out.write("(prefix){var sel=document.getElementById(prefix+'Selection');if(sel!=null){var text='';var form=document.getElementById(prefix+'Form');var desc=document.getElementById(prefix+'Desc');if((form!=null)||(desc!=null)){switch(sel.value){"); //$NON-NLS-1$
    for (final SelectionAction action : this.m_actions) {
      action._jsRenderFormSwitchCase(out, encoded);
    }
    out.write("default:{if(form!=null){form.action=window.location.pathname;form.method='get';form.target='_self';}}"); //$NON-NLS-1$
    out.write("}if(desc!=null){if((text!=null)&&(text.length>0)){desc.innerHTML='"); //$NON-NLS-1$
    out.append(this.m_prefix);
    out.write("'+text;}else{desc.innerHTML='';}}"); //$NON-NLS-1$
    out.write("}}}"); //$NON-NLS-1$
  }
}
