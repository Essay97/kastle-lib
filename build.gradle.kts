plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.dokka)
    `java-library`
}

version = "1.0-SNAPSHOT"
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

sqldelight {
    databases {
        create("Database") {
            packageName.set("io.github.essay97.kastle.db")
        }
    }
}
