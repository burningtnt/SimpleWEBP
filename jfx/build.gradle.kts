import kotlin.streams.toList

buildscript {
    repositories { mavenCentral() }

    dependencies {
        classpath("org.glavo.kala:kala-platform:0.10.0")
    }
}

plugins {
    id("java-library")
    id("maven-publish")
    id("checkstyle")
}

group = "com.github.burningtnt"
version = "0.8.1"
description = "Minimal library for reading WEBP images"

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_9
    targetCompatibility = JavaVersion.VERSION_1_9
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://libraries.minecraft.net")
}

val javadocJar = tasks.create<Jar>("javadocJar") {
    group = "build"
    archiveClassifier.set("javadoc")
}

tasks.compileJava {
    options.release.set(8)

    sourceCompatibility = "8"
    targetCompatibility = "8"
}

tasks.test {
    useJUnitPlatform()

    jvmArgs("--illegal-access=deny")

    listOf(
            "javafx.graphics/com.sun.javafx.iio",
            "javafx.graphics/com.sun.javafx.iio.common"
    ).forEach { string ->
        jvmArgs("--add-exports", "${string}=ALL-UNNAMED")
    }
}

tasks.getByName("build") {
    dependsOn(tasks.getByName("checkstyleMain") {
        group = "build"
    })
    dependsOn(tasks.getByName("checkstyleTest") {
        group = "build"
    })
}

run {
    var classifer = when (kala.platform.Platform.CURRENT_PLATFORM.operatingSystem) {
        kala.platform.OperatingSystem.LINUX -> "linux"
        kala.platform.OperatingSystem.WINDOWS -> "win"
        kala.platform.OperatingSystem.MACOS -> "mac"
        else -> return@run
    }

    when (kala.platform.Platform.CURRENT_PLATFORM.architecture) {
        kala.platform.Architecture.X86_64 -> {}
        kala.platform.Architecture.X86 -> classifer += "-x86"
        kala.platform.Architecture.AARCH64 -> classifer += "-aarch64"
        kala.platform.Architecture.ARM -> if (classifer == "linux") classifer =
                "linux-arm32-monocle" else return@run

        else -> return@run
    }

    val modules = listOf("base", "graphics")

    dependencies {
        for (module in modules) {
            compileOnly("org.openjfx:javafx-$module:19.0.2.1:$classifer")
            testImplementation("org.openjfx:javafx-$module:19.0.2.1:$classifer")
        }
    }
}

dependencies {
    api(rootProject)
    compileOnly("com.github.burningtnt:BytecodeImplGenerator:975a0fcfde5abfa407787fa816376de9e3e23fec")
    testImplementation(project)

    testImplementation("org.apache.commons:commons-imaging:1.0-alpha3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.glavo:simple-png:0.3.0")
}

tasks.getByName<JavaCompile>("compileJava") {
    val bytecodeClasses = listOf(
            "net/burningtnt/webp/jfx/WEBPImageLoader",
            "net/burningtnt/webp/jfx/WEBPImageLoaderFactory"
    )

    doLast {
        javaexec {
            classpath(project.sourceSets["main"].compileClasspath)
            mainClass.set("net.burningtnt.bcigenerator.BytecodeImplGenerator")
            System.getProperty("bci.debug.address")?.let { address -> jvmArgs("-agentlib:jdwp=transport=dt_socket,server=n,address=$address,suspend=y") }
            args(bytecodeClasses.stream().map { s -> project.layout.buildDirectory.file("classes/java/main/$s.class").get().asFile.path }.toList())
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}
