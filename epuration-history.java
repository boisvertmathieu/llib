package com.votreentreprise.pdlepuration.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pdl_epuration_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpurationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_date", nullable = false)
    private LocalDateTime executionDate;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "pdl_count")
    private Integer pdlCount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EpurationStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    public enum EpurationStatus {
        SUCCESS, FAILURE
    }
}
