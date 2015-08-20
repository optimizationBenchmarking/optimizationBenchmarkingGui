package org.optimizationBenchmarking.gui.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.evaluation.impl.EvaluationModuleDescriptions;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.EvaluationModules;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.EvaluationModulesBuilder;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescription;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescriptions;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleEntry;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.io.EvaluationXMLInput;
import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.AbstractTextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** Some support for configuration I/O */
public final class EvaluationIO {

  /** the defaults id */
  private static final String DEFAULTS_ID = "defaults"; //$NON-NLS-1$

  /** the forbidden constructor */
  private EvaluationIO() {
    ErrorUtils.doNotCall();
  }

  /**
   * Get the evaluation module descriptions
   * 
   * @param handle
   *          the handle
   * @return the evaluation module descriptions
   */
  public static final ModuleDescriptions getDescriptions(
      final Handle handle) {
    return EvaluationModuleDescriptions.getDescriptions(handle);
  }

  /**
   * Get the configuration dumps corresponding to the given paths.
   *
   * @param basePath
   *          the base path
   * @param relPaths
   *          the paths
   * @param handle
   *          the handle
   * @return the dumps
   */
  public static final EvaluationModules[] load(final String basePath,
      final String[] relPaths, final Handle handle) {
    final Controller controller;
    final EvaluationModules[] result;
    BasicFileAttributes bfa;
    Path root, path;
    String relPath;
    int i;

    if (relPaths == null) {
      handle.failure(//
          "Set of paths to load and edit evaluation files cannot be null."); //$NON-NLS-1$
      return null;
    }

    i = relPaths.length;
    if (i <= 0) {
      handle.failure(//
          "Set of paths to load and edit evaluation files cannot empty."); //$NON-NLS-1$
      return null;
    }

    controller = handle.getController();
    root = controller.getRootDir();
    if (basePath != null) {
      root = controller.resolve(handle, basePath, root);
    }

    result = new EvaluationModules[relPaths.length];
    for (i = 0; i < result.length; i++) {
      relPath = relPaths[i];
      try {
        path = handle.getController().resolve(handle, relPath, root);
        if (path != null) {
          try {
            bfa = Files.readAttributes(path, BasicFileAttributes.class,
                LinkOption.NOFOLLOW_LINKS);
          } catch (final Throwable xyz) {
            bfa = null;
          }

          if ((bfa != null) && bfa.isRegularFile() && (bfa.size() > 0L)) {

            try (final EvaluationModulesBuilder builder = new EvaluationModulesBuilder()) {
              EvaluationXMLInput.getInstance().use().setLogger(handle)
                  .addPath(path).setDestination(builder).create().call();
              result[i] = builder.getResult();
            }

            continue;
          }

          if ((bfa == null) || (bfa.isRegularFile() && (bfa.size() <= 0L))) {
            result[i] = EvaluationModules.empty();
            if (handle.isLoggable(Level.INFO)) {
              handle
                  .info("Evaluation file '" + relPath + //$NON-NLS-1$
                      "' does not exist or is empty, evaluation module list is empty.");//$NON-NLS-1$
            }
            continue;
          }

          handle.failure("Path '" + relPath + //$NON-NLS-1$
              "' does not identify a file."); //$NON-NLS-1$
        }
      } catch (final Throwable error) {
        handle.failure("Failed to load path '" + //$NON-NLS-1$
            relPath + '\'' + '.', error);
      }
    }

    return result;
  }

  /**
   * Write the form fields corresponding to the given evaluation module set
   *
   * @param prefix
   *          the id field prefix
   * @param descriptions
   *          the module descriptions
   * @param modules
   *          the evaluation modules
   * @param out
   *          the the output destination
   * @param jsCollector
   *          the java script collector
   * @return the next valid index
   * @throws IOException
   *           if i/o fails
   */
  public static final int putFormFields(final int prefix,
      final ModuleDescriptions descriptions,
      final EvaluationModules modules, final JspWriter out,
      final ArrayList<String> jsCollector) throws IOException {
    final ITextOutput encoded, wrapper;
    ModuleDescription md;
    String xprefix;
    int index;

    wrapper = AbstractTextOutput.wrap(out);
    encoded = Encoder.htmlEncode(wrapper);
    index = prefix;
    for (ModuleEntry me : modules.getEntries()) {
      md = descriptions.forModule(me.getModule());
      out.append("<h3>"); //$NON-NLS-1$
      encoded.append(md.getName());
      out.append("</h3><p>");//$NON-NLS-1$
      encoded.append(md.getDescription());
      out.append("</h3></p>");//$NON-NLS-1$
      out.append("<input type=\"hidden\" name=\"");//$NON-NLS-1$
      encoded.append(TextUtils.className(me.getModule().getClass()));
      xprefix = Integer.toString((index++), Character.MAX_RADIX);
      out.append("\" value=\"");//$NON-NLS-1$
      out.append(xprefix);
      out.append("\"/>");//$NON-NLS-1$
      ConfigIO._putFormFields(xprefix,
          md.getParameters().dump(me.getConfiguration()), out,
          jsCollector, wrapper, encoded);
    }

    out.append("<h3>Default Values</h3><p>Here you can edit the default values for any argument not provided above.</p><input type=\"hidden\" name=\"");//$NON-NLS-1$
    out.append(DEFAULTS_ID);
    xprefix = Integer.toString((index++), Character.MAX_RADIX);
    out.append(':');
    out.append(xprefix);
    out.append("\"/>");//$NON-NLS-1$
    ConfigIO._putFormFields(xprefix, descriptions.getJointParameters()
        .dump(modules.getConfiguration()), out, jsCollector, wrapper,
        encoded);

    return index;
  }

}
