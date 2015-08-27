package org.optimizationBenchmarking.gui.modules;

import java.nio.file.Path;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** This class provides the functionality for deleting a file. */
public final class Delete {

  /**
   * Delete a set of files
   *
   * @param relPaths
   *          the paths
   * @param handle
   *          the handle
   */
  public static final void delete(final String[] relPaths,
      final Handle handle) {
    final Controller controller;
    final Path root;
    Path path;
    int i;

    if (relPaths == null) {
      handle.failure("Set of paths to delete cannot be null."); //$NON-NLS-1$
      return;
    }

    i = relPaths.length;
    if (i <= 0) {
      handle.failure("Set of paths to delete cannot empty."); //$NON-NLS-1$
      return;
    }

    controller = handle.getController();
    root = controller.getRootDir();
    for (i = 0; i < relPaths.length; i++) {
      path = controller.resolve(handle, relPaths[i], null);
      if (path != null) {
        if (root.equals(path) || root.startsWith(path)) {
          handle.failure(//
              "You cannot delete the root directory, but deleting '"//$NON-NLS-1$
                  + relPaths[i] + "' would mean exactly that.");//$NON-NLS-1$
          continue;
        }
        try {
          PathUtils.delete(path);
          handle.success("Element '" + relPaths[i] + //$NON-NLS-1$
              "' has been deleted."); //$NON-NLS-1$
        } catch (final Throwable error) {
          handle.failure(((("Failed to delete path '" //$NON-NLS-1$
              + relPaths[i]) + '\'') + '.'), error);
        }
      }
    }
  }

  /** the file io */
  private Delete() {
    ErrorUtils.doNotCall();
  }
}