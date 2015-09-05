package org.optimizationBenchmarking.gui.utils.files;

import java.util.HashMap;

import org.optimizationBenchmarking.utils.graphics.graphic.EGraphicFormat;
import org.optimizationBenchmarking.utils.io.IFileType;
import org.optimizationBenchmarking.utils.io.xml.IXMLFileType;
import org.optimizationBenchmarking.utils.io.xml.XMLFileType;
import org.optimizationBenchmarking.utils.tools.impl.latex.ELaTeXFileType;

/** the fs element type */
public enum EFSElementType {

  /** the next higher folder */
  NEXT_UP(false, "folderUp"), //$NON-NLS-1$

  /** the root of a list */
  LIST_ROOT(false, "folderCur"), //$NON-NLS-1$

  /** a folder */
  FOLDER(false, "folder"), //$NON-NLS-1$

  /** a file */
  FILE(true, "file"), //$NON-NLS-1$

  /** an xml file */
  XML(true, "xml"), //$NON-NLS-1$
  /** an pdf file */
  PDF(true, "pdf"), //$NON-NLS-1$
  /** an tex file */
  TEX(true, "tex"), //$NON-NLS-1$

  /** an EDI file */
  EDI(true, "edi"), //$NON-NLS-1$
  /** an EDI dimensions file */
  EDI_DIMENSIONS(true, "ediDimensions"), //$NON-NLS-1$
  /** an EDI instances file */
  EDI_INSTANCES(true, "ediInstances"), //$NON-NLS-1$
  /** an EDI experiment file */
  EDI_EXPERIMENT(true, "ediExperiment"), //$NON-NLS-1$
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
    EFSElementType.MAP.put(ELaTeXFileType.PDF, PDF);
    EFSElementType.MAP.put(ELaTeXFileType.TEX, TEX);
    EFSElementType.MAP.put(EGraphicFormat.PDF, PDF);
  }

  /** is this a file type? */
  private final boolean m_isFile;

  /** the icon */
  private final String m_icon;

  /**
   * create
   *
   * @param isFile
   *          if this type is a file
   * @param icon
   *          the icon
   */
  EFSElementType(final boolean isFile, final String icon) {
    this.m_isFile = isFile;
    this.m_icon = icon;
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
   * get the icon belonging to this file system element
   *
   * @return the icon belonging to this file system element
   */
  public final String getIcon() {
    return this.m_icon;
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
