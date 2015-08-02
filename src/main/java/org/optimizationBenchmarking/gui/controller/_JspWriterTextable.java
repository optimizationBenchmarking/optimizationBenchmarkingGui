package org.optimizationBenchmarking.gui.controller;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.AbstractTextOutput;

/** An internal wrapper for jsp writers to ITextOutput */
final class _JspWriterTextable extends AbstractTextOutput {
  /** the internal jsp writer */
  private final JspWriter m_out;

  /**
   * create the text output object
   *
   * @param out
   *          the output writer
   */
  _JspWriterTextable(final JspWriter out) {
    super();
    if (out == null) {
      throw new IllegalArgumentException("JspWriter must not be null."); //$NON-NLS-1$
    }
    this.m_out = out;
  }

  /** {@inheritDoc} */
  @Override
  public final _JspWriterTextable append(final CharSequence csq) {
    try {
      this.m_out.append(csq);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append CharSequence to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public final _JspWriterTextable append(final CharSequence csq,
      final int start, final int end) {
    try {
      this.m_out.append(csq, start, end);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append CharSequence to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public final _JspWriterTextable append(final char c) {
    try {
      this.m_out.write(c);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append char to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final String s) {
    try {
      this.m_out.write(s);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append String to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final String s, final int start, final int end) {
    try {
      this.m_out.write(s, start, (end - start));
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append String to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final char[] chars) {
    try {
      this.m_out.write(chars);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append char[] to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final char[] chars, final int start,
      final int end) {
    try {
      this.m_out.write(chars, start, (end - start));
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append char[] to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final byte v) {
    try {
      this.m_out.print(v);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append byte to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final short v) {
    try {
      this.m_out.print(v);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append short to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final int v) {
    try {
      this.m_out.print(v);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append int to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final long v) {
    try {
      this.m_out.print(v);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append long to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final float v) {
    try {
      this.m_out.print(v);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append float to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final double v) {
    try {
      this.m_out.print(v);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append double to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final boolean v) {
    try {
      this.m_out.print(v);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append boolean to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void append(final Object o) {
    try {
      this.m_out.print(o);
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append Object to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void appendLineBreak() {
    try {
      this.m_out.println();
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to append line break to " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void flush() {//
    try {
      this.m_out.flush();
    } catch (final Throwable ioe) {
      RethrowMode.AS_RUNTIME_EXCEPTION.rethrow((//
          "Error while trying to flush " //$NON-NLS-1$
          + TextUtils.className(this.getClass())), true, ioe);
    }
  }

}
