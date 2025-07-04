<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.queue;

import com.bytefacets.collections.NumUtils;
import com.bytefacets.collections.arrays.${type.name}Array;
import com.bytefacets.collections.functional.${type.name}Consumer;
import com.bytefacets.collections.functional.${type.name}Iterable;
import com.bytefacets.collections.types.${type.name}Type;

import java.util.NoSuchElementException;

import static com.bytefacets.collections.exception.ResizeException.cannotResize;
import static com.bytefacets.collections.CapacityCalculator.calculateNewCapacity;

/**
 * A deque for ${type.javaType} values.
 */
<#if type.generic>@SuppressWarnings("unchecked")</#if>
public final class ${type.name}Deque${generics} implements ${type.name}Iterable${generics} {
    private ${type.arrayType}[] values;
    private int head = 0;
    private int tail = 0;
    private int size = 0;

    public ${type.name}Deque(final int initialCapacity) {
        if(initialCapacity < 1) {
            throw new IllegalArgumentException("Invalid initial capacity: " + initialCapacity);
        }
        values = ${type.name}Array.create(NumUtils.nextPowerOf2(initialCapacity));
    }

    /** Iterates the contents of the deque from the head to the tail. */
    @Override
    public void forEach(final ${type.name}Consumer${generics} consumer) {
        int pos = head;
        while(pos != tail) {
            consumer.accept(${type.cast}values[pos]);
            pos = inc(pos, values.length);
        }
    }

    /** Adds the value to the front of the deque. */
    public void addFirst(final ${type.javaType} value) {
        values[head = dec(head, values.length)] = value;
        size++;
        if(head == tail) {
            grow();
        }
    }

    /** Adds the value to the back of the deque. */
    public void addLast(final ${type.javaType} value) {
        values[tail] = value;
        size++;
        if(head == (tail = inc(tail, values.length))) {
            grow();
        }
    }

    /** Current size of the deque. */
    public int size() {
        return size;
    }

    /**
     * Removes a value from the head of the deque.
     *
     * @throws NoSuchElementException if the deque is empty
     */
    public ${type.javaType} removeFirst() {
        if(size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }
        final ${type.javaType} value = ${type.cast}values[head];
        values[head] = ${type.name}Type.DEFAULT;;
        head = inc(head, values.length);
        size--;
        return value;
    }

    /**
     * Removes a value from the tail of the deque.
     *
     * @throws NoSuchElementException if the deque is empty
     */
    public ${type.javaType} removeLast() {
        if(size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }
        tail = dec(tail, values.length);
        final ${type.javaType} value = ${type.cast}values[tail];
        values[tail] = ${type.name}Type.DEFAULT;;
        size--;
        return value;
    }

    /**
     * Returns the head of the deque without removing it.
     *
     * @throws NoSuchElementException if the deque is empty
     */
    public ${type.javaType} peekFirst() {
        if(size == 0) {
             throw new NoSuchElementException("Deque is empty");
        }
        return ${type.cast}values[head];
    }

    /**
     * Returns the tail of the deque without removing it.
     *
     * @throws NoSuchElementException if the deque is empty
     */
    public ${type.javaType} peekLast() {
        if(size == 0) {
             throw new NoSuchElementException("Deque is empty");
        }
        return ${type.cast}values[dec(tail, values.length)];
    }

    /** Whether the deque has anything in it. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Clears the contents of the deque. */
    public void clear() {
        <#if type.name == "Generic" || type.name == "String">
        if(head > tail) {
            final int numValuesAtEnd = values.length - head;
            ${type.name}Array.fill(values, null, head, numValuesAtEnd);
            ${type.name}Array.fill(values, null, 0, tail);
        } else {
            ${type.name}Array.fill(values, null, head, tail - head);
        }
        </#if>
        head = tail = 0;
        size = 0;
    }

    /**
     * Circularly decrements i, mod modulus.
     * Precondition and postcondition: 0 <= i < modulus.
     */
    private static int dec(int i, int modulus) {
        if(--i < 0) i = modulus - 1;
        return i;
    }

    private static int inc(int i, int modulus) {
        if(++i >= modulus) i = 0;
        return i;
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private void grow() {
        final int newSize = calculateNewCapacity(this, values.length, Integer.MAX_VALUE);
        if(newSize == values.length) {
            throw cannotResize(getClass(), values.getClass().getName(), values.length, newSize);
        }
        final ${type.arrayType}[] newValues = ${type.name}Array.create(newSize);

        // rebuild the queue taking the end of the queue and putting it at the beginning
        final int numValuesAtEnd = values.length - tail;
        final int numValuesAtBeg = tail;
        System.arraycopy(values, tail, newValues, 0, numValuesAtEnd);

        // then taking the beginning of the old array, and put it after
        System.arraycopy(values, 0, newValues, numValuesAtEnd, numValuesAtBeg);
        head = 0; // pop from the beginning
        tail = size; // put after the number in the collection

        values = newValues;
    }

    // VisibleForTesting
    ${type.arrayType}[] array() {
        return values;
    }

    // VisibleForTesting
    boolean isReset() {
        return head == 0 && tail == 0;
    }
}