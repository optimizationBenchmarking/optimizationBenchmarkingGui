package test.junit.org.optimizationBenchmarking.gui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
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

  /** test whether the tool can work without a browser at a random port */
  @Test(timeout = 3600000)
  public void testNoBrowserAndRandomPort() {
    this.__checkServerAtPort(true, ApplicationTest.__getFreePort());
  }

  /** test whether the tool can work with a browser at a random port */
  @Test(timeout = 3600000)
  public void testWithBrowserAndRandomPort() {
    this.__checkServerAtPort(false, ApplicationTest.__getFreePort());
  }

  /**
   * get a free port.
   *
   * @return the port
   */
  private static final int __getFreePort() {
    final Random rand;
    int port;

    rand = new Random();
    for (;;) {
      port = (1000 + rand.nextInt(31000));
      try {
        try (ServerSocket sock = new ServerSocket(port)) {
          //
        }
        Thread.sleep(5000);
        return port;
      } catch (final Throwable ignore) {
        //
      }
    }
  }

  /**
   * Check the server at a given port
   *
   * @param port
   *          the port
   * @param noBrowser
   *          should we go without browser
   */
  private final void __checkServerAtPort(final boolean noBrowser,
      final int port) {
    final ApplicationInstanceBuilder builder;
    String host;
    InetAddress addr;
    boolean has;

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
        Thread.sleep(2500);
        host = "localhost";//$NON-NLS-1$
        addr = InetAddress.getByName(host);
        if (addr instanceof Inet6Address) {
          host = ('[' + addr.getHostAddress() + ']');
        }
        Thread.sleep(2500);

        try (final InputStream is = new URL(((("http://" + host) + ':') + //$NON-NLS-1$
            port)
            + page).openStream()) {
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

    } catch (final Throwable error) {
      throw new AssertionError("Server does not behave as expected.",//$NON-NLS-1$
          error);
    }
  }

}
