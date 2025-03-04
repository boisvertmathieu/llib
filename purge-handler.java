package com.votreentreprise.pdlepuration.service;

import com.votreentreprise.pdlepuration.model.PdlModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface à implémenter par les applications utilisant la librairie
 * pour définir la logique d'épuration spécifique à leur base de données
 */
public interface PdlPurgeHandler {

    /**
     * Méthode appelée pour épurer les données correspondant aux PDL fournis
     * Cette méthode doit être implémentée par l'application cliente pour définir
     * sa propre logique d'épuration
     *
     * @param pdls Liste des PDL à épurer
     * @throws PdlPurgeException Si une erreur survient pendant l'épuration
     */
    @Transactional
    void purgeData(List<PdlModel> pdls) throws PdlPurgeException;
    
    /**
     * Exception levée en cas d'erreur lors de l'épuration
     */
    class PdlPurgeException extends RuntimeException {
        public PdlPurgeException(String message) {
            super(message);
        }
        
        public PdlPurgeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
