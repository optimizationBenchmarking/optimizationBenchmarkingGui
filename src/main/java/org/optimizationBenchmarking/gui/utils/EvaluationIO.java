package org.optimizationBenchmarking.gui.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.evaluation.impl.EvaluationModuleDescriptions;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.EvaluationModules;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.EvaluationModulesBuilder;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescription;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescriptions;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleEntry;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleEntryBuilder;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.io.EvaluationXMLInput;
import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.io.EvaluationXMLOutput;
import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.ControllerUtils;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.config.ConfigurationBuilder;
import org.optimizationBenchmarking.utils.config.Definition;
import org.optimizationBenchmarking.utils.config.DefinitionXMLInput;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.AbstractTextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** Some support for configuration I/O */
public final class EvaluationIO {

  /** the defaults id */
  private static final String DEFAULTS_ID = "defaults"; //$NON-NLS-1$

  /** the module prefix */
  public static final String PARAMETER_EVALUATION_PREFIX = "evaluationPrefix"; //$NON-NLS-1$

  /** a module */
  private static final String PARAMETER_MODULE = "module[]";//$NON-NLS-1$

  /** the definition separator char */
  private static final char DEF_SEP = ';';

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
  public static final Loaded<EvaluationModules> load(
      final String basePath, final String[] relPaths, final Handle handle) {
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
              return new Loaded<>(path, root, builder.getResult());
            }
          }

          if ((bfa == null) || (bfa.isRegularFile() && (bfa.size() <= 0L))) {
            if (handle.isLoggable(Level.INFO)) {
              handle.info(//
                  "Evaluation file '" + relPath + //$NON-NLS-1$
                      "' does not exist or is empty, evaluation module list is empty.");//$NON-NLS-1$
            }
            return new Loaded<>(path, root, EvaluationModules.empty());
          }

          handle.failure("Path '" + relPath + //$NON-NLS-1$
              "' does not identify a file."); //$NON-NLS-1$
        }
      } catch (final Throwable error) {
        handle.failure("Failed to load path '" + //$NON-NLS-1$
            relPath + '\'' + '.', error);
      }
    }

    return null;
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
   * @throws IOException
   *           if i/o fails
   */
  public static final void putFormFields(final String prefix,
      final ModuleDescriptions descriptions,
      final EvaluationModules modules, final JspWriter out,
      final ArrayList<String> jsCollector) throws IOException {
    final ITextOutput encoded, wrapper;
    ModuleDescription md;
    String formPrefix;
    int id;

    wrapper = AbstractTextOutput.wrap(out);
    encoded = Encoder.htmlEncode(wrapper);
    id = 0;
    for (final ModuleEntry me : modules.getEntries()) {
      md = descriptions.forModule(me.getModule());
      out.append("<h3>"); //$NON-NLS-1$
      encoded.append(md.getName());
      out.append("</h3><p>");//$NON-NLS-1$
      encoded.append(md.getDescription());
      out.append("</p>");//$NON-NLS-1$
      out.append("<input type=\"hidden\" name=\"");//$NON-NLS-1$
      out.append(EvaluationIO.PARAMETER_MODULE);
      out.append("\" value=\"");//$NON-NLS-1$
      formPrefix = (prefix + ConfigIO.PREFIX_SEPARATOR + Integer.toString(
          (id++), Character.MAX_RADIX));
      out.append(formPrefix);
      out.append(EvaluationIO.DEF_SEP);
      encoded.append(TextUtils.className(me.getModule().getClass()));
      out.append("\"/>");//$NON-NLS-1$
      ConfigIO._putFormFields(formPrefix,
          md.getParameters().dump(me.getConfiguration()), out,
          jsCollector, wrapper, encoded);
    }

    out.append("<h3>Default Values</h3><p>Here you can edit the default values for any argument not provided above.</p><input type=\"hidden\" name=\"");//$NON-NLS-1$
    out.append("<input type=\"hidden\" name=\"");//$NON-NLS-1$
    out.append(EvaluationIO.PARAMETER_MODULE);
    out.append("\" value=\"");//$NON-NLS-1$
    formPrefix = (prefix + ConfigIO.PREFIX_SEPARATOR + EvaluationIO.DEFAULTS_ID);
    out.append(formPrefix);
    out.append(EvaluationIO.DEF_SEP);
    out.append("\"/>");//$NON-NLS-1$
    ConfigIO._putFormFields(formPrefix, descriptions.getJointParameters()
        .dump(modules.getConfiguration()), out, jsCollector, wrapper,
        encoded);
  }

  /**
   * Load a configuration from a request
   *
   * @param request
   *          the request with all the parameters
   * @param prefix
   *          the prefix
   * @param handle
   *          a handle
   * @return the configuration
   */
  private static final EvaluationModules _loadModulesFromRequest(
      final String prefix, final HttpServletRequest request,
      final Handle handle) {
    final String[] strings;
    Definition def;
    String cfgPrefix, str, clazz, id;
    int i, j;

    try (final EvaluationModulesBuilder builder = new EvaluationModulesBuilder()) {
      strings = request.getParameterValues(EvaluationIO.PARAMETER_MODULE);
      if (strings != null) {

        // get the configuration
        cfgPrefix = (prefix + ConfigIO.PREFIX_SEPARATOR + EvaluationIO.DEFAULTS_ID);
        findConfig: for (i = strings.length; (--i) >= 0;) {
          str = strings[i];
          j = str.lastIndexOf(EvaluationIO.DEF_SEP);

          if (j < 0) {
            clazz = null;
            id = TextUtils.prepare(str);
          } else {
            clazz = TextUtils.prepare(str.substring(j + 1));
            id = TextUtils.prepare(str.substring(0, j));
          }
          Logger.getGlobal().severe(str + ' ' + clazz + ' ' + id);
          if (cfgPrefix.equals(id) && (clazz == null)) {
            strings[i] = null;
            try (final ConfigurationBuilder cb = builder
                .setConfiguration()) {
              ConfigIO._loadConfigFromRequest(cfgPrefix, request,//
                  EvaluationModuleDescriptions//
                      .getDescriptions(handle)//
                      .getJointParameters(), handle, cb);
            }
            break findConfig;
          }
        }

        // ok, we got the base config, now load all the modules
        for (final String string : strings) {
          if (string == null) {
            continue;
          }
          j = string.lastIndexOf(EvaluationIO.DEF_SEP);
          if (j < 0) {
            continue;
          }
          clazz = TextUtils.prepare(string.substring(j + 1));
          id = TextUtils.prepare(string.substring(0, j));

          if ((clazz != null) && (id != null)) {
            try (final ModuleEntryBuilder meb = builder.addModule()) {
              meb.setModule(clazz);
              def = DefinitionXMLInput.getInstance().forClass(
                  meb.getModule().getClass(), handle);

              try (final ConfigurationBuilder cb = meb.setConfiguration()) {
                ConfigIO._loadConfigFromRequest(id, request, def, handle,
                    cb);
              }
            }
          }
        }
      }
      return builder.getResult();
    }
  }

  /**
   * Store the contents into a file.
   *
   * @param handle
   *          the handle
   * @param request
   *          the request
   */
  public static final void store(final Handle handle,
      final HttpServletRequest request) {
    final String path, submit;
    final Path realPath;
    final String prefix;
    final EvaluationModules modules;

    submit = request.getParameter(ControllerUtils.INPUT_SUBMIT);
    if (submit.equalsIgnoreCase(FileIO.PARAM_SAVE)) {
      path = request.getParameter(ControllerUtils.PARAMETER_SELECTION);
      if (path != null) {
        realPath = handle.getController().resolve(handle, path, null);
        if (realPath != null) {

          prefix = TextUtils.prepare(request
              .getParameter(EvaluationIO.PARAMETER_EVALUATION_PREFIX));

          try {
            modules = EvaluationIO._loadModulesFromRequest(prefix,
                request, handle);
            EvaluationXMLOutput.getInstance().use().setLogger(handle)
                .setPath(realPath).setSource(modules).create().call();
            handle.success("Successfully stored configuration file '" + //$NON-NLS-1$
                path + '\'' + '.');
          } catch (final Throwable error) {
            handle.failure("Failed to store configuration file '" //$NON-NLS-1$
                + path + '\'' + '.', error);
          }
        }
      } else {
        handle.failure("No path provided."); //$NON-NLS-1$
      }
    } else {
      handle.unknownSubmit(submit);
    }
  }
}
