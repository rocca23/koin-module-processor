repositories {
    google()
    mavenCentral()
}
plugins {
    id("java-library")
    kotlin("jvm") version "1.5.0"
    kotlin("kapt") version "1.5.0"
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    val autoService = "1.0-rc7"
    implementation("com.google.auto.service:auto-service-annotations:$autoService")
    kapt("com.google.auto.service:auto-service:$autoService")
    implementation("com.squareup:kotlinpoet:1.6.0")
    compileOnly("io.insert-koin:koin-core:3.0.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.rocca23"
            artifactId = "koin-module-processor"
            version = "1.0.2"
            from(components["java"])
            pom {
                name.set("Koin Module Processor")
                description.set("Annotation Processor to generate a list of Koin Modules")
                url.set("https://github.com/rocca23/koin-module-processor")
                licenses {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
                developers {
                    developer {
                        id.set("rocca23")
                        url.set("https://github.com/rocca23")
                    }
                }
            }
        }
    }
    repositories {
        mavenCentral()
    }
}