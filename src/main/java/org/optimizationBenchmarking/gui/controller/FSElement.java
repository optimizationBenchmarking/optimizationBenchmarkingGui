package org.optimizationBenchmarking.gui.controller;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.logging.Level;

import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.ESimpleDateFormat;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

/** a file system element */
public class FSElement implements Comparable<FSElement> {

  /** the root path */
  static final String ROOT_PATH = "/"; //$NON-NLS-1$

  /** the actual path */
  private final Path m_path;

  /** the name */
  private final String m_name;

  /** the relative path */
  private final String m_relative;

  /** the size of the file */
  private final long m_size;

  /** the change time */
  private final long m_changeTime;

  /** the element type */
  private final EFSElementType m_type;

  /** the size string */
  private String m_sizeString;
  /** the time string */
  private String m_timeString;

  /**
   * Create the file system element
   *
   * @param path
   *          the path
   * @param name
   *          the name of the
   * @param relative
   *          the relative path
   * @param type
   *          the element type
   * @param size
   *          the size
   * @param time
   *          the time
   */
  FSElement(final Path path, final String name, final String relative,
      final EFSElementType type, final long size, final long time) {
    super();
    this.m_name = name;
    this.m_path = path;
    this.m_relative = relative;
    this.m_type = type;
    this.m_size = size;
    this.m_changeTime = time;
  }

