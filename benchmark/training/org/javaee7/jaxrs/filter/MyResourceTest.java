package org.javaee7.jaxrs.filter;


import java.net.URL;
import javax.ws.rs.client.Entity;
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
     * Test of getFruit method, of class MyResource.
     */
    @Test
    public void testGetFruit() {
        String result = target.request().get(String.class);
        Assert.assertEquals("Likely that the headers set in the filter were not available in endpoint", "apple", result);
    }

    /**
     * Test of getFruit2 method, of class MyResource.
     */
    @Test
    public void testPostFruit() {
        String result = target.request().post(Entity.text("apple"), String.class);
        Assert.assertEquals("Likely that the headers set in the filter were not available in endpoint", "apple", result);
    }
}

