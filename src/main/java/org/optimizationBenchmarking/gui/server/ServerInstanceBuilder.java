package org.optimizationBenchmarking.gui.server;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.io.paths.TempDir;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.tools.impl.abstr.ToolJobBuilder;
import org.optimizationBenchmarking.utils.tools.spec.IConfigurableToolJobBuilder;

/** The server builder */
public final class ServerInstanceBuilder extends
    ToolJobBuilder<ServerInstance, ServerInstanceBuilder> implements
    IConfigurableToolJobBuilder {

  /** the port parameter */
  public static final String PARAM_PORT = "port"; //$NON-NLS-1$

  /** the default port for the gui server */
  public static final int DEFAULT_PORT = 8080;

  /** the container initializers */
  private static final String CONTAINER_INIT = "org.eclipse.jetty.containerInitializers";//$NON-NLS-1$

  /** the instance manager name */
  private static final String IM_NAME = InstanceManager.class.getName();

  /** this is set to {@code true} when the server instance is created */
  private boolean m_created;

  /** the port to use */
  private int m_port;

  /** the web root */
  private String m_webRoot;

  /** the list of servlets */
  private LinkedHashMap<Class<? extends HttpServlet>, String> m_servlets;

  /** the used servlet paths */
  private HashSet<String> m_usedPaths;

  /** the attributes */
  private HashMap<String, Object> m_attributes;

  /** create the server builder */
  ServerInstanceBuilder() {
    super();
    this.m_port = ServerInstanceBuilder.DEFAULT_PORT;
    this.m_servlets = new LinkedHashMap<>();
    this.m_usedPaths = new HashSet<>();
    this.m_usedPaths.add("/"); //$NON-NLS-1$

    this.m_attributes = new HashMap<>();
    // block some attributes
    this.m_attributes.put(ServerInstanceBuilder.CONTAINER_INIT,
        ServerInstanceBuilder.CONTAINER_INIT);
    this.m_attributes.put(ServerInstanceBuilder.IM_NAME,
        ServerInstanceBuilder.IM_NAME);
    this.m_attributes.put(Configuration.PARAM_LOGGER,
        Configuration.PARAM_LOGGER);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final ServerInstanceBuilder configure(
      final Configuration config) {
    final Logger oldLogger, newLogger;

    this.__checkCreated();

    this.setPort(config.getInt(ServerInstanceBuilder.PARAM_PORT, 0, 65535,
        this.m_port));

    oldLogger = this.getLogger();
    newLogger = config.getLogger(Configuration.PARAM_LOGGER, oldLogger);
    if ((oldLogger != null) || (newLogger != null)) {
      this.setLogger(newLogger);
    }

    return this;
  }

  /**
   * Check whether the server instance has been created, throw an
   * {@link java.lang.IllegalStateException} if so, do nothing otherwise.
   */
  private final void __checkCreated() {
    if (this.m_created) {
      throw new IllegalStateException(//
          "Server instance has already been created.");//$NON-NLS-1$
    }
  }

  /**
   * Set the port of the server builder
   *
   * @param port
   *          the port to use: {@code 0} for random assignment
   */
  public synchronized final void setPort(final int port) {
    if ((port < 0) || (port >= 65536)) {
      throw new IllegalArgumentException(//
          "Port must be in range 0..65535, but is " //$NON-NLS-1$
              + port);
    }
    this.__checkCreated();
    this.m_port = port;
  }

  /**
   * Set the web root
   *
   * @param webRoot
   *          the web root
   */
  public synchronized final void setWebRoot(final String webRoot) {
    final String root;

    root = TextUtils.prepare(webRoot);
    if (root == null) {
      throw new IllegalArgumentException(//
          "Web root cannot be null, empty, or just consist of white space, but you specified '" //$NON-NLS-1$
              + webRoot + '\'');
    }
    this.__checkCreated();
    this.m_webRoot = webRoot;
  }

  /**
   * Add an attribute
   *
   * @param name
   *          the attribute name
   * @param value
   *          the attribute value
   */
  public synchronized final void addAttribute(final String name,
      final Object value) {
    final Object old;

    if (name == null) {
      throw new IllegalArgumentException(//
          "Attribute name cannot be null."); //$NON-NLS-1$
    }
    if (value == null) {
      throw new IllegalArgumentException(//
          "Value of attribute '" + //$NON-NLS-1$
              name + "' cannot be null."); //$NON-NLS-1$
    }

    old = this.m_attributes.get(name);
    if (old != null) {
      if (old == value) {
        return;
      }
      throw new IllegalArgumentException(//
          "Value of attribute '" + //$NON-NLS-1$
              name + "' has already been set to '" //$NON-NLS-1$
              + old + "', cannot be set to '" + //$NON-NLS-1$
              value + '\'' + '.');
    }

    this.__checkCreated();
    this.m_attributes.put(name, value);
  }

  /**
   * Add servlet
   *
   * @param clazz
   *          the clazz of the servlet
   * @param path
   *          the path to the servlet
   */
  public synchronized final void addServlet(
      final Class<? extends HttpServlet> clazz, final String path) {
    final String prep;

    if (clazz == null) {
      throw new IllegalArgumentException(//
          "Servlet class must be instance of HttpServlet, but is " //$NON-NLS-1$
              + TextUtils.className(clazz));
    }

    prep = TextUtils.prepare(path);
    if (prep == null) {
      throw new IllegalArgumentException(//
          "Path to servlet cannot be empty, null, or just consist of white space, but is '"//$NON-NLS-1$
              + path + '\'');
    }

    this.__checkCreated();

    if (!(this.m_usedPaths.add(prep))) {
      throw new IllegalArgumentException(//
          "Path '" + path + "' is already assigned to a servlet.");//$NON-NLS-1$//$NON-NLS-2$
    }

    if (this.m_servlets.get(clazz) != null) {
      throw new IllegalArgumentException(//
          "Servlet '" //$NON-NLS-1$
              + TextUtils.className(clazz) + //
              "' is already assigned to a path.");//$NON-NLS-1$
    }
    this.m_servlets.put(clazz, prep);
  }

  /**
   * Create the server instance.
   *
   * @param logger
   *          the logger
   * @param port
   *          the port
   * @param webRoot
   *          the web root
   * @param servlets
   *          the servlets
   * @param attributes
   *          the attributes
   * @return the server instance
   * @throws Exception
   *           if something goes wrong
   */
  @SuppressWarnings("resource")
  private static final ServerInstance __create(final Logger logger,
      final int port, final String webRoot,
      final LinkedHashMap<Class<? extends HttpServlet>, String> servlets,
      final HashMap<String, Object> attributes) throws Exception {
    final String baseURI;
    final TempDir temp;
    final org.eclipse.jetty.server.Server server;
    final ServerConnector connector;
    final WebAppContext context;

    if (webRoot == null) {
      throw new IllegalStateException("You must set the web root."); //$NON-NLS-1$
    }

    if (logger != null) {
      org.eclipse.jetty.util.log.Log.setLog(new _Logger(logger));
    }

    temp = new TempDir();

    try {

      server = new org.eclipse.jetty.server.Server();
      server.setStopAtShutdown(true);
      try {

        connector = new ServerConnector(server);
        try {
          if (port > 0) {
            connector.setPort(port);
          }
          server.addConnector(connector);

          context = new WebAppContext();
          try {
            context.setContextPath("/"); //$NON-NLS-1$
            context.setAttribute("javax.servlet.context.tempdir",//$NON-NLS-1$
                PathUtils.getPhysicalPath(temp.getPath(), false));
            context
                .setAttribute(
                    "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",//$NON-NLS-1$
                    ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/.*taglibs.*\\.jar$");//$NON-NLS-1$

            baseURI = ServerInstanceBuilder.class.getResource(webRoot)
                .toURI().toASCIIString();
            context.setResourceBase(baseURI);

            attributes.put(ServerInstanceBuilder.CONTAINER_INIT,
                ServerInstanceBuilder.__jspInitializers());
            attributes.put(ServerInstanceBuilder.IM_NAME,//
                new SimpleInstanceManager());
            attributes.put(Configuration.PARAM_LOGGER, logger);

            for (final Map.Entry<String, Object> entry : attributes
                .entrySet()) {
              context.setAttribute(entry.getKey(), entry.getValue());
            }

            context.addBean(//
                new ServletContainerInitializersStarter(context), true);
            context.setClassLoader(ServerInstanceBuilder
                .__getUrlClassLoader());

            context.addServlet(ServerInstanceBuilder.__jspServletHolder(),
                "*.jsp");//$NON-NLS-1$

            for (final Map.Entry<Class<? extends HttpServlet>, String> entry : servlets
                .entrySet()) {
              context.addServlet(entry.getKey(), entry.getValue());
            }

            context
                .addServlet(
                    ServerInstanceBuilder.__defaultServletHolder(baseURI),
                    "/");//$NON-NLS-1$

            server.setHandler(context);
            server.start();

            return new ServerInstance(logger, temp, server, connector,
                context);

          } catch (final Exception e4) {
            context.destroy();
            throw e4;
          }
        } catch (final Exception e3) {
          connector.close();
          throw e3;
        }
      } catch (final Exception e2) {
        server.destroy();
        throw e2;
      }
    } catch (final Exception e1) {
      temp.close();
      throw e1;
    }
  }

  /**
   * Ensure the JSP engine is initialized correctly
   *
   * @return the JSP initializers
   */
  private static final List<ContainerInitializer> __jspInitializers() {
    final JettyJasperInitializer sci;
    final ContainerInitializer initializer;

    sci = new JettyJasperInitializer();
    initializer = new ContainerInitializer(sci, null);
    return new ArrayListView<>(new ContainerInitializer[] { initializer });
  }

  /**
   * Set {@link java.lang.ClassLoader} of the context to be sane (needed
   * for JSTL). JSP requires a non-System {@link java.lang.ClassLoader}.
   * This method simply wraps the embedded System
   * {@link java.lang.ClassLoader} in a way that makes it suitable for JSP
   * to use.
   *
   * @return the {@link java.lang.ClassLoader}
   */
  private static final ClassLoader __getUrlClassLoader() {
    return new URLClassLoader(new URL[0],
        ServerInstanceBuilder.class.getClassLoader());
  }

  /**
   * Create JSP Servlet (must be named "jsp")
   *
   * @return the servlet holder
   */
  private static final ServletHolder __jspServletHolder() {
    ServletHolder holderJsp;

    holderJsp = new ServletHolder("jsp", JettyJspServlet.class); //$NON-NLS-1$
    holderJsp.setInitOrder(0);
    holderJsp.setInitParameter("compilerTargetVM", "1.7");//$NON-NLS-1$//$NON-NLS-2$
    holderJsp.setInitParameter("compilerSourceVM", "1.7");//$NON-NLS-1$//$NON-NLS-2$
    holderJsp.setInitParameter("validating", "true");//$NON-NLS-1$//$NON-NLS-2$
    holderJsp.setInitParameter("enableTldValidation", "true");//$NON-NLS-1$//$NON-NLS-2$
    holderJsp.setInitParameter("trimSpaces", "true");//$NON-NLS-1$//$NON-NLS-2$
    holderJsp.setInitParameter("keepgenerated", "false");//$NON-NLS-1$//$NON-NLS-2$
    holderJsp.setInitParameter("saveByteCode", "false");//$NON-NLS-1$//$NON-NLS-2$
    return holderJsp;
  }

  /**
   * Create Default Servlet (must be named "default")
   *
   * @param baseURI
   *          the base uri
   * @return the servlet
   */
  private static final ServletHolder __defaultServletHolder(
      final String baseURI) {
    ServletHolder holderDefault;

    holderDefault = new ServletHolder("default",//$NON-NLS-1$
        DefaultServlet.class);

    holderDefault.setInitParameter("resourceBase", baseURI);//$NON-NLS-1$
    holderDefault.setInitParameter("dirAllowed", "false");//$NON-NLS-1$//$NON-NLS-2$
    return holderDefault;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final ServerInstance create() throws IOException {
    final Logger logger;
    final LinkedHashMap<Class<? extends HttpServlet>, String> set;
    final HashMap<String, Object> attributes;
    ServerInstance inst;

    this.__checkCreated();
    this.m_created = true;

    set = this.m_servlets;
    this.m_servlets = null;
    this.m_usedPaths = null;
    attributes = this.m_attributes;
    this.m_attributes = null;

    logger = this.getLogger();
    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info("Begin starting the web server.");//$NON-NLS-1$
    }

    try {
      inst = ServerInstanceBuilder.__create(logger, this.m_port,
          this.m_webRoot, set, attributes);
    } catch (final Exception error) {
      ErrorUtils.logError(logger, "Error while starting web server.",//$NON-NLS-1$
          error, false, RethrowMode.AS_IO_EXCEPTION);
      return null;
    }

    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info(//
          "The web server has successfully started and now can be accessed at '"//$NON-NLS-1$
              + inst.getLocalURL()//
              + "' for browsers running on this machine and at '"//$NON-NLS-1$
              + inst.getGlobalURL() + "' for any browser.");//$NON-NLS-1$
    }

    return inst;
  }
}
