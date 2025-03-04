package com.votreentreprise.pdlepuration.service;

import com.votreentreprise.pdlepuration.client.EpurationPdlClient;
import com.votreentreprise.pdlepuration.db.EpurationHistory;
import com.votreentreprise.pdlepuration.db.EpurationHistoryRepository;
import com.votreentreprise.pdlepuration.model.PdlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class EpurationService {

    private final EpurationPdlClient epurationPdlClient;
    private final EpurationHistoryRepository epurationHistoryRepository;
    private final PdlPurgeHandler pdlPurgeHandler;

    /**
     * Exécute l'épuration des PDL entre deux dates
     *
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     */
    @Transactional
    public void purge(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Début de l'épuration pour la période du {} au {}", dateDebut, dateFin);
        
        LocalDateTime startTime = LocalDateTime.now();
        List<PdlModel> pdlList = new ArrayList<>();
        
        try {
            // Récupérer tous les PDL à épurer
            pdlList = epurationPdlClient.getPdlToExpurgate(dateDebut, dateFin)
                    .collectList()
                    .block();
            
            if (pdlList == null || pdlList.isEmpty()) {
                log.info("Aucun PDL à épurer pour cette période");
                saveExecutionHistory(startTime, dateDebut, dateFin, 0, EpurationHistory.EpurationStatus.SUCCESS, null);
                return;
            }
            
            log.info("{} PDL à épurer", pdlList.size());
            
            // Appel à l'implémentation fournie par le projet client
            pdlPurgeHandler.purgeData(pdlList);
            
            // Sauvegarder l'historique d'exécution
            saveExecutionHistory(startTime, dateDebut, dateFin, pdlList.size(), 
                    EpurationHistory.EpurationStatus.SUCCESS, null);
            
            log.info("Épuration terminée avec succès pour {} PDL", pdlList.size());
            
        } catch (Exception e) {
            log.error("Erreur lors de l'épuration", e);
            
            // Sauvegarder l'historique avec le statut d'erreur
            saveExecutionHistory(startTime, dateDebut, dateFin, pdlList.size(), 
                    EpurationHistory.EpurationStatus.FAILURE, e.getMessage());
            
            // Relancer l'exception (sera gérée par la transaction)
            if (e instanceof PdlPurgeHandler.PdlPurgeException) {
                throw (PdlPurgeHandler.PdlPurgeException) e;
            } else {
                throw new PdlPurgeHandler.PdlPurgeException("Erreur lors de l'épuration: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Récupère le timestamp de la dernière exécution réussie
     *
     * @return Le timestamp de la dernière exécution ou null si aucune
     */
    public LocalDateTime getLastExecutionTimestamp() {
        return epurationHistoryRepository.findLatestSuccessful()
                .map(EpurationHistory::getEndDate)
                .orElse(null);
    }
    
    /**
     * Enregistre un historique d'exécution
     */
    private void saveExecutionHistory(LocalDateTime executionTime, LocalDate startDate, LocalDate endDate, 
                                     Integer pdlCount, EpurationHistory.EpurationStatus status, String errorMessage) {
        
        EpurationHistory history = EpurationHistory.builder()
                .executionDate(executionTime)
                .startDate(startDate.atStartOfDay())
                .endDate(endDate.atStartOfDay())
                .pdlCount(pdlCount)
                .status(status)
                .errorMessage(errorMessage)
                .build();
        
        epurationHistoryRepository.save(history);
    }
}
