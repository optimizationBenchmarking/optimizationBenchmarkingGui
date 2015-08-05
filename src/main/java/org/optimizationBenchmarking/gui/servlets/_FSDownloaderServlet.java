package org.optimizationBenchmarking.gui.servlets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.optimizationBenchmarking.gui.controller.FSElement;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.io.EArchiveType;
import org.optimizationBenchmarking.utils.io.MimeTypeDetector;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** A java servlet for downloading a set of FS elements. */
class _FSDownloaderServlet extends HttpServlet {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** create */
  _FSDownloaderServlet() {
    super();
  }

  /**
   * provide a ZIP archive with the specific elements
   *
   * @param response
   *          the response
   * @param rootPath
   *          the root path
   * @param elements
   *          the elements
   * @param handle
   *          the handle
   * @throws IOException
   *           if I/O fails
   */
  final void _download(final HttpServletResponse response,
      final Path rootPath, final Iterable<FSElement> elements,
      final Handle handle) throws IOException {
    final ArrayList<Path> paths;
    final String name;
    Path root, current, done;
    int i;

    root = null;
    paths = new ArrayList<>();

    outer: for (final FSElement el : elements) {
      current = el.getFullPath();

      // remove duplicates
      for (i = paths.size(); (--i) >= 0;) {
        done = paths.get(i);
        if (done.equals(current) || current.startsWith(done)) {
          if (handle.isLoggable(Level.FINE)) {
            handle.warning("Path '" + rootPath.relativize(current) + //$NON-NLS-1$
                "' ignored during copying, since it is already considered in '"//$NON-NLS-1$
                + rootPath.relativize(done) + '\'' + '.');
          }
          continue outer;
        }
        if (done.startsWith(current)) {
          paths.remove(i);
          if (handle.isLoggable(Level.FINE)) {
            handle.warning("Path '" + rootPath.relativize(done) + //$NON-NLS-1$
                "' ignored during copying, since it is already considered in '"//$NON-NLS-1$
                + rootPath.relativize(current) + '\'' + '.');
          }
        }
      }
      paths.add(current);

      // find common root
      if (el.getType().isFile()) {
        current = current.getParent();
      }
      if (root == null) {
        root = current;
      } else {
        loop: for (; current != null; current = current.getParent()) {
          if (root.equals(current)) {
            break loop;
          }
          if (root.startsWith(current)) {
            root = current;
            break loop;
          }
        }
      }
    }

    // If there is exactly one single file only, we can download it
    // directly, without zipping.
    if (paths.size() == 1) {
      current = paths.get(0);
      if (Files.isRegularFile(current, LinkOption.NOFOLLOW_LINKS)) {
        response.setContentType(MimeTypeDetector.getInstance()
            .getMimeType(current));
        response
            .setHeader(
                "Content-Disposition", //$NON-NLS-1$
                "attachment; filename=\"" + //$NON-NLS-1$
                    PathUtils.sanitizePathComponent(PathUtils
                        .getName(current) + '"'));
        Files.copy(current, response.getOutputStream());
        return;
      }
    }

    // zip!

    if (rootPath.equals(root)) {
      name = "data.zip";//$NON-NLS-1$
    } else {
      name = (PathUtils.sanitizePathComponent(PathUtils.getName(root)) + ".zip");//$NON-NLS-1$
    }

    response.setContentType("application/zip"); //$NON-NLS-1$
    response.setHeader("Content-Disposition", //$NON-NLS-1$
        "attachment; filename=\"" + name + '"');//$NON-NLS-1$
    EArchiveType.ZIP.compressPathsToStream(paths, root,
        response.getOutputStream());
  }
}
