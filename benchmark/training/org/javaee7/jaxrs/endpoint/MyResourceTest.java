package org.javaee7.jaxrs.endpoint;


import java.net.URL;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
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
    private static WebTarget target;

    @ArquillianResource
    private URL base;

    /**
     * Test of POST method, of class MyResource.
     */
    @Test
    public void test1Post() {
        MyResourceTest.target.request().post(Entity.text("apple"));
        String r = MyResourceTest.target.request().get(String.class);
        Assert.assertEquals("[apple]", r);
    }

    /**
     * Test of PUT method, of class MyResource.
     */
    @Test
    public void test2Put() {
        MyResourceTest.target.request().put(Entity.text("banana"));
        String r = MyResourceTest.target.request().get(String.class);
        Assert.assertEquals("[apple, banana]", r);
    }

    /**
     * Test of GET method, of class MyResource.
     */
    @Test
    public void test3GetAll() {
        String r = MyResourceTest.target.request().get(String.class);
        Assert.assertEquals("[apple, banana]", r);
    }

    /**
     * Test of GET method, of class MyResource.
     */
    @Test
    public void test4GetOne() {
        String r = MyResourceTest.target.path("apple").request().get(String.class);
        Assert.assertEquals("apple", r);
    }

    /**
     * Test of GET method, of class MyResource.
     */
    @Test
    public void test5Delete() {
        MyResourceTest.target.path("banana").request().delete();
        String r = MyResourceTest.target.request().get(String.class);
        Assert.assertEquals("[apple]", r);
        MyResourceTest.target.path("apple").request().delete();
        r = MyResourceTest.target.request().get(String.class);
        Assert.assertEquals("[]", r);
    }
}

