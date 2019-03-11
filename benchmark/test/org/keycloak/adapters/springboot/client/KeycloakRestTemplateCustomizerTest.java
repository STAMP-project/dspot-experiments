package org.keycloak.adapters.springboot.client;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;


public class KeycloakRestTemplateCustomizerTest {
    private KeycloakRestTemplateCustomizer customizer;

    private KeycloakSecurityContextClientRequestInterceptor interceptor = Mockito.mock(KeycloakSecurityContextClientRequestInterceptor.class);

    @Test
    public void interceptorIsAddedToRequest() {
        RestTemplate restTemplate = new RestTemplate();
        customizer.customize(restTemplate);
        Assert.assertTrue(restTemplate.getInterceptors().contains(interceptor));
    }
}

