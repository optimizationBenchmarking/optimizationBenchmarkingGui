package org.optimizationBenchmarking.gui.controller;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.PageContext;

import org.eclipse.jetty.webapp.WebAppContext;
import org.optimizationBenchmarking.gui.application.ApplicationInstanceBuilder;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;

/** The file manager bean */
public final class Controller implements Serializable {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** the root path */
  private final Path m_root;

  /** the logger */
  private final Logger m_logger;

  /** the current path (directory) */
  private Path m_current;

  /** the selected elements */
  private final HashSet<FSElement> m_selected;

  /** the servlet handle */
  private final _ServletHandle m_servletHandle;

  /**
   * Create the file manager bean
   */
  public Controller() {
    super();

    final WebAppContext ctx;
    final Path p;
    final Logger log, parent;
    Object o;

    ctx = WebAppContext.getCurrentWebAppContext();

    o = ctx.getAttribute(ApplicationInstanceBuilder.PARAM_ROOT_PATH);
    if ((o != null) && (o instanceof Path)) {
      p = ((Path) o);
      if (!(Files.isDirectory(p))) {
        throw new IllegalArgumentException("Path '" + //$NON-NLS-1$
            p + "' is not a directory.");//$NON-NLS-1$
      }
    } else {
      throw new IllegalStateException(
          ApplicationInstanceBuilder.PARAM_ROOT_PATH
              + " must be an instance of java.nio.file.Path, but is " + o); //$NON-NLS-1$
    }

    o = ctx.getAttribute(Configuration.PARAM_LOGGER);
    if (o != null) {
      if (o instanceof Logger) {
        log = ((Logger) o);
      } else {
        throw new IllegalStateException(Configuration.PARAM_LOGGER + //
            " must be an instance of java.logging.Logger, but is " + o); //$NON-NLS-1$
      }
    } else {
      log = Logger.getAnonymousLogger();
      parent = Configuration.getGlobalLogger();
      if (parent != null) {
        log.setParent(parent);
      }
    }

    if ((log.getLevel() == null) || (!(log.isLoggable(Result.SUCCESS)))) {
      log.setLevel(Result.SUCCESS);
    }

    this.m_current = this.m_root = p;
    this.m_logger = log;
    this.m_selected = new HashSet<>();
    this.m_servletHandle = new _ServletHandle(this);
  }

  /**
   * Get the logger
   *
   * @return the logger
   */
  final Logger _getLogger() {
    return this.m_logger;
  }

  /**
   * Get the root path
   *
   * @return the root path
   */
  public final Path getRootDir() {
    return this.m_root;
  }

  /**
   * Create a job handle
   *
   * @param pageContext
   *          the page context object
   * @return the handle
   */
  public final Handle createJspHandle(final PageContext pageContext) {
    final _JspHandle res;
    res = new _JspHandle(this, pageContext);
    this.m_servletHandle._flush(res);
    return res;
  }

  /**
   * The servlet handle
   *
   * @return the servlet handle
   */
  public final Handle createServletHandle() {
    return this.m_servletHandle;
  }

  /**
   * Get the state of the controller
   *
   * @param handle
   *          the handle
   * @return the current state of the controller
   */
  public synchronized final ControllerState getState(final Handle handle) {
    final HashSet<FSElement> collector;
    final Path root;
    Path path;
    int res;

    collector = new HashSet<>();
    root = this.m_root;
    path = this.m_current;

    for (;;) {
      res = FSElement.changeCollection(true, root, root, path, collector,
          handle);
      if (root.equals(path)) {
        break;
      }
      path = PathUtils.normalize(path.getParent());
      if ((path == null) || (!(path.startsWith(root)))) {
        break;
      }
      if (res < 0) {
        handle.warning("Current path '" + //$NON-NLS-1$
            root.relativize(this.m_current).toString() + //
            "' seemingly does no longer exist, setting current path to '" //$NON-NLS-1$
            + root.relativize(path).toString() + '\'' + '.');
        this.m_current = path;
      }
    }

    return new ControllerState(//
        root.relativize(this.m_current).toString(),//
        FSElement.collectionToList(collector),//
        FSElement._dir(root, this.m_current, handle),//
        FSElement.collectionToList(this.m_selected));
  }

  /**
   * Set the log level.
   *
   * @param level
   *          the log level
   * @param handle
   *          the handle
   */
  public synchronized final void setLogLevel(final Handle handle,
      final String level) {
    final Level loglevel;
    final String use;
    String strRep;

    use = TextUtils.prepare(level);
    if (use == null) {
      handle.failure(//
          "Log level name cannot be null, empty, or just consist of white space, but '"//$NON-NLS-1$
              + level + "' does/is. Logging remains at level " //$NON-NLS-1$
              + this.m_logger.getLevel() + '.');
      return;
    }

    try {
      loglevel = Level.parse(use);
    } catch (final Throwable error) {
      handle.failure("The string '" + level + //$NON-NLS-1$
          "' does not identify a proper log level. Logging remains at level " //$NON-NLS-1$
          + this.m_logger.getLevel() + '.', error);
      return;
    }

    strRep = loglevel.toString();
    if (!(level.equals(strRep))) {
      strRep += (" (parsed from string '"//$NON-NLS-1$
          + level + "')");//$NON-NLS-1$
    }

    if (loglevel.intValue() > Result.SUCCESS.intValue()) {
      handle.failure(//
          "Cannot set log level to " + strRep + //$NON-NLS-1$
              ", since this level would not permit SUCCESS events to be displayed anymore. Logging remains at level " //$NON-NLS-1$
              + this.m_logger.getLevel() + '.');
      return;
    }

    this.m_logger.setLevel(loglevel);
    handle.success("Successfully set log level to " + strRep + '.');//$NON-NLS-1$
  }

