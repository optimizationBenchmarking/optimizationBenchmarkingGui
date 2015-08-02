package org.optimizationBenchmarking.gui.server;

import java.io.Closeable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.io.paths.TempDir;
import org.optimizationBenchmarking.utils.net.LocalHost;
import org.optimizationBenchmarking.utils.tools.impl.abstr.ToolJob;

/** An instance of the server. Typically, there will be only one. */
public final class ServerInstance extends ToolJob implements Closeable {

  /** the port */
  private org.eclipse.jetty.server.Server m_server;

  /** the connector */
  ServerConnector m_connector;

  /** the temp folder */
  private TempDir m_temp;

  /** the web application context */
  private WebAppContext m_webApp;

  /** the local URL */
  private URL m_localURL;
  /** the global URL */
  private URL m_globalURL;

  /**
   * create the server instance
   *
   * @param logger
   *          the logger
   * @param temp
   *          the temp directory
   * @param server
   *          the server
   * @param connector
   *          the connector
   * @param webApp
   *          the web application
   */
  ServerInstance(final Logger logger, final TempDir temp,
      final org.eclipse.jetty.server.Server server,
      final ServerConnector connector, final WebAppContext webApp) {
    super(logger);
    this.m_temp = temp;
    this.m_server = server;
    this.m_connector = connector;
    this.m_webApp = webApp;
  }

  /**
   * Obtain the port of the server
   *
   * @return the port of the server
   */
  public final int getPort() {
    return this.m_connector.getLocalPort();
  }

  /**
   * Get the URL to the entry point site for browsers running on the same
   * machine.
   *
   * @return the URL to the entry point site for browsers running on the
   *         same machine.
   */
  public final URL getLocalURL() {
    if (this.m_localURL == null) {
      this.m_localURL = ServerInstance.getServerBaseURL(
          LocalHost.LOCAL_HOST, this.getPort());
      if ((this.m_globalURL != null)
          && (this.m_localURL.equals(this.m_globalURL))) {
        this.m_globalURL = this.m_localURL;
      }
    }
    return this.m_localURL;
  }

  /**
   * Get the URL to the entry point site for browsers running on any
   * machine.
   *
   * @return the URL to the entry point site for browsers running on any
   *         machine.
   */
  public final URL getGlobalURL() {
    if (this.m_globalURL == null) {
      this.m_globalURL = ServerInstance.getServerBaseURL(
          LocalHost.getMostLikelyPublicName(), this.getPort());
      if ((this.m_localURL != null)
          && (this.m_localURL.equals(this.m_globalURL))) {
        this.m_globalURL = this.m_localURL;
      }
    }
    return this.m_globalURL;
  }

  /**
   * Obtain the URL for a given host and port.
   *
   * @param host
   *          the host
   * @param port
   *          the port
   * @return the URL
   */
  public static final URL getServerBaseURL(final String host,
      final int port) {
    try {
      return new URL((("http://" + host) + ':' + port) + '/'); //$NON-NLS-1$
    } catch (final MalformedURLException error) {
      throw new RuntimeException((((((//
          "Error while creating URL for host '" + host) //$NON-NLS-1$
          + "' and port '") + port) + '\'') + '.'), error);//$NON-NLS-1$
    }
  }

  /** Wait for the server to join. */
  public final void waitFor() {
    final org.eclipse.jetty.server.Server server;
    synchronized (this) {
      server = this.m_server;
    }
    if (server != null) {
      try {
        server.join();
      } catch (final Throwable error) {
        ErrorUtils.logError(this.getLogger(),//
            "Error while waiting for server.", //$NON-NLS-1$
            error, false, RethrowMode.AS_RUNTIME_EXCEPTION);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void close() {
    final Logger log;
    final org.eclipse.jetty.server.Server server;
    final ServerConnector connector;
    final TempDir temp;
    final WebAppContext context;
    Object error;

    synchronized (this) {
      temp = this.m_temp;
      server = this.m_server;
      connector = this.m_connector;
      context = this.m_webApp;
      this.m_temp = null;
      this.m_server = null;
      this.m_connector = null;
      this.m_webApp = null;
    }

    if ((server == null) && (connector == null) && (temp == null)
        && (context == null)) {
      return;
    }

    log = this.getLogger();

    if ((log != null) && (log.isLoggable(Level.INFO))) {
      log.info("Now shutting down web server."); //$NON-NLS-1$
    }

    error = null;
    if (server != null) {
      try {
        try {
          server.stop();
          server.join();
        } finally {
          server.destroy();
        }
      } catch (final Throwable err) {
        error = err;
      }
    }

    if (connector != null) {
      try {
        connector.close();
      } catch (final Throwable err) {
        ErrorUtils.aggregateError(error, err);
      }
    }

    if (context != null) {
      try {
        context.destroy();
      } catch (final Throwable err) {
        ErrorUtils.aggregateError(error, err);
      }
    }

    if (temp != null) {
      try {
        temp.close();
      } catch (final Throwable err) {
        ErrorUtils.aggregateError(error, err);
      }
    }

    if (error != null) {
      ErrorUtils.logError(log, //
          "Error while shutting down web server.", //$NON-NLS-1$
          error, false, RethrowMode.DONT_RETHROW);
    } else {
      if ((log != null) && (log.isLoggable(Level.INFO))) {
        log.info("Web server successfully shut down."); //$NON-NLS-1$
      }
    }
  }
}
