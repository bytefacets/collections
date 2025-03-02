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
                    username = providers.gradleProperty("gpr.user")
                        .orElse(System.getenv("USERNAME") ?: "none")
                        .get()
                    password = providers.gradleProperty("gpr.key")
                        .orElse(System.getenv("TOKEN") ?: "none")
                        .get()
                }
            }
        }
    }
}

include("collections")
include("examples")
