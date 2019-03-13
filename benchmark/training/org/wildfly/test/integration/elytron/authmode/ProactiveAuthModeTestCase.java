package org.wildfly.test.integration.elytron.authmode;


import java.net.URI;
import java.net.URL;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.management.util.CLIWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.test.security.common.AbstractElytronSetupTask;
import org.wildfly.test.security.common.elytron.ConfigurableElement;


@RunAsClient
@RunWith(Arquillian.class)
@org.jboss.as.arquillian.api.ServerSetup(ProactiveAuthModeTestCase.ServerSetup.class)
public class ProactiveAuthModeTestCase {
    private static final String NAME = ProactiveAuthModeTestCase.class.getSimpleName();

    @ArquillianResource
    private URL url;

    @Test
    public void testUnsecuredResourceWithValidCredential() throws Exception {
        HttpGet request = new HttpGet(new URI(((url.toExternalForm()) + "unsecure.jsp")));
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("user1", "password1");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, credentials);
        request.addHeader(new BasicScheme().authenticate(credentials, "UTF-8", false));
        try (CloseableHttpClient httpClient = HttpClients.custom().build()) {
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_OK, statusCode);
                Assert.assertEquals("Unexpected content of HTTP response.", "user1", EntityUtils.toString(response.getEntity()));
            }
        }
    }

    @Test
    public void testSecuredResourceWithValidCredential() throws Exception {
        HttpGet request = new HttpGet(new URI(((url.toExternalForm()) + "secure.jsp")));
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("user1", "password1");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, credentials);
        request.addHeader(new BasicScheme().authenticate(credentials, "UTF-8", false));
        try (CloseableHttpClient httpClient = HttpClients.custom().build()) {
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_OK, statusCode);
                Assert.assertEquals("Unexpected content of HTTP response.", "user1", EntityUtils.toString(response.getEntity()));
            }
        }
    }

    @Test
    public void testSecuredResourceWithInvalidCredential() throws Exception {
        HttpGet request = new HttpGet(new URI(((url.toExternalForm()) + "secure.jsp")));
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("user1", "password2");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, credentials);
        request.addHeader(new BasicScheme().authenticate(credentials, request));
        try (CloseableHttpClient httpClient = HttpClients.custom().build()) {
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_UNAUTHORIZED, statusCode);
            }
        }
    }

    @Test
    public void testUnsecureResourceWithInvalidCredential() throws Exception {
        HttpGet request = new HttpGet(new URI(((url.toExternalForm()) + "unsecure.jsp")));
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("user1", "password2");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, credentials);
        request.addHeader(new BasicScheme().authenticate(credentials, request));
        try (CloseableHttpClient httpClient = HttpClients.custom().build()) {
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_UNAUTHORIZED, statusCode);
            }
        }
    }

    @Test
    public void testUnsecureResourceWithoutCredential() throws Exception {
        HttpGet request = new HttpGet(new URI(((url.toExternalForm()) + "unsecure.jsp")));
        try (CloseableHttpClient httpClient = HttpClients.custom().build()) {
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("Unexpected status code in HTTP response.", HttpStatus.SC_OK, statusCode);
                Assert.assertEquals("Unexpected content of HTTP response.", "", EntityUtils.toString(response.getEntity()));
            }
        }
    }

    static class ServerSetup extends AbstractElytronSetupTask {
        @Override
        protected ConfigurableElement[] getConfigurableElements() {
            ConfigurableElement[] configurableElements = new ConfigurableElement[1];
            configurableElements[0] = new ConfigurableElement() {
                @Override
                public String getName() {
                    return "Enable CONSTRAINT-DRIVEN auth";
                }

                @Override
                public void create(CLIWrapper cli) throws Exception {
                    cli.sendLine("/subsystem=undertow/servlet-container=default:write-attribute(name=proactive-authentication, value=true)");
                }

                @Override
                public void remove(CLIWrapper cli) throws Exception {
                    cli.sendLine("/subsystem=undertow/servlet-container=default:undefine-attribute(name=proactive-authentication)");
                }
            };
            return configurableElements;
        }
    }
}

