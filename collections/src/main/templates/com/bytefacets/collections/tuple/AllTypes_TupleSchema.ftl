<#ftl strip_whitespace=true>
package com.bytefacets.collections.tuple;

import com.bytefacets.collections.hash.StringIntIndexedMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TupleSchema {
    private final StringIntIndexedMap nameIndex = new StringIntIndexedMap(16);
<#list types as type>
    final int ${type.name?lower_case}Count;
</#list>
    private TupleSchema(
<#list types as type>final int ${type.name?lower_case}Count<#sep>, </#sep>
</#list>) {
<#list types as type>
    this.${type.name?lower_case}Count = ${type.name?lower_case}Count;
</#list>
}

    int typeIndex(final String name) {
        return nameIndex.get(name);
    }

    public int fields() {
        return nameIndex.size();
    }

    public static TupleSchemaBuilder builder() {
        return new TupleSchemaBuilder();
    }

    public static class TupleSchemaBuilder {
        private final StringIntIndexedMap nameIndex = new StringIntIndexedMap(16);
<#list types as type>
        private final List<String> ${type.name?lower_case}Fields = new ArrayList<>(4);
</#list>
        private TupleSchemaBuilder() {

        }

        public TupleSchema build() {
            final var schema = new TupleSchema(<#list types as type>${type.name?lower_case}Fields.size()<#sep>, </#sep>
                                              </#list>);
            schema.nameIndex.copyFrom(nameIndex);
            return schema;
        }

<#list types as type>
        public TupleSchemaBuilder ${type.name?lower_case}Field(final String name) {
            final int before = nameIndex.size();
            final int entry = nameIndex.add(name);
            if(before == nameIndex.size()) {
                throw new RuntimeException("Duplicate field name: " + name);
            }
            nameIndex.putValueAt(entry, ${type.name?lower_case}Fields.size());
            ${type.name?lower_case}Fields.add(name);
            return this;
        }
</#list>
    }
}