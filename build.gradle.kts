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
            val repoUrl = /*if (version.toString().endsWith("SNAPSHOT")) {
                "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            } else {
                "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            }*/
                "file:///Users/enrico/maven-local"
            url = uri(repoUrl)
            name = "localRepo"
        }
    }

    publications {
        create<MavenPublication>("kastleLib") {
            groupId = "io.github.essay97"
            artifactId = "kastle-lib"
            version = "0.0.1"

            from(components["java"])
            artifact(javadocJar)

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
