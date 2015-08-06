package org.optimizationBenchmarking.gui.utils;

import java.nio.file.Path;
import java.util.logging.Level;

import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.Evaluator;
import org.optimizationBenchmarking.gui.controller.Controller;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.config.ConfigurationBuilder;
import org.optimizationBenchmarking.utils.config.ConfigurationPropertiesInput;
import org.optimizationBenchmarking.utils.config.ConfigurationXMLInput;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;

/** A class performing the evaluation procedure. */
public final class Evaluation {

  /**
   * Start a set of evaluation processes based on a list of paths, each
   * identifying a configuration file.
   *
   * @param relPaths
   *          the paths
   * @param handle
   *          the handle
   */
  public static final void evaluate(final String[] relPaths,
      final Handle handle) {
    if (relPaths != null) {
      if (relPaths.length > 0) {
        for (final String relPath : relPaths) {
          Evaluation.evaluate(relPath, handle);
        }
      } else {
        handle.warning(//
            "The provided path list was empty, i.e., there is nothing to do."); //$NON-NLS-1$
      }
    } else {
      handle.warning(//
          "The provided path list was null (probably because it was not specified at all), i.e., there is nothing to do."); //$NON-NLS-1$
    }
  }

  /**
   * Start an evaluation process based on the given path. The path
   * identifies a configuration file.
   *
   * @param relPath
   *          the path
   * @param handle
   *          the handle
   */
  public static final void evaluate(final String relPath,
      final Handle handle) {
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
        if (extension != null) {
          extension = TextUtils.toLowerCase(extension);
        }
        try (final ConfigurationBuilder builder = new ConfigurationBuilder()) {

          builder.setBasePath(path.getParent());

          switch (extension) {
            case ".txt"://$NON-NLS-1$
            case ".properties": {//$NON-NLS-1$
              if (handle.isLoggable(Level.INFO)) {
                handle.info("Loading configuration file '" + //$NON-NLS-1$
                    relPath + "' as Java properties file.");//$NON-NLS-1$
              }
              ConfigurationPropertiesInput.getInstance().use()
                  .setLogger(handle).addPath(path).setDestination(builder)
                  .create().call();
              break;
            }

            default: {
              if (handle.isLoggable(Level.INFO)) {
                handle.info("Loading configuration file '" + //$NON-NLS-1$
                    relPath + "' as XML file.");//$NON-NLS-1$
              }
              ConfigurationXMLInput.getInstance().use().setLogger(handle)
                  .addPath(path).setDestination(builder).create().call();
            }
          }

          config = builder.getResult();

        } catch (final Throwable error) {
          handle.failure("Failed to load configuration file '" + //$NON-NLS-1$
              relPath + '\'' + '.', error);
          return;
        }

        if (config != null) {
          try {
            Evaluator.getInstance().use().configure(config)
                .setLogger(handle).create().run();
            handle.success(//
                "The evaluation procedure has seemingly completed successfully.");//$NON-NLS-1$
          } catch (final Throwable error) {
            handle.failure(//
                "Failed to run evaluation process based on configuration file '" //$NON-NLS-1$
                    + relPath + '\'' + '.', error);
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
