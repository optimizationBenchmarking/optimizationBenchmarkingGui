package org.optimizationBenchmarking.gui.server;

import org.optimizationBenchmarking.utils.reflection.ReflectionUtils;
import org.optimizationBenchmarking.utils.tools.impl.abstr.Tool;
import org.optimizationBenchmarking.utils.tools.spec.IConfigurableJobTool;

/**
 * The server is a configurable tool.
 */
public final class ServerTool extends Tool implements IConfigurableJobTool {

  /** the error when checking for required classes */
  private final Throwable m_error;

  /** create */
  ServerTool() {
    Throwable error;

    // set JSP to always use the installed JDK's javac
    System.setProperty("org.apache.jasper.compiler.disablejsr199", //$NON-NLS-1$
        "false"); //$NON-NLS-1$

    error = null;
    try {
      ReflectionUtils
          .ensureClassesAreLoaded(
              "org.apache.tomcat.InstanceManager", //$NON-NLS-1$
              "org.apache.tomcat.SimpleInstanceManager", //$NON-NLS-1$
              "org.eclipse.jetty.annotations.ServletContainerInitializersStarter", //$NON-NLS-1$
              "org.eclipse.jetty.apache.jsp.JettyJasperInitializer", //$NON-NLS-1$
              "org.eclipse.jetty.jsp.JettyJspServlet", //$NON-NLS-1$
              "org.eclipse.jetty.plus.annotation.ContainerInitializer", //$NON-NLS-1$
              "org.eclipse.jetty.server.Server", //$NON-NLS-1$
              "org.eclipse.jetty.server.ServerConnector", //$NON-NLS-1$
              "org.eclipse.jetty.servlet.DefaultServlet", //$NON-NLS-1$
              "org.eclipse.jetty.servlet.ServletHolder", //$NON-NLS-1$
              "org.eclipse.jetty.webapp.WebAppContext", //$NON-NLS-1$
              "javax.servlet.ServletException", //$NON-NLS-1$
              "javax.servlet.http.Cookie", //$NON-NLS-1$
              "javax.servlet.http.HttpServlet", //$NON-NLS-1$
              "javax.servlet.http.HttpServletRequest", //$NON-NLS-1$
              "javax.servlet.http.HttpServletResponse"); //$NON-NLS-1$
    } catch (final Throwable r) {
      error = r;
    }
    this.m_error = error;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean canUse() {
    return (this.m_error == null);
  }

  /** {@inheritDoc} */
  @Override
  public final void checkCanUse() {
    if (this.m_error != null) {
      throw new IllegalStateException(//
          "Cannot instantiate GUI server, since some required classes could not be found in the classpath",//$NON-NLS-1$
          this.m_error);
    }
    super.checkCanUse();
  }

  /** {@inheritDoc} */
  @Override
  public final ServerInstanceBuilder use() {
    this.checkCanUse();
    return new ServerInstanceBuilder();
  }

  /**
   * Get the globally shared instance of the server tool
   *
   * @return the globally shared instance of the server tool
   */
  public static final ServerTool getInstance() {
    return __ServerHolder.INSTANCE;
  }

  /** the server holder class */
  private static final class __ServerHolder {
    /** the globally shared instance of the server tool */
    static final ServerTool INSTANCE = new ServerTool();
  }

}
