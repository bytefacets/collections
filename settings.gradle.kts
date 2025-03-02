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
                    username = System.getenv("USERNAME") ?: providers.gradleProperty("gpr.user").orElse("no-username").get()
                    password = "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"
//                    password = System.getenv("TOKEN") ?: providers.gradleProperty("gpr.key").orElse("no-token").get()
                    System.out.println("GitHubPackages.u=" + username)
                    System.out.println("GitHubPackages.p=" + password)
                }
            }
        }
    }
}

include("collections")
include("examples")
