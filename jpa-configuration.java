package com.votreentreprise.pdlepuration.config;

import com.votreentreprise.pdlepuration.db.EpurationHistory;
import com.votreentreprise.pdlepuration.db.EpurationHistoryRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Configuration JPA pour les entités et repositories de la librairie.
 * Cette configuration ne s'active que si JPA et une source de données sont disponibles.
 */
@Configuration
@ConditionalOnClass({LocalContainerEntityManagerFactoryBean.class, EntityManagerFactory.class})
@ConditionalOnBean(EntityManagerFactory.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EntityScan(basePackageClasses = {EpurationHistory.class})
@EnableJpaRepositories(basePackageClasses = {EpurationHistoryRepository.class})
public class PdlEpurationJpaConfiguration {
    // Cette classe active les fonctionnalités JPA pour les entités et repositories de la librairie
}
