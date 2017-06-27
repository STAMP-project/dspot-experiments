

package com.twilio.jwt.taskrouter;


/**
 * Test class for {@link PolicyUtils}.
 */
public class AmplPolicyUtilsTest {
    @org.junit.Test
    public void testDefaultWorkerPolicies() {
        java.lang.String workspaceSid = "WS123";
        java.lang.String workerSid = "WK123";
        com.twilio.jwt.taskrouter.Policy activities = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.activities(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy tasks = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allTasks(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy reservations = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allReservations(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy workerFetch = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.worker(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(activities, tasks, reservations, workerFetch);
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultWorkerPolicies(workspaceSid, workerSid));
    }

    @org.junit.Test
    public void testDefaultEventBridgePolicies() {
        java.lang.String accountSid = "AC123";
        java.lang.String channelId = "CH123";
        java.lang.String url = com.google.common.base.Joiner.on('/').join("https://event-bridge.twilio.com/v1/wschannels", accountSid, channelId);
        com.twilio.jwt.taskrouter.Policy get = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy post = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.POST).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(get, post);
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultEventBridgePolicies(accountSid, channelId));
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultEventBridgePolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultEventBridgePolicies_cf14() {
        java.lang.String accountSid = "AC123";
        java.lang.String channelId = "CH123";
        java.lang.String url = com.google.common.base.Joiner.on('/').join("https://event-bridge.twilio.com/v1/wschannels", accountSid, channelId);
        com.twilio.jwt.taskrouter.Policy get = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy post = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.POST).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(get, post);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_3 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testDefaultEventBridgePolicies_cf14__22 = // StatementAdderMethod cloned existing statement
post.equals(vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testDefaultEventBridgePolicies_cf14__22);
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultEventBridgePolicies(accountSid, channelId));
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultEventBridgePolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultEventBridgePolicies_cf33() {
        java.lang.String accountSid = "AC123";
        java.lang.String channelId = "CH123";
        java.lang.String url = com.google.common.base.Joiner.on('/').join("https://event-bridge.twilio.com/v1/wschannels", accountSid, channelId);
        com.twilio.jwt.taskrouter.Policy get = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy post = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.POST).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(get, post);
        // AssertGenerator replace invocation
        java.lang.String o_testDefaultEventBridgePolicies_cf33__20 = // StatementAdderMethod cloned existing statement
post.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testDefaultEventBridgePolicies_cf33__20, "Policy{url=https://event-bridge.twilio.com/v1/wschannels/AC123/CH123, method=POST, queryFilter=null, postFilter=null, allowed=true}");
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultEventBridgePolicies(accountSid, channelId));
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultWorkerPolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultWorkerPolicies_cf2249() {
        java.lang.String workspaceSid = "WS123";
        java.lang.String workerSid = "WK123";
        com.twilio.jwt.taskrouter.Policy activities = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.activities(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy tasks = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allTasks(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy reservations = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allReservations(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy workerFetch = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.worker(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(activities, tasks, reservations, workerFetch);
        // AssertGenerator replace invocation
        java.lang.String o_testDefaultWorkerPolicies_cf2249__33 = // StatementAdderMethod cloned existing statement
reservations.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testDefaultWorkerPolicies_cf2249__33, "Policy{url=https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/WK123/Reservations/**, method=GET, queryFilter=null, postFilter=null, allowed=true}");
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultWorkerPolicies(workspaceSid, workerSid));
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultWorkerPolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultWorkerPolicies_cf2230() {
        java.lang.String workspaceSid = "WS123";
        java.lang.String workerSid = "WK123";
        com.twilio.jwt.taskrouter.Policy activities = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.activities(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy tasks = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allTasks(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy reservations = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allReservations(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy workerFetch = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.worker(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(activities, tasks, reservations, workerFetch);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_943 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testDefaultWorkerPolicies_cf2230__35 = // StatementAdderMethod cloned existing statement
tasks.equals(vc_943);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testDefaultWorkerPolicies_cf2230__35);
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultWorkerPolicies(workspaceSid, workerSid));
    }
}

