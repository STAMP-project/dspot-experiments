package io.hawt.web.filters;


import KeycloakServlet.HAWTIO_KEYCLOAK_CLIENT_CONFIG;
import KeycloakServlet.KEYCLOAK_CLIENT_CONFIG;
import io.hawt.system.ConfigManager;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;


public class ContentSecurityPolicyFilterTest {
    private ContentSecurityPolicyFilter contentSecurityPolicyFilter;

    private FilterConfig filterConfig;

    private ServletContext servletContext;

    private ConfigManager configManager;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private String keycloakConfigFile;

    @Test
    public void shouldSetHeader() throws Exception {
        // given
        contentSecurityPolicyFilter.init(filterConfig);
        // when
        contentSecurityPolicyFilter.addHeaders(request, response);
        // then
        Mockito.verify(response).addHeader("Content-Security-Policy", ("default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; " + ("style-src 'self' 'unsafe-inline'; font-src 'self' data:; img-src 'self' data:; " + "connect-src 'self'; frame-src 'self'")));
    }

    @Test
    public void shouldSetHeaderWithKeycloakServerWhenConfigParameterIsSet() throws Exception {
        // given
        Mockito.when(configManager.get(KEYCLOAK_CLIENT_CONFIG, null)).thenReturn(keycloakConfigFile);
        contentSecurityPolicyFilter.init(filterConfig);
        // when
        contentSecurityPolicyFilter.addHeaders(request, response);
        // then
        Mockito.verify(response).addHeader("Content-Security-Policy", ("default-src 'self'; script-src 'self' localhost:8180 'unsafe-inline' 'unsafe-eval'; " + ("style-src 'self' 'unsafe-inline'; font-src 'self' data:; img-src 'self' data:; " + "connect-src 'self' localhost:8180; frame-src 'self' localhost:8180")));
    }

    @Test
    public void shouldSetHeaderWithKeycloakServerWhenSystemPropertyIsSet() throws Exception {
        // given
        System.setProperty(HAWTIO_KEYCLOAK_CLIENT_CONFIG, keycloakConfigFile);
        contentSecurityPolicyFilter.init(filterConfig);
        // when
        contentSecurityPolicyFilter.addHeaders(request, response);
        // then
        Mockito.verify(response).addHeader("Content-Security-Policy", ("default-src 'self'; script-src 'self' localhost:8180 'unsafe-inline' 'unsafe-eval'; " + ("style-src 'self' 'unsafe-inline'; font-src 'self' data:; img-src 'self' data:; " + "connect-src 'self' localhost:8180; frame-src 'self' localhost:8180")));
    }
}

