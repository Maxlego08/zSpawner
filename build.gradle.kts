plugins {
    `java-library`
    id("com.gradleup.shadow") version "8.3.6"
}

allprojects {
    group = "fr.maxlego08.spawner"
    version = rootProject.version

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://libraries.minecraft.net/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.groupez.dev/releases")
        // SuperiorSkyblock
        maven("https://repo.bg-software.com/repository/api/")
        // FoliaLib
        maven("https://repo.tcoded.com/releases")
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
        compileOnly("fr.maxlego08.menu:zmenu-api:1.1.1.0")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}
