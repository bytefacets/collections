package com.bytefacets.collections;

public interface EntryIterator {
    void reset();

    int currentEntry();

    void remove();

    boolean next();
}
