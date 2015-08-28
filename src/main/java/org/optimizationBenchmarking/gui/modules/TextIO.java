package org.optimizationBenchmarking.gui.modules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.gui.utils.editor.EditorModule;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;
import org.optimizationBenchmarking.utils.text.tokenizers.LineIterator;

/** A set of utility methods to deal with text files */
public final class TextIO extends EditorModule<String> {

  /** the contents */
  private static final String FIELD_CONTENTS = "contents"; //$NON-NLS-1$

  /** the globally shared instance of the text-io */
  public static final TextIO INSTANCE = new TextIO();

  /** create */
  private TextIO() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final String createEmpty(final Handle handle) {
    return ""; //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  protected final String loadFile(final Path file, final Handle handle)
      throws IOException {
    final MemoryTextOutput mto;
    String string;

    mto = new MemoryTextOutput();

    try (final InputStream is = PathUtils.openInputStream(file)) {
      try (final InputStreamReader isr = new InputStreamReader(is)) {
        try (final BufferedReader br = new BufferedReader(isr)) {
          while ((string = br.readLine()) != null) {
            mto.append(string);
            mto.appendLineBreak();
          }
        }
      }
    }

    return mto.toString();
  }

  /** {@inheritDoc} */
  @Override
  protected final void storeToFile(final String data, final Path file,
      final Handle handle) throws IOException {
    try (final OutputStream os = PathUtils.openOutputStream(file)) {
      try (final OutputStreamWriter osw = new OutputStreamWriter(os)) {
        try (final BufferedWriter bw = new BufferedWriter(osw)) {
          for (final String str : new LineIterator(data)) {
            bw.write(str);
            bw.newLine();
          }
        }
      }
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void formPutEditorFields(final String prefix,
      final String data, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;

    out = page.getOut();
    encoded = page.getHTMLEncoded();
    out.write("<textarea class=\"editor\" rows=\"25\" cols=\"70\" name=\"");//$NON-NLS-1$
    encoded.append(//
        Page.fieldNameFromPrefixAndName(prefix, TextIO.FIELD_CONTENTS));
    out.write("\" wrap=\"off\" autofocus>");//$NON-NLS-1$
    page.printLines(data, false, false);
    out.write("</textarea>"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  protected final String loadFromRequest(final String prefix,
      final HttpServletRequest request, final Handle handle) {
    final String param;
    param = request.getParameter(Page.fieldNameFromPrefixAndName(prefix,
        TextIO.FIELD_CONTENTS));
    if (param == null) {
      handle.warning(//
          "No contents found to be stored. Will store empty string."); //$NON-NLS-1$
    }
    return ""; //$NON-NLS-1$
  }
}