package org.optimizationBenchmarking.gui.server;

import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.io.paths.TempDir;
import org.optimizationBenchmarking.utils.net.NetworkUtils;
import org.optimizationBenchmarking.utils.tools.impl.abstr.ToolJob;

/** An instance of the server. Typically, there will be only one. */
public final class ServerInstance extends ToolJob implements Closeable {

  /** the port */
  private org.eclipse.jetty.server.Server m_server;

  /** the connector */
  ServerConnector m_connector;

  /** the temp folder (used for the generated classes) */
  private TempDir m_temp;

  /** the second temp folder (used for uploads) */
  private TempDir m_temp2;

  /** the shutdown hook */
  private __Shutdown m_shutdown;

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
   *          the temp directory (used for the generated classes)
   * @param temp2
   *          the second temp directory (used for uploads)
   * @param server
   *          the server
   * @param connector
   *          the connector
   * @param webApp
   *          the web application
   */
  @SuppressWarnings("unused")
  ServerInstance(final Logger logger, final TempDir temp,
      final TempDir temp2, final org.eclipse.jetty.server.Server server,
      final ServerConnector connector, final WebAppContext webApp) {
    super(logger);
    this.m_temp = temp;
    this.m_temp2 = temp2;
    this.m_server = server;
    this.m_connector = connector;
    this.m_webApp = webApp;

    __Shutdown sd;

    sd = new __Shutdown(this);
    try {
      Runtime.getRuntime().addShutdownHook(sd);
    } catch (final Throwable error) {
      sd = null;
    }
    this.m_shutdown = sd;
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
      this.m_localURL = NetworkUtils.getServerBaseURL(
          NetworkUtils.LOCAL_HOST, this.getPort());
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
      this.m_globalURL = NetworkUtils.getServerBaseURL(
          NetworkUtils.getMostLikelyPublicName(), this.getPort());
      if ((this.m_localURL != null)
          && (this.m_localURL.equals(this.m_globalURL))) {
        this.m_globalURL = this.m_localURL;
      }
    }
    return this.m_globalURL;
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
    Logger log;
    org.eclipse.jetty.server.Server server;
    ServerConnector connector;
    TempDir temp, temp2;
    WebAppContext context;
    __Shutdown sd;
    Object error;

    synchronized (this) {
      temp = this.m_temp;
      this.m_temp = null;
      temp2 = this.m_temp2;
      this.m_temp2 = null;
      server = this.m_server;
      this.m_server = null;
      connector = this.m_connector;
      this.m_connector = null;
      context = this.m_webApp;
      this.m_webApp = null;
      sd = this.m_shutdown;
      this.m_shutdown = null;
    }

    if ((server == null) && (connector == null) && (temp == null)
        && (temp2 == null) && (context == null) && (sd == null)) {
      return;
    }

    log = this.getLogger();

    if ((log != null) && (log.isLoggable(Level.INFO))) {
      log.info("Now shutting down web server."); //$NON-NLS-1$
    }

    error = null;

    if (sd != null) {
      try {
        Runtime.getRuntime().removeShutdownHook(sd);
      } catch (final Throwable err) {
        error = err;
      } finally {
        sd = null;
      }
    }

    if (server != null) {
      try {
        try {
          server.stop();
          server.join();
        } finally {
          server.destroy();
        }
      } catch (final Throwable err) {
        ErrorUtils.aggregateError(error, err);
      } finally {
        server = null;
      }
    }

    if (connector != null) {
      try {
        connector.close();
      } catch (final Throwable err) {
        ErrorUtils.aggregateError(error, err);
      } finally {
        connector = null;
      }
    }

    if (context != null) {
      try {
        context.destroy();
      } catch (final Throwable err) {
        ErrorUtils.aggregateError(error, err);
      } finally {
        context = null;
      }
    }

    if (temp != null) {
      try {
        temp.close();
      } catch (final Throwable err) {
        ErrorUtils.aggregateError(error, err);
      } finally {
        temp = null;
      }
    }

    if (temp2 != null) {
      try {
        temp2.close();
      } catch (final Throwable err) {
        ErrorUtils.aggregateError(error, err);
      } finally {
        temp2 = null;
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

  /** the shutdown hook */
  private static final class __Shutdown extends Thread {

    /** the reference to the instance */
    private final WeakReference<ServerInstance> m_ref;

    /**
     * create
     *
     * @param inst
     *          the server instance
     */
    __Shutdown(final ServerInstance inst) {
      super();
      this.m_ref = new WeakReference<>(inst);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("resource")
    @Override
    public final void run() {
      final ServerInstance inst;
      inst = this.m_ref.get();
      if (inst != null) {
        inst.close();
      }
    }
  }
}
