package test.junit.org.optimizationBenchmarking.gui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.optimizationBenchmarking.gui.application.ApplicationInstance;
import org.optimizationBenchmarking.gui.application.ApplicationInstanceBuilder;
import org.optimizationBenchmarking.gui.application.ApplicationTool;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/**
 * Test the application.
 */
public class ApplicationTest {

  /** create */
  public ApplicationTest() {
    super();
  }

  /**
   * Get the application tool
   *
   * @return the application tool
   */
  protected final ApplicationTool getInstance() {
    return ApplicationTool.getInstance();
  }

  /** test whether the tool can be used */
  @Test(timeout = 3600000)
  public void testToolCanUse() {
    Assert.assertTrue(this.getInstance().canUse());
  }

  /**
   * test whether the tool can be used: if not, this method should throw an
   * exception
   */
  @Test(timeout = 3600000)
  public void testToolCheckCanUse() {
    this.getInstance().checkCanUse();
  }

  /** test whether the tool returns a non-{@code null} tool job builder */
  @Test(timeout = 3600000)
  public void testToolCanCreateToolJobBuilder() {
    Assert.assertNotNull(this.getInstance().use());
  }

  /**
   * test whether the tool can work without a browser at a random port
   * accessed via a socket
   */
  @Test(timeout = 3600000)
  public void testNoBrowserAndRandomPortSocket() {
    this.__checkServerAtPort(true, ApplicationTest.__getFreePort(), 0);
  }

  /**
   * test whether the tool can work with a browser at a random port
   * accessed via a socket
   */
  @Test(timeout = 3600000)
  public void testWithBrowserAndRandomPortSocket() {
    this.__checkServerAtPort(false, ApplicationTest.__getFreePort(), 0);
  }

  /**
   * test whether the tool can work without a browser at a random port
   * accessed via the server's global url
   */
  @Test(timeout = 3600000)
  public void testNoBrowserAndRandomPortGlobalURL() {
    this.__checkServerAtPort(true, ApplicationTest.__getFreePort(), 1);
  }

  /**
   * test whether the tool can work with a browser at a random port
   * accessed via the server's global url
   */
  @Test(timeout = 3600000)
  public void testWithBrowserAndRandomPortGlobalURL() {
    this.__checkServerAtPort(false, ApplicationTest.__getFreePort(), 1);
  }

  /**
   * test whether the tool can work without a browser at a random port
   * accessed via the server's local url
   */
  @Test(timeout = 3600000)
  public void testNoBrowserAndRandomPortLocalURL() {
    this.__checkServerAtPort(true, ApplicationTest.__getFreePort(), 1);
  }

  /**
   * test whether the tool can work with a browser at a random port
   * accessed via the server's local url
   */
  @Test(timeout = 3600000)
  public void testWithBrowserAndRandomPortLocalURL() {
    this.__checkServerAtPort(false, ApplicationTest.__getFreePort(), 1);
  }

  /**
   * test whether the tool can work without a browser at port 8080 accessed
   * via a socket
   */
  @Test(timeout = 3600000)
  public void testNoBrowserAndPort8080Socket() {
    if (ApplicationTest.__checkPort(8080)) {
      this.__checkServerAtPort(true, 8080, 0);
    }
  }

  /**
   * test whether the tool can work with a browser at port 8080 accessed
   * via a socket
   */
  @Test(timeout = 3600000)
  public void testWithBrowserAndPort8080Socket() {
    if (ApplicationTest.__checkPort(8080)) {
      this.__checkServerAtPort(false, 8080, 0);
    }
  }

  /**
   * test whether the tool can work without a browser at port 8080 accessed
   * via the server's global url
   */
  @Test(timeout = 3600000)
  public void testNoBrowserAndPort8080GlobalURL() {
    if (ApplicationTest.__checkPort(8080)) {
      this.__checkServerAtPort(true, 8080, 1);
    }
  }

  /**
   * test whether the tool can work with a browser at port 8080 accessed
   * via the server's global url
   */
  @Test(timeout = 3600000)
  public void testWithBrowserAndPort8080GlobalURL() {
    if (ApplicationTest.__checkPort(8080)) {
      this.__checkServerAtPort(false, 8080, 1);
    }
  }

  /**
   * test whether the tool can work without a browser at port 8080 accessed
   * via the server's local url
   */
  @Test(timeout = 3600000)
  public void testNoBrowserAndPort8080LocalURL() {
    if (ApplicationTest.__checkPort(8080)) {
      this.__checkServerAtPort(true, 8080, 1);
    }
  }

  /**
   * test whether the tool can work with a browser at port 8080 accessed
   * via the server's local url
   */
  @Test(timeout = 3600000)
  public void testWithBrowserAndPort8080LocalURL() {
    if (ApplicationTest.__checkPort(8080)) {
      this.__checkServerAtPort(false, 8080, 1);
    }
  }

  /**
   * test whether the tool can work without a browser at port 80 accessed
   * via a socket
   */
  @Test(timeout = 3600000)
  public void testNoBrowserAndPort80Socket() {
    if (ApplicationTest.__checkPort(80)) {
      this.__checkServerAtPort(true, 80, 0);
    }
  }

