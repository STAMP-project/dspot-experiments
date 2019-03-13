package org.javaee7.servlet.resource.packaging;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Jakub Marchwicki
 */
@RunWith(Arquillian.class)
public class ResourcePackagingTest {
    @ArquillianResource
    private URL base;

    @Test
    public void getMyResourceJarStyles() throws MalformedURLException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(URI.create(new URL(base, "styles.css").toExternalForm()));
        Response response = target.request().get();
        Assert.assertThat(response.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        String style = response.readEntity(String.class);
        Assert.assertThat(style, CoreMatchers.startsWith("body {"));
    }
}

