

package com.twilio.jwt.taskrouter;


/**
 * Test class for {@link TaskRouterCapability}.
 */
public class AmplTaskRouterCapabilityTest {
    private static final java.lang.String ACCOUNT_SID = "AC123";

    private static final java.lang.String AUTH_TOKEN = "secret";

    private static final java.lang.String WORKSPACE_SID = "WS123";

    private static final java.lang.String WORKER_SID = "WK123";

    @org.junit.Test
    public void testToken() {
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.workspaces()).build());
        final com.twilio.jwt.Jwt jwt = new com.twilio.jwt.taskrouter.TaskRouterCapability.Builder(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.AUTH_TOKEN, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID).policies(policies).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.AUTH_TOKEN.getBytes()).parseClaimsJws(jwt.toJwt()).getBody();
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, claims.get("workspace_sid"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID, claims.get("channel"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, claims.get("account_sid"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, claims.getIssuer());
        org.junit.Assert.assertTrue(((claims.getExpiration().getTime()) > (new java.util.Date().getTime())));
    }

    @org.junit.Test
    public void testWorkerToken() {
        final java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.twilio.jwt.taskrouter.PolicyUtils.defaultWorkerPolicies(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID);
        final java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> activityUpdateFilter = new java.util.HashMap<>();
        activityUpdateFilter.put("ActivitySid", com.twilio.jwt.taskrouter.FilterRequirement.REQUIRED);
        final com.twilio.jwt.taskrouter.Policy allowActivityUpdates = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.worker(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID)).method(com.twilio.http.HttpMethod.POST).postFilter(activityUpdateFilter).build();
        final com.twilio.jwt.taskrouter.Policy allowTasksUpdate = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allTasks(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID)).method(com.twilio.http.HttpMethod.POST).build();
        final com.twilio.jwt.taskrouter.Policy allowReservationUpdate = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allReservations(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID)).method(com.twilio.http.HttpMethod.POST).build();
        policies.add(allowActivityUpdates);
        policies.add(allowTasksUpdate);
        policies.add(allowReservationUpdate);
        final com.twilio.jwt.Jwt jwt = new com.twilio.jwt.taskrouter.TaskRouterCapability.Builder(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.AUTH_TOKEN, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID).policies(policies).build();
        final io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.AUTH_TOKEN.getBytes()).parseClaimsJws(jwt.toJwt()).getBody();
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, claims.get("workspace_sid"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID, claims.get("channel"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, claims.get("account_sid"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, claims.getIssuer());
        org.junit.Assert.assertTrue(((claims.getExpiration().getTime()) > (new java.util.Date().getTime())));
        final java.util.List<com.twilio.jwt.taskrouter.Policy> claimedPolicies = ((java.util.List<com.twilio.jwt.taskrouter.Policy>) (claims.get("policies")));
        final int connectionPolicies = 2;
        org.junit.Assert.assertEquals(((policies.size()) + connectionPolicies), claimedPolicies.size());
    }

    /* amplification of com.twilio.jwt.taskrouter.TaskRouterCapabilityTest#testToken */
    @org.junit.Test(timeout = 10000)
    public void testToken_cf5() {
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.workspaces()).build());
        final com.twilio.jwt.Jwt jwt = new com.twilio.jwt.taskrouter.TaskRouterCapability.Builder(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.AUTH_TOKEN, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID).policies(policies).build();
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.AUTH_TOKEN.getBytes()).parseClaimsJws(jwt.toJwt()).getBody();
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, claims.get("workspace_sid"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID, claims.get("channel"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, claims.get("account_sid"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, claims.getIssuer());
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.lang.Object> o_testToken_cf5__26 = // StatementAdderMethod cloned existing statement
jwt.getHeaders();
        // AssertGenerator add assertion
        java.util.HashMap map_1238699320 = new java.util.HashMap<Object, Object>();
	map_1238699320.put("alg", "HS256");
	org.junit.Assert.assertEquals(map_1238699320, o_testToken_cf5__26);;
        org.junit.Assert.assertTrue(((claims.getExpiration().getTime()) > (new java.util.Date().getTime())));
    }

    /* amplification of com.twilio.jwt.taskrouter.TaskRouterCapabilityTest#testWorkerToken */
    @org.junit.Test(timeout = 10000)
    public void testWorkerToken_cf38() {
        final java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.twilio.jwt.taskrouter.PolicyUtils.defaultWorkerPolicies(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID);
        final java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> activityUpdateFilter = new java.util.HashMap<>();
        activityUpdateFilter.put("ActivitySid", com.twilio.jwt.taskrouter.FilterRequirement.REQUIRED);
        final com.twilio.jwt.taskrouter.Policy allowActivityUpdates = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.worker(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID)).method(com.twilio.http.HttpMethod.POST).postFilter(activityUpdateFilter).build();
        final com.twilio.jwt.taskrouter.Policy allowTasksUpdate = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allTasks(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID)).method(com.twilio.http.HttpMethod.POST).build();
        final com.twilio.jwt.taskrouter.Policy allowReservationUpdate = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allReservations(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID)).method(com.twilio.http.HttpMethod.POST).build();
        policies.add(allowActivityUpdates);
        policies.add(allowTasksUpdate);
        policies.add(allowReservationUpdate);
        final com.twilio.jwt.Jwt jwt = new com.twilio.jwt.taskrouter.TaskRouterCapability.Builder(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.AUTH_TOKEN, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID).policies(policies).build();
        final io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.AUTH_TOKEN.getBytes()).parseClaimsJws(jwt.toJwt()).getBody();
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, claims.get("workspace_sid"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID, claims.get("channel"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, claims.get("account_sid"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, claims.getIssuer());
        org.junit.Assert.assertTrue(((claims.getExpiration().getTime()) > (new java.util.Date().getTime())));
        final java.util.List<com.twilio.jwt.taskrouter.Policy> claimedPolicies = ((java.util.List<com.twilio.jwt.taskrouter.Policy>) (claims.get("policies")));
        final int connectionPolicies = 2;
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.lang.Object> o_testWorkerToken_cf38__55 = // StatementAdderMethod cloned existing statement
jwt.getHeaders();
        // AssertGenerator add assertion
        java.util.HashMap map_1949083729 = new java.util.HashMap<Object, Object>();
	map_1949083729.put("alg", "HS256");
	org.junit.Assert.assertEquals(map_1949083729, o_testWorkerToken_cf38__55);;
        org.junit.Assert.assertEquals(((policies.size()) + connectionPolicies), claimedPolicies.size());
    }

    /* amplification of com.twilio.jwt.taskrouter.TaskRouterCapabilityTest#testWorkerToken */
    @org.junit.Test(timeout = 10000)
    public void testWorkerToken_add29_cf77() {
        final java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.twilio.jwt.taskrouter.PolicyUtils.defaultWorkerPolicies(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID);
        final java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> activityUpdateFilter = new java.util.HashMap<>();
        activityUpdateFilter.put("ActivitySid", com.twilio.jwt.taskrouter.FilterRequirement.REQUIRED);
        final com.twilio.jwt.taskrouter.Policy allowActivityUpdates = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.worker(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID)).method(com.twilio.http.HttpMethod.POST).postFilter(activityUpdateFilter).build();
        final com.twilio.jwt.taskrouter.Policy allowTasksUpdate = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allTasks(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID)).method(com.twilio.http.HttpMethod.POST).build();
        final com.twilio.jwt.taskrouter.Policy allowReservationUpdate = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allReservations(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID)).method(com.twilio.http.HttpMethod.POST).build();
        policies.add(allowActivityUpdates);
        // AssertGenerator replace invocation
        boolean o_testWorkerToken_add29__26 = // MethodCallAdder
