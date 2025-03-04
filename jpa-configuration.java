package com.votreentreprise.pdlepuration.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.votreentreprise.pdlepuration.db")
@EnableJpaRepositories("com.votreentreprise.pdlepuration.db")
public class PdlEpurationJpaConfiguration {
    // Cette classe active les fonctionnalités JPA pour les entités et repositories de la librairie
}
