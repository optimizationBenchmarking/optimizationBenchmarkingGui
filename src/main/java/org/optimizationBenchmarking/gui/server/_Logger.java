package org.optimizationBenchmarking.gui.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.util.log.AbstractLogger;

/**
 * <p>
 * Implementation of Jetty {@link Logger} based on
 * {@link java.util.logging.Logger}.
 * </p>
 * <p>
 * You can also set the logger level using <a
 * href="http://java.sun.com/j2se/1.5.0/docs/guide/logging/overview.html">
 * standard java.util.logging configuration</a>.
 * </p>
 */
final class _Logger extends AbstractLogger {
  /** the internal logger */
  private final java.util.logging.Logger m_logger;
  /** the configured log level */
  private Level m_configuredLevel;

  /**
   * create the logger
   *
   * @param logger
   *          the logger to wrap
   */
  _Logger(final Logger logger) {
    super();
    this.m_logger = logger;
    this.m_configuredLevel = null;
  }

  /** {@inheritDoc} */
  @Override
  public final String getName() {
    return this.m_logger.getName();
  }

  /** {@inheritDoc} */
  @Override
  public final void warn(final String msg, final Object... args) {
    if (this.m_logger.isLoggable(Level.WARNING)) {
      this.m_logger.log(Level.WARNING, this.format(msg, args));
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void warn(final Throwable thrown) {
    this.warn("", thrown); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void warn(final String msg, final Throwable thrown) {
    this.m_logger.log(Level.WARNING, msg, thrown);
  }

  /** {@inheritDoc} */
  @Override
  public final void info(final String msg, final Object... args) {
    if (this.m_logger.isLoggable(Level.INFO)) {
      this.m_logger.log(Level.INFO, this.format(msg, args));
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void info(final Throwable thrown) {
    this.info("", thrown); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void info(final String msg, final Throwable thrown) {
    this.m_logger.log(Level.INFO, msg, thrown);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isDebugEnabled() {
    return this.m_logger.isLoggable(Level.FINE);
  }

  /** {@inheritDoc} */
  @Override
  public final void setDebugEnabled(final boolean enabled) {
    if (enabled) {
      if (this.m_logger.isLoggable(Level.FINE)) {
        return;
      }
      this.m_configuredLevel = this.m_logger.getLevel();
      this.m_logger.setLevel(Level.FINE);
    } else {
      if (this.m_configuredLevel != null) {
        this.m_logger.setLevel(this.m_configuredLevel);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void debug(final String msg, final Object... args) {
    if (this.m_logger.isLoggable(Level.FINE)) {
      this.m_logger.log(Level.FINE, this.format(msg, args));
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void debug(final String msg, final long arg) {
    if (this.m_logger.isLoggable(Level.FINE)) {
      this.m_logger.log(Level.FINE, this.format(msg, Long.valueOf(arg)));
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void debug(final Throwable thrown) {
    this.debug("", thrown); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final void debug(final String msg, final Throwable thrown) {
    this.m_logger.log(Level.FINE, msg, thrown);
  }

  /** {@inheritDoc} */
  @Override
  protected final org.eclipse.jetty.util.log.Logger newLogger(
      final String fullname) {
    final Logger logger;
    final _Logger log;

    logger = Logger.getLogger(fullname);
    logger.setLevel(this.m_logger.getLevel());
    log = new _Logger(logger);
    log.m_configuredLevel = this.m_configuredLevel;
    return log;
  }

  /** {@inheritDoc} */
  @Override
  public final void ignore(final Throwable ignored) {
    // do nothing
  }

  /**
   * format a message
   *
   * @param plainMsg
   *          the message
   * @param args
   *          the arguments
   * @return the formatted message
   */
  private String format(final String plainMsg, final Object... args) {
    final String msg, braces;
    final StringBuilder builder;
    int start, bracesIndex;

    msg = String.valueOf(plainMsg); // Avoids NPE

    braces = "{}"; //$NON-NLS-1$
    builder = new StringBuilder();
    start = 0;
    for (final Object arg : args) {
      bracesIndex = msg.indexOf(braces, start);
      if (bracesIndex < 0) {
        builder.append(msg.substring(start));
        builder.append(' ');
        builder.append(arg);
        start = msg.length();
      } else {
        builder.append(msg.substring(start, bracesIndex));
        builder.append(String.valueOf(arg));
        start = bracesIndex + braces.length();
      }
    }
    builder.append(msg.substring(start));
    return builder.toString();
  }
}
