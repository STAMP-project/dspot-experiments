

package com.twilio.jwt.taskrouter;


/**
 * Test class for {@link Policy}.
 */
public class AmplPolicyTest {
    @org.junit.Test
    public void testToJson() throws java.io.IOException {
        java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> filter = new java.util.HashMap<>();
        filter.put("foo", com.twilio.jwt.taskrouter.FilterRequirement.REQUIRED);
        com.twilio.jwt.taskrouter.Policy p = new com.twilio.jwt.taskrouter.Policy.Builder().url("http://localhost").method(com.twilio.http.HttpMethod.GET).postFilter(java.util.Collections.<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement>emptyMap()).queryFilter(filter).build();
        org.junit.Assert.assertEquals("{\"url\":\"http://localhost\",\"method\":\"GET\",\"query_filter\":{\"foo\":{\"required\":true}},\"post_filter\":{},\"allow\":true}", p.toJson());
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyTest#testToJson */
    @org.junit.Test(timeout = 10000)
    public void testToJson_cf9() throws java.io.IOException {
        java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> filter = new java.util.HashMap<>();
        filter.put("foo", com.twilio.jwt.taskrouter.FilterRequirement.REQUIRED);
        com.twilio.jwt.taskrouter.Policy p = new com.twilio.jwt.taskrouter.Policy.Builder().url("http://localhost").method(com.twilio.http.HttpMethod.GET).postFilter(java.util.Collections.<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement>emptyMap()).queryFilter(filter).build();
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // AssertGenerator replace invocation
        boolean o_testToJson_cf9__14 = // StatementAdderMethod cloned existing statement
p.equals(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testToJson_cf9__14);
        org.junit.Assert.assertEquals("{\"url\":\"http://localhost\",\"method\":\"GET\",\"query_filter\":{\"foo\":{\"required\":true}},\"post_filter\":{},\"allow\":true}", p.toJson());
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyTest#testToJson */
    @org.junit.Test(timeout = 10000)
    public void testToJson_cf29() throws java.io.IOException {
        java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> filter = new java.util.HashMap<>();
        filter.put("foo", com.twilio.jwt.taskrouter.FilterRequirement.REQUIRED);
        com.twilio.jwt.taskrouter.Policy p = new com.twilio.jwt.taskrouter.Policy.Builder().url("http://localhost").method(com.twilio.http.HttpMethod.GET).postFilter(java.util.Collections.<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement>emptyMap()).queryFilter(filter).build();
        // AssertGenerator replace invocation
        java.lang.String o_testToJson_cf29__12 = // StatementAdderMethod cloned existing statement
p.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testToJson_cf29__12, "Policy{url=http://localhost, method=GET, queryFilter={foo=REQUIRED}, postFilter={}, allowed=true}");
        org.junit.Assert.assertEquals("{\"url\":\"http://localhost\",\"method\":\"GET\",\"query_filter\":{\"foo\":{\"required\":true}},\"post_filter\":{},\"allow\":true}", p.toJson());
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyTest#testToJson */
    @org.junit.Test(timeout = 10000)
    public void testToJson_cf10_cf154_failAssert52() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> filter = new java.util.HashMap<>();
            filter.put("foo", com.twilio.jwt.taskrouter.FilterRequirement.REQUIRED);
            com.twilio.jwt.taskrouter.Policy p = new com.twilio.jwt.taskrouter.Policy.Builder().url("http://localhost").method(com.twilio.http.HttpMethod.GET).postFilter(java.util.Collections.<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement>emptyMap()).queryFilter(filter).build();
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_3 = new java.lang.Object();
            // AssertGenerator replace invocation
            boolean o_testToJson_cf10__14 = // StatementAdderMethod cloned existing statement
p.equals(vc_3);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testToJson_cf10__14);
            // StatementAdderOnAssert create null value
            com.twilio.jwt.taskrouter.Policy vc_66 = (com.twilio.jwt.taskrouter.Policy)null;
            // StatementAdderMethod cloned existing statement
            vc_66.getMethod();
            // MethodAssertGenerator build local variable
            Object o_22_0 = p.toJson();
            org.junit.Assert.fail("testToJson_cf10_cf154 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyTest#testToJson */
    @org.junit.Test(timeout = 10000)
    public void testToJson_cf23_cf290() throws java.io.IOException {
        java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> filter = new java.util.HashMap<>();
        filter.put("foo", com.twilio.jwt.taskrouter.FilterRequirement.REQUIRED);
        com.twilio.jwt.taskrouter.Policy p = new com.twilio.jwt.taskrouter.Policy.Builder().url("http://localhost").method(com.twilio.http.HttpMethod.GET).postFilter(java.util.Collections.<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement>emptyMap()).queryFilter(filter).build();
        // AssertGenerator replace invocation
        java.lang.String o_testToJson_cf23__12 = // StatementAdderMethod cloned existing statement
p.getUrl();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testToJson_cf23__12, "http://localhost");
        // AssertGenerator replace invocation
        java.lang.String o_testToJson_cf23_cf290__16 = // StatementAdderMethod cloned existing statement
p.toString();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testToJson_cf23_cf290__16, "Policy{url=http://localhost, method=GET, queryFilter={foo=REQUIRED}, postFilter={}, allowed=true}");
        org.junit.Assert.assertEquals("{\"url\":\"http://localhost\",\"method\":\"GET\",\"query_filter\":{\"foo\":{\"required\":true}},\"post_filter\":{},\"allow\":true}", p.toJson());
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyTest#testToJson */
    @org.junit.Test(timeout = 10000)
    public void testToJson_cf29_cf310_cf851_failAssert34() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> filter = new java.util.HashMap<>();
            filter.put("foo", com.twilio.jwt.taskrouter.FilterRequirement.REQUIRED);
            com.twilio.jwt.taskrouter.Policy p = new com.twilio.jwt.taskrouter.Policy.Builder().url("http://localhost").method(com.twilio.http.HttpMethod.GET).postFilter(java.util.Collections.<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement>emptyMap()).queryFilter(filter).build();
            // AssertGenerator replace invocation
            java.lang.String o_testToJson_cf29__12 = // StatementAdderMethod cloned existing statement
p.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testToJson_cf29__12, "Policy{url=http://localhost, method=GET, queryFilter={foo=REQUIRED}, postFilter={}, allowed=true}");
            // StatementAdderOnAssert create null value
            java.lang.Object vc_142 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_142);
            // AssertGenerator replace invocation
            boolean o_testToJson_cf29_cf310__18 = // StatementAdderMethod cloned existing statement
