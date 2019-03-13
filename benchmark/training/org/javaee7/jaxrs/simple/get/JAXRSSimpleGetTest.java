/**
 * Copyright Payara Services Limited *
 */
package org.javaee7.jaxrs.simple.get;


import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This sample tests one of the simplest possible JAX-RS resources; one that only
 * has a single method responding to a GET request and returning a (small) string.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class JAXRSSimpleGetTest {
    @ArquillianResource
    private URL base;

    @Test
    @RunAsClient
    public void testGet() throws IOException {
        String response = newClient().target(URI.create(new URL(base, "rest/resource/hi").toExternalForm())).request(TEXT_PLAIN).get(String.class);
        System.out.println("-------------------------------------------------------------------------");
        System.out.println(("Response: \n\n" + response));
        System.out.println("-------------------------------------------------------------------------");
        Assert.assertTrue(response.contains("hi"));
    }
}

