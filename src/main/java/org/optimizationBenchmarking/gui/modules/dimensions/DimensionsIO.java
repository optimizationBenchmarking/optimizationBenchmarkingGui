package org.optimizationBenchmarking.gui.modules.dimensions;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.data.impl.DimensionDirectionParser;
import org.optimizationBenchmarking.experimentation.data.impl.DimensionTypeParser;
import org.optimizationBenchmarking.experimentation.data.impl.abstr.BasicDimensionSet;
import org.optimizationBenchmarking.experimentation.data.spec.EDimensionDirection;
import org.optimizationBenchmarking.experimentation.data.spec.EDimensionType;
import org.optimizationBenchmarking.experimentation.data.spec.IDimension;
import org.optimizationBenchmarking.experimentation.data.spec.IDimensionSet;
import org.optimizationBenchmarking.experimentation.io.impl.edi.EDIOutput;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.data.DimensionInput;
import org.optimizationBenchmarking.gui.data.DimensionsBuilder;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.gui.utils.editor.EditorModule;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.config.DefinitionElement;
import org.optimizationBenchmarking.utils.parsers.NumberParser;
import org.optimizationBenchmarking.utils.reflection.EPrimitiveType;
import org.optimizationBenchmarking.utils.reflection.PrimitiveTypeParser;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * This editor module allows us to generate a dynamic form for editing
 * dimension files.
 */
public final class DimensionsIO extends EditorModule<IDimensionSet> {
  /** the dimension id */
  static final String DIMENSION_SET = "dimension[]"; //$NON-NLS-1$

  /** the globally shared instance of the dimension i/o */
  public static final DimensionsIO INSTANCE = new DimensionsIO();

  /** the dimension's name */
  private static final String DIMENSION_NAME = "name";//$NON-NLS-1$
  /** the dimension's description */
  private static final String DIMENSION_DESC = "description";//$NON-NLS-1$
  /** the dimension's type */
  private static final String DIMENSION_TYPE = "dimension type";//$NON-NLS-1$
  /** the dimension's direction */
  private static final String DIMENSION_DIRECTION = "direction";//$NON-NLS-1$
  /** the dimension's data */
  private static final String DIMENSION_DATA_TYPE = "data type";//$NON-NLS-1$

  /** the lower bound of the dimension */
  private static final String DIMENSION_MIN = "lower bound";//$NON-NLS-1$
  /** the upper bound of the dimension */
  private static final String DIMENSION_MAX = "upper bound";//$NON-NLS-1$

  /** the dimension types */
  private static final ArrayListView<DefinitionElement> DIMENSION_TYPES;
  /** the dimension directions */
  private static final ArrayListView<DefinitionElement> DIMENSION_DIRECTIONS;
  /** the dimension data types */
  private static final ArrayListView<DefinitionElement> DIMENSION_DATA_TYPES;

