plugins {
    kotlin("jvm") version "1.8.21"
    id ("java")

    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.1.0" // Adds runServer and runMojangMappedServer tasks for testing

    //Shading & relocating into the plugin JAR
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "xyz.gameoholic"
version = "0.1"
description = "Lumber game."
val apiVersion = "1.20"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        url = uri("https://repo.gameoholic.xyz/releases")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT") //the paper dev bundle is a compile-only dependency, paper itself provides it. No need to shade

    compileOnly("xyz.gameoholic:partigon:1.3.4")
    compileOnly ("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    implementation ("org.spongepowered:configurate-hocon:4.0.0")
    implementation("net.objecthunter", "exp4j","0.4.8")
    implementation("fr.mrmicky", "fastboard" ,"2.0.2")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.34"))
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
}

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}


tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to apiVersion
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }



    shadowJar {
        // helper function to relocate a package into our package
        fun reloc(pkg: String) = relocate(pkg, "${project.group}.${project.name}.dependency.$pkg")

        reloc("fr.mrmicky.fastboard")
    }

}