  /**
   * test whether the tool can work with a browser at port 80 accessed via
   * a socket
   */
  @Test(timeout = 3600000)
  public void testWithBrowserAndPort80Socket() {
    if (ApplicationTest.__checkPort(80)) {
      this.__checkServerAtPort(false, 80, 0);
    }
  }

  /**
   * test whether the tool can work without a browser at port 80 accessed
   * via the server's global url
   */
  @Test(timeout = 3600000)
  public void testNoBrowserAndPort80GlobalURL() {
    if (ApplicationTest.__checkPort(80)) {
      this.__checkServerAtPort(true, 80, 1);
    }
  }

  /**
   * test whether the tool can work with a browser at port 80 accessed via
   * the server's global url
   */
  @Test(timeout = 3600000)
  public void testWithBrowserAndPort80GlobalURL() {
    if (ApplicationTest.__checkPort(80)) {
      this.__checkServerAtPort(false, 80, 1);
    }
  }

  /**
   * test whether the tool can work without a browser at port 80 accessed
   * via the server's local url
   */
  @Test(timeout = 3600000)
  public void testNoBrowserAndPort80LocalURL() {
    if (ApplicationTest.__checkPort(80)) {
      this.__checkServerAtPort(true, 80, 1);
    }
  }

  /**
   * test whether the tool can work with a browser at port 80 accessed via
   * the server's local url
   */
  @Test(timeout = 3600000)
  public void testWithBrowserAndPort80LocalURL() {
    if (ApplicationTest.__checkPort(80)) {
      this.__checkServerAtPort(false, 80, 1);
    }
  }

  /**
   * get a free port.
   *
   * @return the port, or {@code -1} if none could be found
   */
  private static final int __getFreePort() {
    final Random rand;
    int port, count;

    rand = new Random();
    for (count = 1000; (--count) >= 0;) {
      port = (1000 + rand.nextInt(31000));
      if (ApplicationTest.__checkPort(port)) {
        return port;
      }
    }
    return (-1);
  }

  /**
   * check whether a port is free
   *
   * @param port
   *          the port
   * @return {@code true} if it can be used, {@code false} otherwise
   */
  private static final boolean __checkPort(final int port) {
    try {
      try (ServerSocket sock = new ServerSocket(port)) {
        //
      }
      Thread.sleep(5000);
      return true;
    } catch (final Throwable ignore) {
      //
    }
    return false;
  }

  /**
   * Check the server at a given port
   *
   * @param port
   *          the port
   * @param noBrowser
   *          should we go without browser
   * @param streamSource
   *          {@code 0} - socket, {@code 1} - global url, {@code 2} - local
   *          url
   */
  private final void __checkServerAtPort(final boolean noBrowser,
      final int port, final int streamSource) {
    final ApplicationInstanceBuilder builder;

    if (port < 0) {
      return;
    }

    builder = this.getInstance().use();
    builder.setDontOpenBrowser(noBrowser);
    builder.setRootPath(PathUtils.getTempDir());
    builder.setPort(port);

    try (final ApplicationInstance instance = builder.create()) {
      Thread.sleep(30000);

      for (final String page : new String[] {//
      "/index.jsp", //$NON-NLS-1$
          "/controller.jsp", //$NON-NLS-1$
          "/logLevel.jsp" //$NON-NLS-1$
      }) {
        Thread.sleep(5000);

        switch (streamSource) {
          case 0: {
            try (Socket sock = new Socket("localhost", port)) {//$NON-NLS-1$

              try (OutputStreamWriter osw = new OutputStreamWriter(
                  sock.getOutputStream())) {
                osw.write("GET ");//$NON-NLS-1$
                osw.write(page);
                osw.write(" HTTP/1.1\r\n\r\n\r\n");//$NON-NLS-1$
                osw.flush();
                sock.shutdownOutput();

                try (final InputStream is = sock.getInputStream()) {
                  ApplicationTest.__checkInputStream(is, page);
                }
              }
            }
            break;
          }

          case 1: {
            try (final InputStream is = new URL(instance.getGlobalURL()
                .toString() + page.substring(1)).openStream()) {
              ApplicationTest.__checkInputStream(is, page);
            }
            break;
          }

          default: {
            try (final InputStream is = new URL(instance.getLocalURL()
                .toString() + page.substring(1)).openStream()) {
              ApplicationTest.__checkInputStream(is, page);
            }
            break;
          }
        }
      }
    } catch (final Throwable error) {
      throw new AssertionError("Server does not behave as expected.",//$NON-NLS-1$
          error);
    }
  }

  /**
   * Check an input stream
   *
   * @param is
   *          the stream
   * @param page
   *          the page
   * @throws Throwable
   *           if something goes wrong
   */
  private static final void __checkInputStream(final InputStream is,
      final String page) throws Throwable {
    boolean has;
    try (final InputStreamReader ir = new InputStreamReader(is)) {
      try (final BufferedReader br = new BufferedReader(ir)) {
        has = false;
        while (br.readLine() != null) {
          has = true;
        }
        if (!(has)) {
          throw new AssertionError("Page '" + page + //$NON-NLS-1$
              "' is empty.");//$NON-NLS-1$
        }
      }
    }
  }
}
