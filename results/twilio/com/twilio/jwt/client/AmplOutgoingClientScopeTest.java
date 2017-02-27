

package com.twilio.jwt.client;


/**
 * Test class for {@link OutgoingClientScope}.
 */
public class AmplOutgoingClientScopeTest {
    @org.junit.Test
    public void testGenerate() throws java.io.UnsupportedEncodingException {
        java.util.Map<java.lang.String, java.lang.String> params = new java.util.HashMap<>();
        params.put("foo", "bar");
        com.twilio.jwt.client.Scope scope = new com.twilio.jwt.client.OutgoingClientScope.Builder("AP123").clientName("CL123").params(params).build();
        org.junit.Assert.assertEquals("scope:client:outgoing?appSid=AP123&clientName=CL123&appParams=foo%3Dbar", scope.getPayload());
    }
}

