plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.dokka)
    `java-library`
    `maven-publish`
    signing
}

version = "0.0.1"
group = "io.github.essay97"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.arrow)
    api(libs.kotlinx.datetime)
    implementation(libs.sqlite.driver)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("io.github.essay97.kastle.db")
        }
    }
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            val repoUrl = if (version.toString().endsWith("SNAPSHOT")) {
                "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            } else {
                "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            }
            url = uri(repoUrl)
            name = "OSSRH"
        }
    }

    publications {
        create<MavenPublication>("kastle-lib") {
            groupId = "io.github.essay97"
            artifactId = "kastle-lib"
            version = "0.0.1"

            from(components["java"])
            artifact(javadocJar)

            pom {
                name = "Kastle Library"
                description = "Develop games for the Kastle Engine"
                developers {
                    developer {
                        name = "Enrico Saggiorato"
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["kastle-lib"])
}
