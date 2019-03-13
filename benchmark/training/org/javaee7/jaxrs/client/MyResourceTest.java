package org.javaee7.jaxrs.client;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import java.io.StringReader;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MyResourceTest {
    private WebTarget target;

    @ArquillianResource
    private URL base;

    /**
     * Test of getList method, of class MyResource.
     */
    @Test
    public void test1PostAndGet() {
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap();
        map.add("name", "Penny");
        map.add("age", "1");
        target.request().post(Entity.form(map));
        map.clear();
        map.add("name", "Leonard");
        map.add("age", "2");
        target.request().post(Entity.form(map));
        map.clear();
        map.add("name", "Sheldon");
        map.add("age", "3");
        target.request().post(Entity.form(map));
        Person[] list = target.request().get(Person[].class);
        Assert.assertEquals(3, list.length);
        Assert.assertEquals("Penny", list[0].getName());
        Assert.assertEquals(1, list[0].getAge());
        Assert.assertEquals("Leonard", list[1].getName());
        Assert.assertEquals(2, list[1].getAge());
        Assert.assertEquals("Sheldon", list[2].getName());
        Assert.assertEquals(3, list[2].getAge());
    }

    /**
     * Test of getPerson method, of class MyResource.
     */
    @Test
    public void test2GetSingle() {
        Person p = target.path("{id}").resolveTemplate("id", "1").request(APPLICATION_XML).get(Person.class);
        Assert.assertEquals("Leonard", p.getName());
        Assert.assertEquals(2, p.getAge());
    }

    /**
     * Test of putToList method, of class MyResource.
     */
    @Test
    public void test3Put() {
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap();
        map.add("name", "Howard");
        map.add("age", "4");
        target.request().post(Entity.form(map));
        Person[] list = target.request().get(Person[].class);
        Assert.assertEquals(4, list.length);
        Assert.assertEquals("Howard", list[3].getName());
        Assert.assertEquals(4, list[3].getAge());
    }

    /**
     * Test of deleteFromList method, of class MyResource.
     */
    @Test
    public void test4Delete() {
        target.path("{name}").resolveTemplate("name", "Howard").request().delete();
        Person[] list = target.request().get(Person[].class);
        Assert.assertEquals(3, list.length);
    }

    @Test
    public void test5ClientSideNegotiation() {
        String json = target.request().accept(APPLICATION_JSON).get(String.class);
        JsonReader reader = Json.createReader(new StringReader(json));
        JsonArray jsonArray = reader.readArray();
        Assert.assertEquals(1, jsonArray.getJsonObject(0).getInt("age"));
        Assert.assertEquals("Penny", jsonArray.getJsonObject(0).getString("name"));
        Assert.assertEquals(2, jsonArray.getJsonObject(1).getInt("age"));
        Assert.assertEquals("Leonard", jsonArray.getJsonObject(1).getString("name"));
        Assert.assertEquals(3, jsonArray.getJsonObject(2).getInt("age"));
        Assert.assertEquals("Sheldon", jsonArray.getJsonObject(2).getString("name"));
    }

    @Test
    public void test6DeleteAll() {
        Person[] list = target.request().get(Person[].class);
        for (Person p : list) {
            target.path("{name}").resolveTemplate("name", p.getName()).request().delete();
        }
        list = target.request().get(Person[].class);
        Assert.assertEquals(0, list.length);
    }
}

