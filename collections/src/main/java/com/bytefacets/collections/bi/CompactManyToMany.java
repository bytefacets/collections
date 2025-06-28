// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.bi;

/**
 * A collection which can hold and access integer associations in n-n relationships. It is called
 * "Compact" because the integers should be 0-based as the integer values are used as array offsets
 * internally for performance and memory reasons. If your domain values are not compact, they can be
 * made compact by first performing a range transformation using an {@link
 * com.bytefacets.collections.hash.IntIndexedSet IntIndexedSet}.
 */
public final class CompactManyToMany extends CompactOneToMany {
    private final Side right;
    private final Mapping rightMapping;

    public CompactManyToMany() {
        this(16, 16, 16, true);
    }

    public CompactManyToMany(
            final int initialLefts,
            final int initialRights,
            final int initialMappingCapacity,
            final boolean includeCounts) {
        super(initialLefts, initialMappingCapacity, includeCounts);
        right = new Side(initialRights, initialMappingCapacity, includeCounts);
        rightMapping = new Mapping(right, this::getLeftAt);
    }

    @Override
    protected void insertRight(final int rightKey, final int entry) {
        right.insertEntry(rightKey, entry);
    }

    @Override
    protected void removeRight(final int rightKey, final int entry) {
        right.removeEntry(rightKey, entry);
    }

    /**
     * Fluent-style method returning an object to access associations for the given rightKey. The
     * Mapping that is returned is singular for interacting with rights in this collection. In other
     * words, the Mapping returned is reused for every call of withRight on this collection.
     */
    public Mapping withRight(final int rightKey) {
        rightMapping.resetKey(rightKey);
        return rightMapping;
    }
}
