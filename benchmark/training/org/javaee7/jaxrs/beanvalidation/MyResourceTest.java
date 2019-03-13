package org.javaee7.jaxrs.beanvalidation;


import java.net.URL;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
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
    private static WebTarget target;

    @ArquillianResource
    private URL base;

    @Test
    public void testInvalidRequest() {
        try {
            MyResourceTest.target.request().post(Entity.text("fo"), String.class);
            Assert.fail("Request must fail with payload < 3");
        } catch (BadRequestException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testValidRequest() {
        String r = MyResourceTest.target.request().post(Entity.text("foo"), String.class);
        Assert.assertEquals("foo", r);
    }

    @Test
    public void testValidPostRequest() {
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap();
        map.add("name", "Penny");
        map.add("age", "1");
        MyResourceTest.target.request().post(Entity.form(map));
        map.clear();
        map.add("name", "Leonard");
        map.add("age", "2");
        MyResourceTest.target.request().post(Entity.form(map));
    }

    @Test
    public void testInvalidPostRequest() {
        try {
            MultivaluedHashMap<String, String> map = new MultivaluedHashMap();
            map.add("name", null);
            map.add("age", "1");
            MyResourceTest.target.request().post(Entity.form(map));
        } catch (BadRequestException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testInvalidPostRequestLesserAge() {
        try {
            MultivaluedHashMap<String, String> map = new MultivaluedHashMap();
            map.add("name", "Penny");
            map.add("age", "0");
            MyResourceTest.target.request().post(Entity.form(map));
        } catch (BadRequestException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testInvalidPostRequestGreaterAge() {
        try {
            MultivaluedHashMap<String, String> map = new MultivaluedHashMap();
            map.add("name", "Penny");
            map.add("age", "11");
            MyResourceTest.target.request().post(Entity.form(map));
        } catch (BadRequestException e) {
            Assert.assertNotNull(e);
        }
    }
}

