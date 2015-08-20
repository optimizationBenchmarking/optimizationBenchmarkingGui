package org.optimizationBenchmarking.gui.utils;

import java.nio.file.Path;

/**
 * A loaded element
 *
 * @param <T>
 *          the element type
 */
public final class Loaded<T> extends FileDesc {

  /** the loaded element */
  private final T m_loaded;

  /**
   * Create the file system element
   *
   * @param path
   *          the path
   * @param root
   *          the root path
   * @param loaded
   *          the loaded element
   */
  Loaded(final Path path, final Path root, final T loaded) {
    super(path, root);
    if (loaded == null) {
      throw new IllegalArgumentException("Loaded value cannot be null."); //$NON-NLS-1$
    }
    this.m_loaded = loaded;
  }

  /**
   * Get the loaded value
   *
   * @return the loaded value
   */
  public final T getLoaded() {
    return this.m_loaded;
  }
}
