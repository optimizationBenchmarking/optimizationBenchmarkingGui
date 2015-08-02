package org.optimizationBenchmarking.gui.application;

import org.optimizationBenchmarking.gui.server.ServerTool;
import org.optimizationBenchmarking.utils.tools.impl.abstr.ToolSuite;
import org.optimizationBenchmarking.utils.tools.spec.IConfigurableJobTool;

/**
 * The gui server tool.
 */
public class ApplicationTool extends ToolSuite implements
    IConfigurableJobTool {

  /** create the gui server tool */
  ApplicationTool() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean canUse() {
    return ServerTool.getInstance().canUse();
  }

  /** {@inheritDoc} */
  @Override
  public final void checkCanUse() {
    ServerTool.getInstance().checkCanUse();
  }

  /** {@inheritDoc} */
  @Override
  public final ApplicationInstanceBuilder use() {
    this.checkCanUse();
    return new ApplicationInstanceBuilder();
  }

  /**
   * Get the globally shared instance of the gui server tool
   *
   * @return the globally shared instance of the gui server tool
   */
  public static final ApplicationTool getInstance() {
    return __GuiServerToolHolder.INSTANCE;
  }

  /** the server tool holder */
  private static final class __GuiServerToolHolder {
    /** the shared gui server instance */
    static final ApplicationTool INSTANCE = new ApplicationTool();
  }
}
