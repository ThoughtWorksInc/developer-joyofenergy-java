rootProject.name = "developer-joyofenergy-java"

pluginManagement {
    val versions_version: String by settings
    val spotless_version: String by settings
    plugins {
        id("com.github.ben-manes.versions") version versions_version
        id("com.diffplug.spotless") version spotless_version
    }
}
