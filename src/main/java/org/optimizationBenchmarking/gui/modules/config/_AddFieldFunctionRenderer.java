package org.optimizationBenchmarking.gui.modules.config;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/** The add field function renderer */
final class _AddFieldFunctionRenderer extends FunctionRenderer {

  /** the shared instance */
  static final _AddFieldFunctionRenderer INSTANCE = new _AddFieldFunctionRenderer();

  /**
   * create the choice function renderer
   */
  private _AddFieldFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;

    out = page.getOut();
    out.write("(prefix){var name=document.getElementById(");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(false, "prefix", false, //$NON-NLS-1$
        ConfigIO.NEW_FIELD_NAME, true);
    out.write(").value;if((name!=null)&&(name.length>0)){var newbody='");//$NON-NLS-1$
    out.write(ConfigIO.CONFIG_ROW_START_1);
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        "name", false);//$NON-NLS-1$    
    out.write(ConfigIO.SUFFIX_FIELD_ROW);
    out.write(ConfigIO.CONFIG_ROW_START_2);
    out.write("'+name+'");//$NON-NLS-1$
    out.write(ConfigIO.CONFIG_NAME_END);

    out.write("<input type=\"text\" size=\"60\" name=\"");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        "name", false);//$NON-NLS-1$   
    out.write("\" id=\"");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        "name", false);//$NON-NLS-1$   
    out.write("\">"); //$NON-NLS-1$

    out.write(ConfigIO.CONFIG_FIELD_END);
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        "name", false);//$NON-NLS-1$    
    out.write(ConfigIO.ENABLER_SUFFIX);
    out.write("\" id=\"");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        "name", false);//$NON-NLS-1$    
    out.write(ConfigIO.ENABLER_SUFFIX);
    out.write("\" checked onclick=\"");//$NON-NLS-1$
    out.write(page.getFunction(_ToggleFunctionRenderer.INSTANCE));
    out.write("(\\'");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        "name", false);//$NON-NLS-1$   
    out.write("\\')\"/></td></tr>';var rowToInsertBefore=document.getElementById('");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        ConfigIO.ADD_FIELD_ROW_ID, true);
    out.write("');var dummy=document.createElement('table');dummy.innerHTML=newbody;var insert=dummy.firstChild;if(insert.tagName.toUpperCase()=='TBODY'){insert=insert.firstChild;}rowToInsertBefore.parentNode.insertBefore(insert,rowToInsertBefore);dummy.innerHTML='");//$NON-NLS-1$
    out.write(ConfigIO.CONFIG_ROW_SPACER);
    out.write("';insert=dummy.firstChild;if(insert.tagName.toUpperCase()=='TBODY'){insert=insert.firstChild;}rowToInsertBefore.parentNode.insertBefore(insert,rowToInsertBefore);}}");//$NON-NLS-1$
  }
}
