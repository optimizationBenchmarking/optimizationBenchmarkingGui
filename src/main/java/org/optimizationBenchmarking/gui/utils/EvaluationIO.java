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
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

/** Some support for configuration I/O */
public final class EvaluationIO {

  /** the defaults id */
  private static final String DEFAULTS_ID = "defaults"; //$NON-NLS-1$

  /** the module prefix */
  public static final String PARAMETER_EVALUATION_PREFIX = "evaluationPrefix"; //$NON-NLS-1$

  /** a module */
  private static final String PARAMETER_MODULE = "module[]";//$NON-NLS-1$

  /** only one file at a time, dude */
  private static final String ONLY_ONE = //
  "You can only edit one evaluation file at a time, the other specified files are ignored.";//$NON-NLS-1$

  /** successfully loaded the configuration file */
  private static final String LOAD_SUCCESS = //
  "Successfully loaded evaluation file "; //$NON-NLS-1$

  /** the add module function name */
  private static final String MODULE_ADD_FUNCTION_NAME = "addModule"; //$NON-NLS-1$
  /** the module change function name */
  private static final String MODULE_CHANGE_FUNCTION_NAME = "moduleChanged"; //$NON-NLS-1$
  /** the select */
  private static final String MODULE_ADD_SELECT_ID = "moduleAddSelect"; //$NON-NLS-1$
  /** the description paragraph */
  private static final String MODULE_ADD_DESC_ID = "moduleAddDes"; //$NON-NLS-1$

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
    Loaded<EvaluationModules> result;
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
          "Set of paths to load and edit evaluation files cannot be empty."); //$NON-NLS-1$
      return null;
    }

    controller = handle.getController();
    root = controller.getRootDir();
    if (basePath != null) {
      root = controller.resolve(handle, basePath, root);
    }

    for (i = 0; i < relPaths.length; i++) {
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
              result = new Loaded<>(path, root, builder.getResult());
            }
            if (i < (relPaths.length - 1)) {
              handle.warning(EvaluationIO.ONLY_ONE);
            }
            handle.success(EvaluationIO.LOAD_SUCCESS
                + result.getRelativePath() + '\'');
            return result;
          }

          if ((bfa == null) || (bfa.isRegularFile() && (bfa.size() <= 0L))) {
            if (handle.isLoggable(Level.INFO)) {
              handle.info(//
                  "Evaluation file '" + relPath + //$NON-NLS-1$
                      "' does not exist or is empty, evaluation module list is empty.");//$NON-NLS-1$
            }
            result = new Loaded<>(path, root, EvaluationModules.empty());
            if (i < (relPaths.length - 1)) {
              handle.warning(EvaluationIO.ONLY_ONE);
            }
            handle.success(EvaluationIO.LOAD_SUCCESS
                + result.getRelativePath() + '\'');
            return result;
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
    String formPrefix, divId;
    int id;

    wrapper = AbstractTextOutput.wrap(out);
    encoded = Encoder.htmlEncode(wrapper);

    id = 0;
    for (final ModuleEntry me : modules.getEntries()) {
      md = descriptions.forModule(me.getModule());

      divId = EvaluationIO.__beginModuleHead(out, prefix, id++);
      encoded.append(md.getName());
      EvaluationIO.__endModuleHead(out, divId, true);
      out.append("</h3><p>");//$NON-NLS-1$
      encoded.append(md.getDescription());
      out.append("</p>");//$NON-NLS-1$
      out.append("<input type=\"hidden\" name=\"");//$NON-NLS-1$
      out.append(EvaluationIO.PARAMETER_MODULE);
      out.append("\" value=\"");//$NON-NLS-1$
      formPrefix = ConfigIO._fieldNameFromPrefixAndName(prefix,
          ConfigIO._fieldNameFromPrefixAndName("c",//$NON-NLS-1$
              Integer.toString(id, Character.MAX_RADIX)));
      out.append(formPrefix);
      out.append(EvaluationIO.DEF_SEP);
      encoded.append(TextUtils.className(me.getModule().getClass()));
      out.append("\"/>");//$NON-NLS-1$
      ConfigIO._putFormFields(formPrefix,
          md.getParameters().dump(me.getConfiguration()), out,
          jsCollector, wrapper, encoded);
      out.append("</div>");//$NON-NLS-1$
    }

    out.append("<h3>Default Values</h3><p>Here you can edit the default values for any argument not provided above.</p><input type=\"hidden\" name=\"");//$NON-NLS-1$
    out.append(EvaluationIO.PARAMETER_MODULE);
    out.append("\" value=\"");//$NON-NLS-1$
    formPrefix = ConfigIO._fieldNameFromPrefixAndName(prefix,
        EvaluationIO.DEFAULTS_ID);
    out.append(formPrefix);
    out.append(EvaluationIO.DEF_SEP);
    out.append("\"/>");//$NON-NLS-1$
    ConfigIO._putFormFields(formPrefix, descriptions.getJointParameters()
        .dump(modules.getConfiguration()), out, jsCollector, wrapper,
        encoded);
  }

  /**
   * begin the module head
   *
   * @param out
   *          the output
   * @param prefix
   *          the prefix
   * @param id
   *          the id counter
   * @return the div id
   * @throws IOException
   *           if something fails
   */
  private static final String __beginModuleHead(final JspWriter out,
      final String prefix, final int id) throws IOException {
    final String divId;
    out.append("<div id=\""); //$NON-NLS-1$
    divId = ConfigIO._fieldNameFromPrefixAndName(prefix,
        ConfigIO._fieldNameFromPrefixAndName("d",//$NON-NLS-1$
            Integer.toString(id, Character.MAX_RADIX)));
    out.append(divId);
    out.append("\"><h3>"); //$NON-NLS-1$
    return divId;
  }

  /**
   * make the buttons
   *
   * @param out
   *          the output
   * @param id
   *          the div id
   * @param canDelete
   *          can we delete this element?
   * @throws IOException
   *           if something fails
   */
  private static final void __endModuleHead(final JspWriter out,
      final String id, final boolean canDelete) throws IOException {
    out.write("<span class=\"moduleCtrls\">");//$NON-NLS-1$
    if (canDelete) {
      out.write("<input type=\"button\" value=\"delete\" onclick=\"this.parentElement.parentElement.parentElement.remove()\"/>");//$NON-NLS-1$
    }
    out.write("</span></h3>");//$NON-NLS-1$
  }

  /**
   * Load a configuration from a request
   *
   * @param descriptions
   *          the descriptions
   * @param request
   *          the request with all the parameters
   * @param prefix
   *          the prefix
   * @param handle
   *          a handle
   * @return the configuration
   */
  private static final EvaluationModules _loadModulesFromRequest(
      final String prefix, final ModuleDescriptions descriptions,
      final HttpServletRequest request, final Handle handle) {
    final String[] strings;
    Definition def;
    String cfgPrefix, str, clazz, id;
    int i, j;

    try (final EvaluationModulesBuilder builder = new EvaluationModulesBuilder()) {
      strings = request.getParameterValues(EvaluationIO.PARAMETER_MODULE);
      if (strings != null) {

        // get the configuration
        cfgPrefix = ConfigIO._fieldNameFromPrefixAndName(prefix,
            EvaluationIO.DEFAULTS_ID);
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
                  descriptions.getJointParameters(), handle, cb);
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
   * @param descriptions
   *          the descriptions
   * @param request
   *          the request
   */
  public static final void store(final Handle handle,
      final ModuleDescriptions descriptions,
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
                descriptions, request, handle);
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

  /**
   * Put a button for adding evaluation modules.
   *
   * @param descriptions
   *          the descriptions
   * @param prefix
   *          the prefix
   * @param out
   *          the the output destination
   * @param jsCollector
   *          the javascript list
   * @throws IOException
   *           if i/o fails
   */
  public static final void putAdd(final String prefix,
      final ModuleDescriptions descriptions, final JspWriter out,
      final ArrayList<String> jsCollector) throws IOException {
    final ITextOutput encoded;
    final String addFuncName, changeFuncName, selectId, parId, newPrefix;

    encoded = XMLCharTransformer.getInstance().transform(
        AbstractTextOutput.wrap(out));

    out.write("<p class=\"controllerActions\">");//$NON-NLS-1$
    out.write("<input type=\"button\" onclick=\""); //$NON-NLS-1$
    addFuncName = ((ConfigIO._functionNameFromPrefixAndName(prefix,
        EvaluationIO.MODULE_ADD_FUNCTION_NAME) + '(') + ')');
    encoded.append(addFuncName);
    out.write("\" value=\"save &amp; add module\"/>&nbsp;<select id=\"");//$NON-NLS-1$"
    selectId = ConfigIO._fieldNameFromPrefixAndName(prefix,
        EvaluationIO.MODULE_ADD_SELECT_ID);
    encoded.append(selectId);
    out.write("\" onchange=\""); //$NON-NLS-1$
    changeFuncName = ((ConfigIO._functionNameFromPrefixAndName(prefix,
        EvaluationIO.MODULE_CHANGE_FUNCTION_NAME) + '(') + ')');
    encoded.append(changeFuncName);
    out.write("\">"); //$NON-NLS-1$
    for (final ModuleDescription md : descriptions) {
      out.write("<option>");//$NON-NLS-1$
      encoded.append(md.getName());
      out.write("</option>");//$NON-NLS-1$
    }
    out.write("</select>");//$NON-NLS-1$
    out.write("</p><p id=\"");//$NON-NLS-1$
    parId = ConfigIO._fieldNameFromPrefixAndName(prefix,
        EvaluationIO.MODULE_ADD_DESC_ID);
    encoded.append(parId);
    out.write("\"/>");//$NON-NLS-1$

    out.write(ConfigIO.JAVASCRIPT_START);
    out.write("function ");//$NON-NLS-1$
    encoded.append(addFuncName);

    out.write("{var form=document.getElementById('");//$NON-NLS-1$
    encoded.append(prefix);
    out.write("');if(form!=null){var sel=document.getElementById('");//$NON-NLS-1$
    encoded.append(selectId);
    out.write("');if(sel!=null){var text=null;");//$NON-NLS-1$
    out.append("switch(String(sel.value)){");//$NON-NLS-1$
    newPrefix = (ConfigIO._fieldNameFromPrefixAndName(prefix, "__new__") + //$NON-NLS-1$
    EvaluationIO.DEF_SEP);
    for (final ModuleDescription md : descriptions) {
      out.write("case '");//$NON-NLS-1$
      encoded.append(md.getName());
      out.write("':{text='<input type=\"hidden\" name=\"");//$NON-NLS-1$
      out.append(EvaluationIO.PARAMETER_MODULE);
      out.append("\" value=\"");//$NON-NLS-1$
      encoded.append(newPrefix);
      encoded.append(TextUtils.className(md.getModuleClass()));
      out.write("\"/>';break;}");//$NON-NLS-1$
    }
    //    out.append("default:{return;}}var dummy=document.createElement('form');dummy.innerHTML=text;var insert=dummy.firstChild;sel.parentNode.insertBefore(insert,sel);dummy.submit.call(");//$NON-NLS-1$
    // // out.append(FileIO.PARAM_SAVE);

    out.write("default:{return;}}var dummy=document.createElement('form');dummy.innerHTML=text;var insert=dummy.firstChild;sel.parentNode.insertBefore(insert,sel);dummy.innerHTML='<input type=\"hidden\" name=\"");//$NON-NLS-1$
    out.write(ControllerUtils.INPUT_SUBMIT);
    out.write("\" value=\"");//$NON-NLS-1$
    out.write(FileIO.PARAM_SAVE);
    out.write("\"/>';insert=dummy.firstChild;sel.parentNode.insertBefore(insert,sel);form.submit();}}}");//$NON-NLS-1$

    out.write("function ");//$NON-NLS-1$
    encoded.append(changeFuncName);
    out.write("{var par=document.getElementById('");//$NON-NLS-1$
    encoded.append(parId);
    out.write("');if(par!=null){var sel=document.getElementById('");//$NON-NLS-1$
    encoded.append(selectId);
    out.write("');if(sel!=null){switch(String(sel.value)){");//$NON-NLS-1$
    for (final ModuleDescription md : descriptions) {
      out.write("case '");//$NON-NLS-1$
      encoded.append(md.getName());
      out.write("':{par.innerHTML='");//$NON-NLS-1$
      out.write(ConfigIO.CURRENT_SELECTION);
      encoded.append(md.getName());
      ConfigIO.printText(md.getDescription(), out, encoded);
      out.write("';break;}");//$NON-NLS-1$
    }
    out.write("default:{par.innerHTML='';}}}}}");//$NON-NLS-1$
    out.write(ConfigIO.JAVASCRIPT_END);
    jsCollector.add(changeFuncName);
  }
}
