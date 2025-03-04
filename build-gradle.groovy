plugins {
    id 'java-library'
    id 'maven-publish'
    id 'org.springframework.boot' version '3.2.0' apply false
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.votreentreprise'
version = '0.1.0'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES
    }
}

dependencies {
    // Spring Boot
    api 'org.springframework.boot:spring-boot-starter'
    api 'org.springframework.boot:spring-boot-starter-web'
    api 'org.springframework.boot:spring-boot-starter-webflux'
    api 'org.springframework.boot:spring-boot-starter-data-jpa'
    api 'org.springframework.hateoas:spring-hateoas'
    
    // Flyway pour les migrations DB
    api 'org.flywaydb:flyway-core'
    
    // Lombok pour réduire le code boilerplate
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    // Pour les tests
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'io.projectreactor:reactor-test'
    testImplementation 'com.h2database:h2'
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        mavenJava(MavenPublication) {
            from components.java
        }
    }
    repositories {
        maven {
            // Définir l'URL de votre dépôt Maven (Nexus, Artifactory ou autre)
            // url = "https://your-maven-repo.com/repository/maven-releases/"
            // credentials {
            //     username = findProperty("mavenUsername") ?: "anonymous"
            //     password = findProperty("mavenPassword") ?: ""
            // }
        }
    }
}

test {
    useJUnitPlatform()
}

bootJar {
    enabled = false
}

jar {
    enabled = true
    archiveClassifier = ''
}
