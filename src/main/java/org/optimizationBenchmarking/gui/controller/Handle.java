package org.optimizationBenchmarking.gui.controller;

import java.io.Closeable;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.optimizationBenchmarking.utils.text.ESimpleDateFormat;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.TextOutputWriter;
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

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
 * failed}. </p>
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
public final class Handle extends Logger implements Closeable {

  /** the level values */
  private static final int[] LEVEL_VALUES = {//
  Level.ALL.intValue(),//
      Level.FINEST.intValue(),//
      Level.FINER.intValue(),//
      Level.FINE.intValue(),//
      Level.CONFIG.intValue(),//
      Level.INFO.intValue(),//
      Result.SUCCESS.intValue(),//
      Level.WARNING.intValue(),//
      Result.FAILURE.intValue(),//
      Level.SEVERE.intValue() //
  };
  /** the level classes */
  private static final String[] LEVEL_CLASSES = {//
  "logAll", //$NON-NLS-1$
      "logFinest", //$NON-NLS-1$
      "logFiner", //$NON-NLS-1$
      "logFine", //$NON-NLS-1$
      "logConfig", //$NON-NLS-1$
      "logInfo", //$NON-NLS-1$
      "logSuccess", //$NON-NLS-1$
      "logWarning", //$NON-NLS-1$
      "logFailure", //$NON-NLS-1$
      "logSevere", //$NON-NLS-1$
  };

  /** the owner */
  private Controller m_owner;

  /** the formatter */
  private Formatter m_format;

  /** the internal print textOut */
  private JspWriter m_out;

  /** the encoded text output */
  private ITextOutput m_encoded;

  /** the encoded print writer */
  private PrintWriter m_encodedWriter;

  /** is this the first invocation? */
  private volatile boolean m_first;

  /**
   * Create the HTML formatter
   *
   * @param pageContext
   *          the page context
   * @param owner
   *          the owning controller
   */
  Handle(final Controller owner, final PageContext pageContext) {
    super(null, null);

    final Handler[] handlers;
    JspWriter writer;
    Formatter formatter, current;

    if (pageContext == null) {
      throw new IllegalArgumentException(
          "The PageContext must not be null.");//$NON-NLS-1$
    }

    if (owner == null) {
      throw new IllegalArgumentException(
          "The controller must not be null.");//$NON-NLS-1$
    }

    try {
      writer = pageContext.getOut();
    } catch (final Throwable error) {
      throw new RuntimeException(//
          "Error when trying to obtain JspWriter of PageContext.", //$NON-NLS-1$
          error);
    }
    if (writer == null) {
      throw new IllegalArgumentException(
          "JspWriter of PageContext must not be null.");//$NON-NLS-1$
    }

    this.m_out = writer;
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

    this.m_format = formatter;
    this.m_first = true;
  }

  /**
   * Obtain the encoded text output device.
   *
   * @return the encoded text output device
   */
  private final ITextOutput __getEncoded() {
    if (this.m_encoded == null) {
      this.m_encoded = XMLCharTransformer.getInstance().transform(
          new _JspWriterTextable(this.m_out),
          TextUtils.DEFAULT_NORMALIZER_FORM);
    }
    return this.m_encoded;
  }

  /**
   * Obtain a print writer which is encoded as well
   *
   * @return the encoded print writer
   */
  private final PrintWriter __getEncodedWriter() {
    if (this.m_encodedWriter == null) {
      this.m_encodedWriter = new PrintWriter(TextOutputWriter.wrap(//
          this.__getEncoded()));
    }
    return this.m_encodedWriter;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void log(final LogRecord record) {
    final JspWriter out;
    final PrintWriter encodedWriter;
    final ITextOutput encoded;
    final String levelClass;
    final Formatter format;
    final Throwable error;
    final Logger parent;
    Level level;
    int index;

    if (record == null) {
      return;
    }
    level = record.getLevel();
    if (level == null) {
      level = Level.INFO;
    }
    if (!(this.isLoggable(level))) {
      return;
    }

    out = this.m_out;
    synchronized (out) {
      try {
        if (this.m_first) {
          this.m_first = false;
          out.write(//
          "<table class=\"logTable\"><tr class=\"logHeaderRow\"><th class=\"logHeaderCell\">Type</th><th class=\"logHeaderCell\">When</th><th class=\"logHeaderCell\">What</th></tr>"); //$NON-NLS-1$
        }

        out.write("<tr class=\""); //$NON-NLS-1$
        index = Arrays.binarySearch(Handle.LEVEL_VALUES, level.intValue());
        if (index < 0) {
          index = (-(index + 1));
        }
        if (index >= Handle.LEVEL_VALUES.length) {
          index = (Handle.LEVEL_VALUES.length - 1);
        }

        levelClass = Handle.LEVEL_CLASSES[index];
        out.write(levelClass);
        out.write("\"><td class=\"logIconCol\">"); //$NON-NLS-1$
        out.write("<img src=\"/icons/"); //$NON-NLS-1$
        out.write(levelClass);
        out.write(".png\" class=\"logIcon\" alt=\""); //$NON-NLS-1$
        out.write(level.getName());
        out.write("\"/></td><td class=\"logTimeCol\">"); //$NON-NLS-1$
        out.write(ESimpleDateFormat.DATE_TIME.format(record.getMillis()));
        out.write("</td><td class=\"logMsgCol\">"); //$NON-NLS-1$

        format = this.m_format;
        encoded = this.__getEncoded();
        if (format != null) {
          encoded.append(format.formatMessage(record));
        } else {
          encoded.append(record.getMessage());
        }

        error = record.getThrown();
        if (error != null) {
          out.write("<pre class=\"logPre\"><code class=\"logCode\">"); //$NON-NLS-1$

          encodedWriter = this.__getEncodedWriter();

          synchronized (this.m_encoded) {
            error.printStackTrace(encodedWriter);
            encodedWriter.flush();
          }
          out.write("</code></pre>");//$NON-NLS-1$
        }
        out.write("</td></tr>"); //$NON-NLS-1$

      } catch (final Throwable errorx) {
        throw new IllegalStateException(
            "Something went wrong when logging.", //$NON-NLS-1$
            errorx);
      }
    }

    parent = this.getParent();
    if (parent != null) {
      parent.log(record);
    }
  }

  /**
   * Get the controller of this handle
   *
   * @return the controller of this handle
   */
  public final Controller getController() {
    return this.m_owner;
  }

  /** {@inheritDoc} */
  @Override
  public final void close() {
    final JspWriter pw;
    synchronized (this) {
      pw = this.m_out;
      this.m_out = null;
      this.m_encoded = null;
      this.m_format = null;
      this.m_owner = null;
      this.m_encodedWriter = null;
    }
    if (pw != null) {
      synchronized (pw) {
        if (!(this.m_first)) {
          try {
            pw.write("</table>"); //$NON-NLS-1$
            pw.flush();
          } catch (final Throwable errorx) {
            throw new IllegalStateException(
                "Something went wrong when finishing the log.", //$NON-NLS-1$
                errorx);
          }
        }
      }
    }
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
}
