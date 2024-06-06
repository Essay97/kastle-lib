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

kotlin {
    jvmToolchain(8)
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("io.github.essay97.kastle.db")
        }
    }
}

val dokkaHtmlJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc-java") // "javadoc" is needed for Dokka that will be included in the Maven Central zip
}

publishing {
    repositories {
        maven {
            val repoUrl = "file://${System.getProperty("user.home")}/maven-local"

            url = uri(repoUrl)
            name = "localRepo"
        }
    }

    publications {
        create<MavenPublication>("kastleLib") {
            groupId = project.group.toString()
            artifactId = rootProject.name
            version = project.version.toString()

            from(components["java"])
            artifact(dokkaHtmlJar)

            pom {
                name = "Kastle Library"
                description = "Develop games for the Kastle Engine"
                url = "https://github.com/Essay97/kastle-lib"

                developers {
                    developer {
                        name = "Enrico Saggiorato"
                    }
                }

                licenses {
                    license {
                        url = "https://www.gnu.org/licenses/gpl-3.0.html"
                        name = "GNU General Public License v3.0"
                    }
                }

                scm {
                    url = "https://github.com/Essay97/kastle-lib"
                    connection = "scm:git://github.com:Essay97/kastle-lib.git"
                    developerConnection = "scm:git://github.com:Essay97/kastle-lib.git"
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["kastleLib"])
}
