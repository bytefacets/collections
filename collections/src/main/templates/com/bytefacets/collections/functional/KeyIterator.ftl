<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.functional;

import java.util.Objects;

/** Interface for iterating a collection of ${type.javaType}. */
public interface ${type.name}Iterator${generics} extends ${type.name}Iterable${generics}{
    boolean hasNext();

    ${type.javaType} next();

    default void forEach(final ${type.name}Consumer${generics} action) {
        Objects.requireNonNull(action, "action");
        while(hasNext()) {
           action.accept(next());
        }
    }
}
