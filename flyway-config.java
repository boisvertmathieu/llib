package com.votreentreprise.pdlepuration.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

/**
 * Configuration Flyway pour la librairie PDL Epuration.
 * Cette configuration n'interfère pas avec la configuration Flyway principale
 * et utilise les mêmes paramètres de connexion.
 */
@Slf4j
@Configuration
@ConditionalOnClass(Flyway.class)
@AutoConfigureAfter(FlywayAutoConfiguration.class)
@EnableConfigurationProperties(FlywayProperties.class)
@ConditionalOnProperty(prefix = "pdl.epuration", name = "flyway-enabled", havingValue = "true", matchIfMissing = true)
public class PdlEpurationFlywayConfiguration {

    /**
     * Customiseur pour la configuration Flyway principale.
     * Ajoute nos locations de migration aux locations existantes.
     */
    @Bean
    public FlywayConfigurationCustomizer pdlEpurationFlywayCustomizer() {
        return configuration -> {
            // Ajoute nos locations spécifiques
            String[] existingLocations = configuration.getLocations();
            String[] newLocations = new String[existingLocations.length + 1];
            System.arraycopy(existingLocations, 0, newLocations, 0, existingLocations.length);
            newLocations[existingLocations.length] = "classpath:db/migration/pdlepuration";
            
            configuration.locations(newLocations);
            log.info("Configuration Flyway customisée pour inclure les migrations PDL Epuration");
        };
    }
}
