package com.baeldung.springbootsecurity.oauth2server;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringBootAuthorizationServerApplication.class, properties = { "security.oauth2.client.client-id=client", "security.oauth2.client.client-secret=baeldung" })
public class DefaultConfigAuthorizationServerIntegrationTest extends OAuth2IntegrationTestSupport {
    @Test
    public void givenOAuth2Context_whenAccessTokenIsRequested_ThenAccessTokenValueIsNotNull() {
        ClientCredentialsResourceDetails resourceDetails = getClientCredentialsResourceDetails("client", Arrays.asList("read", "write"));
        OAuth2RestTemplate restTemplate = getOAuth2RestTemplate(resourceDetails);
        OAuth2AccessToken accessToken = restTemplate.getAccessToken();
        Assert.assertNotNull(accessToken);
    }
}

