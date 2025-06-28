<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.functional;

/** Functional interface for ${type.javaType}. */
public interface ${type.name}Iterable${generics} {
    void forEach(${type.name}Consumer${generics} action);
}
