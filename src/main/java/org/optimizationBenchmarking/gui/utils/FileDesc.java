package org.optimizationBenchmarking.gui.utils;

import java.nio.file.Path;

import org.optimizationBenchmarking.gui.controller.FSElement;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** A description item of a file. */
public class FileDesc {

  /** the name */
  private final String m_name;

  /** the relative path */
  private final String m_relative;

  /** the real path */
  private final Path m_path;

  /**
   * Create the file system element
   *
   * @param path
   *          the path
   * @param name
   *          the name of the
   * @param relative
   *          the relative path
   */
  protected FileDesc(final Path path, final String name,
      final String relative) {
    super();
    if (name == null) {
      throw new IllegalArgumentException("Name cannot be null."); //$NON-NLS-1$
    }
    if (path == null) {
      throw new IllegalArgumentException("Path cannot be null."); //$NON-NLS-1$
    }
    if (relative == null) {
      throw new IllegalArgumentException("Relative path cannot be null."); //$NON-NLS-1$
    }
    this.m_name = name;
    this.m_path = path;
    this.m_relative = relative;
  }

  /**
   * Create the file system element
   *
   * @param path
   *          the path
   * @param root
   *          the root path
   */
  protected FileDesc(final Path path, final Path root) {
    this(path, PathUtils.getName(path),//
        String.valueOf(root.relativize(path)));
  }

  /**
   * Get the name of the file system element
   *
   * @return the name of the file system element
   */
  public final String getName() {
    return this.m_name;
  }

  /**
   * Get the relative path of this file system element
   *
   * @return the relative path of this file system element
   */
  public final String getRelativePath() {
    return this.m_relative;
  }

  /**
   * Get the full path
   *
   * @return the full path
   */
  public final Path getFullPath() {
    return this.m_path;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    return ((o == this) || ((o instanceof FSElement) && //
    (this.getFullPath().equals(((FSElement) o).getFullPath()))));
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return this.getFullPath().hashCode();
  }
}
