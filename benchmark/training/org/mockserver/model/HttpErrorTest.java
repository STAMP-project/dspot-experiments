package org.mockserver.model;


import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpErrorTest {
    @Test
    public void shouldAlwaysCreateNewObject() {
        Assert.assertEquals(new HttpError().error(), HttpError.error());
        Assert.assertNotSame(HttpError.error(), HttpError.error());
    }

    @Test
    public void returnsDelay() {
        Assert.assertEquals(new Delay(TimeUnit.DAYS, 10), new HttpError().withDelay(TimeUnit.DAYS, 10).getDelay());
    }

    @Test
    public void returnsDropConnection() {
        Assert.assertEquals(true, new HttpError().withDropConnection(true).getDropConnection());
    }

    @Test
    public void returnsResponseBytes() {
        Assert.assertArrayEquals("some_bytes".getBytes(StandardCharsets.UTF_8), new HttpError().withResponseBytes("some_bytes".getBytes(StandardCharsets.UTF_8)).getResponseBytes());
    }

    @Test
    public void shouldReturnFormattedRequestInToString() {
        TestCase.assertEquals((((((((((((((("{" + (NEW_LINE)) + "  \"delay\" : {") + (NEW_LINE)) + "    \"timeUnit\" : \"DAYS\",") + (NEW_LINE)) + "    \"value\" : 10") + (NEW_LINE)) + "  },") + (NEW_LINE)) + "  \"dropConnection\" : true,") + (NEW_LINE)) + "  \"responseBytes\" : \"c29tZV9ieXRlcw==\"") + (NEW_LINE)) + "}"), HttpError.error().withDelay(TimeUnit.DAYS, 10).withDropConnection(true).withResponseBytes("some_bytes".getBytes(StandardCharsets.UTF_8)).toString());
    }
}

