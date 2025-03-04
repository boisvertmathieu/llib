package com.votreentreprise.pdlepuration.scheduler;

import com.votreentreprise.pdlepuration.config.PdlEpurationProperties;
import com.votreentreprise.pdlepuration.service.EpurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class EpurationScheduler {

    private final EpurationService epurationService;
    private final PdlEpurationProperties properties;

    /**
     * Exécute l'épuration automatique selon l'expression cron configurée
     */
    @Scheduled(cron = "${pdl.epuration.cron-expression:0 0 2 ? * SUN}")
    public void scheduleEpuration() {
        log.info("Démarrage de l'épuration automatique programmée");
        
        try {
            // Récupérer la date de la dernière exécution
            LocalDateTime lastExecution = epurationService.getLastExecutionTimestamp();
            
            // Définir la date de début
            LocalDate startDate;
            if (lastExecution == null) {
                log.info("Aucune exécution précédente trouvée, épuration complète nécessaire");
                // Si pas d'exécution précédente, on prend une date lointaine (10 ans en arrière)
                startDate = LocalDate.now().minusYears(10);
            } else {
                log.info("Dernière exécution trouvée: {}", lastExecution);
                // Sinon, on prend la date de la dernière exécution
                startDate = lastExecution.toLocalDate();
            }
            
            // La date de fin est aujourd'hui
            LocalDate endDate = LocalDate.now();
            
            // Exécuter l'épuration
            epurationService.purge(startDate, endDate);
            
        } catch (Exception e) {
            log.error("Erreur lors de l'épuration automatique programmée", e);
        }
    }
}
