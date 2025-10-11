plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("gg.jte.gradle") version "3.1.12"
    checkstyle
    id("org.sonarqube") version "6.3.1.5724"
    id("jacoco")
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.zaxxer:HikariCP:6.0.0")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.postgresql:postgresql:42.7.4")

    implementation("io.javalin:javalin:6.6.0")
    implementation("io.javalin:javalin-bundle:6.6.0")
    implementation("io.javalin:javalin-rendering:6.6.0")
    implementation("gg.jte:jte:3.1.12")

    implementation("org.slf4j:slf4j-simple:2.0.9")

    implementation("com.konghq:unirest-java:3.14.2")
    implementation("org.jsoup:jsoup:1.17.2")

    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("io.javalin:javalin-testtools:6.6.0")
}

application {
    mainClass.set("hexlet.code.App")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

checkstyle {
    toolVersion = "10.12.4"
    config = resources.text.fromFile("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
}

sonar {
    properties {
        property("sonar.projectKey", "KryWeak_java-project-72")
        property("sonar.organization", "kryweak")
    }
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.shadowJar {
    archiveFileName.set("app-1.0.jar")
    dependsOn(tasks.precompileJte)
    mergeServiceFiles()
}

jte {
    sourceDirectory.set(project.file("src/main/resources/templates").toPath())
    precompile()
}

tasks.register("runApp") {
    dependsOn(tasks.shadowJar)
    doLast {
        javaexec {
            mainClass.set("hexlet.code.App")
            classpath = files(tasks.shadowJar.get().archiveFile)
            environment("PORT", "7070")
            environment("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1")
        }
    }
}