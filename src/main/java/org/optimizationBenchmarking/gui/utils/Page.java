package org.optimizationBenchmarking.gui.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.AbstractTextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.tokenizers.LineIterator;
import org.optimizationBenchmarking.utils.text.transformations.JavaCharTransformer;
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

/** A collector for java script functions which can flush them at the end */
public final class Page implements Closeable {

  /** the end of the java script */
  private static final char[] JAVASCRIPT_END = { '<', '/', 's', 'c', 'r',
      'i', 'p', 't', '>', };
  /** the start of the java script */
  private static final char[] JAVASCRIPT_START = { '<', 's', 'c', 'r',
      'i', 'p', 't', ' ', 't', 'y', 'p', 'e', '=', '"', 't', 'e', 'x',
      't', '/', 'j', 'a', 'v', 'a', 's', 'c', 'r', 'i', 'p', 't', '"', '>' };

  /** the line break */
  private static final char[] INDENT = { '&', 'n', 'b', 's', 'p', ';',
      '&', 'n', 'b', 's', 'p', ';', '&', 'n', 'b', 's', 'p', ';', '&',
      'n', 'b', 's', 'p', ';' };
  /** the line break */
  private static final char[] BR = { '<', 'b', 'r', '/', '>' };

  /** the function */
  private static final char[] FUNCTION = { 'f', 'u', 'n', 'c', 't', 'i',
      'o', 'n', ' ', };

  /** the on-load */
  private static final char[] ON_LOAD = { 'w', 'i', 'n', 'd', 'o', 'w',
      '.', 'o', 'n', 'l', 'o', 'a', 'd', '=', 'f', 'u', 'n', 'c', 't',
      'i', 'o', 'n', '(', ')', '{' };

  /** the prefix separator */
  private static final char PREFIX_SEPARATOR = '_';

  /** the id counter */
  private int m_idCounter;

  /** the ids */
  private LinkedHashMap<FunctionRenderer, String> m_functions;

  /** the output writer */
  private JspWriter m_out;

  /** the invocations to be done one page load */
  private LinkedHashSet<String> m_onLoad;

  /** the wrapped encoded text output */
  private ITextOutput m_wrapped;

  /** the html encoded text output */
  private ITextOutput m_htmlEncoded;
  /** the javascript encoded text output */
  private ITextOutput m_jsEncoded;

  /**
   * Create the page
   *
   * @param out
   *          the output writer
   */
  public Page(final JspWriter out) {
    super();
    this.m_out = out;
    this.m_wrapped = AbstractTextOutput.wrap(out);
    this.m_htmlEncoded = XMLCharTransformer.getInstance().transform(
        this.m_wrapped);
  }

  /**
   * Print the lines in a string
   *
   * @param multiLines
   *          the multiple lines
   * @param indent
   *          should we indent?
   * @param useBR
   *          should we use <code>&lt;br/&gt;</code> for line breaks or
   *          real line breaks
   * @throws IOException
   *           if I/O fails
   */
  public final void printLines(final String multiLines,
      final boolean indent, final boolean useBR) throws IOException {
    this.printLines(new LineIterator(multiLines), indent, useBR);
  }

  /**
   * Print the lines in a string
   *
   * @param multiLines
   *          the multiple lines
   * @param indent
   *          should we indent?
   * @param useBR
   *          should we use <code>&lt;br/&gt;</code> for line breaks or
   *          real line breaks
   * @throws IOException
   *           if I/O fails
   */
  public final void printLines(final Iterable<String> multiLines,
      final boolean indent, final boolean useBR) throws IOException {
    boolean first;

    first = true;
    for (final String line : multiLines) {
      if (first) {
        first = false;
      } else {
        if (useBR) {
          this.m_out.write(Page.BR);
        } else {
          this.m_out.println();
        }
        if (indent) {
          this.m_out.write(Page.INDENT);
        }
      }
      this.m_htmlEncoded.append(line);
    }
  }

  /**
   * Create the page
   *
   * @param context
   *          the page context
   */
  public Page(final PageContext context) {
    this(context.getOut());
  }

  /**
   * Get the raw page writer
   *
   * @return the raw page writer
   */
  public final JspWriter getOut() {
    return this.m_out;
  }

  /**
   * Get the html encoded page writer
   *
   * @return the html encoded page writer
   */
  public final ITextOutput getHTMLEncoded() {
    return this.m_htmlEncoded;
  }

  /**
   * Get the javascript encoded page writer
   *
   * @return the javascript encoded page writer
   */
  public final ITextOutput getJSEncoded() {
    if (this.m_jsEncoded == null) {
      this.m_jsEncoded = JavaCharTransformer.getInstance().transform(
          this.m_wrapped);
    }
    return this.m_jsEncoded;
  }

  /**
   * Get a new prefix
   *
   * @return the new prefix
   */
  public final String newPrefix() {
    return Integer.toString((this.m_idCounter++), Character.MAX_RADIX);
  }

  /**
   * Get the id of the given function
   *
   * @param renderer
   *          the renderer
   * @return the id
   */
  public final String getFunction(final FunctionRenderer renderer) {
    String id;

    if (this.m_functions == null) {
      this.m_functions = new LinkedHashMap<>();
    } else {
      id = this.m_functions.get(renderer);
      if (id != null) {
        return id;
      }
    }

    id = ('_' + this.newPrefix());
    this.m_functions.put(renderer, id);
    return id;
  }

  /**
   * Invoke the given function on load
   *
   * @param invocation
   *          the invocation
   */
  public final void onLoad(final String invocation) {
    final int len;
    final String use;

    use = TextUtils.prepare(invocation);
    if ((use == null) || ((len = use.length()) <= 0)) {
      throw new IllegalArgumentException(//
          "Invocation cannot be null, empty, or just consist of white space, but you specified '" //$NON-NLS-1$
              + use + '\'' + '.');
    }
    if (this.m_onLoad == null) {
      this.m_onLoad = new LinkedHashSet<>();
    }

    this.m_onLoad.add((use.charAt(len - 1) == ';') ? use : (use + ';'));
  }

