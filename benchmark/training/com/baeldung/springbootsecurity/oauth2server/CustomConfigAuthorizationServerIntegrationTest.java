package com.baeldung.springbootsecurity.oauth2server;


import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringBootAuthorizationServerApplication.class)
@ActiveProfiles("authz")
public class CustomConfigAuthorizationServerIntegrationTest extends OAuth2IntegrationTestSupport {
    @Test
    public void givenOAuth2Context_whenAccessTokenIsRequested_ThenAccessTokenValueIsNotNull() {
        ClientCredentialsResourceDetails resourceDetails = getClientCredentialsResourceDetails("baeldung", Collections.singletonList("read"));
        OAuth2RestTemplate restTemplate = getOAuth2RestTemplate(resourceDetails);
        OAuth2AccessToken accessToken = restTemplate.getAccessToken();
        Assert.assertNotNull(accessToken);
    }

    @Test(expected = OAuth2AccessDeniedException.class)
    public void givenOAuth2Context_whenAccessTokenIsRequestedWithInvalidException_ThenExceptionIsThrown() {
        ClientCredentialsResourceDetails resourceDetails = getClientCredentialsResourceDetails("baeldung", Collections.singletonList("write"));
        OAuth2RestTemplate restTemplate = getOAuth2RestTemplate(resourceDetails);
        restTemplate.getAccessToken();
    }

    @Test
    public void givenOAuth2Context_whenAccessTokenIsRequestedByClientWithWriteScope_ThenAccessTokenIsNotNull() {
        ClientCredentialsResourceDetails resourceDetails = getClientCredentialsResourceDetails("baeldung-admin", Collections.singletonList("write"));
        OAuth2RestTemplate restTemplate = getOAuth2RestTemplate(resourceDetails);
        OAuth2AccessToken accessToken = restTemplate.getAccessToken();
        Assert.assertNotNull(accessToken);
    }
}

