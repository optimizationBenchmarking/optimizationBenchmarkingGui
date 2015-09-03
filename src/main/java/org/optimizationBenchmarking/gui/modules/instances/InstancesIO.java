package org.optimizationBenchmarking.gui.modules.instances;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.optimizationBenchmarking.experimentation.data.impl.abstr.BasicInstanceSet;
import org.optimizationBenchmarking.experimentation.data.impl.partial.PartialExperimentSetBuilder;
import org.optimizationBenchmarking.experimentation.data.spec.IDimension;
import org.optimizationBenchmarking.experimentation.data.spec.IFeature;
import org.optimizationBenchmarking.experimentation.data.spec.IFeatureValue;
import org.optimizationBenchmarking.experimentation.data.spec.IInstance;
import org.optimizationBenchmarking.experimentation.data.spec.IInstanceSet;
import org.optimizationBenchmarking.experimentation.io.impl.edi.EDIOutput;
import org.optimizationBenchmarking.experimentation.io.impl.edi.FlatEDIInput;
import org.optimizationBenchmarking.gui.controller.Handle;
import org.optimizationBenchmarking.gui.utils.Loaded;
import org.optimizationBenchmarking.gui.utils.Page;
import org.optimizationBenchmarking.gui.utils.editor.EditorModule;
import org.optimizationBenchmarking.utils.collections.iterators.EnumerationIterator;
import org.optimizationBenchmarking.utils.collections.iterators.IterablePlusOne;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.parsers.LooseBooleanParser;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** A form which allows you to edit instance sets. */
public final class InstancesIO extends EditorModule<IInstanceSet> {
  /** the globally shared instance of the instance i/o */
  public static final InstancesIO INSTANCE = new InstancesIO();

  /** the instance id */
  static final String INSTANCE_SET = "instance[]"; //$NON-NLS-1$
  /** the instance's name */
  private static final String INSTANCE_NAME = "name";//$NON-NLS-1$
  /** the instance's description */
  private static final String INSTANCE_DESC = "description";//$NON-NLS-1$
  /** the dimension blueprint */
  private static final String BLUEPRINT_ID = "_blueprint_";//$NON-NLS-1$
  /** the feature prefix */
  private static final String PREFIX_FEATURE = "_fe_";//$NON-NLS-1$
  /** the lower bound prefix */
  private static final String PREFIX_LOWER = "_lo_";//$NON-NLS-1$
  /** the upper bound prefix */
  private static final String PREFIX_UPPER = "_up_";//$NON-NLS-1$
  /** the features prefix */
  private static final String PREFIX_FEATURE_DESC = "_fs_";//$NON-NLS-1$

  /** create the instances */
  private InstancesIO() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  protected final IInstanceSet createEmpty(final Handle handle) {
    return new BasicInstanceSet(null,//
        ((Collection) (ArraySetView.EMPTY_SET_VIEW)));
  }