  /**
   * flush the loaded stuff
   *
   * @throws IOException
   *           if i/o fails
   */
  private final void __flushOnLoad() throws IOException {
    if (this.m_onLoad != null) {
      try {
        this.m_out.write(Page.ON_LOAD);
        for (final String call : this.m_onLoad) {
          this.m_out.write(call);
        }
        this.m_out.write('}');
      } finally {
        this.m_onLoad = null;
      }
    }
  }

  /**
   * flush the loaded stuff
   *
   * @throws IOException
   *           if i/o fails
   */
  private final void __flushFunctions() throws IOException {
    if (this.m_functions != null) {
      try {
        for (final Map.Entry<FunctionRenderer, String> entry : this.m_functions
            .entrySet()) {
          this.m_out.write(Page.FUNCTION);
          this.m_out.write(entry.getValue());
          entry.getKey().render(this);
        }
      } finally {
        this.m_functions = null;
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void close() throws IOException {
    try {
      if ((this.m_out != null)
          && ((this.m_functions != null) || (this.m_onLoad != null))) {
        this.m_out.write(Page.JAVASCRIPT_START);
        this.__flushFunctions();
        this.__flushOnLoad();
        this.m_out.write(Page.JAVASCRIPT_END);
      }
    } finally {
      this.m_out = null;
      this.m_wrapped = null;
      this.m_htmlEncoded = null;
      this.m_jsEncoded = null;
      this.m_onLoad = null;
      this.m_functions = null;
    }
  }

  /**
   * Get the field prefix for a given name and prefix
   *
   * @param prefix
   *          the prefix
   * @return the field name
   */
  public static final String fieldPrefixFromPrefix(final String prefix) {
    if (prefix == null) {
      return ""; //$NON-NLS-1$
    }
    return (prefix + Page.PREFIX_SEPARATOR);
  }

  /**
   * Get the parameter name from a given prefix and field name
   *
   * @param prefix
   *          the prefix
   * @param field
   *          the field name
   * @return the parameter name, or {@code null} if it does not match to
   *         the prefix/field name
   */
  public static final String nameFromPrefixAndFieldName(
      final String prefix, final String field) {
    final int prefixLen, fieldLen;

    if ((prefix == null) || (field == null)) {
      return field;
    }
    fieldLen = field.length();
    prefixLen = prefix.length();

    if (fieldLen <= (prefixLen + 1)) {
      return null;
    }
    if (field.startsWith(prefix)) {
      if (field.charAt(prefixLen) == Page.PREFIX_SEPARATOR) {
        return TextUtils.prepare(field.substring(prefixLen + 1));
      }
    }

    return null;
  }

  /**
   * Get the field name for a given name and prefix
   *
   * @param prefix
   *          the prefix
   * @param name
   *          the name
   * @return the field name
   */
  public static final String fieldNameFromPrefixAndName(
      final String prefix, final String name) {
    if (prefix == null) {
      return name;
    }
    return (prefix + Page.PREFIX_SEPARATOR + name);
  }

  /**
   * Get the field name for a given name and prefix, but as JavaScript
   * command
   *
   * @param inString
   *          are we inside a string ({@code true}) or in normal code (
   *          {@code false})
   * @param prefix
   *          the prefix
   * @param prefixIsString
   *          {@code true} if the {@code prefix} is a {@code String},
   *          {@code false} if it is a variable name
   * @param name
   *          the name
   * @param nameIsString
   *          {@code true} if the {@code name} is a {@code String},
   *          {@code false} if it is a variable name
   * @throws IOException
   *           if I/O fails
   */
  public final void fieldNameFromPrefixAndNameJS(final boolean inString,
      final String prefix, final boolean prefixIsString,
      final String name, final boolean nameIsString) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = this.m_out;
    encoded = this.getJSEncoded();
    if (prefix != null) {
      if (prefixIsString && nameIsString) {
        if (!inString) {
          out.write('\'');
        }
        encoded.append(prefix);
        out.write(Page.PREFIX_SEPARATOR);
        encoded.append(name);
        if (!inString) {
          out.write('\'');
        }
        return;
      }

      if (prefixIsString) {
        if (!inString) {
          out.write('\'');
        }
        encoded.append(prefix);
        out.write(Page.PREFIX_SEPARATOR);
        out.write('\'');
        out.write('+');
        encoded.append(name);
        if (inString) {
          out.write('\'');
        }
        return;
      }

      if (nameIsString) {
        if (inString) {
          out.write('\'');
          out.write('+');
        }
        encoded.append(prefix);
        out.write('+');
        out.write('\'');
        out.write(Page.PREFIX_SEPARATOR);
        encoded.append(name);
        if (!inString) {
          out.write('\'');
        }
        return;
      }

      if (inString) {
        out.write('\'');
        out.write('+');
      }
      encoded.append(prefix);
      out.write('+');
      out.write('\'');
      out.write(Page.PREFIX_SEPARATOR);
      out.write('\'');
      out.write('+');
      encoded.append(name);
      if (inString) {
        out.write('+');
        out.write('\'');
      }
      return;
    }

    if (nameIsString) {
      if (!inString) {
        out.write('\'');
      }
      encoded.append(name);
      if (!inString) {
        out.write('\'');
      }
    } else {
      if (inString) {
        out.write('\'');
        out.write('+');
      }
      encoded.append(name);
      if (inString) {
        out.write('+');
        out.write('\'');
      }
    }
  }
}
