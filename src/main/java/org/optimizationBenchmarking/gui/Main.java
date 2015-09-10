package org.optimizationBenchmarking.gui;

import java.awt.GraphicsEnvironment;
import java.awt.SplashScreen;

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
      if (instance.hasBrowser()) {
        try {
          Thread.sleep(400);
        } catch (final Throwable ignorable) {
          // ingore
        }
      }
      Main.__closeSplash();
      instance.waitFor();
    } finally {
      Main.__closeSplash();
    }
  }

  /** close the splash screen, if it is open */
  private static final void __closeSplash() {
    final SplashScreen screen;
    try {
      if (!(GraphicsEnvironment.isHeadless())) {
        screen = SplashScreen.getSplashScreen();
        if (screen != null) {
          screen.close();
        }
      }
    } catch (final Throwable ignorable) {
      // iignore
    }
  }
}
