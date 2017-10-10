

package com.twilio.jwt.client;


/**
 * Test class for {@link EventStreamScope}.
 */
public class AmplEventStreamScopeTest {
    @org.junit.Test
    public void testGenerate() throws java.io.UnsupportedEncodingException {
        java.util.Map<java.lang.String, java.lang.String> filters = new java.util.HashMap<>();
        filters.put("foo", "bar");
        com.twilio.jwt.client.Scope scope = new com.twilio.jwt.client.EventStreamScope.Builder().filters(filters).build();
        org.junit.Assert.assertEquals("scope:stream:subscribe?path=/2010-04-01/Events&appParams=foo%3Dbar", scope.getPayload());
    }
}

