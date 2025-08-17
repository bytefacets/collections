<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.hash;

import com.bytefacets.collections.types.${key.name}Type;
<#if key != value>import com.bytefacets.collections.types.${value.name}Type;</#if>

import com.bytefacets.collections.EntryIterator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

<#if key.generic || value.generic>@SuppressWarnings("unchecked")</#if>
class ${key.name}${value.name}IndexedMapTest {
    private ${key.name}${value.name}IndexedMap${instanceGenerics} map = new ${key.name}${value.name}IndexedMap${instanceGenerics}(16);
    private Map<Object, Object> expected = new HashMap<>();

    @AfterEach
    void doValidation() {
        validate();
    }

    @Test
    void shouldUseInitializeToDefaultValueWhenNewKey() {
        map.putValueAt(0, createValue(1));
        map.add(createKey(2));
        assertEquals(${value.name}Type.DEFAULT, map.getValueAt(0));
        map.clear(); // avoid the default validation in tearDown bc we're using null
    }

    @Test
    void shouldUseCurrentValueWhenAddingExistingKey() {
        add(createKey(2), createValue(1));
        map.add(createKey(2));
        assertEquals(createValue(1), map.getValueAt(0));
    }

    @Test
    void shouldAllocateEntriesInOrder() {
        assertEquals(0, add(createKey(30), createValue(4)));
        assertEquals(1, add(createKey(31), createValue(5)));
        assertEquals(2, add(createKey(32), createValue(6)));
    }

    @Test
    void shouldReturnEntryWhenRemoving() {
        IntStream.of(30, 31, 32).forEach(key -> add(createKey(key), createValue(key*2)));
        assertEquals(1, remove(createKey(31)));
        assertEquals(2, remove(createKey(32)));
    }

    @Test
    void shouldUseFreeList() {
        IntStream.range(30, 40).forEach(key -> add(createKey(key), createValue(key*2)));
        final int free35 = remove(createKey(35));
        final int free37 = remove(createKey(37));
        assertEquals(free37, add(createKey(90), createValue(1)));
        assertEquals(free35, add(createKey(91), createValue(2)));
        assertEquals(10, add(createKey(92), createValue(3)));
    }

    @Test
    void shouldGrow() {
        IntStream.range(0, 64).forEach(key -> add(createKey(key), createValue(key*2)));
    }

    @Test
    void shouldNotUseReservedEntries() {
        IntStream.range(30, 34).forEach(key -> add(createKey(key), createValue(key*2)));

        removeAndReserve(2);
        removeAndReserve(1);

        assertEquals(4, add(createKey(45), createValue(55)));
        assertEquals(5, add(createKey(46), createValue(56)));

        map.freeReservedEntry(2);
        map.freeReservedEntry(1);

        assertEquals(1, add(createKey(47), createValue(57)));
        assertEquals(2, add(createKey(48), createValue(58)));
    }

    @Test
    void shouldCopyFromLargerSource() {
        IntStream.range(0, 64).forEach(key -> add(createKey(key), createValue(key*2)));

        // create a small one
        final ${key.name}${value.name}IndexedMap copy = new ${key.name}${value.name}IndexedMap(2);
        copy.copyFrom(map);
        map.clear();
        map = copy; // swap before validation
    }

    @Test
    void shouldCopyFromSmallerSource() {
        IntStream.range(0, 8).forEach(key -> add(createKey(key), createValue(key*2)));

        // create a small one
        final ${key.name}${value.name}IndexedMap copy = new ${key.name}${value.name}IndexedMap(256);
        copy.copyFrom(map);
        map.clear();
        map = copy; // swap before validation
    }

    // TODO: copy should ensure to wipe all contents

    @Test
    void shouldClear() {
        IntStream.range(0, 64).forEach(key -> add(createKey(key), createValue(key*2)));

        map.clear();
        expected.clear();
    }

    private void removeAndReserve(int entry) {
        expected.remove(map.getKeyAt(entry));
        map.removeAtAndReserve(entry);
    }

    private int add(${key.arrayType} key, ${value.arrayType} value) {
        expected.put(key, value);
        return map.put(key, value);
    }

    private int remove(${key.arrayType} key) {
        expected.remove(key);
        return map.remove(key);
    }

    private void validate() {
        Map<Object, Object> copy = new HashMap<>(expected);
        assertEquals(copy.size(), map.size());
        map.forEachEntry((e) -> {
            final var key = map.getKeyAt(e);
            final var val = map.getValueAt(e);
            assertEquals(e, map.lookupEntry(key));
            assertEquals(val, copy.remove(key));
        });
        assertEquals(0, copy.size());

        Map<Object, Object> copy2 = new HashMap<>(expected);
        EntryIterator it = map.iterator();
        while(it.next()) {
            final var key = map.getKeyAt(it.currentEntry());
            final var val = map.getValueAt(it.currentEntry());
            assertEquals(val, copy2.remove(key));
        }
        assertEquals(0, copy2.size());

        Map<Object, Object> copy3 = new HashMap<>(expected);
        List<Object> result = new ArrayList<>(copy3.size());
        map.forEach(result::add);
        for(int i = 0, len = map.size(); i < len; i++) {
            assertTrue(copy3.remove(result.get(i)) != null);
        }
        assertEquals(0, copy3.size());
    }

    private ${key.arrayType} createKey(final int keyAsInt) {
        return ${key.name}Type.castTo${key.name}(keyAsInt);
    }

    private ${value.arrayType} createValue(final int valueAsInt) {
        return ${value.name}Type.castTo${value.name}(valueAsInt);
    }
}
