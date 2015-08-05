package org.optimizationBenchmarking.gui.controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This is a handle to an ongoing operation. It acts a
 * {@link java.util.logging.Logger} as long as it exists, i.e., until the
 * {@link #close()} method is called. During this time, it may store log
 * records in a controller.
 */
final class _ServletHandle extends Handle implements Comparator<LogRecord> {

  /** the log records */
  private final LogRecord[] m_records;

  /** the size */
  private int m_size;

  /**
   * Create the HTML formatter
   *
   * @param owner
   *          the owning controller
   */
  _ServletHandle(final Controller owner) {
    super(owner);
    this.m_records = new LogRecord[128];
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void log(final LogRecord record) {
    final int size;
    final LogRecord[] records;
    final Level level;
    LogRecord current, worst;
    int i, worstIndex, worstLevel, curLevel;

    if (record == null) {
      return;
    }
    level = Handle._getLevel(record);
    if (!(this.isLoggable(level))) {
      return;
    }

    size = this.m_size;
    records = this.m_records;
    if (size < records.length) {
      records[size] = record;
      this.m_size = (size + 1);
      return;
    }

    worst = record;
    worstLevel = level.intValue();
    worstIndex = (-1);
    for (i = records.length; (--i) >= 0;) {
      current = records[i];
      curLevel = Handle._getLevel(current).intValue();
      if ((curLevel < worstLevel) || //
          ((curLevel == worstLevel) && //
          (current.getMillis() <= worst.getMillis()))) {//
        worst = current;
        worstLevel = curLevel;
        worstIndex = i;
      }
    }

    if (worstIndex >= 0) {
      records[worstIndex] = record;
    }
  }

  /**
   * Flush the contents collected to another handle.
   *
   * @param handle
   *          the handle
   */
  synchronized final void _flush(final Handle handle) {
    final int size;
    final LogRecord[] records;
    LogRecord rec;
    int i;

    if ((handle == null) || (handle == this)) {
      return;
    }

    size = this.m_size;
    records = this.m_records;

    if (size >= records.length) {
      Arrays.sort(records, this);
    }

    for (i = 0; i < size; i++) {
      rec = records[i];
      records[i] = null;
      handle.log(rec);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final int compare(final LogRecord o1, final LogRecord o2) {
    return Long.compare(o1.getMillis(), o2.getMillis());
  }
}
