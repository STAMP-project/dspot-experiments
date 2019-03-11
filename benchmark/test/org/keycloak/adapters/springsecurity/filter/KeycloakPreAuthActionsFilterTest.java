package org.keycloak.adapters.springsecurity.filter;


import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.NodesRegistrationManagement;
import org.keycloak.adapters.PreAuthActionsHandler;
import org.keycloak.adapters.spi.UserSessionManagement;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter.PreAuthActionsHandlerFactory;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;


public class KeycloakPreAuthActionsFilterTest {
    private KeycloakPreAuthActionsFilter filter;

    @Mock
    private NodesRegistrationManagement nodesRegistrationManagement;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private AdapterDeploymentContext deploymentContext;

    @Mock
    private PreAuthActionsHandlerFactory preAuthActionsHandlerFactory;

    @Mock
    private UserSessionManagement userSessionManagement;

    @Mock
    private PreAuthActionsHandler preAuthActionsHandler;

    @Mock
    private KeycloakDeployment deployment;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Test
    public void shouldIgnoreChainWhenPreAuthActionHandlerHandled() throws Exception {
        Mockito.when(preAuthActionsHandler.handleRequest()).thenReturn(true);
        filter.doFilter(request, response, chain);
        Mockito.verifyZeroInteractions(chain);
        Mockito.verify(nodesRegistrationManagement).tryRegister(deployment);
    }

    @Test
    public void shouldContinueChainWhenPreAuthActionHandlerDidNotHandle() throws Exception {
        Mockito.when(preAuthActionsHandler.handleRequest()).thenReturn(false);
        filter.doFilter(request, response, chain);
        Mockito.verify(chain).doFilter(request, response);
        Mockito.verify(nodesRegistrationManagement).tryRegister(deployment);
    }
}

