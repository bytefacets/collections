<#ftl strip_whitespace=true>
package com.bytefacets.collections;

import com.bytefacets.collections.functional.${type.name}Iterable;
import com.bytefacets.collections.functional.${type.name}Consumer;

public interface ${type.name}KeyedCollection${generics} extends ${type.name}Iterable${generics} {
    void forEachEntry(com.bytefacets.collections.functional.IntConsumer consumer);

    void forEach(${type.name}Consumer${generics} consumer);

    int[] collectEntries(int[] target);

    ${type.javaType}[] collectKeys(${type.javaType}[] target);

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