  static {
    DefinitionElement[] de;
    int i;

    de = new DefinitionElement[EDimensionType.INSTANCES.size()];
    i = 0;

    de[i++] = new DefinitionElement(
        DimensionTypeParser.getHumanReadable(EDimensionType.ITERATION_FE),//
        "A machine-independent time measure in terms of objective function evaluations (FEs). An objective function evaluation corresponds to a fully created solution which was passed to the objective function. One FE may have different real time costs for different algorithms. In the Traveling Salesman Problem, for instance, some local search steps can be performed in O(1), crossovers in an Evolutionary Algorithm may take O(n), and a solution creation in an Ant Colony Optimization algorithm may take O(n\u0062) steps, where 'n' is the problem scale. Sometimes there may exist a way to measure runtime in an algorithm-independent fashion which is more precise, i.e., a sub-FE measure."); //$NON-NLS-1$

    de[i++] = new DefinitionElement(
        DimensionTypeParser.getHumanReadable(//
            EDimensionType.ITERATION_SUB_FE),//
        " A machine-independent time measure with a granularity below that of FEs. Such a time measure should be the fairest machine-independent measure to compare different algorithms."); //$NON-NLS-1$

    de[i++] = new DefinitionElement(
        DimensionTypeParser.getHumanReadable(//
            EDimensionType.ITERATION_ALGORITHM_STEP),//
        "A machine-independent time measurement in algorithm steps. Such measurements are only comparable amongst equivalent algorithm configurations. For instance, in a Genetic Algorithm, an algorithm step may be one generation. Generation counts are only comparable for the same population sizes and operators. Instead of counting generations, one would therefore rather measure runtime in terms of FEs."); //$NON-NLS-1$

    de[i++] = new DefinitionElement(
        DimensionTypeParser.getHumanReadable(//
            EDimensionType.RUNTIME_CPU),//
        "The dimension represents a time measurement based on CPU clock time. Such measurements are only comparable for measurements taken on the same computer, since they are machine dependent. The reason is that one machine M1 may be faster than another one (say, M2), it may perform many more algorithm steps within the same amount of seconds than the other. Thus, if an algorithm A1 executed M1 gets better results than algorithm A2 executed on M2 in the same time, we cannot make any assumption about which of them is better."); //$NON-NLS-1$

    de[i++] = new DefinitionElement(
        DimensionTypeParser.getHumanReadable(//
            EDimensionType.RUNTIME_NORMALIZED),//
        "In order to mitigate the machine dependency of CPU time measures, we can normalize them with a machine-dependent performance indicator. Such a performance indicator should be proportional (or inverse-proportional) to how fast a machine can execute algorithms (of a given family). Such a normalization procedure makes runtime time measurements based on real time comparable, even if they stem from different machines. However, we can never make real time measurements fully unbiased, as there may always be stuff such as scheduling anomalies, other processes running on the same computers, interferences by the Java virtual machines, influences of swapping and paging, hard drive access issues, etc., that may bias them."); //$NON-NLS-1$

    de[i++] = new DefinitionElement(
        DimensionTypeParser.getHumanReadable(//
            EDimensionType.QUALITY_PROBLEM_DEPENDENT),//
        "A problem-dependent quality measure, such as objective values. The values in this dimension can only be compared for the same problem."); //$NON-NLS-1$

    de[i++] = new DefinitionElement(
        DimensionTypeParser.getHumanReadable(//
            EDimensionType.QUALITY_PROBLEM_INDEPENDENT),//
        "A problem-independent quality measure, such as normalized objective values."); //$NON-NLS-1$

    DIMENSION_TYPES = new ArrayListView<>(de);

    de = new DefinitionElement[EDimensionDirection.INSTANCES.size()];
    i = 0;

    de[i++] = new DefinitionElement(
        DimensionDirectionParser.getHumanReadable(//
            EDimensionDirection.INCREASING),//
        "The values of this dimension are (non-strictly) increasing, i.e., (1, 2, 2, 3, 4) is a valid sequence of values, while (1, 2, 1, 3, 4) is not. Many runtime measures based on actually consumed CPU time behave like, since multiple things may happen within one (real) time unit due to the limited resolution of computer clocks."); //$NON-NLS-1$

    de[i++] = new DefinitionElement(
        DimensionDirectionParser.getHumanReadable(//
            EDimensionDirection.INCREASING_STRICTLY),//
        "The values of this dimension are strictly increasing, i.e., (1, 2, 3, 4, 5) is a valid sequence of values, while (1, 2, 2, 3, 4) is not. Many algorithm-step based runtime metrics, like countine FEs, behave like that."); //$NON-NLS-1$

    de[i++] = new DefinitionElement(
        DimensionDirectionParser.getHumanReadable(//
            EDimensionDirection.DECREASING),//
        "The values of this dimension are (non-strictly) decreasing, i.e., (5, 4, 4, 3, 1) is a valid sequence of values, while (5, 4, 5, 3, 1) is not. Quality dimensions like the 'best-so-far measured objective value' (updated only when improvements are discovered) behave like that."); //$NON-NLS-1$

    de[i++] = new DefinitionElement(
        DimensionDirectionParser.getHumanReadable(//
            EDimensionDirection.DECREASING_STRICTLY),//
        "The values of this dimension are strictly decreasing, i.e., (5, 4, 3, 2, 1) is a valid sequence of values, while (5, 4, 4, 3, 1) is not."); //$NON-NLS-1$

    DIMENSION_DIRECTIONS = new ArrayListView<>(de);

    de = new DefinitionElement[6];
    i = 0;

    de[i++] = new DefinitionElement(PrimitiveTypeParser.getHumanReadable(//
        EPrimitiveType.BYTE),//
        "A byte is a signed, 8-bit integer, with values ranging at most from " //$NON-NLS-1$
            + Byte.MIN_VALUE + " to " + Byte.MAX_VALUE + '.');//$NON-NLS-1$

    de[i++] = new DefinitionElement(PrimitiveTypeParser.getHumanReadable(//
        EPrimitiveType.SHORT),//
        "A short is a signed, 16-bit integer, with values ranging at most from " //$NON-NLS-1$
            + Short.MIN_VALUE + " to " + Short.MAX_VALUE + '.');//$NON-NLS-1$

    de[i++] = new DefinitionElement(PrimitiveTypeParser.getHumanReadable(//
        EPrimitiveType.INT),//
        "An int is a signed, 32-bit integer, with values ranging at most from " //$NON-NLS-1$
            + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE + '.');//$NON-NLS-1$

    de[i++] = new DefinitionElement(PrimitiveTypeParser.getHumanReadable(//
        EPrimitiveType.LONG),//
        "A long is a signed, 64-bit integer, with values ranging at most from " //$NON-NLS-1$
            + Long.MIN_VALUE + " to " + Long.MAX_VALUE + '.');//$NON-NLS-1$

    de[i++] = new DefinitionElement(
        PrimitiveTypeParser.getHumanReadable(//
            EPrimitiveType.FLOAT),//
        "A float is a 32-bit floating point number with the smallest positve representable value " //$NON-NLS-1$
            + Float.MIN_VALUE//
            + " and the largest finite positive representable value " + //$NON-NLS-1$
            Float.MAX_VALUE + '.');

    de[i++] = new DefinitionElement(
        PrimitiveTypeParser.getHumanReadable(//
            EPrimitiveType.DOUBLE),//
        "A double is a 64-bit floating point number with the smallest positve representable value " //$NON-NLS-1$
            + Double.MIN_VALUE//
            + " and the largest finite positive representable value " + //$NON-NLS-1$
            Double.MAX_VALUE + '.');

    DIMENSION_DATA_TYPES = new ArrayListView<>(de);
  }

