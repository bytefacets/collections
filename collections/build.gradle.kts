plugins {
    java
    id("com.bytefacets.template_processor") version "0.6.0"
}

template_processor {
    main {
        excludedFiles.set(listOf(
            "BoolIndexedCollection.java",
            "BaseBoolHeap.java",
            "BaseBoolIndex.java",
            "BoolIndexedSet.java"))
    }
    test {
        excludedFiles.set(listOf("BoolIndexedSetTest.java"))
    }
}