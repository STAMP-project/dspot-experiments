

package com.twilio.jwt.accesstoken;


/**
 * Test class for {@link AccessToken}.
 */
public class AmplAccessTokenTest {
    private static final java.lang.String ACCOUNT_SID = "AC123";

    private static final java.lang.String SIGNING_KEY_SID = "SK123";

    private static final java.lang.String SECRET = "secret";

    private void validateToken(io.jsonwebtoken.Claims claims) {
        org.junit.Assert.assertEquals(com.twilio.jwt.accesstoken.AmplAccessTokenTest.SIGNING_KEY_SID, claims.getIssuer());
        org.junit.Assert.assertEquals(com.twilio.jwt.accesstoken.AmplAccessTokenTest.ACCOUNT_SID, claims.getSubject());
        org.junit.Assert.assertNotNull(claims.getExpiration());
        org.junit.Assert.assertNotNull(claims.getId());
        org.junit.Assert.assertNotNull(claims.get("grants"));
        org.junit.Assert.assertTrue(claims.getId().startsWith(((claims.getIssuer()) + "-")));
        org.junit.Assert.assertTrue(((claims.getExpiration().getTime()) > (new java.util.Date().getTime())));
    }

    @org.junit.Test
    public void testEmptyToken() {
        com.twilio.jwt.Jwt token = new com.twilio.jwt.accesstoken.AccessToken.Builder(com.twilio.jwt.accesstoken.AmplAccessTokenTest.ACCOUNT_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SIGNING_KEY_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET.getBytes()).parseClaimsJws(token.toJwt()).getBody();
        validateToken(claims);
    }

