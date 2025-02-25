<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.hash;

import com.bytefacets.collections.EntryIterator;
import com.bytefacets.collections.types.${type.name}Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

<#if type.generic>@SuppressWarnings("unchecked")</#if>
class ${type.name}IndexedSetTest {
    private ${type.name}IndexedSet${instanceGenerics} set = new ${type.name}IndexedSet${instanceGenerics}(16);
    private Set<Object> expected = new HashSet<>();

    @AfterEach
    void doValidation() {
        validate();
    }

    @Test
    void shouldAllocateEntriesInOrder() {
        assertEquals(0, add(createKey(30)));
        assertEquals(1, add(createKey(31)));
        assertEquals(2, add(createKey(32)));
    }

    @Test
    void shouldReturnEntryWhenRemoving() {
        IntStream.of(30, 31, 32).forEach(key -> add(createKey(key)));
        assertEquals(1, remove(createKey(31)));
        assertEquals(2, remove(createKey(32)));
    }

    @Test
    void shouldUseFreeList() {
        IntStream.range(30, 40).forEach(key -> add(createKey(key)));
        final int free35 = remove(createKey(35));
        final int free37 = remove(createKey(37));
        assertEquals(free37, add(createKey(90)));
        assertEquals(free35, add(createKey(91)));
        assertEquals(10, add(createKey(92)));
    }

    @Test
    void shouldGrow() {
        IntStream.range(0, 64).forEach(key -> add(createKey(key)));
    }

    @Test
    void shouldNotUseReservedEntries() {
        IntStream.range(30, 34).forEach(key -> add(createKey(key)));

        removeAndReserve(2);
        removeAndReserve(1);

        assertEquals(4, add(createKey(45)));
        assertEquals(5, add(createKey(46)));

        set.freeReservedEntry(2);
        set.freeReservedEntry(1);

        assertEquals(1, add(createKey(47)));
        assertEquals(2, add(createKey(48)));
    }

    @Test
    void shouldCopyFromLargerSource() {
        IntStream.range(0, 64).forEach(key -> add(createKey(key)));

        // create a small one
        final ${type.name}IndexedSet copy = new ${type.name}IndexedSet(2);
        copy.copyFrom(set);
        set.clear();
        set = copy; // swap before validation
    }

    @Test
    void shouldCopyFromSmallerSource() {
        IntStream.range(0, 8).forEach(key -> add(createKey(key)));

        // create a small one
        final ${type.name}IndexedSet copy = new ${type.name}IndexedSet(256);
        copy.copyFrom(set);
        set.clear();
        set = copy; // swap before validation
    }

    // TODO: copy should ensure to wipe all contents

    @Test
    void shouldClear() {
        IntStream.range(0, 64).forEach(key -> add(createKey(key)));

        set.clear();
        expected.clear();
    }

    private void removeAndReserve(int entry) {
        expected.remove(set.getKeyAt(entry));
        set.removeAtAndReserve(entry);
    }

    private int add(${type.arrayType} value) {
        expected.add(value);
        return set.add(value);
    }

    private int remove(${type.arrayType} value) {
        expected.remove(value);
        return set.remove(value);
    }

    private void validate() {
        Set<Object> copy = new HashSet<>(expected);
        assertEquals(copy.size(), set.size());
        set.forEachEntry((e) -> {
            assertEquals(e, set.lookupEntry(set.getKeyAt(e)));
            assertTrue(copy.remove(set.getKeyAt(e)));
        });
        assertEquals(0, copy.size());

        Set<Object> copy2 = new HashSet<>(expected);
        EntryIterator it = set.iterator();
        while(it.next()) {
            assertTrue(copy2.remove(set.getKeyAt(it.currentEntry())));
        }
        assertEquals(0, copy2.size());

        Set<Object> copy3 = new HashSet<>(expected);
        List<Object> result = new ArrayList<>(copy3.size());
        set.forEach(result::add);
        for(int i = 0, len = set.size(); i < len; i++) {
            assertTrue(copy3.remove(result.get(i)));
        }
        assertEquals(0, copy3.size());
    }

    private ${type.arrayType} createKey(final int keyAsInt) {
        return ${type.name}Type.castTo${type.name}(keyAsInt);
    }
}
