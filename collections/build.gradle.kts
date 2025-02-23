plugins {
    java
    id("com.bytefacets.template_processor") version "0.0.3"
}

template_processor {
    main {
        excludedFiles.set(listOf(
            "BoolKeyedCollection.java",
            "BaseBoolHeap.java",
            "BaseBoolIndex.java",
            "BoolIndexedSet.java"))
    }
    test {
        excludedFiles.set(listOf("BoolIndexedSetTest.java"))
    }
}