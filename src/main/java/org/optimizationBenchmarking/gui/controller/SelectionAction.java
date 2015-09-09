package org.optimizationBenchmarking.gui.controller;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.utils.comparison.EComparison;
import org.optimizationBenchmarking.utils.hash.HashObject;
import org.optimizationBenchmarking.utils.hash.HashUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A class for representing actions which can re-direct a form based on the
 * state of a {@code select}.
 */
public final class SelectionAction extends HashObject {

  /** the action's name */
  final String m_name;
  /** the target page */
  private final String m_page;
  /** the target frane */
  private final String m_frame;
  /** the method name */
  private final String m_method;
  /** the description */
  private final String m_desc;

  /**
   * Create the controller action
   *
   * @param name
   *          the action's name
   * @param page
   *          the action's target page
   * @param frame
   *          the action's target frame ({@code null} means {@code _self})
   * @param method
   *          the action's method ({@code null} means {@code get})
   * @param desc
   *          the action's description
   */
  public SelectionAction(final String name, final String page,
      final String frame, final String method, final String desc) {
    super();
    this.m_name = name;
    this.m_page = page;
    this.m_frame = ((frame != null) ? frame : "_self"); //$NON-NLS-1$
    this.m_method = ((method != null) ? method : "get"); //$NON-NLS-1$
    this.m_desc = desc;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    final SelectionAction sel;

    if (o == this) {
      return true;
    }

    if (o instanceof SelectionAction) {
      sel = ((SelectionAction) o);
      return (EComparison.equals(this.m_name, sel.m_name) && //
          EComparison.equals(this.m_page, sel.m_page) && //
          EComparison.equals(this.m_frame, sel.m_frame) && //
          EComparison.equals(this.m_method, sel.m_method) && //
      EComparison.equals(this.m_desc, sel.m_desc));
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  protected final int calcHashCode() {
    return HashUtils.combineHashes(
        HashUtils.combineHashes(HashUtils.combineHashes(//
            HashUtils.hashCode(this.m_name),//
            HashUtils.hashCode(this.m_page)),
            HashUtils.hashCode(this.m_frame)),//
        HashUtils.combineHashes(//
            HashUtils.hashCode(this.m_method),//
            HashUtils.hashCode(this.m_desc)));
  }

  /**
   * Render the {@code case} statement to be included in a {@code switch}
   * statement to update a form's action, page, and method as well as the
   * action's description.
   *
   * @param out
   *          the output writer
   * @param encoded
   *          the encoded writer
   * @throws IOException
   *           if i/o fails
   */
  final void _jsRenderFormSwitchCase(final JspWriter out,
      final ITextOutput encoded) throws IOException {
    out.write("case '"); //$NON-NLS-1$
    encoded.append(this.m_name);
    out.write("':{if(form!=null){form.method='"); //$NON-NLS-1$
    out.write(this.m_method);
    out.write("';form.action='"); //$NON-NLS-1$
    out.write(this.m_page);
    out.write("';form.target='"); //$NON-NLS-1$
    out.write(this.m_frame);
    out.write("';}if(desc!=null){text='");//$NON-NLS-1$
    if (this.m_desc != null) {
      encoded.append(this.m_desc);
    }
    out.write("';}break;}"); //$NON-NLS-1$
  }
}
