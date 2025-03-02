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
                    username = settings.extra["gpr.user"] as String? ?: System.getenv("USERNAME")
                    password = settings.extra["gpr.key"] as String? ?: System.getenv("TOKEN")
                }
            }
        }
    }
}

include("collections")
include("examples")
