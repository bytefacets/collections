<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.store;

public interface ${type.name}MatrixStore<#if type.generic><T></#if> {
<#if type.generic>
    T getGeneric(int row, int field);

    void setGeneric(int row, int field, T value);
<#else>
    ${type.javaType} get${type.name}(int row, int field);

    void set${type.name}(int row, int field, ${type.javaType} value);
</#if>
}