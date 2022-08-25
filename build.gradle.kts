plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.6.10-1.0.2")
    compileOnly("io.insert-koin:koin-core:3.1.2")
}

val sourcesJar by tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(project.sourceSets.main.get().allJava.srcDirs)
}
group = "com.rocca23"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.rocca23"
            artifactId = "koin-module-processor"
            version = "2.0.0"
            from(components["java"])
            artifact(sourcesJar)
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