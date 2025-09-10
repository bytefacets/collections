module com.bytefacets.collections {
    requires static com.github.spotbugs.annotations;
    requires java.sql;

    exports com.bytefacets.collections.arrays;
    exports com.bytefacets.collections.bi;
    exports com.bytefacets.collections.exception;
    exports com.bytefacets.collections.functional;
    exports com.bytefacets.collections.hash;
    exports com.bytefacets.collections.heap;
    exports com.bytefacets.collections.id;
    exports com.bytefacets.collections.queue;
    exports com.bytefacets.collections.store;
    exports com.bytefacets.collections.types;
    exports com.bytefacets.collections.vector;
    exports com.bytefacets.collections;
}
