package org.optimizationBenchmarking.gui.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;
import org.optimizationBenchmarking.utils.text.tokenizers.LineIterator;
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

/** A set of utility methods to deal with text files */
public final class FileIO {

  /**
   * Load a set of files
   *
   * @param relPaths
   *          the paths
   * @param handle
   *          the handle
   * @return the file's contents
   */
  public static final String[] load(final String[] relPaths,
      final Handle handle) {
    final String[] res;
    final MemoryTextOutput mto;
    final ITextOutput encoded;
    int i;

    if (relPaths == null) {
      handle.failure("Set of paths to load and edit cannot be null."); //$NON-NLS-1$
      return null;
    }

    i = relPaths.length;
    if (i <= 0) {
      handle.failure("Set of paths to load and edit cannot empty."); //$NON-NLS-1$
      return null;
    }

    res = new String[i];
    mto = new MemoryTextOutput();
    encoded = XMLCharTransformer.getInstance().transform(mto);

    for (i = 0; i < res.length; i++) {
      if (FileIO.__load(relPaths[i], handle, encoded)) {
        encoded.flush();
        mto.flush();
        res[i] = mto.toString();
      }
      mto.clear();
    }

    return res;
  }

  /**
   * Load a file
   *
   * @param relPath
   *          the path
   * @param handle
   *          the handle
   * @param mto
   *          the text output
   * @return {@code true} on success, {@code false} on failure
   */
  private static final boolean __load(final String relPath,
      final Handle handle, final ITextOutput mto) {
    final Path path, parent;
    String string;

    path = handle.getController().resolve(handle, relPath, null);
    if (path != null) {
      try {
        // ensure that file exists
        parent = path.getParent();
        if (parent != null) {
          Files.createDirectories(parent);
        }

        try {
          Files.createFile(path);
          handle.info("Created empty file '" + relPath + '\'' + '.'); //$NON-NLS-1$
        } catch (final FileAlreadyExistsException exce) {
          handle.finer("File '" + relPath + "' exists.");//$NON-NLS-1$//$NON-NLS-2$
        }

        try (final InputStream is = PathUtils.openInputStream(path)) {
          try (final InputStreamReader isr = new InputStreamReader(is)) {
            try (final BufferedReader br = new BufferedReader(isr)) {
              while ((string = br.readLine()) != null) {
                mto.append(string);
                mto.appendLineBreak();
              }
            }
          }
        }

        handle.success("Finished loading file '" + relPath + '\'' + '.'); //$NON-NLS-1$
        return true;
      } catch (final Throwable error) {
        handle.failure(("Failed to load file '" + //$NON-NLS-1$
            relPath + '\'' + '.'), error);
      }
    }

    return false;
  }

  /**
   * Store the contents into a file.
   *
   * @param relPath
   *          the path
   * @param handle
   *          the handle
   * @param contents
   *          the contents
   */
  public static final void store(final String relPath,
      final Handle handle, final String contents) {
    final Path path;

    path = handle.getController().resolve(handle, relPath, null);
    if (path != null) {
      try {
        try (final OutputStream os = PathUtils.openOutputStream(path)) {
          try (final OutputStreamWriter osw = new OutputStreamWriter(os)) {
            try (final BufferedWriter bw = new BufferedWriter(osw)) {
              for (final String str : new LineIterator(contents)) {
                bw.write(str);
                bw.newLine();
              }
            }
          }
        }

        handle.success("Successfully stored the contents of file '" + //$NON-NLS-1$
            relPath + '\'' + '.');
      } catch (final Throwable error) {
        handle.failure("Failed to store the contents of file '" + //$NON-NLS-1$
            relPath + '\'' + '.', error);
      }
    }
  }

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
          handle.success("Path '" + relPaths[i] + //$NON-NLS-1$
              "' has been deleted."); //$NON-NLS-1$
        } catch (final Throwable error) {
          handle.failure(((("Failed to delete path '" //$NON-NLS-1$
              + relPaths[i]) + '\'') + '.'), error);
        }
      }
    }
  }

  /** the file io */
  private FileIO() {
    ErrorUtils.doNotCall();
  }
}