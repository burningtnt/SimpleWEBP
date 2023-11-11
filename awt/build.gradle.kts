import java.io.FileOutputStream
import java.util.zip.ZipOutputStream
import java.util.zip.ZipFile

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
    api(rootProject)
    testImplementation(project)

    testImplementation("org.apache.commons:commons-imaging:1.0-alpha3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
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
