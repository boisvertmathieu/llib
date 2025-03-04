package com.votreentreprise.pdlepuration.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlModel {
    
    /**
     * Identifiant unique du PDL
     */
    private String pdlId;
    
    /**
     * Date d'épuration du PDL dans le système source
     */
    private LocalDateTime dateEpuration;
}
