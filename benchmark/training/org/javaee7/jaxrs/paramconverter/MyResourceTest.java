package org.javaee7.jaxrs.paramconverter;


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
 * @author Xavier Coulon
 */
@RunWith(Arquillian.class)
public class MyResourceTest {
    private static WebTarget target;

    @ArquillianResource
    private URL base;

    @Test
    public void testRequestWithQueryParam() {
        String r = MyResourceTest.target.queryParam("search", "foo").request().get(String.class);
        Assert.assertEquals("foo", r);
    }

    @Test
    public void testRequestWithNoQueryParam() {
        String r = MyResourceTest.target.request().get(String.class);
        Assert.assertEquals("bar", r);
    }

    @Test
    public void testRequestWithPathParam() {
        String r = MyResourceTest.target.path("/foo").request().get(String.class);
        Assert.assertEquals("foo", r);
    }
}

