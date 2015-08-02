package org.optimizationBenchmarking.gui.controller;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.PageContext;

import org.eclipse.jetty.webapp.WebAppContext;
import org.optimizationBenchmarking.gui.application.ApplicationInstanceBuilder;
import org.optimizationBenchmarking.utils.config.Configuration;

/** The file manager bean */
public final class Controller implements Serializable {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** the root path */
  private final Path m_root;

  /** the logger */
  private final Logger m_logger;

  /** the current path (directory) */
  private Path m_current;

  /**
   * Create the file manager bean
   */
  public Controller() {
    super();

    final WebAppContext ctx;
    final Path p;
    final Logger log, parent;
    Object o;

    ctx = WebAppContext.getCurrentWebAppContext();

    o = ctx.getAttribute(ApplicationInstanceBuilder.PARAM_ROOT_PATH);
    if ((o != null) && (o instanceof Path)) {
      p = ((Path) o);
      if (!(Files.isDirectory(p))) {
        throw new IllegalArgumentException("Path '" + //$NON-NLS-1$
            p + "' is not a directory.");//$NON-NLS-1$
      }
    } else {
      throw new IllegalStateException(
          ApplicationInstanceBuilder.PARAM_ROOT_PATH
              + " must be an instance of java.nio.file.Path, but is " + o); //$NON-NLS-1$
    }

    o = ctx.getAttribute(Configuration.PARAM_LOGGER);
    if (o != null) {
      if (o instanceof Logger) {
        log = ((Logger) o);
      } else {
        throw new IllegalStateException(Configuration.PARAM_LOGGER + //
            " must be an instance of java.logging.Logger, but is " + o); //$NON-NLS-1$
      }
    } else {
      log = Logger.getAnonymousLogger();
      parent = Configuration.getGlobalLogger();
      if (parent != null) {
        log.setParent(parent);
      }
    }

    if ((log.getLevel() == null) || (!(log.isLoggable(Result.SUCCESS)))) {
      log.setLevel(Result.SUCCESS);
    }

    this.m_root = p;
    this.m_logger = log;
  }

  /**
   * Get the logger
   *
   * @return the logger
   */
  final Logger _getLogger() {
    return this.m_logger;
  }

  /**
   * Get the root path
   *
   * @return the root path
   */
  public final Path getRootDir() {
    return this.m_root;
  }

  /**
   * Create a job handle
   *
   * @param pageContext
   *          the page context object
   * @return the handle
   */
  public final Handle createHandle(final PageContext pageContext) {
    return new Handle(this, pageContext);
  }

  /**
   * Get the current directory
   *
   * @return the current directory
   */
  public synchronized final Path getCurrentDir() {
    return this.m_current;
  }

  /**
   * Set the log level.
   *
   * @param level
   *          the log level
   * @param handle
   *          the handle
   */
  public synchronized final void setLogLevel(final Handle handle,
      final String level) {
    final Level loglevel;
    String strRep;

    if (level == null) {
      handle
          .failure("Log level name cannot be null. Logging remains at level " //$NON-NLS-1$
              + this.m_logger.getLevel() + '.');
      return;
    }

    try {
      loglevel = Level.parse(level);
    } catch (final Throwable error) {
      handle.failure("The string '" + level + //$NON-NLS-1$
          "' does not identify a proper log level. Logging remains at level " //$NON-NLS-1$
          + this.m_logger.getLevel() + '.', error);
      return;
    }

    strRep = loglevel.toString();
    if (!(level.equals(strRep))) {
      strRep += (" (parsed from string '"//$NON-NLS-1$
          + level + "')");//$NON-NLS-1$
    }

    if (loglevel.intValue() > Result.SUCCESS.intValue()) {
      handle.failure(//
          "Cannot set log level to " + strRep + //$NON-NLS-1$
              ", since this level would not permit SUCCESS events to be displayed anymore. Logging remains at level " //$NON-NLS-1$
              + this.m_logger.getLevel() + '.');
      return;
    }

    this.m_logger.setLevel(loglevel);
    handle.success("Successfully set log level to " + strRep + '.');//$NON-NLS-1$
  }

  /**
   * Get the current log level
   *
   * @return the current log level
   */
  public synchronized final String getLogLevel() {
    return String.valueOf(this.m_logger.getLevel());
  }
}