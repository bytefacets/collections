plugins {
    `java-library`
    id("com.bytefacets.template_processor") version "0.11.0"
}

apply(plugin = "com.bytefacets.template_processor")

template_processor {
    main {
        excludedFiles.set(listOf(
            "BoolIndexedCollection.java",
            "BaseBoolHeap.java",
            "BaseBoolIndex.java",
            "BoolIndexedSet.java",
            "LargeStringStore.java",
            "LargeGenericStore.java",
            "LargeStringMatrixStore.java",
            "LargeGenericMatrixStore.java",
            "LargeStringChunkMatrixStore.java",
            "LargeGenericChunkMatrixStore.java",
            "LargeStringChunkStore.java",
            "LargeGenericChunkStore.java"))
    }
    test {
        excludedFiles.set(listOf(
            "BoolIndexedSetTest.java",
            "LargeStringChunkMatrixStoreTest.java",
            "LargeGenericChunkMatrixStoreTest.java",
            "LargeStringChunkStoreTest.java",
            "LargeGenericChunkStoreTest.java"))
    }
}