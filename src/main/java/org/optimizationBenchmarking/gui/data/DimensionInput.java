package org.optimizationBenchmarking.gui.data;

import org.optimizationBenchmarking.experimentation.io.impl.edi.EDIInputToolBase;

/**
 * A driver for Experiment Data Interchange (EDI) input only for dimension
 * data. EDI is our default, canonical format for storing and exchanging
 * {@link org.optimizationBenchmarking.experimentation.data experiment data
 * structures}.
 */
public final class DimensionInput extends
    EDIInputToolBase<DimensionsBuilder> {

  /** create */
  DimensionInput() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "EDI Experiment Data Input  for Dimensions"; //$NON-NLS-1$
  }

  /**
   * get the instance of the {@link DimensionInput}
   *
   * @return the instance of the {@link DimensionInput}
   */
  public static final DimensionInput getInstance() {
    return __DimensionInputLoader.INSTANCE;
  }

  /** the loader */
  private static final class __DimensionInputLoader {
    /** create */
    static final DimensionInput INSTANCE = new DimensionInput();
  }
}
