plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.dokka)
    `java-library`
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

tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

tasks.register("prepareMaven") {
    dependsOn(tasks["dokkaJavadocJar"])
    dependsOn(tasks.build)
}
