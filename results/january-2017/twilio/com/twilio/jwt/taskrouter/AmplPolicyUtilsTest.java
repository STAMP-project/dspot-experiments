

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

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultEventBridgePolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultEventBridgePolicies_cf36_cf769() {
        java.lang.String accountSid = "AC123";
        java.lang.String channelId = "CH123";
        java.lang.String url = com.google.common.base.Joiner.on('/').join("https://event-bridge.twilio.com/v1/wschannels", accountSid, channelId);
        com.twilio.jwt.taskrouter.Policy get = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy post = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.POST).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(get, post);
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> o_testDefaultEventBridgePolicies_cf36__20 = // StatementAdderMethod cloned existing statement
post.getPostFilter();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testDefaultEventBridgePolicies_cf36__20);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_322 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_322);
        // AssertGenerator replace invocation
        boolean o_testDefaultEventBridgePolicies_cf36_cf769__26 = // StatementAdderMethod cloned existing statement
post.equals(vc_322);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testDefaultEventBridgePolicies_cf36_cf769__26);
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultEventBridgePolicies(accountSid, channelId));
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultEventBridgePolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultEventBridgePolicies_literalMutation4_cf173_cf4525_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String accountSid = "A*123";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(accountSid, "A*123");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(accountSid, "A*123");
            java.lang.String channelId = "CH123";
            java.lang.String url = com.google.common.base.Joiner.on('/').join("https://event-bridge.twilio.com/v1/wschannels", accountSid, channelId);
            com.twilio.jwt.taskrouter.Policy get = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
            com.twilio.jwt.taskrouter.Policy post = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.POST).allowed(true).build();
            java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(get, post);
            // StatementAdderOnAssert create null value
            java.lang.Object vc_82 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_82);
            // AssertGenerator replace invocation
            boolean o_testDefaultEventBridgePolicies_literalMutation4_cf173__24 = // StatementAdderMethod cloned existing statement
