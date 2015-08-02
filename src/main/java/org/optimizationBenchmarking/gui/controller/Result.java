package org.optimizationBenchmarking.gui.controller;

import java.util.logging.Level;

/** The result levels */
public final class Result extends Level {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** An operation has succeeded */
  public static final Result SUCCESS = new Result("SUCCESS", //$NON-NLS-1$
      ((Level.INFO.intValue() + Level.WARNING.intValue()) >>> 1));

  /** An operation has failed */
  public static final Result FAILURE = new Result("FAILURE",//$NON-NLS-1$;
      ((Level.WARNING.intValue() + Level.SEVERE.intValue()) >>> 1));

  /**
   * Create the result level
   *
   * @param name
   *          the name
   * @param level
   *          the level
   */
  private Result(final String name, final int level) {
    super(name, level);
  }
}
