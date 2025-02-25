<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.store;

public interface ${type.name}Store<#if type.generic><T></#if> {
<#if type.generic>
    T getGeneric(int index);

    void setGeneric(int index, T value);
<#else>
    ${type.javaType} get${type.name}(int index);

    void set${type.name}(int index, ${type.javaType} value);
</#if>
}