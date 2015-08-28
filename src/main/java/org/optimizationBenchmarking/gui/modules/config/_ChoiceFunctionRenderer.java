package org.optimizationBenchmarking.gui.modules.config;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.utils.FunctionRenderer;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.gui.utils.editor.EditorModule;
import org.optimizationBenchmarking.utils.config.DefinitionElement;
import org.optimizationBenchmarking.utils.config.InstanceParameter;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** The choice function renderer */
final class _ChoiceFunctionRenderer extends FunctionRenderer {
  /** the choice function renderer */
  private final InstanceParameter<?> m_instance;

  /**
   * create the choice function renderer
   *
   * @param instance
   *          the instance parameter
   */
  _ChoiceFunctionRenderer(final InstanceParameter<?> instance) {
    super();
    this.m_instance = instance;
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return (2342357 ^ this.m_instance.hashCode());
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    return ((o == this) || //
    ((o instanceof _ChoiceFunctionRenderer) && //
    (this.m_instance.equals(((_ChoiceFunctionRenderer) o).m_instance))));
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void render(final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getEncoded();
    out.write("(id){var text='");//$NON-NLS-1$
    out.write(EditorModule.PLEASE_SELECT_OPTION);
    out.write("';switch(document.getElementById(id).value){");//$NON-NLS-1$
    for (final DefinitionElement de : this.m_instance.getChoices()) {
      out.write("case '");//$NON-NLS-1$
      encoded.append(de.getName());
      out.write("':{text='");//$NON-NLS-1$
      page.printLines(de.getDescription(), true, true);
      out.write("';break;}");//$NON-NLS-1$
    }
    out.write("}document.getElementById(id+'");//$NON-NLS-1$
    out.write(ConfigIO.SUFFIX_CHOICE_CELL);
    out.write("').innerHTML='");//$NON-NLS-1$
    out.write(ConfigIO.CURRENT_SELECTION);
    out.write("'+text;}");//$NON-NLS-1$
  }
}
