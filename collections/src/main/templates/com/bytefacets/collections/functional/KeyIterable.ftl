<#ftl strip_whitespace=true>
package com.bytefacets.collections.functional;

public interface ${type.name}Iterable${generics} {
    void forEach(final ${type.name}Consumer${generics} action);
}
