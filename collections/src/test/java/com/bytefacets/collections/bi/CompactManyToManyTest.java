package com.bytefacets.collections.bi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class CompactManyToManyTest {
    final CompactManyToMany collection = new CompactManyToMany(5, 5, 5, true);

    @Test
    void shouldGrowMappings() {
        IntStream.range(0, 100).forEach(right -> collection.put(1, right));
        assertThat(collection.size(), equalTo(100));
    }

    @Test
    void shouldGrowForLefts() {
        IntStream.range(0, 100).forEach(left -> collection.put(left, 0));
        assertThat(collection.size(), equalTo(100));
    }

    @Test
    void shouldGrowForRights() {
        IntStream.range(0, 100).forEach(right -> collection.put(0, right));
        assertThat(collection.size(), equalTo(100));
    }

    @Test
    void shouldIterateLeftUsingForEach() {
        IntStream.range(0, 5).forEach(right -> collection.put(2, right));
        final List<Integer> rights = new ArrayList<>();
        collection.withLeft(2).forEachValue(rights::add);
        assertThat(rights, containsInAnyOrder(0, 1, 2, 3, 4));
    }

    @Test
    void shouldIterateRightUsingForEach() {
        IntStream.range(0, 5).forEach(left -> collection.put(left, 2));
        final List<Integer> lefts = new ArrayList<>();
        collection.withRight(2).forEachValue(lefts::add);
        assertThat(lefts, containsInAnyOrder(0, 1, 2, 3, 4));
    }

    @Test
    void shouldIterateLeftUsingIterator() {
        IntStream.range(0, 5).forEach(right -> collection.put(2, right));
        final List<Integer> rights = new ArrayList<>();
        final var iterator = collection.withLeft(2).valueIterator();
        while (iterator.hasNext()) {
            rights.add(iterator.next());
        }
        assertThat(rights, containsInAnyOrder(0, 1, 2, 3, 4));
    }

    @Test
    void shouldIterateRightUsingIterator() {
        IntStream.range(0, 5).forEach(left -> collection.put(left, 2));
        final List<Integer> lefts = new ArrayList<>();
        final var iterator = collection.withRight(2).valueIterator();
        while (iterator.hasNext()) {
            lefts.add(iterator.next());
        }
        assertThat(lefts, containsInAnyOrder(0, 1, 2, 3, 4));
    }

    @Test
    void shouldRemoveMapping() {
        IntStream.range(0, 5).forEach(right -> collection.put(2, right));
        final Set<Integer> remaining = new HashSet<>(Set.of(0, 1, 2, 3, 4));
        IntStream.of(3, 4, 0, 2, 1)
                .forEach(
                        right -> {
                            collection.remove(2, right);
                            remaining.remove(right);

                            final var iterator = collection.withLeft(2).valueIterator();
                            final List<Integer> rights = new ArrayList<>();
                            iterator.forEach(rights::add);
                            assertThat(rights, containsInAnyOrder(remaining.toArray()));
                        });
    }

    @Test
    void shouldReturnNegativeOneWhenUnmappingUnknown() {
        assertThat(collection.remove(28, 26), equalTo(-1));
    }
}
