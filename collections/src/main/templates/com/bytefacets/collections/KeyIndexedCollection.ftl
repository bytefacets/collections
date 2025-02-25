<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections;

import com.bytefacets.collections.functional.${type.name}Iterable;
import com.bytefacets.collections.functional.${type.name}Consumer;

public interface ${type.name}IndexedCollection${generics} extends ${type.name}Iterable${generics} {
    /** Consume each entry in the collection. */
    void forEachEntry(com.bytefacets.collections.functional.IntConsumer consumer);

    /** Consume each ${type.javaType} key in the collection. */
    void forEach(${type.name}Consumer${generics} consumer);

    int lookupEntry(${type.javaType} key);

    ${type.javaType} getKeyAt(int entry);

    boolean containsKey(${type.javaType} key);

    int remove(${type.javaType} key);

    void removeAt(int entry);

    int add(${type.javaType} key);

    int size();

    boolean isEmpty();

    void clear();
}
