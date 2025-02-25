<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.functional;

/** Supplier of ${type.javaType}. */
public interface ${type.name}Supplier${generics} {
    ${type.javaType} get();
}
