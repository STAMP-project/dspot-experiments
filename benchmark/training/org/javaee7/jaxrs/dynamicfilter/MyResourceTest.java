package org.javaee7.jaxrs.dynamicfilter;


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
    URL base;

    @Test
    public void testGet() {
        String response = target.request().get(String.class);
        Assert.assertEquals("apple", response);
    }

    @Test
    public void testPost() {
        String response = target.request().post(Entity.text("apple"), String.class);
        Assert.assertEquals("apple", response);
    }
}

