package com.votreentreprise.api.controller;

import com.votreentreprise.pdlepuration.service.EpurationService;
import com.votreentreprise.pdlepuration.service.PdlPurgeHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/epuration")
@RequiredArgsConstructor
public class EpurationController {

    private final EpurationService epurationService;

    @PostMapping("/run")
    public ResponseEntity<?> runEpuration(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        
        log.info("Demande d'épuration manuelle pour la période du {} au {}", dateDebut, dateFin);
        
        try {
            epurationService.purge(dateDebut, dateFin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Épuration lancée avec succès");
            response.put("dateDebut", dateDebut);
            response.put("dateFin", dateFin);
            
            return ResponseEntity.ok(response);
        } catch (PdlPurgeHandler.PdlPurgeException e) {
            log.error("Erreur lors de l'épuration manuelle", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/last-execution")
    public ResponseEntity<?> getLastExecution() {
        LocalDateTime lastExecution = epurationService.getLastExecutionTimestamp();
        
        Map<String, Object> response = new HashMap<>();
        response.put("lastExecution", lastExecution);
        
        return ResponseEntity.ok(response);
    }
}
