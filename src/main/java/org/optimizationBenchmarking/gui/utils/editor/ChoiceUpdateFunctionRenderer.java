package org.optimizationBenchmarking.gui.utils.editor;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.DefinitionElement;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** The choice function renderer */
public class ChoiceUpdateFunctionRenderer extends FunctionRenderer {
  /** the choices */
  private final ArrayListView<DefinitionElement> m_choices;

  /**
   * create the choice function renderer
   *
   * @param choices
   *          the list of choices
   */
  public ChoiceUpdateFunctionRenderer(
      final ArrayListView<DefinitionElement> choices) {
    super();
    this.m_choices = choices;
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return (2342357 ^ this.m_choices.hashCode());
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    return ((o == this) || //
    ((o instanceof ChoiceUpdateFunctionRenderer) && //
    (this.m_choices.equals(((ChoiceUpdateFunctionRenderer) o).m_choices))));
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getHTMLEncoded();
    out.write("(id){var text='");//$NON-NLS-1$
    out.write(EditorModule.PLEASE_SELECT_OPTION);
    out.write("';switch(document.getElementById(id).value){");//$NON-NLS-1$
    for (final DefinitionElement de : this.m_choices) {
      out.write("case '");//$NON-NLS-1$
      encoded.append(de.getName());
      out.write("':{text='");//$NON-NLS-1$
      page.printLines(de.getDescription(), true, true);
      out.write("';break;}");//$NON-NLS-1$
    }
    out.write("}document.getElementById(id+'");//$NON-NLS-1$
    out.write(EditorModule.TABLE_CHOICE_CELL_SUFFIX);
    out.write("').innerHTML='");//$NON-NLS-1$
    out.write(EditorModule.CURRENT_SELECTION);
    out.write("'+text;}");//$NON-NLS-1$
  }
}
