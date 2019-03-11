package org.apereo.cas.oidc.web.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.oidc.web.controllers.discovery.OidcWellKnownEndpointController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 * This is {@link OidcWellKnownEndpointControllerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class OidcWellKnownEndpointControllerTests extends AbstractOidcTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Autowired
    @Qualifier("oidcWellKnownController")
    protected OidcWellKnownEndpointController oidcWellKnownController;

    @Test
    public void verifyOperation() throws Exception {
        val res1 = OidcWellKnownEndpointControllerTests.MAPPER.writer().writeValueAsString(oidcWellKnownController.getWellKnownDiscoveryConfiguration());
        Assertions.assertNotNull(res1);
        val res2 = OidcWellKnownEndpointControllerTests.MAPPER.writer().writeValueAsString(oidcWellKnownController.getWellKnownOpenIdDiscoveryConfiguration());
        Assertions.assertNotNull(res2);
    }
}

