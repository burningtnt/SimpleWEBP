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

val javadocJar = tasks.create<Jar>("javadocJar") {
    group = "build"
    archiveClassifier.set("javadoc")
}

tasks.compileJava {
    options.release.set(8)

    sourceCompatibility = "8"
    targetCompatibility = "8"
}

tasks.compileTestJava {
    options.release.set(8)
}

tasks.create<Task>("generateModuleInfo") {
    val outputFile = File(project.buildDir, "classes/module-info.class")

    doLast {
        val cw = org.objectweb.asm.ClassWriter(0)
        cw.visit(org.objectweb.asm.Opcodes.V1_8, org.objectweb.asm.Opcodes.ACC_MODULE, "module-info", null, null, null)

        val mv = cw.visitModule("net.burningtnt.webp", org.objectweb.asm.Opcodes.ACC_MANDATED, null)
        mv.visitRequire("java.base", org.objectweb.asm.Opcodes.ACC_SYNTHETIC, "8")
        mv.visitRequire(
            "javafx.graphics",
            org.objectweb.asm.Opcodes.ACC_MANDATED or org.objectweb.asm.Opcodes.ACC_STATIC,
            "8"
        )
        mv.visitExport("net.burningtnt.webp.vp8l", org.objectweb.asm.Opcodes.ACC_MANDATED)
        mv.visitExport("net.burningtnt.webp.jfx", org.objectweb.asm.Opcodes.ACC_MANDATED)
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
        from("${project.buildDir}/classes/module-info.class") {
        }
    }
}

repositories {
    mavenCentral()
}

checkstyle {
    sourceSets = mutableSetOf()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}


tasks.getByName("build") {
    dependsOn(tasks.getByName("checkstyleMain"))
    dependsOn(tasks.getByName("checkstyleTest"))
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
        kala.platform.Architecture.ARM -> if (classifer == "linux") classifer = "linux-arm32-monocle" else return@run
        else -> return@run
    }

    val modules = listOf("base", "graphics")

    dependencies {
        for (module in modules) {
            implementation("org.openjfx:javafx-$module:17.0.2:$classifer")
            testImplementation("org.openjfx:javafx-$module:17.0.2:$classifer")
        }
    }
}