  /**
   * Get the type of this file system element
   *
   * @return the type of this file system element
   */
  public final EFSElementType getType() {
    return this.m_type;
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

  /**
   * Get the size of the file
   *
   * @return the size
   */
  public final long getSize() {
    return this.m_size;
  }

  /**
   * Get a string representing this element's size, or {@code null} if no
   * size is available
   *
   * @return a string representing this element's size, or {@code null} if
   *         no size is available
   */
  public final String getSizeString() {
    final MemoryTextOutput mto;
    final ITextOutput encoded;

    if (this.m_sizeString == null) {
      if (this.m_size >= 0L) {
        mto = new MemoryTextOutput();
        encoded = XMLCharTransformer.getInstance().transform(mto);
        TextUtils.appendFileSize(this.m_size, encoded);
        encoded.flush();
        this.m_sizeString = mto.toString();
      }
    }
    return this.m_sizeString;
  }

  /**
   * Get the time when the file was changed the last time, or the time when
   * it was created if the change-time is not supported, or
   * {@link java.lang.Long#MIN_VALUE} if neither are supported.
   *
   * @return the time of the file
   */
  public final long getTime() {
    return this.m_changeTime;
  }

  /**
   * Get a string representing this element's time, or {@code null} if no
   * time is available
   *
   * @return a string representing this element's time, or {@code null} if
   *         no time is available
   */
  public final String getTimeString() {
    if (this.m_timeString == null) {
      if (this.m_changeTime >= 0L) {
        this.m_timeString = ESimpleDateFormat.DATE_TIME_MIN
            .format(this.m_changeTime);
      }
    }
    return this.m_timeString;
  }

  /**
   * Get the path of the file system element
   *
   * @return the path of the file system element
   */
  final Path _getPath() {
    return this.m_path;
  }

  /** {@inheritDoc} */
  @Override
  public final int compareTo(final FSElement o) {

    if (o == this) {
      return 0;
    }
    if (o == null) {
      return (-1);
    }

    if (this.m_type.isFile()) {
      if (!(o.m_type.isFile())) {
        return 1;
      }
    } else {
      if (o.m_type.isFile()) {
        return (-1);
      }
    }

    if (this.m_path.startsWith(o.m_path)) {
      return 1;
    }
    if (o.m_path.startsWith(this.m_path)) {
      return (-1);
    }

    return this.m_path.compareTo(o.m_path);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    return ((o == this) || ((o instanceof FSElement) && //
    (this.m_path.equals(((FSElement) o).m_path))));
  }

  /** {@inheritDoc} */
  @Override
  public final int hashCode() {
    return this.m_path.hashCode();
  }

  /**
   * Add/remove the given path to the set
   *
   * @param add
   *          should we add the element ({@code true}) or remove it (
   *          {@code false}?
   * @param root
   *          the overall root
   * @param listRoot
   *          the list root
   * @param path
   *          the path
   * @param attrs
   *          the attributes
   * @param set
   *          the set
   * @param handle
   *          the handle
   * @return {@code 1} if adding the path changed the set, {@code 0}
   *         otherwise, {@code -1} if the element could not be found
   */
  static final int _changeCollection(final boolean add, final Path root,
      final Path listRoot, final Path path,
      final BasicFileAttributes attrs, final Collection<FSElement> set,
      final Handle handle) {
    final EFSElementType type;
    final FSElement el;
    Path use;
    String name, relativePath;
    FileTime fileTime;
    long size, time;

    use = PathUtils.normalize(path);

    if (use.startsWith(root)) {

      size = time = Long.MIN_VALUE;

      if (attrs.isDirectory()) {
        if (use.equals(listRoot)) {
          type = EFSElementType.LIST_ROOT;
          use = listRoot;
        } else {
          if (use.equals(listRoot.getParent())) {
            type = EFSElementType.NEXT_UP;
          } else {
            type = EFSElementType.FOLDER;
          }
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
          if ((handle != null) && handle.isLoggable(Level.FINE)) {
            handle.fine("Ignoring '" + use + //$NON-NLS-1$
                "' since it is neither a regular file nor a directory."); //$NON-NLS-1$
          }
          return (-1);
        }
      }

      if (root.equals(use)) {
        use = root;
        name = relativePath = FSElement.ROOT_PATH;
      } else {
        name = PathUtils.getName(use);
        relativePath = root.relativize(use).toString();
      }

      el = new FSElement(use, name, relativePath, type, size, time);
      synchronized (set) {
        return ((add ? set.add(el) : set.remove(el)) ? 1 : 0);
      }
    }
    if (handle != null) {
      handle.warning("The path '" + path + //$NON-NLS-1$
          "' is not contained in the root path '" + root + //$NON-NLS-1$
          "' and therefore ignored."); //$NON-NLS-1$
    }
    return (-1);
  }

  /**
   * Add or remove the given path to the set
   *
   * @param add
   *          should we add the element ({@code true}) or remove it (
   *          {@code false}?
   * @param root
   *          the overall root
   * @param listRoot
   *          the list root
   * @param path
   *          the path
   * @param set
   *          the set
   * @param handle
   *          the handle
   * @return {@code 1} if adding/removing the path changed the set,
   *         {@code 0} otherwise, {@code -1} if the element could not be
   *         found
   */
  public static final int changeCollection(final boolean add,
      final Path root, final Path listRoot, final Path path,
      final Collection<FSElement> set, final Handle handle) {
    BasicFileAttributes attrs;
    Throwable caught;

    attrs = null;
    caught = null;
    try {
      attrs = Files.readAttributes(path, BasicFileAttributes.class,
          LinkOption.NOFOLLOW_LINKS);
    } catch (final Throwable error) {
      attrs = null;
      caught = error;
    }

    if (attrs == null) {
      if (handle != null) {
        handle.failure((("Could not get attributes of path '" + //$NON-NLS-1$
            path) + "' - maybe it does not exist."), caught);//$NON-NLS-1$
      }
      return (-1);
    }

    return FSElement._changeCollection(add, root, listRoot, path, attrs,
        set, handle);
  }

  /**
   * convert a set to a list
   *
   * @param set
   *          the set
   * @return the list
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static final ArraySetView<FSElement> collectionToList(
      final Collection<FSElement> set) {
    final int size;
    final FSElement[] elements;

    doIt: {
      if (set != null) {
        synchronized (set) {
          size = set.size();
          if (size <= 0) {
            break doIt;
          }
          elements = new FSElement[size];
          set.toArray(elements);
        }
        Arrays.sort(elements);
        return new ArraySetView<>(elements);
      }
    }

    return ((ArraySetView) (ArraySetView.EMPTY_SET_VIEW));
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
    __FSDir col;
    Path up;

    col = null;
    try {
      col = new __FSDir(root, start, handle);
      Files.walkFileTree(start, EnumSet.noneOf(FileVisitOption.class), 1,
          col);

    } catch (final Throwable error) {
      if (handle != null) {
        handle.failure(((((("Failed to scan path '" + start) //$NON-NLS-1$
            + "' under root '") + root) + '\'') + '.'), //$NON-NLS-1$
            error);
      }
    }

    if (col != null) {
      up = start.getParent();
      if ((up != null) && (up.startsWith(root))) {
        FSElement.changeCollection(true, root, start, up,//
            col.m_elements, handle);
      }

      return FSElement.collectionToList(col.m_elements);
    }
    return ((ArraySetView) (ArraySetView.EMPTY_SET_VIEW));
  }

  /** the collector for file system elements */
  private static final class __FSDir extends SimpleFileVisitor<Path> {

    /** the handle */
    private final Handle m_handle;

    /** the root path */
    private final Path m_start;

    /** the root path */
    private final Path m_root;

    /** the elements we already have gathered */
    final HashSet<FSElement> m_elements;

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
    __FSDir(final Path root, final Path start, final Handle handle) {
      super();
      this.m_elements = new HashSet<>();
      this.m_handle = handle;
      this.m_root = root;
      this.m_start = start;
    }

    /** {@inheritDoc} */
    @Override
    public final FileVisitResult preVisitDirectory(final Path dir,
        final BasicFileAttributes attrs) {
      if ((dir != null) && (attrs != null)) {
        if (dir.equals(this.m_start)) {
          return FileVisitResult.CONTINUE;
        }
        if (FSElement._changeCollection(true, this.m_root, this.m_start,
            dir, attrs, this.m_elements, this.m_handle) > 0) {
          return FileVisitResult.CONTINUE;
        }
      }
      return FileVisitResult.SKIP_SUBTREE;
    }

    /** {@inheritDoc} */
    @Override
    public final FileVisitResult visitFile(final Path file,
        final BasicFileAttributes attrs) {
      if ((file != null) && (attrs != null)) {
        FSElement._changeCollection(true, this.m_root, this.m_start, file,
            attrs, this.m_elements, this.m_handle);
      }
      return FileVisitResult.CONTINUE;
    }

    /** {@inheritDoc} */
    @Override
    public final FileVisitResult visitFileFailed(final Path file,
        final IOException exc) {
      if (this.m_handle != null) {
        this.m_handle.log(Level.WARNING,//
            (("Path scan failed for path '" //$NON-NLS-1$
            + file) + "', aborting scan."), exc);//$NON-NLS-1$
      }
      return FileVisitResult.TERMINATE;
    }

    /** {@inheritDoc} */
    @Override
    public final FileVisitResult postVisitDirectory(final Path dir,
        final IOException exc) {
      if (exc != null) {
        if (this.m_handle != null) {
          this.m_handle.log(Level.WARNING,//
              (("Path scan failed somewhere in directory '" //$NON-NLS-1$
              + dir) + "', aborting scan."), exc);//$NON-NLS-1$
        }
        return FileVisitResult.TERMINATE;
      }
      return FileVisitResult.CONTINUE;
    }
  }

}
