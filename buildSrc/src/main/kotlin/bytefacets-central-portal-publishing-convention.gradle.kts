import org.jreleaser.model.Active

// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
plugins {
    id("com.tddworks.central-portal-publisher")
    id("org.jreleaser")
}

jreleaser {
    deploy {
//        environment {
//            variables.set(file("$rootDir/jreleaser-config.yml"))
//        }
        signing {
            active.set(Active.ALWAYS)
            armored.set(true)
        }

        project {
            name.set("{{projectName}}")
            description.set("ByteFacets {{projectName}}")
            authors.set(listOf("ByteFacets"))
            license.set("MIT")
            copyright.set("Copyright © 2025 ByteFacets")
            links {
                homepage.set("https://github.com/bytefacets/" + rootProject.name)
                documentation.set("https://bytefacets.github.io/" + rootProject.name)
                license.set("https://opensource.org/licenses/MIT")
                bugTracker.set("https://github.com/bytefacets/" + rootProject.name + "/issues")
            }
        }
        deploy {
            maven {
                mavenCentral {
                    create("sonatype") {
                        active.set(Active.ALWAYS)
                        url.set("https://central.sonatype.com/api/v1/publisher")
                        stagingRepository("collections/build/sonatype/BuildMaven-java-bundle")
                    }
                }
            }
        }
    }
}

sonatypePortalPublisher {
    settings {
        autoPublish = false
        aggregation = true
    }
}
