package org.optimizationBenchmarking.gui.application;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.gui.controller.Result;
import org.optimizationBenchmarking.gui.server.ServerInstance;
import org.optimizationBenchmarking.gui.server.ServerInstanceBuilder;
import org.optimizationBenchmarking.gui.server.ServerTool;
import org.optimizationBenchmarking.gui.servlets.Download;
import org.optimizationBenchmarking.gui.servlets.Viewer;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.tools.impl.abstr.ToolJobBuilder;
import org.optimizationBenchmarking.utils.tools.impl.browser.Browser;
import org.optimizationBenchmarking.utils.tools.impl.browser.BrowserJob;
import org.optimizationBenchmarking.utils.tools.spec.IConfigurableToolJobBuilder;

/** The builder for the gui server instance. */
public final class ApplicationInstanceBuilder extends
    ToolJobBuilder<ApplicationInstance, ApplicationInstanceBuilder>
    implements IConfigurableToolJobBuilder {

  /**
   * If this parameter is set to {@code true}, then do browser will be
   * started. This usually means that the GUI is just running as server and
   * accessed from elsewhere.
   *
   * @see #m_dontOpenBrowser
   */
  public static final String PARAM_DONT_OPEN_BROWSER = "dontOpenBrowser"; //$NON-NLS-1$

  /**
   * The root path of the data folder
   *
   * @see #m_rootPath
   */
  public static final String PARAM_ROOT_PATH = "rootPath";//$NON-NLS-1$

  /** this is set to {@code true} when the server instance is created */
  private boolean m_created;

  /**
   * If this parameter is set to {@code true}, then do browser will be
   * started. This usually means that the GUI is just running as server and
   * accessed from elsewhere.
   *
   * @see #PARAM_DONT_OPEN_BROWSER
   */
  private boolean m_dontOpenBrowser;

  /** the server instance builder */
  private ServerInstanceBuilder m_server;

  /**
   * the root path
   *
   * @see #PARAM_ROOT_PATH
   */
  private Path m_rootPath;

  /** create the gui server instance builder */
  ApplicationInstanceBuilder() {
    super();

    this.m_server = ServerTool.getInstance().use();
    this.m_server.setWebRoot("/webroot/");//$NON-NLS-1$
  }

  /**
   * Check whether the server instance has been created, throw an
   * {@link java.lang.IllegalStateException} if so, do nothing otherwise.
   */
  private final void __checkCreated() {
    if (this.m_created) {
      throw new IllegalStateException(//
          "Gui server instance has already been created.");//$NON-NLS-1$
    }
  }

  /**
   * If this parameter is set to {@code true}, then do browser will be
   * started. This usually means that the GUI is just running as server and
   * accessed from elsewhere.
   *
   * @param dontOpenBrowser
   *          {@code true} if no browser should be started. This usually
   *          means that the GUI is just running as server and accessed
   *          from elsewhere.
   */
  public synchronized final void setDontOpenBrowser(
      final boolean dontOpenBrowser) {
    this.__checkCreated();
    this.m_dontOpenBrowser = dontOpenBrowser;
  }

  /**
   * Set the root path for this application.
   *
   * @param path
   *          the root path
   */
  public synchronized final void setRootPath(final Path path) {
    final Path use;

    this.__checkCreated();
    use = PathUtils.normalize(path);
    if (!(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))) {
      throw new IllegalArgumentException("The path '" + //$NON-NLS-1$
          path + "' (normalized to '" + use + //$NON-NLS-1$
          "') is not a directory.");//$NON-NLS-1$
    }
    this.m_rootPath = use;
  }

  /**
   * Set the port of the server builder
   *
   * @param port
   *          the port to use: {@code 0} for random assignment
   */
  public synchronized final void setPort(final int port) {
    this.__checkCreated();
    this.m_server.setPort(port);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final ApplicationInstanceBuilder configure(
      final Configuration config) {
    final Logger oldLogger, newLogger;
    final Path newPath;

    this.__checkCreated();

    this.m_server.configure(config);

    this.setDontOpenBrowser(//
    config.getBoolean(ApplicationInstanceBuilder.PARAM_DONT_OPEN_BROWSER,
        this.m_dontOpenBrowser));

    oldLogger = this.getLogger();
    newLogger = config.getLogger(Configuration.PARAM_LOGGER, oldLogger);
    if ((oldLogger != null) || (newLogger != null)) {
      this.setLogger(newLogger);
    }

    newPath = config.getPath(ApplicationInstanceBuilder.PARAM_ROOT_PATH,
        this.m_rootPath);
    if (newPath != null) {
      this.setRootPath(newPath);
    }

    return this;
  }

  /**
   * Add servlets to the instance builder
   *
   * @param builder
   *          the builder
   */
  private static final void __addServlets(
      final ServerInstanceBuilder builder) {
    builder.addServlet(Download.class, "/download"); //$NON-NLS-1$
    builder.addServlet(Viewer.class, "/viewer"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public synchronized final ApplicationInstance create() throws Exception {
    final ServerInstanceBuilder server;
    final ServerInstance instance;
    final URL localURL;
    final Browser btool;
    Path rootPath;
    Logger logger;
    BrowserJob browser;

    this.__checkCreated();
    this.m_created = true;

    logger = this.getLogger();
    if (logger == null) {
      logger = Configuration.getGlobalLogger();
    }
    if ((logger != null) && (!(logger.isLoggable(Result.SUCCESS)))) {
      logger.setLevel(Result.SUCCESS);
    }

    rootPath = this.m_rootPath;
    try {

      if (rootPath == null) {
        rootPath = PathUtils.createPathInside(PathUtils.getCurrentDir(),
            "data");//$NON-NLS-1$
      }

      if ((logger != null) && (logger.isLoggable(Level.CONFIG))) {
        logger.config((("Root directory is '" //$NON-NLS-1$
            + rootPath) + '\'') + '.');
      }
      Files.createDirectories(rootPath);
    } catch (final Throwable error) {
      ErrorUtils.logError(
          logger,
          ((("Error while trying to set root path '" //$NON-NLS-1$
          + rootPath) + '\'') + '.'), error, false,
          RethrowMode.AS_IO_EXCEPTION);
    }

    server = this.m_server;
    this.m_server = null;

    server.setLogger(logger);
    server.addAttribute(ApplicationInstanceBuilder.PARAM_ROOT_PATH,
        rootPath);
    ApplicationInstanceBuilder.__addServlets(server);
    instance = server.create();

    try {
      localURL = instance.getLocalURL();

      if (this.m_dontOpenBrowser) {
        browser = null;
      } else {
        btool = Browser.getInstance();
        if (btool.canUse()) {

          if ((logger != null) && (logger.isLoggable(Level.INFO))) {
            logger.info("Now starting browser to " + localURL); //$NON-NLS-1$
          }

          try {
            browser = btool.use()//
                .setLogger(logger)//
                .setURL(localURL)//
                .create();
          } catch (final Throwable error) {
            browser = null;

            ErrorUtils
                .logError(logger,
                    Level.WARNING,//
                    ((("Error while trying to start browser. Please start the browser manually and browse to '" //$NON-NLS-1$
                    + localURL) + '\'') + '.'), error, true,//
                    RethrowMode.DONT_RETHROW);
          }
        } else {
          browser = null;
          if ((logger != null) && (logger.isLoggable(Level.WARNING))) {
            logger.warning(((//
                "No browser was detected. Please start the browser manually and browse to '" //$NON-NLS-1$
                + localURL) + '\'') + '.');
          }
        }
      }

      return new ApplicationInstance(logger, instance, browser);
    } catch (final Exception e1) {
      instance.close();
      throw e1;
    }
  }
}
