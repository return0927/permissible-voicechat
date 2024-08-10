import kr.entree.spigradle.data.Load

plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("kr.entree.spigradle") version "2.4.3"
}

group = "kr.enak.akuru"
version = "1.0"

val voicechat_api_version = "2.5.0"
repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://maven.maxhenkel.de/repository/public")
    flatDir {
        dirs("libs")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("de.maxhenkel.voicechat:voicechat-bukkit:2.5.20")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

spigot {
    apiVersion = "1.18"
    depends = listOf("voicechat")
    load = Load.POST_WORLD
    main = "$group.permissiblevoicechat.PermissibleVoiceChat"
}

val targetJavaVersion = 17
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

listOf(
    tasks.named("prepareSpigot"),
    tasks.named("downloadPaper"),
).forEach { task ->
    task {
        onlyIf {
            !projectDir.resolve("debug/spigot/server.jar").exists()
        }
    }
}

tasks.whenTaskAdded {
    if (name == "kaptTestKotlin") {
        dependsOn("generateSpigotDescription")
    }
}

tasks.named("prepareSpigotPlugins") {
    dependsOn("shadowJar")
}