package org.optimizationBenchmarking.gui.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

/** A menu */
public final class Menu {

  /** the menu */
  private static final Menu[] MENU = new Menu[] {
      new Menu("/index.jsp", "home"), //$NON-NLS-1$//$NON-NLS-2$
      new Menu("/controller.jsp", "control center"), //$NON-NLS-1$//$NON-NLS-2$
      new Menu("/logLevel.jsp", "set log level"), //$NON-NLS-1$//$NON-NLS-2$
      new Menu("/help.jsp", "help", //$NON-NLS-1$//$NON-NLS-2$
          new Menu("/help/system.jsp", "the system"), //$NON-NLS-1$//$NON-NLS-2$
          new Menu("/help/process.jsp", "the process"), //$NON-NLS-1$//$NON-NLS-2$
          new Menu("/help/fileTypes.jsp", "file types") //$NON-NLS-1$//$NON-NLS-2$
      ),//
      new Menu("/about.jsp", "about"), //$NON-NLS-1$//$NON-NLS-2$
  };

  /** the page */
  private final String m_page;
  /** the name */
  private final String m_name;
  /** the sub-menu items */
  private final Menu[] m_items;

  /**
   * create the menu
   *
   * @param page
   *          the page to which the menu item should lead
   * @param name
   *          the name of the menu item
   * @param sub
   *          the list of sub-items
   */
  Menu(final String page, final String name, final Menu... sub) {
    super();
    this.m_page = page;
    this.m_name = name;
    this.m_items = sub;
  }

  /**
   * render a menu item
   *
   * @param depth
   *          the current depth
   * @param current
   *          the current page
   * @param out
   *          the output writer
   * @throws IOException
   *           if I/O fails
   */
  final void _render(final int depth, final String current,
      final JspWriter out) throws IOException {
    out.write("<div class=\"menuItem"); //$NON-NLS-1$
    out.print(depth);
    if (this.m_page.equalsIgnoreCase(current)) {
      out.write(" menuSelected"); //$NON-NLS-1$
    }
    out.write("\"><a class=\"menuItem\" href=\"");//$NON-NLS-1$
    out.write(this.m_page);
    out.write("\">");//$NON-NLS-1$
    out.write(this.m_name);
    out.write("</a></div>");//$NON-NLS-1$

    for (final Menu sub : this.m_items) {
      sub._render((depth + 1), current, out);
    }
  }

  /**
   * Render the menu
   *
   * @param request
   *          the servlet request
   * @param out
   *          the output destination
   * @throws IOException
   *           if I/O fails
   */
  public static final void renderMenu(final HttpServletRequest request,
      final JspWriter out) throws IOException {
    final String current;

    current = request.getServletPath();
    out.write("<div class=\"menu\">"); //$NON-NLS-1$
    for (final Menu menu : Menu.MENU) {
      menu._render(1, current, out);
    }
    out.write("</div>"); //$NON-NLS-1$
  }

}
