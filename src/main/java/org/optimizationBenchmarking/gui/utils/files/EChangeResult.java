package org.optimizationBenchmarking.gui.utils.files;

/** The result of a change */
public enum EChangeResult {

  /** nothing was done, since no {@link FSElement} could be created */
  ELEMENT_NOT_FOUND,

  /** an {@link FSElement} was created, but the collection did not change */
  NOTHING_CHANGED,

  /** an {@link FSElement} was created, and the collection changed */
  CHANGED;
}
