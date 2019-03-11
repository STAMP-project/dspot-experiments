package org.mockserver.model;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpClassCallbackTest {
    @Test
    public void shouldAlwaysCreateNewObject() {
        Assert.assertEquals(new HttpClassCallback().callback(), HttpClassCallback.callback());
        Assert.assertNotSame(HttpClassCallback.callback(), HttpClassCallback.callback());
    }

    @Test
    public void returnsCallbackClass() {
        Assert.assertEquals("some_class", new HttpClassCallback().withCallbackClass("some_class").getCallbackClass());
        Assert.assertEquals("some_class", HttpClassCallback.callback().withCallbackClass("some_class").getCallbackClass());
        Assert.assertEquals("some_class", HttpClassCallback.callback("some_class").getCallbackClass());
    }

    @Test
    public void shouldReturnFormattedRequestInToString() {
        TestCase.assertEquals((((("{" + (NEW_LINE)) + "  \"callbackClass\" : \"some_class\"") + (NEW_LINE)) + "}"), HttpClassCallback.callback().withCallbackClass("some_class").toString());
    }
}

