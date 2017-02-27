

package com.twilio.taskrouter;


public class AmplWorkflowRuleTargetTest {
    @org.junit.Test
    public void testToJson() throws java.io.IOException {
        com.twilio.taskrouter.WorkflowRuleTarget target = new com.twilio.taskrouter.WorkflowRuleTarget.Builder("QS123").expression("1==1").priority(5).timeout(30).build();
        org.junit.Assert.assertEquals("{\"queue\":\"QS123\",\"expression\":\"1==1\",\"priority\":5,\"timeout\":30}", target.toJson());
    }

    @org.junit.Test
    public void testFromJson() throws java.io.IOException {
        com.twilio.taskrouter.WorkflowRuleTarget target = com.twilio.taskrouter.WorkflowRuleTarget.fromJson("{\"queue\":\"QS123\",\"expression\":\"1==1\",\"priority\":5,\"timeout\":30}");
        org.junit.Assert.assertEquals("QS123", target.getQueue());
        org.junit.Assert.assertEquals("1==1", target.getExpression());
        org.junit.Assert.assertEquals(5, ((int) (target.getPriority())));
        org.junit.Assert.assertEquals(30, ((int) (target.getTimeout())));
    }

    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf21_failAssert20() throws java.io.IOException {
        try {
            com.twilio.taskrouter.WorkflowRuleTarget target = com.twilio.taskrouter.WorkflowRuleTarget.fromJson("{\"queue\":\"QS123\",\"expression\":\"1==1\",\"priority\":5,\"timeout\":30}");
            java.lang.Object o_3_0 = target.getQueue();
            java.lang.Object o_5_0 = target.getExpression();
            java.lang.Object o_7_0 = ((int) (target.getPriority()));
            java.lang.String vc_3 = new java.lang.String();
            com.twilio.taskrouter.WorkflowRule vc_0 = ((com.twilio.taskrouter.WorkflowRule) (null));
            vc_0.fromJson(vc_3);
            java.lang.Object o_15_0 = ((int) (target.getTimeout()));
            org.junit.Assert.fail("testFromJson_cf21 should have thrown JsonMappingException");
        } catch (com.fasterxml.jackson eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testFromJson_cf20_failAssert19_literalMutation39() throws java.io.IOException {
        try {
            com.twilio.taskrouter.WorkflowRuleTarget target = com.twilio.taskrouter.WorkflowRuleTarget.fromJson("{\"queue\":\"QS123\",\"expression\":\"1==1\",\"priority\":5,\"timeout\":30}");
            java.lang.Object o_3_0 = target.getQueue();
            org.junit.Assert.assertEquals(o_3_0, "QS123");
            java.lang.Object o_5_0 = target.getExpression();
            org.junit.Assert.assertEquals(o_5_0, "1==1");
            java.lang.Object o_7_0 = ((int) (target.getPriority()));
            org.junit.Assert.assertEquals(o_7_0, 5);
            java.lang.String String_vc_0 = "1g==1";
            org.junit.Assert.assertEquals(String_vc_0, "1g==1");
            com.twilio.taskrouter.WorkflowRule vc_0 = ((com.twilio.taskrouter.WorkflowRule) (null));
            org.junit.Assert.assertNull(vc_0);
            vc_0.fromJson(String_vc_0);
            java.lang.Object o_15_0 = ((int) (target.getTimeout()));
            org.junit.Assert.fail("testFromJson_cf20 should have thrown JsonParseException");
        } catch (com.fasterxml.jackson eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testToJson_cf94_failAssert12() throws java.io.IOException {
        try {
            com.twilio.taskrouter.WorkflowRuleTarget target = new com.twilio.taskrouter.WorkflowRuleTarget.Builder("QS123").expression("1==1").priority(5).timeout(30).build();
            java.lang.String vc_15 = new java.lang.String();
            com.twilio.taskrouter.WorkflowRule vc_12 = ((com.twilio.taskrouter.WorkflowRule) (null));
            vc_12.fromJson(vc_15);
            java.lang.Object o_13_0 = target.toJson();
            org.junit.Assert.fail("testToJson_cf94 should have thrown JsonMappingException");
        } catch (com.fasterxml.jackson eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testToJson_cf93_failAssert11_literalMutation169_failAssert1() throws java.io.IOException {
        try {
            try {
                com.twilio.taskrouter.WorkflowRuleTarget target = new com.twilio.taskrouter.WorkflowRuleTarget.Builder("QS123").expression("1==1").priority(5).timeout(30).build();
                java.lang.String String_vc_1 = "    }\n";
                com.twilio.taskrouter.WorkflowRule vc_12 = ((com.twilio.taskrouter.WorkflowRule) (null));
                vc_12.fromJson(String_vc_1);
                java.lang.Object o_13_0 = target.toJson();
                org.junit.Assert.fail("testToJson_cf93 should have thrown UnrecognizedPropertyException");
            } catch (com.fasterxml.jackson eee) {
            }
            org.junit.Assert.fail("testToJson_cf93_failAssert11_literalMutation169 should have thrown JsonParseException");
        } catch (com.fasterxml.jackson eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testToJson_cf93_failAssert11_literalMutation168_failAssert0_literalMutation217() throws java.io.IOException {
        try {
            try {
                com.twilio.taskrouter.WorkflowRuleTarget target = new com.twilio.taskrouter.WorkflowRuleTarget.Builder("QS123").expression("1==1").priority(5).timeout(30).build();
                java.lang.String String_vc_1 = "!^MYU(dM7KJ><6ycw,-c^.vZ(8(U^r,Jp9Flz5*yC=M]:bMoV#NG^1yAAF?5P&";
                org.junit.Assert.assertEquals(String_vc_1, "!^MYU(dM7KJ><6ycw,-c^.vZ(8(U^r,Jp9Flz5*yC=M]:bMoV#NG^1yAAF?5P&");
                com.twilio.taskrouter.WorkflowRule vc_12 = ((com.twilio.taskrouter.WorkflowRule) (null));
                org.junit.Assert.assertNull(vc_12);
                vc_12.fromJson(String_vc_1);
                java.lang.Object o_13_0 = target.toJson();
                org.junit.Assert.fail("testToJson_cf93 should have thrown UnrecognizedPropertyException");
            } catch (com.fasterxml.jackson eee) {
            }
            org.junit.Assert.fail("testToJson_cf93_failAssert11_literalMutation168 should have thrown JsonParseException");
        } catch (com.fasterxml.jackson eee) {
        }
    }

    /* amplification of com.twilio.taskrouter.WorkflowRuleTargetTest#testToJson */
    @org.junit.Test(timeout = 10000)
    public void testToJson_cf93_failAssert11_literalMutation168_failAssert0_literalMutation218() throws java.io.IOException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                com.twilio.taskrouter.WorkflowRuleTarget target = new com.twilio.taskrouter.WorkflowRuleTarget.Builder("QS123").expression("1==1").priority(5).timeout(30).build();
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_1 = "!^MYU(dM7KJ&><6ycw,-c^.vZ(8(U^r, p9Flz5*yC=M]:bMoV#NG^1yAAF?5P&";
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(String_vc_1, "!^MYU(dM7KJ&><6ycw,-c^.vZ(8(U^r, p9Flz5*yC=M]:bMoV#NG^1yAAF?5P&");
                // StatementAdderOnAssert create null value
                com.twilio.taskrouter.WorkflowRule vc_12 = (com.twilio.taskrouter.WorkflowRule)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_12);
                // StatementAdderMethod cloned existing statement
                vc_12.fromJson(String_vc_1);
                // MethodAssertGenerator build local variable
                Object o_13_0 = target.toJson();
                org.junit.Assert.fail("testToJson_cf93 should have thrown UnrecognizedPropertyException");
            } catch (com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException eee) {
            }
            org.junit.Assert.fail("testToJson_cf93_failAssert11_literalMutation168 should have thrown JsonParseException");
        } catch (com.fasterxml.jackson.core.JsonParseException eee) {
        }
    }
}

