package org.optimizationBenchmarking.gui.servlets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.Header;
import org.optimizationBenchmarking.utils.io.EArchiveType;
import org.optimizationBenchmarking.utils.io.FileTypeRegistry;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** A java servlet for downloading the selected elements. */
public final class Download extends HttpServlet {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** create */
  public Download() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected final void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    Download.__process(req, resp);
  }

  /** {@inheritDoc} */
  @Override
  protected final void doPost(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    Download.__process(req, resp);
  }

  /**
   * Process a request
   *
   * @param req
   *          the request
   * @param resp
   *          the response
   * @throws ServletException
   *           the error
   * @throws IOException
   *           the io error
   */
  private static final void __process(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    final Controller controller;
    final String[] selection;
    final Path root;
    final ArrayList<Path> selected;
    Path currentRoot;

    controller = ControllerUtils.getController(req);
    if (controller == null) {
      return;
    }

    try (final Handle handle = controller.createServletHandle()) {
      selection = req.getParameterValues(//
          ControllerUtils.PARAMETER_SELECTION);

      if ((selection != null) && (selection.length > 0)) {
        root = controller.getRootDir();

        // collect all selected files and directories
        selected = new ArrayList<>();
        currentRoot = null;
        for (final String sel : selection) {//
          currentRoot = Download.__addPath(
              controller.resolve(handle, sel, null), selected, root,
              handle, currentRoot);
        }

        if (selected.size() > 0) {
          try {
            Download.__download(resp, root, currentRoot, selected, handle);
            handle.success(//
                "Successfully downloaded the selected files.");//$NON-NLS-1$
          } catch (final Throwable error) {
            handle.failure("Failed to download selected files.", error);//$NON-NLS-1$
          }
          return;
        }
      }
      handle.warning("Nothing to download."); //$NON-NLS-1$
    }

    resp.sendRedirect("/controller.jsp");//$NON-NLS-1$
  }

  /**
   * Add a path to a list
   *
   * @param path
   *          the path
   * @param list
   *          the list
   * @param root
   *          the root path
   * @param handle
   *          the handle
   * @param currentRoot
   *          the current root
   * @return the new root
   */
  private static final Path __addPath(final Path path,
      final ArrayList<Path> list, final Path root, final Handle handle,
      final Path currentRoot) {
    Path done;
    int i;

    if (path == null) {
      return currentRoot;
    }

    // remove duplicates
    for (i = list.size(); (--i) >= 0;) {
      done = list.get(i);
      if (done.equals(path) || path.startsWith(done)) {
        if (handle.isLoggable(Level.FINE)) {
          handle.warning("Path '" + root.relativize(path) + //$NON-NLS-1$
              "' ignored during copying, since it is already considered in '"//$NON-NLS-1$
              + root.relativize(done) + '\'' + '.');
        }
        return currentRoot;
      }
      if (done.startsWith(path)) {
        list.remove(i);
        if (handle.isLoggable(Level.FINE)) {
          handle.warning("Path '" + root.relativize(done) + //$NON-NLS-1$
              "' ignored during copying, since it is already considered in '"//$NON-NLS-1$
              + root.relativize(path) + '\'' + '.');
        }
      }
    }
    list.add(path);

    if (Files.isRegularFile(path)) {
      done = path.getParent();
      if (done == null) {
        return currentRoot;
      }
    } else {
      done = path;
    }

    if (currentRoot == null) {
      return done;
    }

    for (; done != null; done = done.getParent()) {
      if (currentRoot.equals(done)) {
        return currentRoot;
      }
      if (currentRoot.startsWith(done)) {
        return done;
      }
    }

    return currentRoot;
  }

  /**
   * provide a ZIP archive with the specific elements
   *
   * @param response
   *          the response
   * @param root
   *          the root path
   * @param archiveRoot
   *          the archive root
   * @param elements
   *          the elements
   * @param handle
   *          the handle
   * @throws IOException
   *           if I/O fails
   */
  private static final void __download(final HttpServletResponse response,
      final Path root, final Path archiveRoot,
      final ArrayList<Path> elements, final Handle handle)
      throws IOException {
    final String name;
    Path done;

    // If there is exactly one single file only, we can download it
    // directly, without zipping.
    if (elements.size() == 1) {
      done = elements.get(0);
      if (Files.isRegularFile(done, LinkOption.NOFOLLOW_LINKS)) {
        response.setContentType(FileTypeRegistry.getInstance()
            .getMimeTypeForPath(done));
        Header.defaultHeader(response);
        response
            .setHeader(
                "Content-Disposition", //$NON-NLS-1$
                "attachment; filename=\"" + //$NON-NLS-1$
                    PathUtils.sanitizePathComponent(PathUtils
                        .getName(done) + '"'));
        Files.copy(done, response.getOutputStream());
        return;
      }
    }

    // zip!

    if (root.equals(archiveRoot) || (root.startsWith(archiveRoot))) {
      name = "data.zip";//$NON-NLS-1$
    } else {
      name = (PathUtils.sanitizePathComponent(PathUtils
          .getName(archiveRoot))//
      + ".zip");//$NON-NLS-1$
    }

    response.setContentType("application/zip"); //$NON-NLS-1$
    Header.defaultHeader(response);
    response.setHeader("Content-Disposition", //$NON-NLS-1$
        "attachment; filename=\"" + name + '"');//$NON-NLS-1$
    EArchiveType.ZIP.compressPathsToStream(elements, archiveRoot,
        response.getOutputStream());
  }
}