  /** {@inheritDoc} */
  @Override
  protected final IInstanceSet loadFile(final Path file,
      final Handle handle) throws IOException {
    final PartialExperimentSetBuilder builder;
    builder = new PartialExperimentSetBuilder();
    FlatEDIInput.getInstance().use().setLogger(handle).addPath(file)
        .setDestination(builder).create().call();
    return builder.getInstanceSet();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final void formPutEditorFields(final String prefix,
      final IInstanceSet data, final Page page) throws IOException {
    final JspWriter out;
    final ITextOutput encoded;
    IFeature feature;
    String instId, instPrefix, title, instName, id, typePrefix, name, desc;
    Object fvalue;
    Number number;
    double d;
    boolean first;

    out = page.getOut();
    encoded = page.getHTMLEncoded();
    instId = Page.fieldNameFromPrefixAndName(prefix,
        InstancesIO.INSTANCE_SET);
    for (final IInstance inst : new IterablePlusOne<>(data.getData(), null)) {

      if (inst != null) {
        instPrefix = Page.fieldNameFromPrefixAndName(prefix,//
            page.newPrefix());
        instName = inst.getName();
      } else {
        instPrefix = Page.fieldNameFromPrefixAndName(prefix,
            InstancesIO.BLUEPRINT_ID);
        instName = "New Instance";//$NON-NLS-1$
      }
      title = ("Instance " + instName);//$NON-NLS-1$

      this.formPutComponentHead(title, null, prefix, instPrefix, true,
          true, true, (inst == null), (inst == null), page);

      out.write("<input type=\"hidden\" name=\""); //$NON-NLS-1$
      out.write(instId);
      out.write("\" value=\"");//$NON-NLS-1$
      out.write(instPrefix);
      out.write("\"/>");//$NON-NLS-1$

      this.formTableBegin(page);

      id = Page.fieldNameFromPrefixAndName(instPrefix,
          InstancesIO.INSTANCE_NAME);
      this.formTableFieldRowBegin(id, InstancesIO.INSTANCE_NAME, false,
          page);
      this.formPutString(id, instName, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append(//
          "The (short, mathematical) name of the instance, as it will appear in formulas in the report and can be used as instance in the evaluation definitions."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);

      this.formTableSpacer(page);
      id = Page.fieldNameFromPrefixAndName(instPrefix,
          InstancesIO.INSTANCE_DESC);
      this.formTableFieldRowBegin(id, InstancesIO.INSTANCE_DESC, false,
          page);
      this.formPutText(id, //
          ((inst != null) ? inst.getDescription()
              : "Enter description here."),//$NON-NLS-1$
          true, page);
      this.formTableFieldRowEndDescRowBegin(id, false, true, page);
      encoded.append("A description of the instance."); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);

      // features

      this.formTableSpacer(page);
      out.write(//
      "<tr class=\"invisible\"><th colspan=\"3\" class=\"invisible\"><hr/>Benchmark Instance Features</th></tr>");//$NON-NLS-1$
      typePrefix = Page.fieldNameFromPrefixAndName(instPrefix,
          InstancesIO.PREFIX_FEATURE);

      if (inst != null) {
        for (final IFeatureValue value : inst.getFeatureSetting()) {
          feature = value.getOwner();

          name = feature.getName();
          id = Page.fieldNameFromPrefixAndName(typePrefix, name);
          this.formTableFieldRowBegin(id, name, true, page);
          fvalue = value.getValue();
          this.formPutString(id,
              ((fvalue != null) ? String.valueOf(fvalue) : null), page);
          this.formTableFieldRowEndDescRowBegin(id, true, true, page);
          encoded.append(//
              "The value of feature '" + name + '\'' + '.'); //$NON-NLS-1$
          this.formTableDescRowEnd(id, false, page);
        }
      }

      this.formTableSpacer(page);
      this.formPutAddField(typePrefix, "add feature",//$NON-NLS-1$
          "feature", null, page); //$NON-NLS-1$

      // lower bounds

      this.formTableSpacer(page);
      out.write(//
      "<tr class=\"invisible\"><th colspan=\"3\" class=\"invisible\"><hr/>Lower Bounds of Dimension Values</th></tr>");//$NON-NLS-1$
      typePrefix = Page.fieldNameFromPrefixAndName(instPrefix,
          InstancesIO.PREFIX_LOWER);

      if (inst != null) {
        lbloop: for (final IDimension dim : data.getOwner()
            .getDimensions().getData()) {
          number = inst.getLowerBound(dim);

          if (number == null) {
            continue lbloop;
          }
          d = number.doubleValue();
          if ((d <= Double.NEGATIVE_INFINITY) || (d != d)) {
            continue lbloop;
          }

          name = dim.getName();
          id = Page.fieldNameFromPrefixAndName(typePrefix, name);
          this.formTableFieldRowBegin(id, "lb[" + name + ']', true, page); //$NON-NLS-1$
          this.formPutFloat(id, number, page);

          this.formTableFieldRowEndDescRowBegin(id, true, true, page);
          encoded.append(//
              "The lower bound of dimension '" + name + '\'' + '.'); //$NON-NLS-1$
          this.formTableDescRowEnd(id, false, page);
        }
      }

      this.formTableSpacer(page);
      this.formPutAddField(typePrefix, "add lower bound",//$NON-NLS-1$
          "dimension", "bound", page); //$NON-NLS-1$//$NON-NLS-2$

      // upper bounds

      this.formTableSpacer(page);
      out.write(//
      "<tr class=\"invisible\"><th colspan=\"3\" class=\"invisible\"><hr/>Upper Bounds of Dimension Values</th></tr>");//$NON-NLS-1$
      typePrefix = Page.fieldNameFromPrefixAndName(instPrefix,
          InstancesIO.PREFIX_UPPER);
      if (inst != null) {
        lbloop: for (final IDimension dim : data.getOwner()
            .getDimensions().getData()) {
          number = inst.getUpperBound(dim);

          if (number == null) {
            continue lbloop;
          }
          d = number.doubleValue();
          if ((d >= Double.POSITIVE_INFINITY) || (d != d)) {
            continue lbloop;
          }

          name = dim.getName();
          id = Page.fieldNameFromPrefixAndName(typePrefix, name);
          this.formTableFieldRowBegin(id, "ub[" + name + ']', true, page); //$NON-NLS-1$

          this.formPutFloat(id, number, page);
          this.formTableFieldRowEndDescRowBegin(id, true, true, page);

          encoded.append(//
              "The upper bound of dimension '" + name + '\'' + '.'); //$NON-NLS-1$
          this.formTableDescRowEnd(id, false, page);
        }
      }

      this.formTableSpacer(page);
      this.formPutAddField(typePrefix, "add upper bound",//$NON-NLS-1$
          "dimension", "bound", page); //$NON-NLS-1$//$NON-NLS-2$

      this.formTableEnd(page);

      this.formPutComponentFoot(title, instPrefix, true, true, page);
    }

    instPrefix = Page.fieldNameFromPrefixAndName(prefix,
        InstancesIO.PREFIX_FEATURE_DESC);

    title = "Feature Descriptions"; //$NON-NLS-1$
    this.formPutComponentHead(title, null, prefix, instPrefix, false,
        false, false, false, false, page);
    this.formTableBegin(page);

    first = true;
    for (final IFeature curFeature : data.getOwner().getFeatures()
        .getData()) {
      if (curFeature == null) {
        continue;
      }
      desc = curFeature.getDescription();
      if (desc == null) {
        continue;
      }

      if (first) {
        first = false;
      } else {
        this.formTableSpacer(page);
      }

      name = curFeature.getName();
      id = Page.fieldNameFromPrefixAndName(instPrefix, name);
      this.formTableFieldRowBegin(id, name, true, page);
      this.formPutText(id, desc, true, page);
      this.formTableFieldRowEndDescRowBegin(id, true, true, page);
      encoded.append(//
          "The description of curFeature '" + name + '\'' + '.'); //$NON-NLS-1$
      this.formTableDescRowEnd(id, false, page);
    }

    if (first) {
      first = false;
    } else {
      this.formTableSpacer(page);
    }

    this.formPutAddTextField(instPrefix, "add description", //$NON-NLS-1$
        "feature", "description", true, page);//$NON-NLS-1$//$NON-NLS-2$

    this.formTableEnd(page);
    this.formPutComponentFoot(title, instPrefix, true, true, page);

  }

  /** {@inheritDoc} */
  @Override
  protected final IInstanceSet loadFromRequest(final String prefix,
      final HttpServletRequest request, final Handle handle) {
    final PartialExperimentSetBuilder builder;
    final String[] strings;
    HashSet<String> done;
    String typePrefix, field, name, enabled, temp, value;

    builder = new PartialExperimentSetBuilder();

    // do the features
    done = new HashSet<>();
    typePrefix = Page.fieldNameFromPrefixAndName(prefix,
        InstancesIO.PREFIX_FEATURE_DESC);
    for (final String param : new EnumerationIterator<>(
        request.getParameterNames())) {
      if (!(done.contains(param))) {
        name = Page.nameFromPrefixAndFieldName(typePrefix, param);
        if (name != null) {
          temp = (param + EditorModule.BUTTON_ENABLE_SUFFIX);
          enabled = request.getParameter(temp);
          if (enabled != null) {
            if (done.add(temp) && done.add(param)) {
              if (LooseBooleanParser.INSTANCE.parseBoolean(enabled)) {
                value = request.getParameter(param);
                if (value != null) {
                  builder.featureDeclare(name, value);
                }
              }
            }
          }
        }
      }
    }

    // now add the instances

    field = Page.fieldNameFromPrefixAndName(prefix,
        InstancesIO.INSTANCE_SET);
    done.add(field);
    strings = request.getParameterValues(field);
    if (strings != null) {
      // do the instances
      for (final String dprefix : strings) {
        if (InstancesIO.BLUEPRINT_ID.equalsIgnoreCase(//
            Page.nameFromPrefixAndFieldName(prefix, dprefix))) {
          continue;
        }

        builder.instanceBegin(true);
        field = Page.fieldNameFromPrefixAndName(dprefix,
            InstancesIO.INSTANCE_NAME);
        done.add(field);
        builder.instanceSetName(request.getParameter(field));
        field = Page.fieldNameFromPrefixAndName(dprefix,
            InstancesIO.INSTANCE_DESC);
        done.add(field);
        builder.instanceSetDescription(request.getParameter(field));

        // features
        typePrefix = Page.fieldNameFromPrefixAndName(dprefix,
            InstancesIO.PREFIX_FEATURE);
        for (final Map.Entry<String, String[]> entry : request
            .getParameterMap().entrySet()) {
          field = entry.getKey();
          if (!(done.contains(field))) {
            name = Page.nameFromPrefixAndFieldName(typePrefix, field);
            if (name != null) {
              temp = (field + EditorModule.BUTTON_ENABLE_SUFFIX);
              enabled = request.getParameter(temp);
              if (enabled != null) {
                if (done.add(temp) && done.add(field)) {
                  if (LooseBooleanParser.INSTANCE.parseBoolean(enabled)) {
                    value = request.getParameter(field);
                    if (value != null) {
                      builder.instanceSetFeatureValue(name, value);
                    }
                  }
                }
              }
            }
          }
        }

        // lower bounds
        typePrefix = Page.fieldNameFromPrefixAndName(dprefix,
            InstancesIO.PREFIX_LOWER);
        for (final Map.Entry<String, String[]> entry : request
            .getParameterMap().entrySet()) {
          field = entry.getKey();
          if (!(done.contains(field))) {
            name = Page.nameFromPrefixAndFieldName(typePrefix, field);
            if (name != null) {
              temp = (field + EditorModule.BUTTON_ENABLE_SUFFIX);
              enabled = request.getParameter(temp);
              if (enabled != null) {
                if (done.add(temp) && done.add(field)) {
                  if (LooseBooleanParser.INSTANCE.parseBoolean(enabled)) {
                    value = request.getParameter(field);
                    if (value != null) {
                      builder.instanceSetLowerBound(name, value);
                    }
                  }
                }
              }
            }
          }
        }

        // upper bounds
        typePrefix = Page.fieldNameFromPrefixAndName(dprefix,
            InstancesIO.PREFIX_UPPER);
        for (final Map.Entry<String, String[]> entry : request
            .getParameterMap().entrySet()) {
          field = entry.getKey();
          if (!(done.contains(field))) {
            name = Page.nameFromPrefixAndFieldName(typePrefix, field);
            if (name != null) {
              temp = (field + EditorModule.BUTTON_ENABLE_SUFFIX);
              enabled = request.getParameter(temp);
              if (enabled != null) {
                if (done.add(temp) && done.add(field)) {
                  if (LooseBooleanParser.INSTANCE.parseBoolean(enabled)) {
                    value = request.getParameter(field);
                    if (value != null) {
                      builder.instanceSetUpperBound(name, value);
                    }
                  }
                }
              }
            }
          }
        }

        builder.instanceEnd();
      }
    }

    return builder.getInstanceSet();
  }

  /** {@inheritDoc} */
  @Override
  protected final void formPutButtons(final String prefix,
      final Loaded<IInstanceSet> data, final Page page) throws IOException {
    this.formPutCopyButton(prefix,
        Page.fieldNameFromPrefixAndName(prefix, InstancesIO.BLUEPRINT_ID),
        "add instance", //$NON-NLS-1$
        null, page);
    page.getOut().append("&nbsp;");//$NON-NLS-1$
    super.formPutButtons(prefix, data, page);
  }

  /** {@inheritDoc} */
  @Override
  protected final void storeToFile(final IInstanceSet data,
      final Path file, final Handle handle) throws IOException {
    EDIOutput.getInstance().use().setLogger(handle).setPath(file)
        .setSource(data).create().call();
  }
}
