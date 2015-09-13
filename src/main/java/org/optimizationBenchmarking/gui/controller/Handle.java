package org.optimizationBenchmarking.gui.controller;

import java.io.Closeable;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * This is a handle to an ongoing operation. It acts a
 * {@link java.util.logging.Logger} as long as it exists, i.e., until the
 * {@link #close()} method is called. During this time, it may write
 * arbitrarily much text in form of HTML block elements to the
 * {@link javax.servlet.http.HttpServletResponse#getWriter() print writer}
 * of the owning {@link javax.servlet.http.HttpServletResponse}. This text
 * will contain the logged text, such as status information whether an
 * operation
 * {@link org.optimizationBenchmarking.gui.controller.Result#SUCCESS
 * succeeded} or
 * {@link org.optimizationBenchmarking.gui.controller.Result#FAILURE
 * failed}.
 * <p>
 * A {@link Handle} is always able to log the following levels:
 * </p>
 * <ul>
 * <li>{@link org.optimizationBenchmarking.gui.controller.Result#SUCCESS}</li>
 * <li>{@link java.util.logging.Level#WARNING}</li>
 * <li>{@link org.optimizationBenchmarking.gui.controller.Result#FAILURE}</li>
 * <li>{@link java.util.logging.Level#SEVERE}</li>
 * </ul>
 */
public class Handle extends Logger implements Closeable {

  /** the owner */
  private Controller m_owner;

  /**
   * Create the handle
   *
   * @param owner
   *          the owning controller
   */
  Handle(final Controller owner) {
    super(null, null);

    final Handler[] handlers;
    Formatter formatter, current;

    if (owner == null) {
      throw new IllegalArgumentException(
          "The controller must not be null.");//$NON-NLS-1$
    }

    this.m_owner = owner;
    this.setParent(owner._getLogger());

    if (!(this.isLoggable(Result.SUCCESS))) {
      this.setLevel(Result.SUCCESS);
    }

    handlers = this.getHandlers();
    formatter = null;
    if (handlers != null) {
      for (final Handler handler : handlers) {
        current = handler.getFormatter();
        if ((current != null) && (formatter == null)) {
          formatter = current;
        }
        this.removeHandler(handler);
      }
    }
    this.setFilter(null);
  }

  /**
   * Get the controller of this handle
   *
   * @return the controller of this handle
   */
  public final Controller getController() {
    return this.m_owner;
  }

  /**
   * Get the level of a log record
   *
   * @param record
   *          the record
   * @return the level
   */
  static final Level _getLevel(final LogRecord record) {
    final Level level;
    level = record.getLevel();
    return ((level != null) ? level : Level.INFO);
  }

  /** close */
  final void _close() {
    this.m_owner = null;
  }

  /**
   * Log a successful completion of an operation
   *
   * @param message
   *          the message
   */
  public final void success(final String message) {
    this.log(Result.SUCCESS, message);
  }

  /**
   * Log a failed operation
   *
   * @param message
   *          the message
   */
  public final void failure(final String message) {
    this.log(Result.FAILURE, message);
  }

  /**
   * Log a failed operation, along with the error which caused the
   * operation to fail
   *
   * @param message
   *          the message
   * @param error
   *          the error leading to the problem
   */
  public final void failure(final String message, final Throwable error) {
    this.log(Result.FAILURE, message, error);
  }

  /**
   * The unknown submit command is ignored
   *
   * @param submit
   *          the submit
   */
  public final void unknownSubmit(final String submit) {
    this.warning("Unknown command '" + submit + //$NON-NLS-1$
        "' is ignored."); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public void close() {
    // does nothing
  }
}
