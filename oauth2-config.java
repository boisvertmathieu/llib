package com.votreentreprise.pdlepuration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(prefix = "pdl.epuration.oauth2", name = "enabled", havingValue = "true")
@ConditionalOnMissingBean(name = "epurationPdlWebClient")
public class OAuth2ClientConfig {

    @Value("${pdl.epuration.oauth2.token-uri}")
    private String tokenUri;

    @Value("${pdl.epuration.oauth2.client-id}")
    private String clientId;

    @Value("${pdl.epuration.oauth2.client-secret}")
    private String clientSecret;

    @Value("${pdl.epuration.oauth2.scope:#{null}}")
    private String scope;
    
    @Value("${pdl.epuration.api-url}")
    private String apiUrl;

    @Bean
    public ClientRegistration clientRegistration() {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId("epuration-pdl-api")
                .tokenUri(tokenUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
        
        if (scope != null && !scope.isEmpty()) {
            builder.scope(scope);
        }
        
        return builder.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(ClientRegistration clientRegistration) {
        return new InMemoryClientRegistrationRepository(clientRegistration);
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService clientService) {

        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager manager = 
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, clientService);
        manager.setAuthorizedClientProvider(provider);

        return manager;
    }

    @Bean
    public WebClient epurationPdlWebClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId("epuration-pdl-api");

        return WebClient.builder()
                .baseUrl(apiUrl)
                .apply(oauth2Client.oauth2Configuration())
                .build();
    }
}