package org.optimizationBenchmarking.gui.modules;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;

import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.Evaluator;
import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.files.FSElement;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.config.ConfigurationBuilder;
import org.optimizationBenchmarking.utils.config.ConfigurationPropertiesInput;
import org.optimizationBenchmarking.utils.config.ConfigurationXMLInput;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.parallel.Execute;
import org.optimizationBenchmarking.utils.tools.impl.abstr.ProducedFileSet;

/** A class performing the evaluation procedure. */
public final class Evaluation {

  /**
   * Start a set of evaluation processes based on a list of paths, each
   * identifying a configuration file.
   *
   * @param relPaths
   *          the paths
   * @param handle
   *          the handle return the produced files
   * @return the produced files
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static final ArrayListView<FSElement> evaluate(
      final String[] relPaths, final Handle handle) {
    final ProducedFileSet collector;

    if (relPaths != null) {
      if (relPaths.length > 0) {
        collector = new ProducedFileSet();
        for (final String relPath : relPaths) {
          Evaluation.__evaluate(relPath, handle, collector);
        }
        return Evaluation.__compile(handle, collector);
      }
      handle.warning(//
          "The provided path list was empty, i.e., there is nothing to do."); //$NON-NLS-1$
      return ((ArrayListView) (ArraySetView.EMPTY_SET_VIEW));
    }
    handle.warning(//
        "The provided path list was null (probably because it was not specified at all), i.e., there is nothing to do."); //$NON-NLS-1$
    return ((ArrayListView) (ArraySetView.EMPTY_SET_VIEW));
  }

  /**
   * Get the produced files
   *
   * @param handle
   *          the handle
   * @param produced
   *          the produced files
   * @return the files
   */
  private static final ArraySetView<FSElement> __compile(
      final Handle handle, final ProducedFileSet produced) {
    final ArrayList<FSElement> list;
    final Path root;

    list = new ArrayList<>();
    root = handle.getController().getRootDir();
    for (final Path path : produced.getProducedFiles().keySet()) {
      FSElement.changeCollection(true, root, root, path, null, list,
          handle);
    }

    return FSElement.collectionToList(list);
  }

  /**
   * Start an evaluation process based on the given path. The path
   * identifies a configuration file.
   *
   * @param relPath
   *          the path
   * @param handle
   *          the handle
   * @param created
   *          a collector for the created files
   */
  private static final void __evaluate(final String relPath,
      final Handle handle, final ProducedFileSet created) {
    final Path path;
    final Controller controller;
    String extension;
    Configuration config;

    if (relPath != null) {
      if (handle.isLoggable(Level.INFO)) {
        handle.info("Now starting evaluation process '" + //$NON-NLS-1$
            relPath + '\'' + '.');
      }
      controller = handle.getController();
      path = controller.resolve(handle, relPath, null);

      if (path != null) {
        extension = PathUtils.getFileExtension(path);

        try (final ConfigurationBuilder builder = //
        new ConfigurationBuilder()) {

          builder.setBasePath(path.getParent());

          if ("txt".equalsIgnoreCase(extension) || //$NON-NLS-1$
              "properties".equalsIgnoreCase(extension)) {//$NON-NLS-1$
            if (handle.isLoggable(Level.INFO)) {
              handle.info("Loading configuration file '" + //$NON-NLS-1$
                  relPath + "' as Java properties file.");//$NON-NLS-1$
            }
            ConfigurationPropertiesInput.getInstance().use()
                .setLogger(handle).addPath(path).setDestination(builder)
                .create().call();
          } else {
            if (handle.isLoggable(Level.INFO)) {
              handle.info("Loading configuration file '" + //$NON-NLS-1$
                  relPath + "' as XML file.");//$NON-NLS-1$
            }
            ConfigurationXMLInput.getInstance().use().setLogger(handle)
                .addPath(path).setDestination(builder).create().call();
          }

          config = builder.getResult();

        } catch (final Throwable error) {
          handle.failure("Failed to load configuration file '" + //$NON-NLS-1$
              relPath + '\'' + '.', error);
          return;
        }

        if (config != null) {
          try {
            Execute.submitToCommonPool(Evaluator.getInstance().use()
                .configure(config).setFileProducerListener(created)
                .setLogger(handle).create(), null).get();
            handle.success(//
                "The evaluation procedure has been completed successfully (seemingly).");//$NON-NLS-1$
          } catch (final Throwable error) {
            handle.failure(//
                "Failed to run evaluation process based on configuration file '" //$NON-NLS-1$
                    + relPath + '\'' + '.',
                error);
            return;
          }
        } else {
          handle.failure("Could not load configuration from file '" //$NON-NLS-1$
              + relPath + '\'' + '.');
        }
      } else {
        handle.failure("The relative path '" + relPath + //$NON-NLS-1$
            "' resolved to null(!?), so I do nothing.");//$NON-NLS-1$
      }

      if (handle.isLoggable(Level.INFO)) {
        handle.info("Finished evaluation process '" + //$NON-NLS-1$
            relPath + '\'' + '.');
      }
    } else {
      handle.failure("The relative path is null, so I do nothing.");//$NON-NLS-1$
    }
  }

  /** the hidden and forbidden constructor */
  private Evaluation() {
    ErrorUtils.doNotCall();
  }
}
