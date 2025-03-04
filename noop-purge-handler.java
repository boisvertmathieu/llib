package com.votreentreprise.pdlepuration.db;

import com.votreentreprise.pdlepuration.service.PdlPurgeHandler;
import lombok.extern.slf4j.Slf4j;
import org.openapi.client.model.ContenuPageRessource;

import java.util.List;

/**
 * Implémentation par défaut de PdlPurgeHandler qui ne fait rien.
 * Cette classe est utilisée uniquement lorsqu'aucune implémentation personnalisée n'est fournie.
 * Elle permet d'éviter les erreurs d'injection de dépendances, mais affiche un avertissement
 * pour rappeler à l'utilisateur qu'une implémentation réelle est nécessaire.
 */
@Slf4j
public class NoOpPdlPurgeHandler implements PdlPurgeHandler {

    public NoOpPdlPurgeHandler() {
        log.warn("====================================================================");
        log.warn("ATTENTION: Utilisation de l'implémentation par défaut de PdlPurgeHandler");
        log.warn("Cette implémentation ne fait RIEN et ne convient pas pour la production.");
        log.warn("Veuillez fournir votre propre implémentation de PdlPurgeHandler.");
        log.warn("====================================================================");
    }

    @Override
    public void purgeData(List<ContenuPageRessource> pdls) {
        log.warn("Méthode purgeData appelée avec {} PDLs, mais aucune action effectuée (implémentation par défaut)", 
                pdls != null ? pdls.size() : 0);
    }
}
