package com.baeldung.keycloak;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springboot.client.KeycloakSecurityContextClientRequestInterceptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBoot.class)
public class KeycloakConfigurationIntegrationTest {
    @Spy
    private KeycloakSecurityContextClientRequestInterceptor factory;

    private MockHttpServletRequest servletRequest;

    @Mock
    public KeycloakSecurityContext keycloakSecurityContext;

    @Mock
    private KeycloakPrincipal keycloakPrincipal;

    @Test
    public void testGetKeycloakSecurityContext() throws Exception {
        Assert.assertNotNull(keycloakPrincipal.getKeycloakSecurityContext());
    }
}

