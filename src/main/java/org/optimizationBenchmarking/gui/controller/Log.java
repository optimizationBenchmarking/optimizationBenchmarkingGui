package org.optimizationBenchmarking.gui.controller;

import java.util.logging.LogRecord;

import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;

/**
 * A list representation of the log.
 */
public class Log extends ArrayListView<LogRecord> {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** entries were skipped since the log got too long */
  private final boolean m_entriesSkipped;

  /**
   * Create the log
   *
   * @param log
   *          the log
   * @param entriesSkipped
   *          entries were skipped since the log got too long
   */
  Log(final LogRecord[] log, final boolean entriesSkipped) {
    super(log);
    this.m_entriesSkipped = entriesSkipped;
  }

  /**
   * Have entries been omitted from the log in order to limit its size?
   *
   * @return {@code true} if entries have been omitted from the log in
   *         order to limit its size, {@code false} if the log is complete
   */
  public final boolean wereEntriesSkipped() {
    return this.m_entriesSkipped;
  }

}
