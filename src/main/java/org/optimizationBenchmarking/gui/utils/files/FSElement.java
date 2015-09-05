package org.optimizationBenchmarking.gui.utils.files;

import java.io.IOException;
import java.io.InputStream;
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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.optimizationBenchmarking.experimentation.io.impl.edi.EDI;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.io.FileTypeRegistry;
import org.optimizationBenchmarking.utils.io.IFileType;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.io.xml.XMLFileType;
import org.optimizationBenchmarking.utils.text.ESimpleDateFormat;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

/** a file system element */
public class FSElement extends FileDesc implements Comparable<FSElement> {

  /** the root path */
  public static final String ROOT_PATH = "/"; //$NON-NLS-1$

  /** the xml input factory */
  private static XMLInputFactory XML_INPUT_FACTORY = null;

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
  private FSElement(final Path path, final String name,
      final String relative, final EFSElementType type, final long size,
      final long time) {
    super(path, name, relative);
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

  /** {@inheritDoc} */
  @Override
  public final int compareTo(final FSElement o) {
    final Path p1, p2;
    Path parent1, parent2;

    if (o == this) {
      return 0;
    }
    if (o == null) {
      return (-1);
    }

    p1 = this.getFullPath();
    p2 = o.getFullPath();
    parent1 = parent2 = null;
    if (this.m_type.isFile()) {
      if (!(o.m_type.isFile())) {
        return 1;
      }
      parent1 = p1.getParent();
      parent2 = p2.getParent();
    } else {
      if (o.m_type.isFile()) {
        return (-1);
      }
    }

    if (p1.startsWith(p2)) {
      return 1;
    }
    if (p2.startsWith(p1)) {
      return (-1);
    }

    if ((parent1 != null) && (parent2 != null)) {
      if (!(parent1.equals(parent2))) {
        if (parent1.startsWith(parent2)) {
          return 1;
        }
        if (parent2.startsWith(parent1)) {
          return (-1);
        }
      }
    }

    return p1.compareTo(p2);
  }

  /**
   * get the file type belonging to a given file
   *
   * @param file
   *          the file
   * @return the file type
   */
  private static final EFSElementType __getFileType(final Path file) {
    IFileType type, nextType;
    String namespace;
    XMLInputFactory ipf;
    XMLStreamReader reader;

    type = FileTypeRegistry.getInstance().getTypeForPath(file);
    if (type != null) {
      if ((type == XMLFileType.XML) || (type == EDI.EDI_XML)) {

        try (final InputStream is = PathUtils.openInputStream(file)) {
          ipf = FSElement.XML_INPUT_FACTORY;
          if (ipf == null) {
            ipf = XMLInputFactory.newFactory();
          }
          reader = ipf.createXMLStreamReader(is);
          try {
            outer: while (reader.hasNext()) {
              if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                namespace = reader.getNamespaceURI();
                if (namespace != null) {
                  nextType = FileTypeRegistry.getInstance()
                      .getTypeForNamespace(namespace);
                  if (nextType == null) {
                    return EFSElementType.forFileType(XMLFileType.XML);
                  }
                  type = nextType;
                  if (nextType == EDI.EDI_XML) {
                    switch (TextUtils.toLowerCase(reader.getLocalName())) {
                      case EDI.ELEMENT_DIMENSIONS: {
                        return EFSElementType.EDI_DIMENSIONS;
                      }
                      case EDI.ELEMENT_EXPERIMENT: {
                        return EFSElementType.EDI_EXPERIMENT;
                      }
                      case EDI.ELEMENT_INSTANCES: {
                        return EFSElementType.EDI_INSTANCES;
                      }
                      default: {
                        return EFSElementType.EDI;
                      }
                    }
                  }
                }
                break outer;
              }
            }
          } finally {
            reader.close();
          }
          FSElement.XML_INPUT_FACTORY = ipf;
        } catch (final Throwable error) {
          // ignore
        }
      }

      return EFSElementType.forFileType(type);
    }
    return EFSElementType.FILE;
  }

  /**
   * Create a file system element from a path
   *
   * @param path
   *          the path
   * @param handle
   *          the handle
   * @return the new element, or {@code null} if none could be created
   */
  public static final FSElement fromPath(final Path path,
      final Handle handle) {
    return FSElement.fromPath(null, null, path, null, handle);
  }

