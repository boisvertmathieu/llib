package com.votreentreprise.pdlepuration.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pdl.epuration")
public class PdlEpurationProperties {
    
    /**
     * URL de l'API EpurationPDL
     */
    private String apiUrl = "http://api-epuration-pdl/api/pdl";
    
    /**
     * Nombre d'éléments par page lors des appels à l'API
     */
    private int pageSize = 10000;
    
    /**
     * Nombre maximum de tentatives en cas d'échec d'un appel API
     */
    private int maxRetries = 3;
    
    /**
     * Expression cron pour le scheduler d'épuration automatique
     * Par défaut: tous les dimanches à 2h du matin
     */
    private String cronExpression = "0 0 2 ? * SUN";

    /**
     * Activer/désactiver le scheduler automatique
     */
    private boolean schedulerEnabled = true;
}
