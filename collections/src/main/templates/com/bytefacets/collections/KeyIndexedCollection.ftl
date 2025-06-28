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

    /** Returns the stable entry for the key or -1 if the key is not in the collection. */
    int lookupEntry(${type.javaType} key);

    /**
     * Returns the key at the given entry.
     *
     * @throws IndexOutOfBoundsException if the entry is outside the bounds of the underlying array
     */
    ${type.javaType} getKeyAt(int entry);

    /** Whether the key exists in the collection. */
    boolean containsKey(${type.javaType} key);

    /**
     * Removes the key from the collection, returning the entry that was freed, or -1 if the key
     * was not found.
     */
    int remove(${type.javaType} key);

    /**
     * Removes the key at the given entry.
     *
     * @throws IndexOutOfBoundsException if the entry is outside the bounds of the underlying array
     */
    void removeAt(int entry);

    /**
     * Adds the key to the collection if it's not yet in the collection and returns the
     * stable entry assigned to the key.
     */
    int add(${type.javaType} key);

    /** The size of the collection. */
    int size();

    /** Whether the size of the collection is zero. */
    boolean isEmpty();

    /** Clears the contents of the collection. */
    void clear();
}
