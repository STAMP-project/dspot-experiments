

package com.twilio.jwt.client;


/**
 * Test class for {@link IncomingClientScope}.
 */
public class AmplIncomingClientScopeTest {
    @org.junit.Test
    public void testGenerate() throws java.io.UnsupportedEncodingException {
        com.twilio.jwt.client.Scope scope = new com.twilio.jwt.client.IncomingClientScope("foobar");
        org.junit.Assert.assertEquals("scope:client:incoming?clientName=foobar", scope.getPayload());
    }
}

