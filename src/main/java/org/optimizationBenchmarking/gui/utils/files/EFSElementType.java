package org.optimizationBenchmarking.gui.utils.files;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.io.EvaluationXML;
import org.optimizationBenchmarking.gui.utils.Image;
import org.optimizationBenchmarking.utils.config.ConfigurationXML;
import org.optimizationBenchmarking.utils.graphics.graphic.EGraphicFormat;
import org.optimizationBenchmarking.utils.io.IFileType;
import org.optimizationBenchmarking.utils.io.xml.IXMLFileType;
import org.optimizationBenchmarking.utils.io.xml.XMLFileType;
import org.optimizationBenchmarking.utils.text.ETextFileType;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.tools.impl.latex.ELaTeXFileType;

/** the fs element type */
public enum EFSElementType {

  /** the next higher folder */
  NEXT_UP(false, "folderUp", //$NON-NLS-1$
      "Move up one folder in the directory tree."), //$NON-NLS-1$

  /** the root of a list */
  LIST_ROOT(false, "folderCur", //$NON-NLS-1$
      "The current folder."), //$NON-NLS-1$

  /** a folder */
  FOLDER(false, "folder", //$NON-NLS-1$
      "A normal folder."), //$NON-NLS-1$

  /** a file */
  FILE(true, "file", //$NON-NLS-1$
      "The normal file. You can view or download it."), //$NON-NLS-1$

  /** an xml file */
  XML(true, "xml", //$NON-NLS-1$
      "An XML file. You may edit it as text file, view, or download it."), //$NON-NLS-1$

  /** an pdf file */
  PDF(true, "pdf", //$NON-NLS-1$
      "A PDF file. You cannot edit it, but you can view and download it."), //$NON-NLS-1$

  /** an tex file */
  TEX(true,
      "tex", //$NON-NLS-1$
      "A LaTeX source file. Such files usually contain a document, such as an article or paper, and can be compiled to PDF with a suitable LaTeX compiler. They can be edited as simple text files."), //$NON-NLS-1$

  /** an EDI file */
  EDI(true,
      "edi", //$NON-NLS-1$
      "An EDI file. The Experiment Data Interchange (EDI) format, is an XML-based format for storing and exchanging data from experiments, as well as about benchmark instances and algorithm setups. This GUI has several editors for sub-sets of EDI, namely for dimensions files, experiment files, and instance set files."), //$NON-NLS-1$

  /** an EDI dimensions file */
  EDI_DIMENSIONS(true,
      "ediDimensions", //$NON-NLS-1$
      "An EDI dimensions file. Such a file holds meta information about the measurement dimensions, i.e., which kind of data is collected during experiments. This type of file can be edited in as 'dimensions file'."), //$NON-NLS-1$

  /** an EDI instances file */
  EDI_INSTANCES(true,
      "ediInstances", //$NON-NLS-1$
      "An EDI instances file. Instances files hold the meta information about the benchmark instances which are used to evaluate algorithms on. Instance files can be edited as 'instances file'."), //$NON-NLS-1$

  /** an EDI experiment file */
  EDI_EXPERIMENT(true,
      "ediExperiment", //$NON-NLS-1$
      "An EDI experiment file. This kind of file holds meta information about one experiment, such as the experiment's name, the used algorithm, and it's parameter settings. You can edit such a file as 'experiment file'."), //$NON-NLS-1$

  /** an evaluation file */
  EVALUATION(true,
      "evaluation", //$NON-NLS-1$
      "An evaluation file specifies what the evaluator should do with the input data, i.e., what kind of statistics it should compute and how it may compare different algorithms. You can edit this kind of file in the 'evaluation file editor'."), //$NON-NLS-1$

  /** a configuration file */
  CONFIGURATION(true,
      "configuration", //$NON-NLS-1$
      "A configuration file specifies the setup of the evaluation process, including where the input data comes from, where the output data should be written, and what format we should use for the output. You can edit this file as 'configuration file'."), //$NON-NLS-1$

  /** an xhtml file */
  XHTML(true, "xhtml", //$NON-NLS-1$
      "A web page in the XHTML format."), //$NON-NLS-1$

  /** a plain text file */
  TEXT(true, "file", //$NON-NLS-1$
      "A plain text file."), //$NON-NLS-1$
  ;

  /** the map */
  private static final HashMap<IFileType, EFSElementType> MAP;

  static {
    MAP = new HashMap<>();

    EFSElementType.MAP.put(XMLFileType.XML, XML);
    EFSElementType.MAP
        .put(
            org.optimizationBenchmarking.experimentation.io.impl.edi.EDI.EDI_XML,
            EDI);
    EFSElementType.MAP.put(EvaluationXML.EVALUATION_XML, EVALUATION);
    EFSElementType.MAP.put(ConfigurationXML.CONFIG_XML, CONFIGURATION);
    EFSElementType.MAP.put(ELaTeXFileType.PDF, PDF);
    EFSElementType.MAP.put(ELaTeXFileType.TEX, TEX);
    EFSElementType.MAP.put(EGraphicFormat.PDF, PDF);
    EFSElementType.MAP
        .put(
            org.optimizationBenchmarking.utils.document.impl.xhtml10.XHTML.XHTML_1_0,
            XHTML);
    EFSElementType.MAP.put(ETextFileType.TXT, TEXT);
    EFSElementType.MAP.put(ETextFileType.CSV, TEXT);
  }

  /** is this a file type? */
  private final boolean m_isFile;

  /** the icon */
  private final String m_icon;

  /** the description of the file */
  private final String m_desc;

  /**
   * create
   *
   * @param isFile
   *          if this type is a file
   * @param icon
   *          the icon
   * @param desc
   *          the description
   */
  EFSElementType(final boolean isFile, final String icon, final String desc) {
    this.m_isFile = isFile;
    this.m_icon = icon;
    this.m_desc = desc;
  }

  /**
   * Get the description of this file type
   *
   * @return the description of this file type
   */
  public final String getDescription() {
    return this.m_desc;
  }

  /**
   * Check whether this file system element type represents a file.
   *
   * @return {@code true} if this file system element type represents a
   *         file, {@code false} if it is something else, like a folder
   */
  public final boolean isFile() {
    return this.m_isFile;
  }

  /**
   * put the icon belonging to this file system element
   *
   * @param out
   *          the output writer
   * @param encoded
   *          the encoded output writer
   * @throws IOException
   *           if I/O fails
   */
  public final void putIcon(final JspWriter out, final ITextOutput encoded)
      throws IOException {
    Image.image(true, this.m_icon, this.m_desc, "folderIcon", //$NON-NLS-1$
        out, encoded);
  }

  /**
   * Get the type belonging to a given file type
   *
   * @param type
   *          the element type
   * @return the corresponding FS type
   */
  public static final EFSElementType forFileType(final IFileType type) {
    final EFSElementType fstype;
    if (type != null) {
      fstype = EFSElementType.MAP.get(type);
      if (fstype != null) {
        return fstype;
      }
      if (type instanceof IXMLFileType) {
        return EFSElementType.XML;
      }
    }
    return EFSElementType.FILE;
  }
}
