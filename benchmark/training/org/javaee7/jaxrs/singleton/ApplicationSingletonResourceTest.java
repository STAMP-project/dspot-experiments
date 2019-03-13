package org.javaee7.jaxrs.singleton;


import java.net.URL;
import java.util.StringTokenizer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
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
public class ApplicationSingletonResourceTest {
    @ArquillianResource
    private URL base;

    private Client client;

    private WebTarget target;

    @Test
    @InSequence(1)
    public void testPost() {
        target.request().post(Entity.text("pineapple"));
        target.request().post(Entity.text("mango"));
        target.request().post(Entity.text("kiwi"));
        target.request().post(Entity.text("passion fruit"));
        String list = target.request().get(String.class);
        StringTokenizer tokens = new StringTokenizer(list, ",");
        Assert.assertEquals(4, tokens.countTokens());
    }

    @Test
    @InSequence(2)
    public void testGet() {
        String response = target.path("2").request().get(String.class);
        Assert.assertEquals("kiwi", response);
    }

    @Test
    @InSequence(3)
    public void testDelete() {
        target.path("kiwi").request().delete();
        String list = target.request().get(String.class);
        StringTokenizer tokens = new StringTokenizer(list, ",");
        Assert.assertEquals(3, tokens.countTokens());
    }

    @Test
    @InSequence(4)
    public void testPut() {
        target.request().put(Entity.text("apple"));
        String list = target.request().get(String.class);
        StringTokenizer tokens = new StringTokenizer(list, ",");
        Assert.assertEquals(4, tokens.countTokens());
    }
}

