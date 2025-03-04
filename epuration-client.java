package com.votreentreprise.pdlepuration.client;

import com.votreentreprise.pdlepuration.config.PdlEpurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapi.client.model.ContenuPageRessource;
import org.openapi.client.model.EpurationPdlResponseRessource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class EpurationPdlClient {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final WebClient webClient;
    private final PdlEpurationProperties properties;

    /**
     * Récupère tous les PDL à épurer entre deux dates
     * Méthode principale qui découpe la période en mois et appelle l'API pour chaque mois
     *
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Flux de PDLs à épurer
     */
    public Flux<ContenuPageRessource> getPdlToExpurgate(LocalDate dateDebut, LocalDate dateFin) {
        log.info("Récupération des PDL à épurer entre {} et {}", dateDebut, dateFin);
        
        List<Flux<ContenuPageRessource>> monthlyFluxes = new ArrayList<>();
        
        // Générér les périodes de dates mois par mois
        List<DatePeriod> periods = generateMonthlyPeriods(dateDebut, dateFin);
        log.info("Périodes générées: {}", periods);
        
        // Pour chaque période, créer un flux qui appelle l'API mois par mois
        for (DatePeriod period : periods) {
            Flux<ContenuPageRessource> monthlyFlux = getPdlByPeriod(period.getStart(), period.getEnd());
            monthlyFluxes.add(monthlyFlux);
        }
        
        // Concaténer tous les flux de manière séquentielle
        return Flux.concat(monthlyFluxes);
    }

    /**
     * Récupère les PDL pour une période spécifique (généralement un mois)
     * Gère la pagination et les tentatives en cas d'erreur
     * 
     * @param dateDebut Date de début de la période
     * @param dateFin Date de fin de la période
     * @return Flux de PDLs pour cette période
     */
    private Flux<ContenuPageRessource> getPdlByPeriod(LocalDate dateDebut, LocalDate dateFin) {
        return getPdlPage(dateDebut, dateFin, 0)
                .expand(response -> {
                    Integer totalPages = response.getInformationsPagination().getNombreTotalPages();
                    Integer currentPage = response.getInformationsPagination().getNumeroPage();
                    
                    if (currentPage < totalPages - 1) {
                        return getPdlPage(dateDebut, dateFin, currentPage + 1);
                    } else {
                        return Mono.empty();
                    }
                })
                .flatMapIterable(EpurationPdlResponseRessource::getContenuPage)
                .filter(Objects::nonNull);
    }

    /**
     * Récupère une page de résultats de l'API
     * 
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @param pageNumber Numéro de page
     * @return Mono contenant la réponse paginée
     */
    private Mono<EpurationPdlResponseRessource> getPdlPage(LocalDate dateDebut, LocalDate dateFin, int pageNumber) {
        String uri = UriComponentsBuilder.fromPath("/epuration")
                .queryParam("numeroPage", pageNumber)
                .queryParam("nombreElementsParPage", properties.getPageSize())
                .queryParam("dateDebut", dateDebut.format(DATE_FORMATTER))
                .queryParam("dateFin", dateFin.format(DATE_FORMATTER))
                .build()
                .toUriString();
        
        log.debug("Appel API: {}", uri);
        
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(EpurationPdlResponseRessource.class)
                .retryWhen(Retry.backoff(properties.getMaxRetries(), Duration.ofSeconds(2))
                        .doBeforeRetry(retrySignal -> 
                                log.warn("Retrying API call after failure: {} (attempt {}/{})", 
                                        retrySignal.failure().getMessage(),
                                        retrySignal.totalRetries() + 1, 
                                        properties.getMaxRetries())))
                .doOnNext(response -> {
                    int totalElements = response.getInformationsPagination().getNombreTotalElements();
                    int pageNum = response.getInformationsPagination().getNumeroPage();
                    int size = response.getContenuPage() != null ? response.getContenuPage().size() : 0;
                    log.debug("Page {} récupérée avec {} PDLs (total: {})", pageNum, size, totalElements);
                });
    }

    /**
     * Génère des périodes mensuelles entre deux dates
     *
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Liste de périodes mensuelles
     */
    private List<DatePeriod> generateMonthlyPeriods(LocalDate dateDebut, LocalDate dateFin) {
        List<DatePeriod> periods = new ArrayList<>();
        
        LocalDate currentStart = dateDebut;
        while (currentStart.isBefore(dateFin)) {
            // Calculer la fin du mois courant ou la date de fin si c'est avant
            LocalDate currentEnd = currentStart.withDayOfMonth(currentStart.lengthOfMonth());
            if (currentEnd.isAfter(dateFin)) {
                currentEnd = dateFin;
            }
            
            // Ajouter la période à la liste
            periods.add(new DatePeriod(currentStart, currentEnd));
            
            // Passer au premier jour du mois suivant
            currentStart = currentStart.plusMonths(1).withDayOfMonth(1);
        }
        
        return periods;
    }

    /**
     * Classe interne représentant une période de dates
     */
    private static class DatePeriod {
        private final LocalDate start;
        private final LocalDate end;
        
        public DatePeriod(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }
        
        public LocalDate getStart() {
            return start;
        }
        
        public LocalDate getEnd() {
            return end;
        }
        
        @Override
        public String toString() {
            return start.format(DATE_FORMATTER) + " → " + end.format(DATE_FORMATTER);
        }
    }
}
