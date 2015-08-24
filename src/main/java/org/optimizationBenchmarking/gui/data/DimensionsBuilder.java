package org.optimizationBenchmarking.gui.data;

import java.util.ArrayList;

import org.optimizationBenchmarking.experimentation.data.impl.abstr.AbstractDimension;
import org.optimizationBenchmarking.experimentation.data.impl.abstr.AbstractNamedElement;
import org.optimizationBenchmarking.experimentation.data.impl.abstr.BasicDimensionSet;
import org.optimizationBenchmarking.experimentation.data.impl.flat.AbstractFlatExperimentSetContext;
import org.optimizationBenchmarking.experimentation.data.spec.EDimensionDirection;
import org.optimizationBenchmarking.experimentation.data.spec.EDimensionType;
import org.optimizationBenchmarking.experimentation.data.spec.IDimensionSet;
import org.optimizationBenchmarking.utils.parsers.NumberParser;

/** A builder for dimensions. */
public final class DimensionsBuilder extends
    AbstractFlatExperimentSetContext {

  /** the list of dimensions */
  private ArrayList<_Dimension> m_list;

  /** do we need a new dimension? */
  private boolean m_needsNew;

  /** create */
  DimensionsBuilder() {
    super();
    this.m_list = new ArrayList<>();
    this.m_needsNew = true;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void dimensionBegin(final boolean forceNew) {
    this.__get(forceNew);
  }

  /**
   * get the dimension
   *
   * @param forceNew
   *          do we need a new one
   * @return the dimension
   */
  private final _Dimension __get(final boolean forceNew) {
    final _Dimension dim;
    final int size;

    size = this.m_list.size();
    if (forceNew || this.m_needsNew || (size <= 0)) {
      dim = new _Dimension(size);
      this.m_list.add(new _Dimension(size));
      this.m_needsNew = false;
      return dim;
    }
    return this.m_list.get(size - 1);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void dimensionEnd() {
    this.m_needsNew = true;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void dimensionSetName(final String name) {
    this.__get(false).m_name = //
    AbstractNamedElement.formatName(name);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void dimensionSetDescription(
      final String description) {
    this.__get(false).m_description = //
    AbstractNamedElement.formatDescription(description);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void dimensionAddDescription(
      final String description) {
    final _Dimension dim;

    dim = this.__get(false);
    dim.m_description = AbstractNamedElement.mergeDescriptions(
        dim.m_description, description);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void dimensionSetDirection(
      final EDimensionDirection direction) {
    AbstractDimension.validateDirection(direction);
    this.__get(false).m_dimensionDirection = direction;
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public synchronized final void dimensionSetParser(
      final NumberParser<?> parser) {
    final _Dimension dim;
    dim = this.__get(false);
    dim.m_dataType = AbstractDimension.validateParser(parser);
    dim.m_parser = ((NumberParser) parser);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void dimensionSetType(final EDimensionType type) {
    AbstractDimension.validateType(type);
    this.__get(false).m_dimensionType = type;
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public synchronized final IDimensionSet getDimensionSet() {
    final ArrayList<_Dimension> list;

    list = this.m_list;
    this.m_list = null;

    if (list == null) {
      throw new IllegalStateException("Dimension set already taken."); //$NON-NLS-1$
    }
    return new BasicDimensionSet(null, ((ArrayList) list));
  }
}
