package org.mockserver.model;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpObjectCallbackTest {
    @Test
    public void returnsCallbackClass() {
        Assert.assertEquals("some_client_id", new HttpObjectCallback().withClientId("some_client_id").getClientId());
    }

    @Test
    public void shouldReturnFormattedRequestInToString() {
        TestCase.assertEquals((((("{" + (NEW_LINE)) + "  \"clientId\" : \"some_client_id\"") + (NEW_LINE)) + "}"), new HttpObjectCallback().withClientId("some_client_id").toString());
    }
}