policies.add(allowTasksUpdate);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testWorkerToken_add29__26);
        policies.add(allowTasksUpdate);
        policies.add(allowReservationUpdate);
        final com.twilio.jwt.Jwt jwt = new com.twilio.jwt.taskrouter.TaskRouterCapability.Builder(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.AUTH_TOKEN, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID).policies(policies).build();
        final io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser().setSigningKey(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.AUTH_TOKEN.getBytes()).parseClaimsJws(jwt.toJwt()).getBody();
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKSPACE_SID, claims.get("workspace_sid"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.WORKER_SID, claims.get("channel"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, claims.get("account_sid"));
        org.junit.Assert.assertEquals(com.twilio.jwt.taskrouter.AmplTaskRouterCapabilityTest.ACCOUNT_SID, claims.getIssuer());
        org.junit.Assert.assertTrue(((claims.getExpiration().getTime()) > (new java.util.Date().getTime())));
        final java.util.List<com.twilio.jwt.taskrouter.Policy> claimedPolicies = ((java.util.List<com.twilio.jwt.taskrouter.Policy>) (claims.get("policies")));
        final int connectionPolicies = 2;
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, java.lang.Object> o_testWorkerToken_add29_cf77__59 = // StatementAdderMethod cloned existing statement
jwt.getHeaders();
        // AssertGenerator add assertion
        java.util.HashMap map_718979537 = new java.util.HashMap<Object, Object>();
	map_718979537.put("alg", "HS256");
	org.junit.Assert.assertEquals(map_718979537, o_testWorkerToken_add29_cf77__59);;
        org.junit.Assert.assertEquals(((policies.size()) + connectionPolicies), claimedPolicies.size());
    }
}