    @org.junit.Test
    public void testOptionalValues() {
        com.twilio.jwt.Jwt token = new com.twilio.jwt.accesstoken.AccessToken.Builder(com.twilio.jwt.accesstoken.AmplAccessTokenTest.ACCOUNT_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SIGNING_KEY_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET).identity(com.twilio.jwt.accesstoken.AmplAccessTokenTest.ACCOUNT_SID).nbf(new java.util.Date()).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET.getBytes()).parseClaimsJws(token.toJwt()).getBody();
        validateToken(claims);
        org.junit.Assert.assertTrue(((claims.getNotBefore().getTime()) <= (new java.util.Date().getTime())));
    }

    @org.junit.Test
    public void testConversationGrant() {
        com.twilio.jwt.accesstoken.ConversationsGrant cg = new com.twilio.jwt.accesstoken.ConversationsGrant().setConfigurationProfileSid("CP123");
        com.twilio.jwt.Jwt token = new com.twilio.jwt.accesstoken.AccessToken.Builder(com.twilio.jwt.accesstoken.AmplAccessTokenTest.ACCOUNT_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SIGNING_KEY_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET).grant(cg).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET.getBytes()).parseClaimsJws(token.toJwt()).getBody();
        validateToken(claims);
        java.util.Map<java.lang.String, java.lang.Object> decodedGrants = ((java.util.Map<java.lang.String, java.lang.Object>) (claims.get("grants")));
        org.junit.Assert.assertEquals(1, decodedGrants.size());
        java.util.Map<java.lang.String, java.lang.Object> grant = ((java.util.Map<java.lang.String, java.lang.Object>) (decodedGrants.get("rtc")));
        org.junit.Assert.assertEquals("CP123", grant.get("configuration_profile_sid"));
    }

    @org.junit.Test
    public void testVideoGrant() {
        com.twilio.jwt.accesstoken.VideoGrant cg = new com.twilio.jwt.accesstoken.VideoGrant().setConfigurationProfileSid("CP123");
        com.twilio.jwt.Jwt token = new com.twilio.jwt.accesstoken.AccessToken.Builder(com.twilio.jwt.accesstoken.AmplAccessTokenTest.ACCOUNT_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SIGNING_KEY_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET).grant(cg).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET.getBytes()).parseClaimsJws(token.toJwt()).getBody();
        validateToken(claims);
        java.util.Map<java.lang.String, java.lang.Object> decodedGrants = ((java.util.Map<java.lang.String, java.lang.Object>) (claims.get("grants")));
        org.junit.Assert.assertEquals(1, decodedGrants.size());
        java.util.Map<java.lang.String, java.lang.Object> grant = ((java.util.Map<java.lang.String, java.lang.Object>) (decodedGrants.get("video")));
        org.junit.Assert.assertEquals("CP123", grant.get("configuration_profile_sid"));
    }

    @org.junit.Test
    public void testIpMessagingGrant() {
        com.twilio.jwt.accesstoken.IpMessagingGrant ipg = new com.twilio.jwt.accesstoken.IpMessagingGrant().setDeploymentRoleSid("RL123").setEndpointId("foobar").setPushCredentialSid("CR123").setServiceSid("IS123");
        com.twilio.jwt.Jwt token = new com.twilio.jwt.accesstoken.AccessToken.Builder(com.twilio.jwt.accesstoken.AmplAccessTokenTest.ACCOUNT_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SIGNING_KEY_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET).grant(ipg).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET.getBytes()).parseClaimsJws(token.toJwt()).getBody();
        validateToken(claims);
        java.util.Map<java.lang.String, java.lang.Object> decodedGrants = ((java.util.Map<java.lang.String, java.lang.Object>) (claims.get("grants")));
        org.junit.Assert.assertEquals(1, decodedGrants.size());
        java.util.Map<java.lang.String, java.lang.Object> grant = ((java.util.Map<java.lang.String, java.lang.Object>) (decodedGrants.get("ip_messaging")));
        org.junit.Assert.assertEquals("RL123", grant.get("deployment_role_sid"));
        org.junit.Assert.assertEquals("foobar", grant.get("endpoint_id"));
        org.junit.Assert.assertEquals("CR123", grant.get("push_credential_sid"));
        org.junit.Assert.assertEquals("IS123", grant.get("service_sid"));
    }

    @org.junit.Test
    public void testSyncGrant() {
        com.twilio.jwt.accesstoken.SyncGrant sg = new com.twilio.jwt.accesstoken.SyncGrant().setEndpointId("foobar").setServiceSid("IS123");
        com.twilio.jwt.Jwt token = new com.twilio.jwt.accesstoken.AccessToken.Builder(com.twilio.jwt.accesstoken.AmplAccessTokenTest.ACCOUNT_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SIGNING_KEY_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET).grant(sg).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET.getBytes()).parseClaimsJws(token.toJwt()).getBody();
        validateToken(claims);
        java.util.Map<java.lang.String, java.lang.Object> decodedGrants = ((java.util.Map<java.lang.String, java.lang.Object>) (claims.get("grants")));
        org.junit.Assert.assertEquals(1, decodedGrants.size());
        java.util.Map<java.lang.String, java.lang.Object> grant = ((java.util.Map<java.lang.String, java.lang.Object>) (decodedGrants.get("data_sync")));
        org.junit.Assert.assertEquals("foobar", grant.get("endpoint_id"));
        org.junit.Assert.assertEquals("IS123", grant.get("service_sid"));
    }

    @org.junit.Test
    public void testTaskRouterGrant() {
        com.twilio.jwt.accesstoken.TaskRouterGrant trg = new com.twilio.jwt.accesstoken.TaskRouterGrant().setWorkspaceSid("WS123").setWorkerSid("WK123").setRole("worker");
        com.twilio.jwt.Jwt token = new com.twilio.jwt.accesstoken.AccessToken.Builder(com.twilio.jwt.accesstoken.AmplAccessTokenTest.ACCOUNT_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SIGNING_KEY_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET).grant(trg).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET.getBytes()).parseClaimsJws(token.toJwt()).getBody();
        validateToken(claims);
        java.util.Map<java.lang.String, java.lang.Object> decodedGrants = ((java.util.Map<java.lang.String, java.lang.Object>) (claims.get("grants")));
        org.junit.Assert.assertEquals(1, decodedGrants.size());
        java.util.Map<java.lang.String, java.lang.Object> grant = ((java.util.Map<java.lang.String, java.lang.Object>) (decodedGrants.get("task_router")));
        org.junit.Assert.assertEquals("WS123", grant.get("workspace_sid"));
        org.junit.Assert.assertEquals("WK123", grant.get("worker_sid"));
        org.junit.Assert.assertEquals("worker", grant.get("role"));
    }

    @org.junit.Test
    public void testCompleteToken() {
        com.twilio.jwt.accesstoken.IpMessagingGrant ipg = new com.twilio.jwt.accesstoken.IpMessagingGrant().setDeploymentRoleSid("RL123").setEndpointId("foobar").setPushCredentialSid("CR123").setServiceSid("IS123");
        com.twilio.jwt.accesstoken.ConversationsGrant cg = new com.twilio.jwt.accesstoken.ConversationsGrant().setConfigurationProfileSid("CP123");
        com.twilio.jwt.Jwt token = new com.twilio.jwt.accesstoken.AccessToken.Builder(com.twilio.jwt.accesstoken.AmplAccessTokenTest.ACCOUNT_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SIGNING_KEY_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET).grant(ipg).grant(cg).nbf(new java.util.Date()).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET.getBytes()).parseClaimsJws(token.toJwt()).getBody();
        validateToken(claims);
        org.junit.Assert.assertTrue(((claims.getNotBefore().getTime()) <= (new java.util.Date().getTime())));
        java.util.Map<java.lang.String, java.lang.Object> decodedGrants = ((java.util.Map<java.lang.String, java.lang.Object>) (claims.get("grants")));
        org.junit.Assert.assertEquals(2, decodedGrants.size());
        java.util.Map<java.lang.String, java.lang.Object> ipmGrant = ((java.util.Map<java.lang.String, java.lang.Object>) (decodedGrants.get("ip_messaging")));
        org.junit.Assert.assertEquals("RL123", ipmGrant.get("deployment_role_sid"));
        org.junit.Assert.assertEquals("foobar", ipmGrant.get("endpoint_id"));
        org.junit.Assert.assertEquals("CR123", ipmGrant.get("push_credential_sid"));
        org.junit.Assert.assertEquals("IS123", ipmGrant.get("service_sid"));
        java.util.Map<java.lang.String, java.lang.Object> cGrant = ((java.util.Map<java.lang.String, java.lang.Object>) (decodedGrants.get("rtc")));
        org.junit.Assert.assertEquals("CP123", cGrant.get("configuration_profile_sid"));
    }

    @org.junit.Test
    public void testVoiceToken() {
        java.util.Map<java.lang.String, java.lang.Object> params = new java.util.HashMap<>();
        params.put("foo", "bar");
        com.twilio.jwt.accesstoken.VoiceGrant pvg = new com.twilio.jwt.accesstoken.VoiceGrant().setOutgoingApplication("AP123", params);
        com.twilio.jwt.Jwt token = new com.twilio.jwt.accesstoken.AccessToken.Builder(com.twilio.jwt.accesstoken.AmplAccessTokenTest.ACCOUNT_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SIGNING_KEY_SID, com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET).grant(pvg).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.accesstoken.AmplAccessTokenTest.SECRET.getBytes()).parseClaimsJws(token.toJwt()).getBody();
        validateToken(claims);
        java.util.Map<java.lang.String, java.lang.Object> decodedGrants = ((java.util.Map<java.lang.String, java.lang.Object>) (claims.get("grants")));
        org.junit.Assert.assertEquals(1, decodedGrants.size());
        java.util.Map<java.lang.String, java.lang.Object> pvgGrant = ((java.util.Map<java.lang.String, java.lang.Object>) (decodedGrants.get("voice")));
        java.util.Map<java.lang.String, java.lang.Object> outgoing = ((java.util.Map<java.lang.String, java.lang.Object>) (pvgGrant.get("outgoing")));
        java.util.Map<java.lang.String, java.lang.Object> outgoingParams = ((java.util.Map<java.lang.String, java.lang.Object>) (outgoing.get("params")));
        org.junit.Assert.assertEquals("AP123", outgoing.get("application_sid"));
        org.junit.Assert.assertEquals("bar", outgoingParams.get("foo"));
    }
}

