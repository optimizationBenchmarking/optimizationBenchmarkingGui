package org.optimizationBenchmarking.gui.controller;

import org.optimizationBenchmarking.gui.utils.files.FSElement;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;

/** the controller state */
public final class ControllerState {

  /** the current path (relative to the root path) */
  private final String m_relPath;

  /** the elements (folder hierarchy) of the current path */
  private final ArraySetView<FSElement> m_path;

  /** the elements in the current folder */
  private final ArraySetView<FSElement> m_current;

  /** the current selection */
  private final ArraySetView<FSElement> m_selected;

  /**
   * The controller state
   *
   * @param relPath
   *          the relative path
   * @param path
   *          the path the elements (folder hierarchy) of the current path
   * @param current
   *          the elements in the current folder
   * @param selected
   *          the selected elements
   */
  ControllerState(final String relPath,
      final ArraySetView<FSElement> path,
      final ArraySetView<FSElement> current,
      final ArraySetView<FSElement> selected) {
    super();
    this.m_relPath = relPath;
    this.m_path = path;
    this.m_current = current;
    this.m_selected = selected;
  }

  /**
   * Get the relative path
   *
   * @return the relative path
   */
  public final String getRelativePath() {
    return this.m_relPath;
  }

  /**
   * Get the elements of the current path, i.e., the folder hierarchy
   *
   * @return the elements of the current path, i.e., the folder hierarchy
   */
  public final ArraySetView<FSElement> getPath() {
    return this.m_path;
  }

  /**
   * Get the folders and files inside the current directory
   *
   * @return the folders and files inside the current directory
   */
  public final ArraySetView<FSElement> getCurrent() {
    return this.m_current;
  }

  /**
   * Get the current selection
   *
   * @return the elements in the current selection
   */
  public final ArraySetView<FSElement> getSelected() {
    return this.m_selected;
  }
}
