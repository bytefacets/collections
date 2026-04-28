plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val jvmTargetVer = JavaLanguageVersion.of(17)

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        // free compiler args
        freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
    }
}

dependencies {
    implementation("io.github.gradle-nexus:publish-plugin:1.3.0")
    implementation("com.tddworks.central-portal-publisher:com.tddworks.central-portal-publisher.gradle.plugin:0.0.5")
    implementation("com.gradle.publish:plugin-publish-plugin:1.2.1")
}