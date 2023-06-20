import java.io.FileOutputStream
import java.util.zip.ZipOutputStream
import java.util.zip.ZipFile


buildscript {
    repositories { mavenCentral() }

    dependencies {
        classpath("org.glavo.kala:kala-platform:0.10.0")
        classpath("org.ow2.asm:asm:9.4")
        classpath("org.ow2.asm:asm-util:9.4")
        classpath("org.ow2.asm:asm-commons:9.4")
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
}

repositories {
    mavenCentral()
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
    jvmArgs("--illegal-access=deny")

    listOf(
        "javafx.graphics/com.sun.javafx.iio",
        "javafx.graphics/com.sun.javafx.iio.common"
    ).forEach { string ->
        jvmArgs("--add-exports", "${string}=ALL-UNNAMED")
    }

    listOf(
        "java.base/java.io",
    ).forEach { string ->
        jvmArgs("--add-opens", "${string}=ALL-UNNAMED")
    }

    println(jvmArgs)
}

tasks.create<Task>("generateModuleInfo") {
    dependsOn(tasks.compileJava)
    group = "build"

    val outputFile = File(project.buildDir, "module-info/module-info.class")

    doLast {
        val cw = org.objectweb.asm.ClassWriter(0)
        cw.visit(org.objectweb.asm.Opcodes.V9, org.objectweb.asm.Opcodes.ACC_MODULE, "module-info", null, null, null)

        val mv = cw.visitModule("net.burningtnt.webp", 0, null)
        mv.visitRequire("java.base", 0, null)
        mv.visitRequire("javafx.graphics", org.objectweb.asm.Opcodes.ACC_STATIC, null)
        mv.visitExport("net.burningtnt.webp.vp8l", 0)
        mv.visitExport("net.burningtnt.webp.jfx", 0)
        mv.visitEnd()

        cw.visitEnd()

        outputFile.parentFile.mkdirs()
        outputFile.deleteOnExit()
        outputFile.createNewFile()
        outputFile.writeBytes(cw.toByteArray())
    }
}

tasks.processResources {
    dependsOn(tasks.getByName("generateModuleInfo"))

    into("") {
        from("${project.buildDir}/module-info/module-info.class") {
        }
    }
}

checkstyle {
    sourceSets = mutableSetOf()
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

tasks.getByName("build") {
    dependsOn(tasks.getByName("checkstyleMain") {
        group = "build"
    })
    dependsOn(tasks.getByName("checkstyleTest") {
        group = "build"
    })
    dependsOn(tasks.create<Task>("lightJar") {
        dependsOn(tasks.jar)
        group = "build"

        val inputFile = File(project.buildDir, "libs/${project.name}-${project.version}.jar")
        val outputFile = File(project.buildDir, "libs/${project.name}-${project.version}-light.jar")

        inputs.files.plus(inputFile)
        outputs.files.plus(outputFile)

        doLast {
            ZipFile(inputFile).use { zipFile ->
                ZipOutputStream(FileOutputStream(outputFile)).use { zipOutputStream ->
                    val inputEntries = zipFile.entries()
                    while (inputEntries.hasMoreElements()) {
                        val inputEntry = inputEntries.nextElement()

                        if (inputEntry.name.startsWith("META-INF/") || inputEntry.name == "module-info.class") {
                            continue
                        }

                        zipOutputStream.putNextEntry(inputEntry)
                        zipOutputStream.write(zipFile.getInputStream(inputEntry).readAllBytes())
                        zipOutputStream.closeEntry()
                    }
                }
            }
        }
    })
}

dependencies {
    testImplementation(project)

    testImplementation("org.apache.commons:commons-imaging:1.0-alpha3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.glavo:simple-png:0.3.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "SimpleWEBP"
            version = project.version.toString()

            from(components["java"])
        }
    }
}

// Setup JavaFX
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
            compileOnly("org.openjfx:javafx-$module:17.0.2:$classifer")
            testImplementation("org.openjfx:javafx-$module:17.0.2:$classifer")
        }
    }
}