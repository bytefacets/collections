<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.functional;

/** Functional interface for components of type ${type.javaType} */
public interface ${type.name}Consumer${generics} {
    /** Receive a ${type.javaType}. */
    void accept(${type.javaType} value);
}
