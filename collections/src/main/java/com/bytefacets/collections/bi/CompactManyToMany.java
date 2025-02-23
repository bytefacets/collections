package com.bytefacets.collections.bi;

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

    public Mapping withRight(final int leftKey) {
        rightMapping.resetKey(leftKey);
        return rightMapping;
    }
}
