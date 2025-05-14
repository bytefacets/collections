rootProject.name = "collections"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/bytefacets/type-template-processor")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

include("collections")
include("examples")
