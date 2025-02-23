package com.bytefacets.collections.hash;

/**
 * This is a generated class. Changes to the class should be made on the template and the generator re-run.
 */
public class ${type.name}IndexedSet${generics} extends Base${type.name}Index${generics} {
    public ${type.name}IndexedSet(int initialCapacity) {
        super(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public ${type.name}IndexedSet(int initialCapacity, double loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public void copyFrom(${type.name}IndexedSet${generics} sourceSet) {
        super.copyFrom(sourceSet);
    }

    @Override
    protected void removeValue(int entry) {
        // nothing to do in a set
    }

    @Override
    protected void growValues(int size) {
        // nothing to do in a set
    }

    @Override
    protected void clearValues() {
        // nothing to do in a set
    }
}