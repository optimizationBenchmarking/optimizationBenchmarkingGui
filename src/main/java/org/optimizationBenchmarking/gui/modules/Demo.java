package org.optimizationBenchmarking.gui.modules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.io.EOSFamily;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.tools.impl.process.EProcessStream;
import org.optimizationBenchmarking.utils.tools.impl.shell.Shell;
import org.optimizationBenchmarking.utils.tools.impl.shell.ShellBuilder;
import org.optimizationBenchmarking.utils.tools.impl.shell.ShellTool;

/** The demo class */
public final class Demo {

  /** the suffixes */
  private static final String[] SUFFIXES;

  static {
    SUFFIXES = new String[EOSFamily.values().length];

    Demo.SUFFIXES[EOSFamily.Windows.ordinal()] = ".bat";//$NON-NLS-1$

    Demo.SUFFIXES[EOSFamily.MacOS.ordinal()] = //
    Demo.SUFFIXES[EOSFamily.Linux.ordinal()] = ".sh"; //$NON-NLS-1$
  }

  /** the forbidden constructor */
  private Demo() {
    ErrorUtils.doNotCall();
  }

  /**
   * Check if the current OS is support.
   *
   * @return {@code true} if the current operating system is supported
   */
  public static final boolean isOSSupported() {
    final EOSFamily os;
    os = EOSFamily.DETECTED;
    return ((os != null) && (Demo.SUFFIXES[os.ordinal()] != null));
  }

  /**
   * install a demo into the current path
   *
   * @param currentPath
   *          the current path
   * @param handle
   *          the handle
   * @param demo
   *          the demo
   */
  @SuppressWarnings("resource")
  public static final void install(final String currentPath,
      final String demo, final Handle handle) {
    final EOSFamily osf;
    final String suffix;
    final ShellTool st;
    final ShellBuilder sb;
    final int exit;
    final BufferedWriter bw;
    final Controller controller;
    Path path;
    URL url;
    String line;

    if (currentPath == null) {
      handle.failure("Current path cannot be null."); //$NON-NLS-1$
      return;
    }

    controller = handle.getController();

    try {
      path = controller.getRootDir().resolve(Paths.get(currentPath));
    } catch (final Throwable error) {
      handle.log(Level.WARNING, ("Problem when dealing with path '" //$NON-NLS-1$
          + currentPath + '\'' + '.'), error);
      path = null;
    }
    if ((path == null) || (!(controller.getCurrentDir().equals(path)))) {
      path = controller.cdAbsolute(handle, currentPath);
    }
    if (path == null) {
      // ok, path was useless and did not work, error logged by cdAbsolute
      return;
    }

    if (demo == null) {
      handle.failure("Demo name cannot be null."); //$NON-NLS-1$
      return;
    }

    osf = EOSFamily.DETECTED;
    if ((osf == null) || ((suffix = Demo.SUFFIXES[osf.ordinal()]) == null)) {
      handle.failure(//
          "Sorry, your operating system ('" + osf//$NON-NLS-1$
              + "') is not supported. This does not mean that you cannot use this software or use the demos. It only means you cannot download the demo directly from within the software."); //$NON-NLS-1$
      return;
    }

    try {
      url = new URL(//
          "https://raw.githubusercontent.com/optimizationBenchmarking/optimizationBenchmarkingDocu/master/examples/" + //$NON-NLS-1$
              demo + "/install" + suffix);//$NON-NLS-1$
    } catch (final Throwable error) {
      handle.failure(//
          "Invalid demo name: '" + demo//$NON-NLS-1$
              + "')."); //$NON-NLS-1$
      return;
    }

    st = ShellTool.getInstance();
    try {
      st.checkCanUse();
    } catch (final Throwable error) {
      handle.failure(//
          "Cannot open a shell on this computer, so we cannot use a shell to download the demo."); //$NON-NLS-1$
      return;
    }

    switch (st.getType()) {
      case DOS: {
        if (EOSFamily.DETECTED != EOSFamily.Windows) {
          handle.failure(//
              "DOS-type shell requires Windows-type operating system, but found " //$NON-NLS-1$
                  + EOSFamily.DETECTED);
          return;
        }
        break;
      }
      case BOURNE: {
        if ((EOSFamily.DETECTED != EOSFamily.Linux)
            && (EOSFamily.DETECTED != EOSFamily.MacOS)) {
          handle.failure(//
              "Bourne-type shell requires Linux- or MacOS-type operating system, but found " //$NON-NLS-1$
                  + EOSFamily.DETECTED);
          return;
        }
        break;
      }
      default: {
        handle.failure(//
            "Cannot handle shell type " + st.getType());//$NON-NLS-1$
        return;
      }
    }

    sb = st.use();
    sb.setDirectory(path);
    sb.setLogger(handle);
    sb.setStdErr(EProcessStream.REDIRECT_TO_LOGGER);
    sb.setStdOut(EProcessStream.REDIRECT_TO_LOGGER);
    sb.setStdIn(EProcessStream.AS_STREAM);
    try (final Shell sh = sb.create()) {

      bw = sh.getStdIn();
      try {
        try (final InputStream is = url.openStream()) {
          try (final InputStreamReader isr = new InputStreamReader(is)) {
            try (final BufferedReader br = new BufferedReader(isr)) {
              while ((line = br.readLine()) != null) {
                if ((line = TextUtils.prepare(line)) != null) {
                  bw.write(line);
                  bw.newLine();
                }
              }
            }
          }
        }
        bw.flush();
      } catch (final Throwable io) {
        handle
            .failure(//
                "Error while loading the installation commands of the example from '" + //$NON-NLS-1$
                    url + "' into the shell.", io);//$NON-NLS-1$
      }

      exit = sh.waitFor();
      if (exit != 0) {
        handle.failure("Shell closed with exit code " + exit);//$NON-NLS-1$
      } else {
        handle.success(//
            "Installation of example '" + demo + //$NON-NLS-1$
                "' has seemingly been successful.");//$NON-NLS-1$
      }
    } catch (final Throwable error) {
      handle.failure("Failed during communication with shell.", error); //$NON-NLS-1$
    }

    if (!(path.equals(controller.getCurrentDir()))) {
      controller.cdAbsolute(handle, currentPath);
    }
  }
}