post.equals(vc_82);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testDefaultEventBridgePolicies_literalMutation4_cf173__24);
            // StatementAdderOnAssert create null value
            com.twilio.jwt.taskrouter.Policy vc_1618 = (com.twilio.jwt.taskrouter.Policy)null;
            // StatementAdderMethod cloned existing statement
            vc_1618.getQueryFilter();
            // MethodAssertGenerator build local variable
            Object o_36_0 = com.twilio.jwt.taskrouter.PolicyUtils.defaultEventBridgePolicies(accountSid, channelId);
            org.junit.Assert.fail("testDefaultEventBridgePolicies_literalMutation4_cf173_cf4525 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultEventBridgePolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultEventBridgePolicies_literalMutation7_literalMutation281_cf3099() {
        java.lang.String accountSid = "https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(accountSid, "https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(accountSid, "https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**");
        java.lang.String channelId = "https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(channelId, "https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(channelId, "https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(channelId, "https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**");
        java.lang.String url = com.google.common.base.Joiner.on('/').join("https://event-bridge.twilio.com/v1/wschannels", accountSid, channelId);
        com.twilio.jwt.taskrouter.Policy get = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy post = new com.twilio.jwt.taskrouter.Policy.Builder().url(url).method(com.twilio.http.HttpMethod.POST).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(get, post);
        // AssertGenerator replace invocation
        java.lang.String o_testDefaultEventBridgePolicies_literalMutation7_literalMutation281_cf3099__26 = // StatementAdderMethod cloned existing statement
post.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testDefaultEventBridgePolicies_literalMutation7_literalMutation281_cf3099__26, "Policy{url=https://event-bridge.twilio.com/v1/wschannels/https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**/https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**, method=POST, queryFilter=null, postFilter=null, allowed=true}");
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultEventBridgePolicies(accountSid, channelId));
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultWorkerPolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultWorkerPolicies_cf4632() {
        java.lang.String workspaceSid = "WS123";
        java.lang.String workerSid = "WK123";
        com.twilio.jwt.taskrouter.Policy activities = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.activities(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy tasks = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allTasks(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy reservations = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allReservations(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy workerFetch = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.worker(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(activities, tasks, reservations, workerFetch);
        // AssertGenerator replace invocation
        java.lang.String o_testDefaultWorkerPolicies_cf4632__33 = // StatementAdderMethod cloned existing statement
workerFetch.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testDefaultWorkerPolicies_cf4632__33, "Policy{url=https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/WK123, method=GET, queryFilter=null, postFilter=null, allowed=true}");
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultWorkerPolicies(workspaceSid, workerSid));
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultWorkerPolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultWorkerPolicies_cf4613() {
        java.lang.String workspaceSid = "WS123";
        java.lang.String workerSid = "WK123";
        com.twilio.jwt.taskrouter.Policy activities = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.activities(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy tasks = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allTasks(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy reservations = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allReservations(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy workerFetch = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.worker(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(activities, tasks, reservations, workerFetch);
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_1643 = new java.lang.Object();
        // AssertGenerator replace invocation
        boolean o_testDefaultWorkerPolicies_cf4613__35 = // StatementAdderMethod cloned existing statement
tasks.equals(vc_1643);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testDefaultWorkerPolicies_cf4613__35);
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultWorkerPolicies(workspaceSid, workerSid));
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultWorkerPolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultWorkerPolicies_cf4638_cf5479() {
        java.lang.String workspaceSid = "WS123";
        java.lang.String workerSid = "WK123";
        com.twilio.jwt.taskrouter.Policy activities = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.activities(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy tasks = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allTasks(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy reservations = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allReservations(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy workerFetch = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.worker(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(activities, tasks, reservations, workerFetch);
        // AssertGenerator replace invocation
        java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> o_testDefaultWorkerPolicies_cf4638__33 = // StatementAdderMethod cloned existing statement
reservations.getQueryFilter();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testDefaultWorkerPolicies_cf4638__33);
        // AssertGenerator replace invocation
        java.lang.String o_testDefaultWorkerPolicies_cf4638_cf5479__37 = // StatementAdderMethod cloned existing statement
tasks.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testDefaultWorkerPolicies_cf4638_cf5479__37, "Policy{url=https://taskrouter.twilio.com/v1/Workspaces/WS123/Tasks/**, method=GET, queryFilter=null, postFilter=null, allowed=true}");
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultWorkerPolicies(workspaceSid, workerSid));
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultWorkerPolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultWorkerPolicies_cf4638_cf5460_cf8818_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String workspaceSid = "WS123";
            java.lang.String workerSid = "WK123";
            com.twilio.jwt.taskrouter.Policy activities = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.activities(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
            com.twilio.jwt.taskrouter.Policy tasks = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allTasks(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
            com.twilio.jwt.taskrouter.Policy reservations = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allReservations(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
            com.twilio.jwt.taskrouter.Policy workerFetch = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.worker(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
            java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(activities, tasks, reservations, workerFetch);
            // AssertGenerator replace invocation
            java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> o_testDefaultWorkerPolicies_cf4638__33 = // StatementAdderMethod cloned existing statement
reservations.getQueryFilter();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testDefaultWorkerPolicies_cf4638__33);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_2003 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_testDefaultWorkerPolicies_cf4638_cf5460__39 = // StatementAdderMethod cloned existing statement
tasks.equals(vc_2003);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testDefaultWorkerPolicies_cf4638_cf5460__39);
            // StatementAdderOnAssert create null value
            com.twilio.jwt.taskrouter.Policy vc_3180 = (com.twilio.jwt.taskrouter.Policy)null;
            // StatementAdderMethod cloned existing statement
            vc_3180.equals(vc_2003);
            // MethodAssertGenerator build local variable
            Object o_47_0 = com.twilio.jwt.taskrouter.PolicyUtils.defaultWorkerPolicies(workspaceSid, workerSid);
            org.junit.Assert.fail("testDefaultWorkerPolicies_cf4638_cf5460_cf8818 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyUtilsTest#testDefaultWorkerPolicies */
    @org.junit.Test(timeout = 10000)
    public void testDefaultWorkerPolicies_literalMutation4605_cf4852_cf6739() {
        java.lang.String workspaceSid = "WS123";
        java.lang.String workerSid = "https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(workerSid, "https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(workerSid, "https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(workerSid, "https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**");
        com.twilio.jwt.taskrouter.Policy activities = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.activities(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy tasks = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allTasks(workspaceSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy reservations = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.allReservations(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        com.twilio.jwt.taskrouter.Policy workerFetch = new com.twilio.jwt.taskrouter.Policy.Builder().url(com.twilio.jwt.taskrouter.UrlUtils.worker(workspaceSid, workerSid)).method(com.twilio.http.HttpMethod.GET).allowed(true).build();
        java.util.List<com.twilio.jwt.taskrouter.Policy> policies = com.google.common.collect.Lists.newArrayList(activities, tasks, reservations, workerFetch);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1762 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1762);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1762);
        // AssertGenerator replace invocation
        boolean o_testDefaultWorkerPolicies_literalMutation4605_cf4852__37 = // StatementAdderMethod cloned existing statement
workerFetch.equals(vc_1762);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testDefaultWorkerPolicies_literalMutation4605_cf4852__37);
        // AssertGenerator replace invocation
        java.lang.String o_testDefaultWorkerPolicies_literalMutation4605_cf4852_cf6739__45 = // StatementAdderMethod cloned existing statement
reservations.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testDefaultWorkerPolicies_literalMutation4605_cf4852_cf6739__45, "Policy{url=https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/https://taskrouter.twilio.com/v1/Workspaces/WS123/Workers/**/Reservations/**, method=GET, queryFilter=null, postFilter=null, allowed=true}");
        org.junit.Assert.assertEquals(policies, com.twilio.jwt.taskrouter.PolicyUtils.defaultWorkerPolicies(workspaceSid, workerSid));
    }
}

