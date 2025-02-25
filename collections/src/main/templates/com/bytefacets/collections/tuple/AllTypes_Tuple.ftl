<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.tuple;

import java.util.Arrays;
import java.util.Objects;

public final class Tuple {
    private final TupleSchema schema;
<#list types as type>
    private final ${type.arrayType}[] ${type.name?lower_case}Values;
</#list>

    Tuple(final TupleSchema schema) {
        this.schema = Objects.requireNonNull(schema, "schema");
<#list types as type>
        ${type.name?lower_case}Values = new ${type.arrayType}[schema.${type.name?lower_case}Count];
</#list>
    }

<#list types as type>
    public ${type.arrayType} get${type.name}(final String name) {
        final int typeIndex = schema.typeIndex(name);
        return ${type.name?lower_case}Values[typeIndex];
    }

    public void set${type.name}(final String name, final ${type.arrayType} value) {
        final int typeIndex = schema.typeIndex(name);
        ${type.name?lower_case}Values[typeIndex] = value;
    }

</#list>

    @Override
    public boolean equals(final Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        final Tuple tuple = (Tuple) o;
        return <#list types as type>Objects.deepEquals(${type.name?lower_case}Values, tuple.${type.name?lower_case}Values)<#sep> && </#sep></#list>;
    }

    @Override
    public int hashCode() {
        return Objects.hash(<#list types as type>Arrays.hashCode(${type.name?lower_case}Values)<#sep>, </#sep></#list>);
    }

}