  /** the forbidden constructor */
  private DimensionsIO() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  protected final IDimensionSet createEmpty(final Handle handle) {
    return new BasicDimensionSet(null,
        ((ArrayListView) (ArraySetView.EMPTY_SET_VIEW)));
  }

  /** {@inheritDoc} */
  @Override
  protected final IDimensionSet loadFile(final Path file,
      final Handle handle) throws IOException {
    final DimensionsBuilder builder;
    builder = new DimensionsBuilder();
    DimensionInput.getInstance().use().setLogger(handle).addPath(file)
        .setDestination(builder).create().call();
    return builder.getDimensionSet();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void formPutEditorFields(final String prefix,
      final IDimensionSet data, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    String dimPrefix, dimTitle, dimName;
    String id;
    EPrimitiveType type;
    double dbound;
    long lbound;
    boolean enabled;
    NumberParser<?> parser;

    out = page.getOut();
    encoded = page.getHTMLEncoded();
    for (final IDimension dim : data.getData()) {
      dimPrefix = Page.fieldNameFromPrefixAndName(prefix,//
          page.newPrefix());

      dimName = dim.getName();
      dimTitle = ("Dimension " + dimName);//$NON-NLS-1$

      this.formPutComponentHead(dimName, null, dimPrefix, true, true,
          true, page);

      out.write("<input type=\"hidden\" name=\""); //$NON-NLS-1$
      out.write(DimensionsIO.DIMENSION_SET);
      out.write("\" value=\"");//$NON-NLS-1$
      out.write(dimPrefix);
      out.write("\"/>");//$NON-NLS-1$

      this.formTableBegin(page);

      id = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_NAME);
      this.formTableFieldRowBegin(id, DimensionsIO.DIMENSION_NAME, false,
          page);
      this.formPutString(id, dimName, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append(//
          "The (short, mathematical) name of the dimension, as it will appear in formulas in the report and can be used as dimension in the evaluation definitions."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);

      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_DESC);
      this.formTableFieldRowBegin(id, DimensionsIO.DIMENSION_DESC, false,
          page);
      this.formPutText(id, dim.getDescription(), page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append("A description of the dimension."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);

      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_TYPE);
      this.formTableFieldRowBegin(id, DimensionsIO.DIMENSION_TYPE, false,
          page);
      this.formPutSelection(id,//
          DimensionTypeParser.getHumanReadable(dim.getDimensionType()),//
          DimensionsIO.DIMENSION_TYPES, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append(//
          "The semantical type of the dimension, i.e., whether it represents a time or solution quality measure."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, true, page);

      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_DIRECTION);
      this.formTableFieldRowBegin(id, DimensionsIO.DIMENSION_DIRECTION,
          false, page);
      this.formPutSelection(id,//
          DimensionDirectionParser.getHumanReadable(dim.getDirection()),//
          DimensionsIO.DIMENSION_DIRECTIONS, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append(//
          "The direction of this dimension, i.e., if values are increasing or decreasing as the measured process is progressing."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, true, page);

      type = dim.getDataType();
      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_DATA_TYPE);
      this.formTableFieldRowBegin(id, DimensionsIO.DIMENSION_DATA_TYPE,
          false, page);
      this.formPutSelection(id,//
          PrimitiveTypeParser.getHumanReadable(type),//
          DimensionsIO.DIMENSION_DATA_TYPES, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append(//
          "The data type to be used to store the dimension's values."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, true, page);

      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_MIN);
      this.formTableFieldRowBegin(id, DimensionsIO.DIMENSION_MIN, true,
          page);
      parser = dim.getParser();
      switch (type) {
        case FLOAT:
        case DOUBLE: {
          dbound = parser.getLowerBoundDouble();
          enabled = ((dbound > Double.NEGATIVE_INFINITY) && (dbound == dbound));
          this.formPutFloat(id, enabled ? Double.valueOf(dbound) : null,//
              Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, page);
          break;
        }
        default: {
          lbound = parser.getLowerBoundLong();
          enabled = (lbound > Long.MIN_VALUE);
          this.formPutInteger(id, //
              enabled ? Long.valueOf(lbound) : null,//
              Long.MIN_VALUE, Long.MAX_VALUE, page);
        }
      }
      this.formTableFieldRowEndDescRowBegin(id, true, enabled, page);
      encoded.append("The lower bound of this dimension values."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);

      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_MAX);
      this.formTableFieldRowBegin(id, DimensionsIO.DIMENSION_MAX, true,
          page);
      parser = dim.getParser();
      switch (type) {
        case FLOAT:
        case DOUBLE: {
          dbound = parser.getUpperBoundDouble();
          enabled = ((dbound < Double.POSITIVE_INFINITY) && (dbound == dbound));
          this.formPutFloat(id, enabled ? Double.valueOf(dbound) : null,//
              Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, page);
          break;
        }
        default: {
          lbound = parser.getUpperBoundLong();
          enabled = (lbound < Long.MAX_VALUE);
          this.formPutInteger(id, //
              enabled ? Long.valueOf(lbound) : null,//
              Long.MIN_VALUE, Long.MAX_VALUE, page);
        }
      }
      this.formTableFieldRowEndDescRowBegin(id, true, enabled, page);
      encoded.append("The upper bound of this dimension values."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);

      this.formTableEnd(page);

      this.formPutComponentFoot(dimTitle, dimPrefix, true, true, page);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final IDimensionSet loadFromRequest(final String prefix,
      final HttpServletRequest request, final Handle handle) {
    // TODO Auto-generated method stub
    return null;
  }

  /** {@inheritDoc} */
  @Override
  protected final void storeToFile(final IDimensionSet data,
      final Path file, final Handle handle) throws IOException {
    EDIOutput.getInstance().use().setLogger(handle).setPath(file)
        .setSource(data).create().call();
  }

  /** {@inheritDoc} */
  @Override
  protected final String getComponentTypeName() {
    return "dimension";//$NON-NLS-1$
  }

}
