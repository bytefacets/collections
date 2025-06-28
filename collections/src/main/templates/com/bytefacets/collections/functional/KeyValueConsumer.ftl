<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.functional;

/** Functional interface for iterating map-like collections of ${key.javaType}, ${value.javaType}.*/
public interface ${key.name}${value.name}Consumer${generics} {
    void accept(${key.javaType} a, ${value.javaType} b);
}