  /**
   * Get the current log level
   *
   * @return the current log level
   */
  public synchronized final String getLogLevel() {
    return String.valueOf(this.m_logger.getLevel());
  }

  /**
   * Change the current working directory.
   *
   * @param handle
   *          the handle
   * @param relPath
   *          the relative path
   */
  public synchronized final void cdAbsolute(final Handle handle,
      final String relPath) {
    this.__cd(handle, this.m_root, relPath);
  }

  /**
   * Change the current working directory relative to it.
   *
   * @param handle
   *          the handle
   * @param relPath
   *          the relative path
   */
  public synchronized final void cdRelative(final Handle handle,
      final String relPath) {
    this.__cd(handle, this.m_current, relPath);
  }

  /**
   * Change the current working directory.
   *
   * @param handle
   *          the handle
   * @param root
   *          the root path to use for resolution
   * @param relPath
   *          the relative path
   */
  private final void __cd(final Handle handle, final Path root,
      final String relPath) {
    final Path p;
    boolean isDir;
    Throwable caught;

    p = this.resolve(handle, relPath, root);
    if (p != null) {

      isDir = false;
      caught = null;
      try {
        isDir = Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS);
      } catch (final Throwable error) {
        caught = error;
        isDir = false;
      }
      if (isDir && (caught == null)) {
        this.m_current = p;
        handle.success("Succeeded in setting the current path to '" //$NON-NLS-1$
            + relPath + "'. The full path is now '" + //$NON-NLS-1$
            this.m_root.relativize(this.m_current).toString() + //
            '\'' + '.');
      } else {
        handle.failure("Path '" + relPath + //$NON-NLS-1$
            "' does not identify a directory.",//$NON-NLS-1$
            caught);
      }
    }
  }

  /**
   * Add or remove some values from the selection
   *
   * @param add
   *          should we add the values ({@code true}) or remove them (
   *          {@code false})
   * @param handle
   *          the handle
   * @param values
   *          the values to add
   */
  public synchronized final void select(final boolean add,
      final Handle handle, final String[] values) {
    Path path;
    int added;

    if (values == null) {
      handle.failure("Selected values cannot be null."); //$NON-NLS-1$
      return;
    }
    if (values.length <= 0) {
      handle.warning("Selected values are empty, nothing changed."); //$NON-NLS-1$
      return;
    }

    added = 0;
    for (final String string : values) {
      path = this.resolve(handle, string, this.m_root);
      if (path != null) {
        if (FSElement.changeCollection(add, this.m_root, this.m_root,
            path, this.m_selected, handle) > 0) {
          ++added;
        }
      }
    }

    if (added > 0) {
      if (added <= 1) {
        handle.success("One element has been added to the selection.");//$NON-NLS-1$
        return;
      }
      handle.success(added + //
          " elements have been added to the selection.");//$NON-NLS-1$
      return;
    }

    handle.warning("The selection has not changed.");//$NON-NLS-1$
  }

  /**
   * Resolve a path against a given root path
   *
   * @param handle
   *          the handle
   * @param relPath
   *          the path name
   * @param root
   *          the root path (or {@code null} for the default root)
   * @return the resolved path
   */
  public final Path resolve(final Handle handle, final String relPath,
      final Path root) {
    final String pre;
    Path path;
    Throwable caught;

    pre = TextUtils.prepare(relPath);
    if (pre == null) {
      handle.failure(//
          "Cannot resolve path whose name is either null, consists of only white space, or is the empty string, so folder name '" //$NON-NLS-1$
              + relPath + "' is not permitted."); //$NON-NLS-1$
      return null;
    }

    caught = null;
    if (FSElement.ROOT_PATH.equals(pre)) {
      path = this.m_root;
    } else {
      path = null;
      try {
        path = Paths.get(relPath);
      } catch (final Throwable error) {
        caught = error;
        path = null;
      }

      if ((path == null) || (caught != null)) {
        handle.failure("Failed to convert path name '" + relPath + //$NON-NLS-1$
            "' to a path.",//$NON-NLS-1$
            caught);
        return null;
      }

      try {
        path = ((root != null) ? root : this.m_root).resolve(path);
      } catch (final Throwable error) {
        caught = error;
        path = null;
      }

      if ((path == null) || (caught != null)) {
        handle.failure("Failed to resolve path '" + relPath + //$NON-NLS-1$
            '\'' + '.', caught);
        return null;
      }

      try {
        path = PathUtils.normalize(path);
      } catch (final Throwable error) {
        caught = error;
        path = null;
      }
    }

    if ((path == null) || (caught != null)) {
      handle.failure("Failed to normalize path '" + relPath + //$NON-NLS-1$
          '\'' + '.', caught);
      return null;
    }

    if (path.startsWith(this.m_root)) {
      return path;
    }
    handle.failure(//
        "Path '" + relPath + //$NON-NLS-1$
            "' points outside of the root data folder (i.e., towards a folder you are not supposed to see...).");//$NON-NLS-1$
    return null;
  }

  /**
   * Obtain the selected elements
   *
   * @return the selected elements
   */
  public synchronized final ArraySetView<FSElement> getSelected() {
    return FSElement.collectionToList(this.m_selected);
  }
}