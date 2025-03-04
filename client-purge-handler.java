package com.votreentreprise.api.configuration;

import com.votreentreprise.api.repository.ClientRepository;
import com.votreentreprise.api.repository.ContractRepository;
import com.votreentreprise.api.repository.MeasureRepository;
import com.votreentreprise.pdlepuration.model.PdlModel;
import com.votreentreprise.pdlepuration.service.PdlPurgeHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomPdlPurgeHandler implements PdlPurgeHandler {

    private final MeasureRepository measureRepository;
    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public void purgeData(List<PdlModel> pdls) throws PdlPurgeException {
        try {
            log.info("Début de l'épuration personnalisée pour {} PDLs", pdls.size());
            
            // Extraire les identifiants de PDL
            List<String> pdlIds = pdls.stream()
                    .map(PdlModel::getPdlId)
                    .collect(Collectors.toList());
            
            // Log des PDLs à épurer
            log.debug("Liste des PDLs à épurer: {}", pdlIds);
            
            // Étape 1: Supprimer les mesures associées aux PDLs
            int deletedMeasures = measureRepository.deleteByPdlIdIn(pdlIds);
            log.info("{} mesures supprimées", deletedMeasures);
            
            // Étape 2: Supprimer les contrats associés aux PDLs
            int deletedContracts = contractRepository.deleteByPdlIdIn(pdlIds);
            log.info("{} contrats supprimés", deletedContracts);
            
            // Étape 3: Supprimer les clients qui n'ont plus de contrats
            int deletedClients = clientRepository.deleteClientsWithoutContracts();
            log.info("{} clients sans contrats supprimés", deletedClients);
            
            log.info("Épuration personnalisée terminée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de l'épuration personnalisée", e);
            throw new PdlPurgeException("Échec de l'épuration personnalisée: " + e.getMessage(), e);
        }
    }
}
