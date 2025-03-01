rootProject.name = "com.bytefacets"

pluginManagement {
    repositories {
        mavenLocal()
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/bytefacets/type-template-processor")
                credentials {
                    username = System.getenv("USERNAME")
                    password = System.getenv("TOKEN")
                }
            }
        }
        gradlePluginPortal()
    }
}

include("collections")
include("examples")
