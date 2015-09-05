package org.optimizationBenchmarking.gui.utils.files;

/** the fs element type */
public enum EFSElementType {

  /** the next higher folder */
  NEXT_UP(false, "folderUp"), //$NON-NLS-1$

  /** the root of a list */
  LIST_ROOT(false, "folderCur"), //$NON-NLS-1$

  /** a folder */
  FOLDER(false, "folder"), //$NON-NLS-1$

  /** a file */
  FILE(true, "file"); //$NON-NLS-1$

  /** is this a file type? */
  private final boolean m_isFile;

  /** the icon */
  private final String m_icon;

  /**
   * create
   *
   * @param isFile
   *          if this type is a file
   * @param icon
   *          the icon
   */
  EFSElementType(final boolean isFile, final String icon) {
    this.m_isFile = isFile;
    this.m_icon = icon;
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

  /**
   * get the icon belonging to this file system element
   *
   * @return the icon belonging to this file system element
   */
  public final String getIcon() {
    return this.m_icon;
  }
}
