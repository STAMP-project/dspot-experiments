

package com.twilio.jwt.client;


/**
 * Test class for {@link ClientCapability}.
 */
public class AmplClientCapabilityTest {
    private static final java.lang.String ACCOUNT_SID = "AC123";

    private static final java.lang.String SECRET = "secret";

    @org.junit.Test
    public void testEmptyToken() {
        com.twilio.jwt.Jwt jwt = new com.twilio.jwt.client.ClientCapability.Builder(com.twilio.jwt.client.AmplClientCapabilityTest.ACCOUNT_SID, com.twilio.jwt.client.AmplClientCapabilityTest.SECRET).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.client.AmplClientCapabilityTest.SECRET.getBytes()).parseClaimsJws(jwt.toJwt()).getBody();
        org.junit.Assert.assertEquals(com.twilio.jwt.client.AmplClientCapabilityTest.ACCOUNT_SID, claims.getIssuer());
        org.junit.Assert.assertTrue(((claims.getExpiration().getTime()) > (new java.util.Date().getTime())));
    }

    @org.junit.Test
    public void testToken() {
        java.util.List<com.twilio.jwt.client.Scope> scopes = com.google.common.collect.Lists.newArrayList(new com.twilio.jwt.client.IncomingClientScope("incomingClient"), new com.twilio.jwt.client.EventStreamScope.Builder().build(), new com.twilio.jwt.client.OutgoingClientScope.Builder("AP123").build());
        com.twilio.jwt.Jwt jwt = new com.twilio.jwt.client.ClientCapability.Builder(com.twilio.jwt.client.AmplClientCapabilityTest.ACCOUNT_SID, com.twilio.jwt.client.AmplClientCapabilityTest.SECRET).scopes(scopes).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.client.AmplClientCapabilityTest.SECRET.getBytes()).parseClaimsJws(jwt.toJwt()).getBody();
        org.junit.Assert.assertEquals(com.twilio.jwt.client.AmplClientCapabilityTest.ACCOUNT_SID, claims.getIssuer());
        org.junit.Assert.assertTrue(((claims.getExpiration().getTime()) > (new java.util.Date().getTime())));
        org.junit.Assert.assertEquals(("scope:client:incoming?clientName=incomingClient " + ("scope:stream:subscribe?path=/2010-04-01/Events " + "scope:client:outgoing?appSid=AP123")), claims.get("scope"));
    }

    /* amplification of com.twilio.jwt.client.ClientCapabilityTest#testEmptyToken */
    @org.junit.Test(timeout = 10000)
    public void testEmptyToken_cf5() {
        com.twilio.jwt.Jwt jwt = new com.twilio.jwt.client.ClientCapability.Builder(com.twilio.jwt.client.AmplClientCapabilityTest.ACCOUNT_SID, com.twilio.jwt.client.AmplClientCapabilityTest.SECRET).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.client.AmplClientCapabilityTest.SECRET.getBytes()).parseClaimsJws(jwt.toJwt()).getBody();
        org.junit.Assert.assertEquals(com.twilio.jwt.client.AmplClientCapabilityTest.ACCOUNT_SID, claims.getIssuer());
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.lang.Object> o_testEmptyToken_cf5__13 = // StatementAdderMethod cloned existing statement
jwt.getHeaders();
        // AssertGenerator add assertion
        java.util.HashMap map_1201689713 = new java.util.HashMap<Object, Object>();
	map_1201689713.put("alg", "HS256");
	org.junit.Assert.assertEquals(map_1201689713, o_testEmptyToken_cf5__13);;
        org.junit.Assert.assertTrue(((claims.getExpiration().getTime()) > (new java.util.Date().getTime())));
    }

    /* amplification of com.twilio.jwt.client.ClientCapabilityTest#testToken */
    @org.junit.Test(timeout = 10000)
    public void testToken_cf82() {
        java.util.List<com.twilio.jwt.client.Scope> scopes = com.google.common.collect.Lists.newArrayList(new com.twilio.jwt.client.IncomingClientScope("incomingClient"), new com.twilio.jwt.client.EventStreamScope.Builder().build(), new com.twilio.jwt.client.OutgoingClientScope.Builder("AP123").build());
        com.twilio.jwt.Jwt jwt = new com.twilio.jwt.client.ClientCapability.Builder(com.twilio.jwt.client.AmplClientCapabilityTest.ACCOUNT_SID, com.twilio.jwt.client.AmplClientCapabilityTest.SECRET).scopes(scopes).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.client.AmplClientCapabilityTest.SECRET.getBytes()).parseClaimsJws(jwt.toJwt()).getBody();
        org.junit.Assert.assertEquals(com.twilio.jwt.client.AmplClientCapabilityTest.ACCOUNT_SID, claims.getIssuer());
        org.junit.Assert.assertTrue(((claims.getExpiration().getTime()) > (new java.util.Date().getTime())));
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.lang.Object> o_testToken_cf82__26 = // StatementAdderMethod cloned existing statement
jwt.getHeaders();
        // AssertGenerator add assertion
        java.util.HashMap map_1722633957 = new java.util.HashMap<Object, Object>();
	map_1722633957.put("alg", "HS256");
	org.junit.Assert.assertEquals(map_1722633957, o_testToken_cf82__26);;
        org.junit.Assert.assertEquals(("scope:client:incoming?clientName=incomingClient " + ("scope:stream:subscribe?path=/2010-04-01/Events " + "scope:client:outgoing?appSid=AP123")), claims.get("scope"));
    }
}

