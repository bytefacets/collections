plugins {
    `java-library`
    id("com.bytefacets.template_processor") version "0.10.0"
}

apply(plugin = "com.bytefacets.template_processor")

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