/**
 * Copyright Payara Services Limited *
 */
package org.javaee7.jacc.permissionsxml;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


/**
 * This tests demonstrates the usage of a <code>permissions.xml</code> file inside
 * a standalone war
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PermissionsXMLServletTest {
    private static final String WEBAPP_SRC = "src/main/webapp";

    @ArquillianResource
    private URL base;

    @Test
    @RunAsClient
    public void test1Startup() throws IOException, URISyntaxException {
        System.out.println(("Testing Servlet from war deployed at " + (new URL(base, "test"))));
        Response response = newClient().target(new URL(base, "test").toURI()).queryParam("tc", "Startup").request(TEXT_PLAIN).get();
        Assert.assertTrue(response.readEntity(String.class).contains("Test:Pass"));
    }

    @Test
    @RunAsClient
    public void test2PermissionsXML() throws IOException, URISyntaxException {
        System.out.println("Running actual permissions.xml test");
        Response response = newClient().target(new URL(base, "test").toURI()).queryParam("tc", "InjectLookup").queryParam("web", "true").request(TEXT_PLAIN).get();
        Assert.assertTrue(response.readEntity(String.class).contains("Test:Pass"));
    }
}

