dependencies {
    // Bundled + relocated into the plugin jar
    implementation(project(":API"))
    implementation("fr.maxlego08.sarah:sarah:1.22")
    implementation("com.tcoded:FoliaLib:0.5.1")

    // Provided at runtime (soft/required plugins, server API)
    compileOnly("com.github.Maxlego08:zTranslator:1.0.0.0")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("fr.maxlego08.shop:zshop-api:3.3.1")
    compileOnly("fr.maxlego08.essentials:zessentials-api:1.0.2.9")
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:2025.1")
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        archiveBaseName.set("zSpawner")
        archiveClassifier.set("")
        destinationDirectory.set(rootProject.file("target"))

        relocate("fr.maxlego08.sarah", "fr.maxlego08.spawner.sarah")
        relocate("com.tcoded.folialib", "fr.maxlego08.spawner.lib.folialib")
    }

    build {
        dependsOn(shadowJar)
    }
}
