package org.optimizationBenchmarking.gui.utils.editor;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;

/** The add text (area) field function renderer */
final class _AddTextFieldFunctionRenderer extends FunctionRenderer {

  /** the shared instance */
  static final _AddTextFieldFunctionRenderer INSTANCE = new _AddTextFieldFunctionRenderer();

  /**
   * create the choice function renderer
   */
  private _AddTextFieldFunctionRenderer() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;

    out = page.getOut();
    out.write("(prefix,wrap){var name=document.getElementById(");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(false, "prefix", false, //$NON-NLS-1$
        EditorModule.NEW_FIELD_NAME, true);
    out.write(").value;if((name!=null)&&(name.length>0)){var value=document.getElementById(");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(false, "prefix", false, //$NON-NLS-1$
        EditorModule.NEW_FIELD_VALUE, true);
    out.write(").value;if(value==null){value='';}var newbody='");//$NON-NLS-1$

    out.write(EditorModule.CONFIG_ROW_START_1);
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        EditorModule.NAME_STRING, false);
    out.write(EditorModule.TABLE_FIELD_ROW_SUFFIX);
    out.write(EditorModule.CONFIG_ROW_START_2);
    out.write("'+name+'");//$NON-NLS-1$
    out.write(EditorModule.CONFIG_NAME_END);
    out.write("/>");//$NON-NLS-1$
    out.write(EditorModule.TEXT_AREA_BEGIN);
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        EditorModule.NAME_STRING, false);
    out.write("\" name=\"");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        EditorModule.NAME_STRING, false);
    out.write("'+(wrap?'");//$NON-NLS-1$
    out.write(EditorModule.TEXT_AREA_WRAPPER_CLASS);
    out.write("':'");//$NON-NLS-1$
    out.write(EditorModule.TEXT_AREA_NOWRAPPER_CLASS);
    out.write("')+value+'"); //$NON-NLS-1$
    out.write(EditorModule.TEXT_AREA_END);
    out.write(EditorModule.CONFIG_FIELD_END);
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        EditorModule.NAME_STRING, false);
    out.write(EditorModule.BUTTON_ENABLE_SUFFIX);
    out.write(EditorModule.ID_STRING);
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        EditorModule.NAME_STRING, false);
    out.write(EditorModule.BUTTON_ENABLE_SUFFIX);
    out.write("\" checked onclick=\"");//$NON-NLS-1$
    out.write(page.getFunction(//
        _FieldEnableUpdateFunctionRenderer.INSTANCE));
    out.write("(\\'");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        EditorModule.NAME_STRING, false);
    out.write("\\')\"/></td></tr>';var rowToInsertBefore=document.getElementById('");//$NON-NLS-1$
    page.fieldNameFromPrefixAndNameJS(true, "prefix", false, //$NON-NLS-1$
        EditorModule.ADD_FIELD_ROW_ID, true);
    out.write("');var dummy=document.createElement('table');dummy.innerHTML=newbody;var insert=dummy.firstChild;if(insert.tagName.toUpperCase()=='TBODY'){insert=insert.firstChild;}rowToInsertBefore.parentNode.insertBefore(insert,rowToInsertBefore);dummy.innerHTML='");//$NON-NLS-1$
    out.write(EditorModule.CONFIG_ROW_SPACER);
    out.write("';insert=dummy.firstChild;if(insert.tagName.toUpperCase()=='TBODY'){insert=insert.firstChild;}rowToInsertBefore.parentNode.insertBefore(insert,rowToInsertBefore);}}");//$NON-NLS-1$
  }
}
