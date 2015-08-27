package org.optimizationBenchmarking.gui.utils;

import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;

/** the file icon class */
public final class FileIcon {

  /**
   * Get the icon belonging to a given file
   *
   * @param desc
   *          the file descriptor
   * @return the icon name
   */
  @SuppressWarnings("incomplete-switch")
  public static final String getFileIcon(final FileDesc desc) {
    String ending;

    ending = PathUtils.getFileExtension(desc.getFullPath());
    if (ending != null) {
      ending = TextUtils.toLowerCase(ending);
      switch (ending) {
        case "pdf": {return ending;} //$NON-NLS-1$
      }
    }

    return "file"; //$NON-NLS-1$
  }

  /** the forbidden constructor */
  private FileIcon() {
    ErrorUtils.doNotCall();
  }

}
