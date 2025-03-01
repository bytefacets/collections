rootProject.name = "com.bytefacets"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/bytefacets/type-template-processor")
                credentials {
                    username = "bytefacets"
                    password = "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"
                }
            }
        }
    }
}

include("collections")
include("examples")
