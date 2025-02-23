<#ftl strip_whitespace=true>
package com.bytefacets.collections.store;

public interface ${type.name}MatrixStore<#if type.isGeneric()><T></#if> {
<#if type.isGeneric()>
    T getGeneric(int row, int field);

    void setGeneric(int row, int field, T value);
<#else>
    ${type.javaType} get${type.name}(int row, int field);

    void set${type.name}(int row, int field, ${type.javaType} value);
</#if>
}