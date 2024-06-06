import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    java
    idea
    eclipse
    id("com.github.ben-manes.versions")
    id("com.diffplug.spotless")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

sourceSets {
    create("functionalTest") {
        java {
            compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            srcDir("src/functional-test/java")
        }
    }
}

idea {
    module {
        testSources.from(sourceSets["functionalTest"].java.srcDirs)
    }
}

val functionalTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val functionalTestRuntimeOnly: Configuration by configurations.getting

configurations {
    configurations["functionalTestImplementation"].extendsFrom(configurations.testImplementation.get())
    configurations["functionalTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())
}


val functionalTest = task<Test>("functionalTest") {
    description = "Runs functional tests."
    group = "verification"

    testClassesDirs = sourceSets["functionalTest"].output.classesDirs
    classpath = sourceSets["functionalTest"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    testLogging {
        events ("failed", "passed", "skipped", "standard_out")
    }
}

dependencies {
    testImplementation("org.assertj:assertj-core:3.15+")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    testLogging {
        events ("failed", "passed", "skipped", "standard_out")
    }
}

tasks.check { dependsOn(functionalTest) }

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
    gradleReleaseChannel="current"
}

spotless {
    java {
        palantirJavaFormat()
        formatAnnotations()
    }
}
