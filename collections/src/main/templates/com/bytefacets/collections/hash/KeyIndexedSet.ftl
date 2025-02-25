<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.hash;

/**
 * An indexed set of ${type.javaType}
 */
public class ${type.name}IndexedSet${generics} extends Base${type.name}Index${generics} {
    public ${type.name}IndexedSet(int initialCapacity) {
        super(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public ${type.name}IndexedSet(int initialCapacity, double loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /** Copies the contents from the given source set into this set. */
    public void copyFrom(${type.name}IndexedSet${generics} sourceSet) {
        super.copyFrom(sourceSet);
    }

    @Override
    protected void removeValue(int entry) {
        // nothing to do in a set
    }

    @Override
    protected void growValues(int size) {
        // nothing to do in a set
    }

    @Override
    protected void clearValues() {
        // nothing to do in a set
    }
}