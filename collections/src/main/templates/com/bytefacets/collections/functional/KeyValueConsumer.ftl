<#ftl strip_whitespace=true>
package com.bytefacets.collections.functional;

public interface ${key.name}${value.name}Consumer${generics} {
    void accept(${key.javaType} a, ${value.javaType} b);
}
