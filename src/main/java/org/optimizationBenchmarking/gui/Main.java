package org.optimizationBenchmarking.gui;

import org.optimizationBenchmarking.gui.application.ApplicationInstance;
import org.optimizationBenchmarking.gui.application.ApplicationTool;
import org.optimizationBenchmarking.utils.config.Configuration;

/** The main class for the GUI. */
public final class Main {

  /**
   * The entry point for the gui.
   *
   * @param args
   *          the command line arguments
   * @throws Throwable
   *           if something goes wrong
   */
  public static final void main(final String[] args) throws Throwable {

    Configuration.setup(args);

    try (final ApplicationInstance instance = ApplicationTool
        .getInstance().use().configure(Configuration.getRoot()).create()) {
      instance.waitFor();
    }
  }

}
