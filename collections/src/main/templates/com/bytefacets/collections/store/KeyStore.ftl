<#ftl strip_whitespace=true>
package com.bytefacets.collections.store;

public interface ${type.name}Store<#if type.isGeneric()><T></#if> {
<#if type.isGeneric()>
    T getGeneric(int index);

    void setGeneric(int index, T value);
<#else>
    ${type.javaType} get${type.name}(int index);

    void set${type.name}(int index, ${type.javaType} value);
</#if>
}