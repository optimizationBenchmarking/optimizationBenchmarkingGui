package org.optimizationBenchmarking.gui.controller;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.logging.Level;

import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** the collector for file system elements */
final class _FSElementCollection extends SimpleFileVisitor<Path> {

  /** the handle */
  private final Handle m_handle;

  /** the root path */
  private final Path m_start;

  /** the root path */
  private final Path m_root;

  /** the elements we already have gathered */
  private final HashSet<FSElement> m_elements;

  /**
   * create the collector
   *
   * @param handle
   *          the handle
   * @param start
   *          the start path
   * @param root
   *          the root path
   */
  private _FSElementCollection(final Path root, final Path start,
      final Handle handle) {
    super();
    this.m_elements = new HashSet<>();
    this.m_handle = handle;
    this.m_root = root;
    this.m_start = start;
  }

  /**
   * Get all the FS elements in a given path
   *
   * @param root
   *          the root folder
   * @param start
   *          the start folder
   * @param handle
   *          the operation handle
   * @return the set of file system elements in that folder
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  static final ArraySetView<FSElement> _dir(final Path root,
      final Path start, final Handle handle) {
    _FSElementCollection col;

    col = null;
    try {
      col = new _FSElementCollection(root, start, handle);
      Files.walkFileTree(start, EnumSet.noneOf(FileVisitOption.class), 1,
          col);

    } catch (final Throwable error) {
      handle.failure(((((("Failed to scan path '" + start) //$NON-NLS-1$
          + "' under root '") + root) + '\'') + '.'), //$NON-NLS-1$
          error);
    }

    if (col != null) {
      return FSElement._collectionToList(col.m_elements);
    }
    return ((ArraySetView) (ArraySetView.EMPTY_SET_VIEW));
  }

  /**
   * Add a given path
   *
   * @param path
   *          the path
   * @param attrs
   *          the attributes
   * @return {@code true} if something new was added, {@code false}
   *         otherwise
   */
  private final boolean __add(final Path path,
      final BasicFileAttributes attrs) {
    final EFSElementType type;
    final Path use;
    final String name, relativePath;
    final FSElement el;
    FileTime fileTime;
    long size, time;

    use = PathUtils.normalize(path);

    size = time = Long.MIN_VALUE;

    if (attrs.isDirectory()) {
      if (use.equals(this.m_start)) {
        type = EFSElementType.LIST_ROOT;
      } else {
        type = EFSElementType.FOLDER;
      }
    } else {
      if (attrs.isRegularFile()) {
        type = EFSElementType.FILE;

        size = attrs.size();

        fileTime = attrs.lastModifiedTime();
        if (fileTime != null) {
          time = fileTime.toMillis();
          if (time <= 0L) {
            time = Long.MIN_VALUE;
          }
        }
        if (time <= 0L) {
          fileTime = attrs.creationTime();
          if (fileTime != null) {
            time = fileTime.toMillis();
            if (time <= 0L) {
              time = Long.MIN_VALUE;
            }
          }
        }

      } else {
        if (this.m_handle.isLoggable(Level.FINE)) {
          this.m_handle.fine("Ignoring '" + use + //$NON-NLS-1$
              "' since it is neither a regular file nor a directory."); //$NON-NLS-1$
        }
        return false;
      }
    }

    name = PathUtils.getName(use);
    relativePath = this.m_root.relativize(use).toString();

    el = new FSElement(use, name, relativePath, type, size, time);
    synchronized (this.m_elements) {
      return this.m_elements.add(el);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final FileVisitResult preVisitDirectory(final Path dir,
      final BasicFileAttributes attrs) {
    if ((dir != null) && (attrs != null) && (this.__add(dir, attrs))) {
      return FileVisitResult.CONTINUE;
    }
    return FileVisitResult.SKIP_SUBTREE;
  }

  /** {@inheritDoc} */
  @Override
  public final FileVisitResult visitFile(final Path file,
      final BasicFileAttributes attrs) {
    if ((file != null) && (attrs != null)) {
      this.__add(file, attrs);
    }
    return FileVisitResult.CONTINUE;
  }

  /** {@inheritDoc} */
  @Override
  public final FileVisitResult visitFileFailed(final Path file,
      final IOException exc) {
    this.m_handle.log(Level.WARNING,//
        (("Path scan failed for path '" //$NON-NLS-1$
        + file) + "', aborting scan."), exc);//$NON-NLS-1$
    return FileVisitResult.TERMINATE;
  }

  /** {@inheritDoc} */
  @Override
  public final FileVisitResult postVisitDirectory(final Path dir,
      final IOException exc) {
    this.m_handle.log(Level.WARNING,//
        (("Path scan failed somewhere in directory '" //$NON-NLS-1$
        + dir) + "', aborting scan."), exc);//$NON-NLS-1$
    return FileVisitResult.TERMINATE;
  }
}
