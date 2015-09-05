package org.optimizationBenchmarking.gui.modules.dimensions;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.data.impl.DimensionDirectionParser;
import org.optimizationBenchmarking.experimentation.data.impl.DimensionTypeParser;
import org.optimizationBenchmarking.experimentation.data.impl.abstr.BasicDimensionSet;
import org.optimizationBenchmarking.experimentation.data.impl.partial.PartialExperimentSetBuilder;
import org.optimizationBenchmarking.experimentation.data.spec.EDimensionDirection;
import org.optimizationBenchmarking.experimentation.data.spec.EDimensionType;
import org.optimizationBenchmarking.experimentation.data.spec.IDimension;
import org.optimizationBenchmarking.experimentation.data.spec.IDimensionSet;
import org.optimizationBenchmarking.experimentation.io.impl.edi.EDIOutput;
import org.optimizationBenchmarking.experimentation.io.impl.edi.FlatEDIInput;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.Encoder;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.gui.utils.editor.EditorModule;
import org.optimizationBenchmarking.gui.utils.files.Loaded;
import org.optimizationBenchmarking.utils.collections.iterators.IterablePlusOne;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.config.DefinitionElement;
import org.optimizationBenchmarking.utils.parsers.AnyNumberParser;
import org.optimizationBenchmarking.utils.parsers.BoundedLooseByteParser;
import org.optimizationBenchmarking.utils.parsers.BoundedLooseDoubleParser;
import org.optimizationBenchmarking.utils.parsers.BoundedLooseFloatParser;
import org.optimizationBenchmarking.utils.parsers.BoundedLooseIntParser;
import org.optimizationBenchmarking.utils.parsers.BoundedLooseLongParser;
import org.optimizationBenchmarking.utils.parsers.BoundedLooseShortParser;
import org.optimizationBenchmarking.utils.parsers.LooseBooleanParser;
import org.optimizationBenchmarking.utils.parsers.LooseByteParser;
import org.optimizationBenchmarking.utils.parsers.LooseDoubleParser;
import org.optimizationBenchmarking.utils.parsers.LooseFloatParser;
import org.optimizationBenchmarking.utils.parsers.LooseIntParser;
import org.optimizationBenchmarking.utils.parsers.LooseLongParser;
import org.optimizationBenchmarking.utils.parsers.LooseShortParser;
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
  private static final String DIMENSION_TYPE_NAME = "dimension type";//$NON-NLS-1$
  /** the dimension's type */
  private static final String DIMENSION_TYPE_ID = "dimensionType";//$NON-NLS-1$
  /** the dimension's direction */
  private static final String DIMENSION_DIRECTION = "direction";//$NON-NLS-1$

  /** the dimension's data */
  private static final String DIMENSION_DATA_TYPE_NAME = "data type";//$NON-NLS-1$
  /** the dimension's data */
  private static final String DIMENSION_DATA_TYPE_ID = "dataType";//$NON-NLS-1$

  /** the lower bound of the dimension */
  private static final String DIMENSION_MIN_NAME = "lower bound";//$NON-NLS-1$
  /** the lower bound of the dimension */
  static final String DIMENSION_MIN_ID = "lowerBound";//$NON-NLS-1$
  /** the upper bound of the dimension */
  private static final String DIMENSION_MAX_NAME = "upper bound";//$NON-NLS-1$
  /** the upper bound of the dimension */
  static final String DIMENSION_MAX_ID = "upperBound";//$NON-NLS-1$

  /** the dimension blueprint */
  private static final String BLUEPRINT_ID = "_blueprint_";//$NON-NLS-1$

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
    final PartialExperimentSetBuilder builder;
    builder = new PartialExperimentSetBuilder();
    FlatEDIInput.getInstance().use().setLogger(handle).addPath(file)
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
    String dimId, dimPrefix, dimTitle, dimName, typeId, lowerId, upperId, id, funcParams;
    EPrimitiveType type;
    double dbound;
    long lbound;
    boolean enabled;
    NumberParser<?> parser;

    out = page.getOut();
    encoded = page.getHTMLEncoded();
    dimId = Page.fieldNameFromPrefixAndName(prefix,
        DimensionsIO.DIMENSION_SET);
    for (final IDimension dim : new IterablePlusOne<>(data.getData(), null)) {

      if (dim != null) {
        dimPrefix = Page.fieldNameFromPrefixAndName(prefix,//
            page.newPrefix());
        dimName = dim.getName();
      } else {
        dimPrefix = Page.fieldNameFromPrefixAndName(prefix,
            DimensionsIO.BLUEPRINT_ID);
        dimName = "New Dimension";//$NON-NLS-1$
      }
      dimTitle = ("Dimension " + dimName);//$NON-NLS-1$

      this.formPutComponentHead(dimTitle, null, prefix, dimPrefix, true,
          true, true, (dim == null), (dim == null), page);

      out.write("<input type=\"hidden\" name=\""); //$NON-NLS-1$
      out.write(dimId);
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
      this.formPutText(id, //
          ((dim != null) ? dim.getDescription()
              : "Enter description here."),//$NON-NLS-1$
          true, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append("A description of the dimension."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);

      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_TYPE_ID);
      this.formTableFieldRowBegin(id, DimensionsIO.DIMENSION_TYPE_NAME,
          false, page);
      this.formPutSelection(
          id,//
          DimensionTypeParser.getHumanReadable(//
              (dim != null) ? dim.getDimensionType()
                  : EDimensionType.ITERATION_FE),//
          DimensionsIO.DIMENSION_TYPES, null, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append(//
          "The semantical type of the dimension, i.e., whether it represents a time or solution quality measure."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, true, page);

      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_DIRECTION);
      this.formTableFieldRowBegin(id, DimensionsIO.DIMENSION_DIRECTION,
          false, page);
      this.formPutSelection(
          id,//
          DimensionDirectionParser.getHumanReadable(//
              (dim != null) ? dim.getDirection()
                  : EDimensionDirection.INCREASING),//
          DimensionsIO.DIMENSION_DIRECTIONS, null, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append(//
          "The direction of this dimension, i.e., if values are increasing or decreasing as the measured process is progressing."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, true, page);

      type = ((dim != null) ? dim.getDataType() : EPrimitiveType.LONG);
      this.formTableSpacer(page);
      typeId = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_DATA_TYPE_ID);
      lowerId = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_MIN_ID);
      upperId = Page.fieldNameFromPrefixAndName(dimPrefix,
          DimensionsIO.DIMENSION_MAX_ID);

      funcParams = ('(' + ('\'' + Encoder.htmlEncode(typeId)) + '\'' + ','
          + '\'' + Encoder.htmlEncode(lowerId) + '\'' + ',' + '\''
          + Encoder.htmlEncode(upperId) + '\'' + ')');

      this.formTableFieldRowBegin(typeId,
          DimensionsIO.DIMENSION_DATA_TYPE_NAME, false, page);
      this.formPutSelection(
          typeId,//
          PrimitiveTypeParser.getHumanReadable(type),//
          DimensionsIO.DIMENSION_DATA_TYPES,
          (page.getFunction(_TypeChangeFunctionRenderer.INSTANCE) + funcParams),
          page);

      out.write(" <input type=\"button\" onclick=\""); //$NON-NLS-1$
      out.write(page.getFunction(_AutoRefineFunctionRenderer.INSTANCE));
      out.write(funcParams);
      out.write("\" value=\"auto-refine\"/>");//$NON-NLS-1$
      this.formTableFieldRowEndDescRowBegin(typeId, false, true, page);
      out.write(//
      "The data type to be used to store the dimension's values. If your type is an integer, press <code>auto-refine</code> to pick the smallest integer data type fitting to the specified bounds."); //$NON-NLS-1$
      this.formTableDescRowEnd(typeId, true, page);

      this.formTableSpacer(page);
      this.formTableFieldRowBegin(lowerId,
          DimensionsIO.DIMENSION_MIN_NAME, true, page);
      parser = ((dim != null) ? dim.getParser() : LooseLongParser.INSTANCE);
      switch (type) {
        case FLOAT:
        case DOUBLE: {
          dbound = parser.getLowerBoundDouble();
          enabled = ((dbound > Double.NEGATIVE_INFINITY) && (dbound == dbound));
          this.formPutFloat(lowerId, enabled ? Double.valueOf(dbound)
              : null, page);
          break;
        }
        default: {
          lbound = parser.getLowerBoundLong();
          enabled = (lbound > Long.MIN_VALUE);
          this.formPutInteger(lowerId, //
              enabled ? Long.valueOf(lbound) : null, page);
        }
      }

      this.formTableFieldRowEndDescRowBegin(lowerId, true, enabled, page);
      encoded.append("The lower bound of this dimension values."); //$NON-NLS-1$
      this.formTableDescRowEnd(lowerId, false, page);

      this.formTableSpacer(page);
      this.formTableFieldRowBegin(upperId,
          DimensionsIO.DIMENSION_MAX_NAME, true, page);
      switch (type) {
        case FLOAT:
        case DOUBLE: {
          dbound = parser.getUpperBoundDouble();
          enabled = ((dbound < Double.POSITIVE_INFINITY) && (dbound == dbound));
          this.formPutFloat(upperId, enabled ? Double.valueOf(dbound)
              : null, page);
          break;
        }
        default: {
          lbound = parser.getUpperBoundLong();
          enabled = (lbound < Long.MAX_VALUE);
          this.formPutInteger(upperId, //
              enabled ? Long.valueOf(lbound) : null, page);
        }
      }
      this.formTableFieldRowEndDescRowBegin(upperId, true, enabled, page);
      encoded.append("The upper bound of this dimension values."); //$NON-NLS-1$
      this.formTableDescRowEnd(upperId, false, page);

      this.formTableEnd(page);

      this.formPutComponentFoot(dimTitle, dimPrefix, true, true, page);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final void formPutButtons(final String prefix,
      final Loaded<IDimensionSet> data, final Page page)
      throws IOException {
    this.formPutCopyButton(
        prefix,
        Page.fieldNameFromPrefixAndName(prefix, DimensionsIO.BLUEPRINT_ID),
        "add dimension", //$NON-NLS-1$
        null, page);
    page.getOut().append("&nbsp;");//$NON-NLS-1$
    super.formPutButtons(prefix, data, page);
  }

  /** {@inheritDoc} */
  @Override
  protected final IDimensionSet loadFromRequest(final String prefix,
      final HttpServletRequest request, final Handle handle) {
    final PartialExperimentSetBuilder builder;
    final String[] strings;
    String id, value;
    EPrimitiveType type;
    Number lower, upper;

    builder = new PartialExperimentSetBuilder();

    strings = request
        .getParameterValues(//
        Page.fieldNameFromPrefixAndName(prefix, DimensionsIO.DIMENSION_SET));

    if (strings != null) {
      for (final String dprefix : strings) {
        if (DimensionsIO.BLUEPRINT_ID.equalsIgnoreCase(//
            Page.nameFromPrefixAndFieldName(prefix, dprefix))) {
          continue;
        }

        builder.dimensionBegin(true);

        builder.dimensionSetName(request.getParameter(//
            Page.fieldNameFromPrefixAndName(dprefix,
                DimensionsIO.DIMENSION_NAME)));
        builder.dimensionSetDescription(request.getParameter(//
            Page.fieldNameFromPrefixAndName(dprefix,
                DimensionsIO.DIMENSION_DESC)));
        builder.dimensionSetType(//
            DimensionTypeParser.INSTANCE.parseString(request.getParameter(//
                Page.fieldNameFromPrefixAndName(dprefix,
                    DimensionsIO.DIMENSION_TYPE_ID))));
        builder.dimensionSetDirection(//
            DimensionDirectionParser.INSTANCE.parseString(request
                .getParameter(//
                Page.fieldNameFromPrefixAndName(dprefix,
                    DimensionsIO.DIMENSION_DIRECTION))));

        type = PrimitiveTypeParser.INSTANCE.parseString(request
            .getParameter(//
            Page.fieldNameFromPrefixAndName(dprefix,
                DimensionsIO.DIMENSION_DATA_TYPE_ID)));

        lower = upper = null;
        try {
          id = Page.fieldNameFromPrefixAndName(dprefix,
              DimensionsIO.DIMENSION_MIN_ID);
          value = request.getParameter(id
              + EditorModule.BUTTON_ENABLE_SUFFIX);
          if (value != null) {
            if (LooseBooleanParser.INSTANCE.parseBoolean(value)) {
              lower = AnyNumberParser.INSTANCE.parseString(//
                  request.getParameter(id));
            }
          }
        } catch (final Throwable error) {
          handle
              .failure(//
                  "Error while trying to parse the lower bound of the dimension.", //$NON-NLS-1$
                  error);
          return null;
        }

        try {
          id = Page.fieldNameFromPrefixAndName(dprefix,
              DimensionsIO.DIMENSION_MAX_ID);
          value = request.getParameter(id
              + EditorModule.BUTTON_ENABLE_SUFFIX);
          if (value != null) {
            if (LooseBooleanParser.INSTANCE.parseBoolean(value)) {
              upper = AnyNumberParser.INSTANCE.parseString(//
                  request.getParameter(id));
            }
          }
        } catch (final Throwable error) {
          handle
              .failure(//
                  "Error while trying to parse the upper bound of the dimension.", //$NON-NLS-1$
                  error);
          return null;
        }

        switch (type) {
          case BYTE: {
            builder.dimensionSetParser(//
                ((lower != null) || (upper != null)) ? //
                new BoundedLooseByteParser(//
                    ((lower != null) ? lower.byteValue() : Byte.MIN_VALUE),//
                    ((upper != null) ? upper.byteValue() : Byte.MAX_VALUE))//
                    : LooseByteParser.INSTANCE);
            break;
          }
          case SHORT: {
            builder.dimensionSetParser(//
                ((lower != null) || (upper != null)) ? //
                new BoundedLooseShortParser(//
                    ((lower != null) ? lower.shortValue()
                        : Short.MIN_VALUE),//
                    ((upper != null) ? upper.shortValue()
                        : Short.MAX_VALUE))//
                    : LooseShortParser.INSTANCE);
            break;
          }
          case INT: {
            builder.dimensionSetParser(//
                ((lower != null) || (upper != null)) ? //
                new BoundedLooseIntParser(//
                    ((lower != null) ? lower.intValue()
                        : Integer.MIN_VALUE),//
                    ((upper != null) ? upper.intValue()
                        : Integer.MAX_VALUE))//
                    : LooseIntParser.INSTANCE);
            break;
          }
          case LONG: {
            builder.dimensionSetParser(//
                ((lower != null) || (upper != null)) ? //
                new BoundedLooseLongParser(//
                    ((lower != null) ? lower.longValue() : Long.MIN_VALUE),//
                    ((upper != null) ? upper.longValue() : Long.MAX_VALUE))//
                    : LooseLongParser.INSTANCE);
            break;
          }
          case FLOAT: {
            builder.dimensionSetParser(//
                ((lower != null) || (upper != null)) ? //
                new BoundedLooseFloatParser(//
                    ((lower != null) ? lower.floatValue()
                        : Float.NEGATIVE_INFINITY),//
                    ((upper != null) ? upper.floatValue()
                        : Float.POSITIVE_INFINITY))//
                    : LooseFloatParser.INSTANCE);
            break;
          }
          case DOUBLE: {
            builder.dimensionSetParser(//
                ((lower != null) || (upper != null)) ? //
                new BoundedLooseDoubleParser(//
                    ((lower != null) ? lower.floatValue()
                        : Double.NEGATIVE_INFINITY),//
                    ((upper != null) ? upper.floatValue()
                        : Double.POSITIVE_INFINITY))//
                    : LooseDoubleParser.INSTANCE);
            break;
          }
          default: {
            handle.failure("Cannot understand primitive type " + type); //$NON-NLS-1$
            return null;
          }
        }

        builder.dimensionEnd();
      }
    }

    return builder.getDimensionSet();
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