<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.queue;

import com.bytefacets.collections.types.${type.name}Type;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ${type.name}HydraDequeTest {
    private final ${type.name}HydraDeque${instanceGenerics} queue = new ${type.name}HydraDeque${instanceGenerics}(2, 3);
    private final Map<Integer, ArrayDeque<Object>> expected = new HashMap<>();

    @Nested
    class AddFirstTests {
        @Test
        void shouldGrowValues() {
            addFirst(new int[] {5}, 1, 30);
            validateViaForwardIterator(5);
        }
        @Test
        void shouldGrowLists() {
            addFirst(new int[] {100}, 1, 30);
            validateViaForwardIterator(100);
        }
        @Test
        void shouldBuildLists() {
            addFirst(new int[] {1}, 1, 4);
            validateViaForwardIterator(1);
            validateViaReverseIterator(1);
        }
        @Test
        void shouldTrackSize() {
            assertThat(queue.isEmpty(), equalTo(true));
            assertThat(queue.size(), equalTo(0));
            queue.addFirst(1, v(5));
            assertThat(queue.size(1), equalTo(1));
            assertThat(queue.size(), equalTo(1));
            queue.addFirst(1, v(5));
            assertThat(queue.size(1), equalTo(2));
            assertThat(queue.size(), equalTo(2));
            queue.addFirst(2, v(6));
            assertThat(queue.size(2), equalTo(1));
            assertThat(queue.size(1), equalTo(2));
            assertThat(queue.size(), equalTo(3));
        }
    }

    @Nested
    class AddLastTests {
        @Test
        void shouldGrowValues() {
            addLast(new int[] {5}, 1, 30);
            validateViaForwardIterator(5);
        }
        @Test
        void shouldGrowLists() {
            addLast(new int[] {100}, 1, 30);
            validateViaForwardIterator(100);
        }
        @Test
        void shouldBuildLists() {
            addLast(new int[] {1}, 1, 4);
            validateViaForwardIterator(1);
            validateViaReverseIterator(1);
        }
        @Test
        void shouldTrackSize() {
            assertThat(queue.isEmpty(), equalTo(true));
            assertThat(queue.size(), equalTo(0));
            queue.addLast(1, v(5));
            assertThat(queue.size(1), equalTo(1));
            assertThat(queue.size(), equalTo(1));
            queue.addLast(1, v(5));
            assertThat(queue.size(1), equalTo(2));
            assertThat(queue.size(), equalTo(2));
            queue.addLast(2, v(6));
            assertThat(queue.size(2), equalTo(1));
            assertThat(queue.size(1), equalTo(2));
            assertThat(queue.size(), equalTo(3));
        }
    }

    @Nested
    class RemoveFirstTests {
        @Test
        void shouldMaintainList() {
            addFirst(new int[] {1,2,3}, 1, 8);
            IntStream.of(1, 2, 3).forEach(${type.name}HydraDequeTest.this::validateRemoveFirst);
        }
        @Test
        void shouldTrackSize() {
            addFirst(new int[] {1,2,3}, 1, 8);
            assertThat(queue.size(), equalTo(21));
            IntStream.range(1,4).forEach(list -> {
                assertThat(queue.size(list), equalTo(7));
            });
            queue.removeFirst(2);
            assertThat(queue.size(2), equalTo(6));
            assertThat(queue.size(), equalTo(20));
        }
    }

    @Nested
    class RemoveLastTests {
        @Test
        void shouldMaintainList() {
            addFirst(new int[] {1,2,3}, 1, 8);
            IntStream.of(1, 2, 3).forEach(${type.name}HydraDequeTest.this::validateRemoveLast);
        }
        @Test
        void shouldTrackSize() {
            addFirst(new int[] {1,2,3}, 1, 8);
            assertThat(queue.size(), equalTo(21));
            IntStream.range(1,4).forEach(list -> {
                assertThat(queue.size(list), equalTo(7));
            });
            queue.removeLast(2);
            assertThat(queue.size(2), equalTo(6));
            assertThat(queue.size(), equalTo(20));
        }
    }

    @Test
    void shouldUseFreeList() {
       final int[] entries = new int[4];
       for(int i = 0; i < 4; i++) {
          entries[i] = queue.addLast(1, v(3 * i));
       }
       IntStream.range(0, 4).forEach(i -> queue.removeFirst(1));
       for(int i = 3; i >=0; i--) {
          assertThat(queue.addLast(1, v(4 * i)), equalTo(entries[i]));
       }
    }

    private void validateViaForwardIterator(final int listId) {
        final ArrayDeque<Object> expectedList = expected.get(listId);
        assertThat(queue.size(listId), equalTo(expectedList.size()));
        final Iterator<Object> expectedIterator = expectedList.iterator();
        final var iterator = queue.iterator(listId);
        while(expectedIterator.hasNext()) {
            assertThat(iterator.next(), equalTo(expectedIterator.next()));
            assertThat(iterator.hasNext(), equalTo(expectedIterator.hasNext()));
        }
    }

    private void validateViaReverseIterator(final int listId) {
        final ArrayDeque<Object> expectedList = expected.get(listId);
        assertThat(queue.size(listId), equalTo(expectedList.size()));
        final Iterator<Object> expectedIterator = expectedList.descendingIterator();
        final var iterator = queue.reverseIterator(listId);
        while(expectedIterator.hasNext()) {
            assertThat(iterator.next(), equalTo(expectedIterator.next()));
            assertThat(iterator.hasNext(), equalTo(expectedIterator.hasNext()));
        }
    }

    private void validateRemoveFirst(final int listId) {
        final ArrayDeque<Object> expectedList = expected.get(listId);
        while(!expectedList.isEmpty()) {
            assertThat(queue.removeFirst(listId), equalTo(expectedList.removeFirst()));
            validateViaForwardIterator(listId);
            validateViaReverseIterator(listId);
        }
    }

    private void validateRemoveLast(final int listId) {
        final ArrayDeque<Object> expectedList = expected.get(listId);
        while(!expectedList.isEmpty()) {
            assertThat(queue.removeLast(listId), equalTo(expectedList.removeLast()));
            validateViaForwardIterator(listId);
            validateViaReverseIterator(listId);
        }
    }

    private void addFirst(final int[] listIds, final int start, final int end) {
        IntStream.range(start, end).forEach(val -> {
            for(int listId : listIds) {
                queue.addFirst(listId, v(listId * val));
                expected.computeIfAbsent(listId, k -> new ArrayDeque<Object>()).addFirst(v(listId * val));
            }
        });
    }

    private void addLast(final int[] listIds, final int start, final int end) {
        IntStream.range(start, end).forEach(val -> {
            for(int listId : listIds) {
                queue.addLast(listId, v(listId * val));
                expected.computeIfAbsent(listId, k -> new ArrayDeque<Object>()).addLast(v(listId * val));
            }
        });
    }

    private static ${type.arrayType} v(final int valAsInt) {
        return ${type.name}Type.castTo${type.name}(valAsInt);
    }

}
