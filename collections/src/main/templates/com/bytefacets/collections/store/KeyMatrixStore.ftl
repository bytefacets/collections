<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.store;

/** A store that allows for multiple fields of the same type at each row. */
public interface ${type.name}MatrixStore${generics} {
    /** Returns the value at the given row and field. */
    ${type.javaType} get${type.name}(int row, int field);

    /** Sets the value at the given row and field. */
    void set${type.name}(int row, int field, ${type.javaType} value);
}