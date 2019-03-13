package org.javaee7.jaxrs.mapping.exceptions;


import java.net.URL;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.WebTarget;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author argupta
 */
@RunWith(Arquillian.class)
public class MyResourceTest {
    @ArquillianResource
    private URL base;

    private WebTarget target;

    /**
     * Test of getOrder method, of class MyResource.
     */
    @Test
    public void testOddOrder() {
        String response = target.path("1").request().get(String.class);
        Assert.assertEquals("1", response);
    }

    /**
     * Test of getOrder method, of class MyResource.
     */
    @Test
    public void testEvenOrder() {
        try {
            System.out.print(target.path("2").request().get(String.class));
        } catch (ClientErrorException e) {
            Assert.assertEquals(412, e.getResponse().getStatus());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}

