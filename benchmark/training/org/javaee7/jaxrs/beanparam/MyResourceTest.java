package org.javaee7.jaxrs.beanparam;


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
    public void testRequestWithAllParams() {
        WebTarget t = MyResourceTest.target.path("/123").path("/abc").queryParam("param1", "foo").queryParam("param2", "bar").queryParam("param3", "baz");
        String r = t.request().get(String.class);
        Assert.assertEquals("/123/abc?param1=foo&param2=bar&param3=baz", r);
    }
}

