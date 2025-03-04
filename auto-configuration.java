package com.votreentreprise.pdlepuration.config;

import com.votreentreprise.pdlepuration.client.EpurationPdlClient;
import com.votreentreprise.pdlepuration.db.EpurationHistoryRepository;
import com.votreentreprise.pdlepuration.scheduler.EpurationScheduler;
import com.votreentreprise.pdlepuration.service.EpurationService;
import com.votreentreprise.pdlepuration.service.PdlPurgeHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(PdlEpurationProperties.class)
@Import(PdlEpurationJpaConfiguration.class)
public class PdlEpurationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebClient epurationWebClient(PdlEpurationProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getApiUrl())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public EpurationPdlClient epurationPdlClient(WebClient epurationWebClient, PdlEpurationProperties properties) {
        return new EpurationPdlClient(epurationWebClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public EpurationService epurationService(
            EpurationPdlClient epurationPdlClient,
            EpurationHistoryRepository epurationHistoryRepository,
            PdlPurgeHandler pdlPurgeHandler) {
        return new EpurationService(epurationPdlClient, epurationHistoryRepository, pdlPurgeHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "pdl.epuration", name = "scheduler-enabled", havingValue = "true", matchIfMissing = true)
    public EpurationScheduler epurationScheduler(EpurationService epurationService, PdlEpurationProperties properties) {
        return new EpurationScheduler(epurationService, properties);
    }
}