p.equals(vc_142);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(o_testToJson_cf29_cf310__18);
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_383 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            com.twilio.jwt.taskrouter.Policy vc_380 = (com.twilio.jwt.taskrouter.Policy)null;
            // StatementAdderMethod cloned existing statement
            vc_380.equals(vc_383);
            // MethodAssertGenerator build local variable
            Object o_30_0 = p.toJson();
            org.junit.Assert.fail("testToJson_cf29_cf310_cf851 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.jwt.taskrouter.PolicyTest#testToJson */
    @org.junit.Test(timeout = 10000)
    public void testToJson_add1_cf65_cf1476_failAssert45() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.Map<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement> filter = new java.util.HashMap<>();
            // AssertGenerator replace invocation
            com.twilio.jwt.taskrouter.FilterRequirement o_testToJson_add1__3 = // MethodCallAdder
filter.put("foo", com.twilio.jwt.taskrouter.FilterRequirement.REQUIRED);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testToJson_add1__3);
            filter.put("foo", com.twilio.jwt.taskrouter.FilterRequirement.REQUIRED);
            com.twilio.jwt.taskrouter.Policy p = new com.twilio.jwt.taskrouter.Policy.Builder().url("http://localhost").method(com.twilio.http.HttpMethod.GET).postFilter(java.util.Collections.<java.lang.String, com.twilio.jwt.taskrouter.FilterRequirement>emptyMap()).queryFilter(filter).build();
            // AssertGenerator replace invocation
            java.lang.String o_testToJson_add1_cf65__16 = // StatementAdderMethod cloned existing statement
p.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testToJson_add1_cf65__16, "Policy{url=http://localhost, method=GET, queryFilter={foo=REQUIRED}, postFilter={}, allowed=true}");
            // StatementAdderOnAssert create null value
            com.twilio.jwt.taskrouter.Policy vc_632 = (com.twilio.jwt.taskrouter.Policy)null;
            // StatementAdderMethod cloned existing statement
            vc_632.toJson();
            // MethodAssertGenerator build local variable
            Object o_24_0 = p.toJson();
            org.junit.Assert.fail("testToJson_add1_cf65_cf1476 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

