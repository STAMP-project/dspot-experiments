package org.javaee7.jaxrs.asyncserver;


import java.net.URL;
import javax.ws.rs.client.WebTarget;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class MyResourceTest {
    private WebTarget target;

    @ArquillianResource
    private URL base;

    /**
     * Test of getList method, of class MyResource.
     */
    @Test
    public void testGetList() {
        String result = target.request().get(String.class);
        Assert.assertEquals("apple", result);
    }
}

