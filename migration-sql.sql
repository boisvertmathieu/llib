-- V1__create_pdl_epuration_history_table.sql
CREATE TABLE pdl_epuration_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    execution_date TIMESTAMP NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    pdl_count INTEGER,
    status VARCHAR(20) NOT NULL,
    error_message VARCHAR(1000)
);

-- Ajout d'un index sur la date d'exécution pour optimiser les requêtes
CREATE INDEX idx_pdl_epuration_history_execution_date ON pdl_epuration_history(execution_date);
