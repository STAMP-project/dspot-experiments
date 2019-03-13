package org.javaee7.jaxrs.readerwriter;


import MyObject.MIME_TYPE;
import java.net.URL;
import javax.ws.rs.client.Client;
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
    Client client;

    WebTarget target;

    @ArquillianResource
    URL base;

    /**
     * Test of postFruit method, of class MyResource.
     */
    @Test
    public void testPostWithCustomMimeType() {
        String fruit = target.request().post(Entity.entity(new MyObject(1), MIME_TYPE), String.class);
        Assert.assertEquals("banana", fruit);
    }

    /**
     * Test of postFruitIndexed method, of class MyResource.
     */
    @Test
    public void testPostSimple() {
        String fruit = target.path("index").request().post(Entity.text("1"), String.class);
        Assert.assertEquals("banana", fruit);
    }
}

