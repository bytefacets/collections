/**
 * Provides in-memory store capabilities.
 *
 * <ul>
 *   <li>*Store: a simple interface abstraction over an array for each given type
 *   <li>*MatrixStore: a simple interface abstraction over a multidimensional array for each given
 *       type, useful for storing multiple "fields" of the same type at each index.
 *   <li>*ChunkStore: an implementation of a Store which is backed by possibly multiple arrays to
 *       accommodate fast growing
 *   <li>*ChunkMatrixStore: an implementation of a MatrixStore which is backed by possibly multiple
 *       arrays to accommodate fast growing
 * </ul>
 *
 * All classes in the package are currently generated from templates.
 */
package com.bytefacets.collections.store;