  /**
   * Create a file system element from a path
   *
   * @param root
   *          the root path, or {@code null} to use the controller's root
   * @param listRoot
   *          the parent path of the current listing process, or
   *          {@code null} if equal to {@code root}
   * @param path
   *          the path
   * @param attrs
   *          the basic file attributes, or {@code null} if none have been
   *          loaded yet
   * @param handle
   *          the handle
   * @return the new element, or {@code null} if none could be created
   */
  public static final FSElement fromPath(final Path root,
      final Path listRoot, final Path path,
      final BasicFileAttributes attrs, final Handle handle) {
    final EFSElementType type;
    final Path useRoot, useListRoot;
    final BasicFileAttributes bfa;
    Path use;
    String name, relativePath;
    FileTime fileTime;
    long size, time;

    use = PathUtils.normalize(path);
    if (root != null) {
      useRoot = root;
    } else {
      if (handle == null) {
        return null;
      }
      useRoot = handle.getController().getRootDir();
    }
    useListRoot = ((listRoot != null) ? listRoot : root);

    if (!(use.startsWith(root))) {
      if (handle != null) {
        handle.warning("The path '" + path + //$NON-NLS-1$
            "' is not contained in the root path '" + root + //$NON-NLS-1$
            "' and therefore ignored."); //$NON-NLS-1$
      }
      return null;
    }

    if (attrs != null) {
      bfa = attrs;
    } else {
      try {
        bfa = Files.readAttributes(use, BasicFileAttributes.class,
            LinkOption.NOFOLLOW_LINKS);
      } catch (final Throwable error) {
        if (handle != null) {
          handle.log(Level.WARNING,
              ("Error when reading attributes of path '"//$NON-NLS-1$
                  + path + '\'' + '.'), error);
        }
        return null;
      }
    }

    if (bfa == null) {
      if (handle != null) {
        handle.warning("The path '" + path + //$NON-NLS-1$
            "' seemingly does (no longer?) exist.");//$NON-NLS-1$
      }
      return null;
    }

    size = time = Long.MIN_VALUE;

    if (bfa.isDirectory()) {
      if (use.equals(useListRoot)) {
        type = EFSElementType.LIST_ROOT;
        use = useListRoot;
      } else {
        if (use.equals(listRoot.getParent())) {
          type = EFSElementType.NEXT_UP;
        } else {
          type = EFSElementType.FOLDER;
        }
      }
    } else {
      if (bfa.isRegularFile()) {

        size = bfa.size();
        fileTime = bfa.lastModifiedTime();
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

        type = ((size > 0L) ? FSElement.__getFileType(use)
            : EFSElementType.FILE);

      } else {
        if ((handle != null) && handle.isLoggable(Level.FINE)) {
          handle.fine("Ignoring '" + use + //$NON-NLS-1$
              "' since it is neither a regular file nor a directory."); //$NON-NLS-1$
        }
        return null;
      }
    }

    if (useRoot.equals(use)) {
      use = useRoot;
      name = relativePath = FSElement.ROOT_PATH;
    } else {
      name = PathUtils.getName(use);
      relativePath = useRoot.relativize(use).toString();
    }

    return new FSElement(use, name, relativePath, type, size, time);
  }

  /**
   * Add or remove the given path to the set
   *
   * @param add
   *          should we add the element ({@code true}) or remove it (
   *          {@code false}?
   * @param root
   *          the overall root (or {@code null} to use the one of the
   *          controller)
   * @param listRoot
   *          the list root (or {@code null} to use {@code root})
   * @param path
   *          the path
   * @param attrs
   *          the file attributes, or {@code null} to load them
   * @param set
   *          the set
   * @param handle
   *          the handle
   * @return the change result
   */
  public static final EChangeResult changeCollection(final boolean add,
      final Path root, final Path listRoot, final Path path,
      final BasicFileAttributes attrs, final Collection<FSElement> set,
      final Handle handle) {
    final FSElement el;

    el = FSElement.fromPath(root, listRoot, path, attrs, handle);
    if (el == null) {
      return EChangeResult.ELEMENT_NOT_FOUND;
    }
    synchronized (set) {
      return ((add ? set.add(el) : set.remove(el)) ? EChangeResult.CHANGED
          : EChangeResult.NOTHING_CHANGED);
    }
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
  public static final ArraySetView<FSElement> dir(final Path root,
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
            null, col.m_elements, handle);
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
        if (FSElement.changeCollection(true, this.m_root, this.m_start,
            dir, attrs, this.m_elements, this.m_handle) == EChangeResult.CHANGED) {
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
        FSElement.changeCollection(true, this.m_root, this.m_start, file,
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
