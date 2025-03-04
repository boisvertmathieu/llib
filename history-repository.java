package com.votreentreprise.pdlepuration.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EpurationHistoryRepository extends JpaRepository<EpurationHistory, Long> {

    /**
     * Trouve la dernière exécution réussie
     * 
     * @return La dernière entrée avec statut SUCCESS, ou vide si aucune n'existe
     */
    @Query("SELECT e FROM EpurationHistory e WHERE e.status = 'SUCCESS' ORDER BY e.executionDate DESC")
    Optional<EpurationHistory> findLatestSuccessful();
}
