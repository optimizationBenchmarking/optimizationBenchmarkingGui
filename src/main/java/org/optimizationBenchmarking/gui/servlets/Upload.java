package org.optimizationBenchmarking.gui.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.io.EArchiveType;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;

/** A java servlet for uploading a set of files. */
public final class Upload extends HttpServlet {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** create */
  public Upload() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected final void doPost(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    final Controller controller;
    final Path dest;
    final String path;

    controller = ControllerUtils.getController(req);
    if (controller == null) {
      return;
    }

    try (final Handle handle = controller.createServletHandle()) {
      path = req.getParameter(ControllerUtils.INPUT_CURRENT_DIR);
      dest = controller.resolve(handle, path, null);
      if (dest != null) {
        if (handle.isLoggable(Level.INFO)) {
          handle.info("Begin uploading files to path '" + //$NON-NLS-1$
              path + '\'' + '.');
        }

        for (final Part part : req.getParts()) {
          if (part == null) {
            continue;
          }
          try {
            if (ControllerUtils.PARAMETER_FILES.equalsIgnoreCase(part
                .getName())) {
              Upload.__upload(part, dest, handle);
            }
          } catch (final Throwable error) {
            handle.log(Level.SEVERE, "Error while loading file.", error);//$NON-NLS-1$
          }
        }
      }
    }

    resp.sendRedirect("/controller.jsp");//$NON-NLS-1$
  }

  /**
   * Upload a file
   *
   * @param part
   *          the part
   * @param dest
   *          the destination
   * @param handle
   *          the handle
   */
  private static final void __upload(final Part part, final Path dest,
      final Handle handle) {
    final Path relPath, destPath;
    final EArchiveType zip;
    String type, str, name;
    boolean isZip;

    name = part.getSubmittedFileName();
    if ((name == null) || (name.length() <= 0)) {
      if (part.getSize() <= 0L) {
        handle.warning(//
            "Message part does not represent file, nothing to do."); //$NON-NLS-1$
        return;
      }
      name = "unknown"; //$NON-NLS-1$
    }
    relPath = Paths.get(name);

    zip = EArchiveType.ZIP;
    type = part.getContentType();

    isZip = false;
    if (type != null) {
      str = zip.getMIMEType();
      isZip = (str.equalsIgnoreCase(type) || //
      TextUtils.toLowerCase(type).contains(str));
    }

    if (!isZip) {
      isZip = zip.getDefaultSuffix().equalsIgnoreCase(
          PathUtils.getFileExtension(relPath));
    }

    try (final InputStream is = part.getInputStream()) {
      if (isZip) {
        if (handle.isLoggable(Level.INFO)) {
          handle.info("Treating '" + name + //$NON-NLS-1$
              "' as zip archive and unpacking it."); //$NON-NLS-1$
        }
        zip.decompressStreamToFolder(is, dest, "upload");//$NON-NLS-1$
      } else {
        destPath = PathUtils.normalize(relPath, dest);
        Files.copy(is, destPath);
      }
      handle.success("Succeeded to upload file '" + name + '\'' + '.');//$NON-NLS-1$
    } catch (final IOException ioe) {
      handle.failure("Failed to upload file '" + name + '\'' + '.');//$NON-NLS-1$
    }
  }
}
