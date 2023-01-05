/*
 * Copyright 2023 Volker Schmidt
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *  
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 *    endorse or promote products derived from this software without specific prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

plugins {
    id("java-library")
    id("checkstyle")
    id("jacoco")
    id("org.checkerframework") version "0.6.20"
    id("com.github.spotbugs") version "5.0.13"
    id("org.sonarqube") version "3.5.0.2730"
}

group = "eu.volsch"
version = "0.9.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

jacoco {
    toolVersion = "0.8.8"
}

sonar {
    properties {
        property("sonar.projectKey", "volsch_stockmountain")
        property("sonar.organization", "volsch")
        property("sonar.host.url", "https://sonarcloud.io")

        property("sonar.java.checkstyle.reportPaths",
                "build/reports/checkstyle/main.xml,build/reports/checkstyle/test.xml")
        property("sonar.java.spotbugs.reportPaths", "build/reports/spotbugs/main.xml")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    val lombokVersion = "1.18.24"
    val mockitoVersion = "3.12.4"
    val junitJupiterVersion = "5.9.1"
    val hamcrestVersion = "2.2"
    val spotbugsAnnotationsVersion = "4.7.3"
    val checkerFrameworkVersion = "3.28.0"

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    implementation("com.github.spotbugs:spotbugs-annotations:$spotbugsAnnotationsVersion")
    compileOnly("org.checkerframework:checker-qual:$checkerFrameworkVersion")
    compileOnly("net.jcip:jcip-annotations:1.0")

    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testImplementation("org.hamcrest:hamcrest:$hamcrestVersion")
    testCompileOnly("org.checkerframework:checker-qual:$checkerFrameworkVersion")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.12.3")

    checkerFramework("org.checkerframework:checker:$checkerFrameworkVersion")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.5".toBigDecimal()
            }
        }
        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "1.0".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "1.0".toBigDecimal()
            }
        }
    }
}

tasks.withType<Checkstyle>().configureEach {
    val archive = configurations.getByName("checkstyle").filter {
        it.name.contains("checkstyle")
    }
    config = resources.text.fromArchiveEntry(archive, "google_checks.xml")
    maxWarnings = 0
    reports {
        require(true)
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.spotbugsMain {
    reports.create("xml") {
        required.set(true)
    }
    reports.create("html") {
        required.set(true)
        setStylesheet("fancy-hist.xsl")
    }
}

tasks.sonar {
    dependsOn(tasks.jacocoTestReport, tasks.checkstyleMain, tasks.checkstyleTest,
            tasks.spotbugsMain, tasks.spotbugsTest)
}
