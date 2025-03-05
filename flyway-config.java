package com.votreentreprise.pdlepuration.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuration Flyway pour s'assurer que les migrations de la librairie sont appliquées
 * sans conflit avec les migrations du projet principal.
 */
@Configuration
@ConditionalOnClass(Flyway.class)
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter(FlywayAutoConfiguration.class)
@ConditionalOnProperty(prefix = "pdl.epuration", name = "flyway-enabled", havingValue = "true", matchIfMissing = true)
public class PdlEpurationFlywayConfiguration {

    /**
     * Configure une instance Flyway séparée pour les migrations de la librairie.
     * Cette instance utilise le même DataSource que l'application principale mais
     * cherche les migrations uniquement dans le package de la librairie.
     */
    @Bean
    public Flyway epurationFlywayMigration(DataSource dataSource) {
        // Créer une nouvelle instance de Flyway configurée pour notre librairie
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/pdlepuration") // Emplacement spécifique à notre librairie
                .baselineOnMigrate(true)
                .table("flyway_schema_history_pdlepuration") // Table spécifique pour notre historique
                .load();
        
        // Exécuter les migrations
        flyway.migrate();
        
        return flyway;
    }
}
