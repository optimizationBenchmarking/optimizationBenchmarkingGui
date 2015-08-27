package org.optimizationBenchmarking.gui.utils;

import java.io.IOException;

/** the base class for function renderers */
public abstract class FunctionRenderer {

  /** create the function renderer */
  protected FunctionRenderer() {
    super();
  }

  /**
   * Render the function, i.e., write its parameters and body. Right before
   * this method is called, the system write {@code function functionName}
   * to the output, where {@code functionName} is replaced with the
   * dynamically assigned function name. You can now write the parameters
   * and the body of the function.
   *
   * @param page
   *          the page
   * @throws IOException
   *           if i/o fails
   */
  public abstract void render(final Page page) throws IOException;

}
