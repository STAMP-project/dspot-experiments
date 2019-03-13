package org.mockserver.model;


import HttpForward.Scheme.HTTPS;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpForwardTest {
    @Test
    public void shouldAlwaysCreateNewObject() {
        Assert.assertEquals(new HttpForward().forward(), HttpForward.forward());
        Assert.assertNotSame(HttpForward.forward(), HttpForward.forward());
    }

    @Test
    public void returnsPort() {
        Assert.assertEquals(new Integer(9090), new HttpForward().withPort(9090).getPort());
    }

    @Test
    public void returnsHost() {
        Assert.assertEquals("some_host", new HttpForward().withHost("some_host").getHost());
    }

    @Test
    public void returnsDelay() {
        Assert.assertEquals(new Delay(TimeUnit.HOURS, 1), new HttpForward().withDelay(new Delay(TimeUnit.HOURS, 1)).getDelay());
        Assert.assertEquals(new Delay(TimeUnit.HOURS, 1), new HttpForward().withDelay(TimeUnit.HOURS, 1).getDelay());
    }

    @Test
    public void returnsScheme() {
        Assert.assertEquals(HTTPS, new HttpForward().withScheme(HTTPS).getScheme());
    }

    @Test
    public void shouldReturnFormattedRequestInToString() {
        TestCase.assertEquals((((((((((((((((("{" + (NEW_LINE)) + "  \"delay\" : {") + (NEW_LINE)) + "    \"timeUnit\" : \"HOURS\",") + (NEW_LINE)) + "    \"value\" : 1") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"host\" : \"some_host\",") + (NEW_LINE)) + "  \"port\" : 9090,") + (NEW_LINE)) + "  \"scheme\" : \"HTTPS\"") + (NEW_LINE)) + "}"), HttpForward.forward().withHost("some_host").withPort(9090).withScheme(HTTPS).withDelay(TimeUnit.HOURS, 1).toString());
    }
}

