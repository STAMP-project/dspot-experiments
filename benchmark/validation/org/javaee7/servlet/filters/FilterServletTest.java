package org.javaee7.servlet.filters;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class FilterServletTest {
    @ArquillianResource
    private URL base;

    @Test
    @RunAsClient
    public void filtered_servlet_should_return_enhanced_foobar_text() throws MalformedURLException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(URI.create(new URL(base, "filtered/TestServlet").toExternalForm()));
        Response response = target.request().get();
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.is(CoreMatchers.equalTo("foo--bar--bar")));
    }

    @Test
    @RunAsClient
    public void standard_servlet_should_return_simple_text() throws MalformedURLException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(URI.create(new URL(base, "TestServlet").toExternalForm()));
        Response response = target.request().get();
        Assert.assertThat(response.readEntity(String.class), CoreMatchers.is(CoreMatchers.equalTo("bar")));
    }
}

