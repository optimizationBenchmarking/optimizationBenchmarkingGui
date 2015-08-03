package org.optimizationBenchmarking.gui.controller;

/** the fs element type */
public enum EFSElementType {

  /** the next higher folder */
  NEXT_UP(false),

  /** the root of a list */
  LIST_ROOT(false),

  /** a folder */
  FOLDER(false),

  /** a file */
  FILE(true);

  /** is this a file type? */
  private final boolean m_isFile;

  /**
   * create
   *
   * @param isFile
   *          if this type is a file
   */
  EFSElementType(final boolean isFile) {
    this.m_isFile = isFile;
  }

  /**
   * Check whether this file system element type represents a file.
   *
   * @return {@code true} if this file system element type represents a
   *         file, {@code false} if it is something else, like a folder
   */
  public final boolean isFile() {
    return this.m_isFile;
  }
}
