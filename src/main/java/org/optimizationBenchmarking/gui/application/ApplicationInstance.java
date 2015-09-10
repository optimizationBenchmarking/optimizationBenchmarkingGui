package org.optimizationBenchmarking.gui.application;

import java.io.Closeable;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.gui.server.ServerInstance;
import org.optimizationBenchmarking.gui.server.ServerInstanceBuilder;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.net.NetworkUtils;
import org.optimizationBenchmarking.utils.tools.impl.abstr.ToolJob;
import org.optimizationBenchmarking.utils.tools.impl.browser.BrowserJob;

/** The instance of the gui server. */
public final class ApplicationInstance extends ToolJob implements
    Closeable {

  /** the server */
  private ServerInstance m_server;

  /** the browser */
  private BrowserJob m_browser;

  /** the start time */
  private final long m_startTime;

  /**
   * create the gui server instance
   *
   * @param logger
   *          the logger
   * @param server
   *          the server instance
   * @param browser
   *          the browser
   */
  ApplicationInstance(final Logger logger, final ServerInstance server,
      final BrowserJob browser) {
    super(logger);
    this.m_startTime = System.currentTimeMillis();
    this.m_server = server;
    this.m_browser = browser;
  }

  /**
   * Check if a browser has been opened
   *
   * @return has a browser been opened?
   */
  public final boolean hasBrowser() {
    return (this.m_browser != null);
  }

  /**
   * Get the URL to the entry point site for browsers running on the same
   * machine.
   *
   * @return the URL to the entry point site for browsers running on the
   *         same machine.
   */
  public final URL getLocalURL() {
    return this.m_server.getLocalURL();
  }

  /**
   * Get the URL to the entry point site for browsers running on any
   * machine.
   *
   * @return the URL to the entry point site for browsers running on any
   *         machine.
   */
  public final URL getGlobalURL() {
    return this.m_server.getGlobalURL();
  }

  /** Wait for the job to finish. */
  public final void waitFor() {
    final ServerInstance server;
    final BrowserJob browser;
    final Logger logger;
    long current;
    boolean error;
    Throwable caught;

    synchronized (this) {
      server = this.m_server;
      browser = this.m_browser;
    }

    try {
      serverWait: {
        if (browser != null) {
          logger = this.getLogger();

          caught = null;
          error = false;
          try {
            if (browser.waitFor() != 0) {
              error = true;
            }
          } catch (final Throwable thr) {
            error = true;
            caught = thr;
          }

          if (error) {
            if ((logger != null) && (logger.isLoggable(Level.SEVERE))) {
              logger
                  .log(
                      Level.SEVERE,
                      (((("The browser process has terminated erroneous. Maybe next time start the GUI with switches '" //$NON-NLS-1$
                      + ApplicationInstanceBuilder.PARAM_DONT_OPEN_BROWSER) + //
                      " and then manually visit '") + //$NON-NLS-1$
                      NetworkUtils.getServerBaseURL(
                          NetworkUtils.LOCAL_HOST,
                          ServerInstanceBuilder.DEFAULT_PORT))//
                      + "' with your web browser."),//$NON-NLS-1$
                      caught);
            }
          }

          if (browser.isWaitForReliable()) {
            current = (System.currentTimeMillis() - this.m_startTime);

            if (current < 10000) {
              if ((logger != null) && (logger.isLoggable(Level.WARNING))) {
                logger
                    .warning((((((((//
                    "The browser was closed rather quickly after it was started (after about ") //$NON-NLS-1$
                    + (current / 1000))//
                    + "s). This could mean that we could not properly capture the browser process and the browser is actually still open. Now, the GUI server shuts down too, so the GUI in the browser dows not work anymore. If this is the case, please restart the GUI and add the command line switch '")//$NON-NLS-1$
                    + ApplicationInstanceBuilder.PARAM_DONT_OPEN_BROWSER)//
                    + "'. Then, the GUI server remains running even if the browser was seemingly closed. You can then close it when you want and visit the GUI via '")//$NON-NLS-1$
                    + NetworkUtils.getServerBaseURL(
                        NetworkUtils.LOCAL_HOST,
                        ServerInstanceBuilder.DEFAULT_PORT)) + '\'') + '.');
              }
            }

            break serverWait;
          }
        }

        server.waitFor();
      }
    } finally {
      this.close();
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void close() {
    final ServerInstance server;
    final BrowserJob browser;

    synchronized (this) {
      server = this.m_server;
      this.m_server = null;
      browser = this.m_browser;
      this.m_browser = null;
    }

    try {
      try {
        if (browser != null) {
          browser.close();
        }
      } finally {
        if (server != null) {
          server.close();
        }
      }
    } catch (final Throwable error) {
      ErrorUtils.logError(this.getLogger(),//
          "Error while closing the GUI server.", //$NON-NLS-1$
          error, false, RethrowMode.AS_RUNTIME_EXCEPTION);
    }
  }
}
