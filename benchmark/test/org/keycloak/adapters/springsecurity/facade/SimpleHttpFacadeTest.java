package org.keycloak.adapters.springsecurity.facade;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class SimpleHttpFacadeTest {
    @Test
    public void shouldRetrieveKeycloakSecurityContext() {
        SimpleHttpFacade facade = new SimpleHttpFacade(new MockHttpServletRequest(), new MockHttpServletResponse());
        Assert.assertNotNull(facade.getSecurityContext());
    }
}

