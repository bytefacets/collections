<#ftl strip_whitespace=true>
package com.bytefacets.collections.functional;

import java.util.Objects;

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
