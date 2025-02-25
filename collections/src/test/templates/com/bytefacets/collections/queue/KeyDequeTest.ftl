<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.queue;

import com.bytefacets.collections.types.${type.name}Type;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ${type.name}DequeTest {
    private final ${type.name}Deque${instanceGenerics} queue = new ${type.name}Deque${instanceGenerics}(3);
    private final ArrayDeque<Object> expected = new ArrayDeque<>();

    @Test
    void shouldGrow() {
        addLast(1, 3);
        drain(1);
        addLast(3, 13);
        validateRemoveFirst();
    }

    private void addLast(final int start, final int end) {
        IntStream.range(start, end).forEach(val -> {
            queue.addLast(v(val));
            expected.addLast(v(val));
        });
    }

    private void addFirst(final int start, final int end) {
        IntStream.range(start, end).forEach(val -> {
            queue.addFirst(v(val));
            expected.addFirst(v(val));
        });
    }

    private void drain(final int targetSize) {
        while(queue.size() > targetSize) {
            queue.removeFirst();
            expected.removeFirst();
        }
    }

    private void validateRemoveFirst() {
        assertThat(expected.size(), equalTo(queue.size()));
        while(!expected.isEmpty()) {
            assertThat(expected.removeFirst(), equalTo(queue.removeFirst()));
            assertThat(expected.size(), equalTo(queue.size()));
        }
    }

    private void validateRemoveLast() {
        assertThat(expected.size(), equalTo(queue.size()));
        while(!expected.isEmpty()) {
            assertThat(expected.removeLast(), equalTo(queue.removeLast()));
            assertThat(expected.size(), equalTo(queue.size()));
        }
    }

    private static ${type.arrayType} v(final int valAsInt) {
        return ${type.name}Type.castTo${type.name}(valAsInt);
    }
}