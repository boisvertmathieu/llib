plugins {
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'java'
}

group = 'com.votreentreprise.api'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
    mavenCentral()
    
    // Référence vers le dépôt Maven local ou d'entreprise contenant votre librairie
    maven {
        url "https://your-maven-repo.com/repository/maven-releases/"
    }
}

dependencies {
    // Dépendances Spring Boot standard
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.flywaydb:flyway-core'
    
    // Votre librairie d'épuration PDL
    implementation 'com.votreentreprise:pdl-epuration-lib:0.1.0'
    
    // Base de données
    runtimeOnly 'org.postgresql:postgresql'
    
    // Tests
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()
}
