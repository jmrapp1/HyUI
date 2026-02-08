import org.gradle.internal.os.OperatingSystem

plugins {
    id("java")
    id("maven-publish")
    id("signing")
}

group = "dev.jonrapp"
version = "1.0-SNAPSHOT"

// Detect Hytale installation directory
val hytaleHome: String by lazy {
    if (project.hasProperty("hytale_home")) {
        project.findProperty("hytale_home") as String
    } else {
        val os = OperatingSystem.current()
        val detectedPath = when {
            os.isWindows -> "${System.getProperty("user.home")}/AppData/Roaming/Hytale"
            os.isMacOsX -> "${System.getProperty("user.home")}/Library/Application Support/Hytale"
            os.isLinux -> {
                val flatpakPath = "${System.getProperty("user.home")}/.var/app/com.hypixel.HytaleLauncher/data/Hytale"
                if (file(flatpakPath).exists()) {
                    flatpakPath
                } else {
                    "${System.getProperty("user.home")}/.local/share/Hytale"
                }
            }
            else -> throw GradleException("Your Hytale install could not be detected automatically. If you are on an unsupported platform or using a custom install location, please define the install location using the hytale_home property.")
        }
        
        if (!file(detectedPath).exists()) {
            throw GradleException("Failed to find Hytale at the expected location. Please make sure you have installed the game. The expected location can be changed using the hytale_home property. Currently looking in $detectedPath")
        }
        
        detectedPath
    }
}

val patchline: String by lazy {
    project.findProperty("patchline") as String? ?: "release"
}

repositories {
    mavenCentral()
}

dependencies {
    println("HyUI: Hytale home: $hytaleHome | Patchline: $patchline")
    implementation(files("$hytaleHome/install/$patchline/package/game/latest/Server/HytaleServer.jar"))
    
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            
            pom {
                name.set("HyUI")
                description.set("A library for better UI management in Hytale.")
                url.set("https://github.com/jmrapp1/HyUI")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("jmrapp1")
                        name.set("Jon Rapp")
                        email.set("jon@jonrapp.dev")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/jmrapp1/HyUI.git")
                    developerConnection.set("scm:git:ssh://github.com/jmrapp1/HyUI.git")
                    url.set("https://github.com/jmrapp1/HyUI")
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "CentralPortal"
            url = uri("https://central.sonatype.com/api/v1/publisher/upload")
            
            credentials {
                username = findProperty("centralPortalUsername") as String? ?: System.getenv("CENTRAL_PORTAL_USERNAME")
                password = findProperty("centralPortalPassword") as String? ?: System.getenv("CENTRAL_PORTAL_PASSWORD")
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}