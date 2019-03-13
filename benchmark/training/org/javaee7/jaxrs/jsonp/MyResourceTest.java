package org.javaee7.jaxrs.jsonp;


import MediaType.APPLICATION_JSON;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
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

    WebTarget targetObject;

    WebTarget targetArray;

    @ArquillianResource
    URL base;

    /**
     * Test of echoObject method, of class MyObjectResource.
     */
    @Test
    public void testEchoObject() {
        JsonObject jsonObject = Json.createObjectBuilder().add("apple", "red").add("banana", "yellow").build();
        JsonObject json = targetObject.request().post(Entity.entity(jsonObject, APPLICATION_JSON), JsonObject.class);
        Assert.assertNotNull(json);
        Assert.assertFalse(json.isEmpty());
        Assert.assertTrue(json.containsKey("apple"));
        Assert.assertEquals("red", json.getString("apple"));
        Assert.assertTrue(json.containsKey("banana"));
        Assert.assertEquals("yellow", json.getString("banana"));
    }

    @Test
    public void testEchoArray() {
        JsonArray jsonArray = Json.createArrayBuilder().add(Json.createObjectBuilder().add("apple", "red")).add(Json.createObjectBuilder().add("banana", "yellow")).build();
        JsonArray json = targetArray.request().post(Entity.entity(jsonArray, APPLICATION_JSON), JsonArray.class);
        Assert.assertNotNull(json);
        Assert.assertEquals(2, json.size());
        Assert.assertTrue(json.getJsonObject(0).containsKey("apple"));
        Assert.assertEquals("red", json.getJsonObject(0).getString("apple"));
        Assert.assertTrue(json.getJsonObject(1).containsKey("banana"));
        Assert.assertEquals("yellow", json.getJsonObject(1).getString("banana"));
    }
}

