package com.twilio.twiml;


/**
 * Test class for {@link Enqueue}.
 */
public class AmplEnqueueTest {
    @org.junit.Test
    public void testXml() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        org.junit.Assert.assertEquals("<Enqueue action=\"/enqueue\" method=\"GET\" waitUrl=\"/wait\" waitUrlMethod=\"POST\" workflowSid=\"WF123\">enqueue</Enqueue>", enqueue.toXml());
    }

    @org.junit.Test
    public void testUrl() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        org.junit.Assert.assertEquals("%3CEnqueue+action%3D%22%2Fenqueue%22+method%3D%22GET%22+waitUrl%3D%22%2Fwait%22+waitUrlMethod%3D%22POST%22+workflowSid%3D%22WF123%22%3Eenqueue%3C%2FEnqueue%3E", enqueue.toUrl());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    /* amplification of testUrl_sd33 */
    @org.junit.Test(timeout = 10000)
    public void testUrl_sd33_sd1352_failAssert5() throws com.twilio.twiml.TwiMLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg1_160 = 2009840260;
            int __DSPOT_arg0_159 = -1249580942;
            com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
            enqueue.toUrl();
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_12 = // StatementAdd: add invocation of a method
            enqueue.getAction();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_12.subSequence(__DSPOT_arg0_159, __DSPOT_arg1_160);
            org.junit.Assert.fail("testUrl_sd33_sd1352 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    /* amplification of testUrl_sd35 */
    @org.junit.Test(timeout = 10000)
    public void testUrl_sd35_sd1438_failAssert6() throws com.twilio.twiml.TwiMLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg3_187 = 214812121;
            char[] __DSPOT_arg2_186 = new char[0];
            int __DSPOT_arg1_185 = 466778171;
            int __DSPOT_arg0_184 = -1521443148;
            com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
            enqueue.toUrl();
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_12 = // StatementAdd: add invocation of a method
            enqueue.getQueueName();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_12.getChars(__DSPOT_arg0_184, __DSPOT_arg1_185, __DSPOT_arg2_186, __DSPOT_arg3_187);
            org.junit.Assert.fail("testUrl_sd35_sd1438 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    /* amplification of testUrl_sd36 */
    @org.junit.Test(timeout = 10000)
    public void testUrl_sd36_sd1543_failAssert11() throws com.twilio.twiml.TwiMLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg0_291 = 733593456;
            com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
            enqueue.toUrl();
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_12 = // StatementAdd: add invocation of a method
            enqueue.getWaitUrl();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_12.substring(__DSPOT_arg0_291);
            org.junit.Assert.fail("testUrl_sd36_sd1543 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    /* amplification of testUrl_sd38 */
    @org.junit.Test(timeout = 10000)
    public void testUrl_sd38_sd1614_failAssert12() throws com.twilio.twiml.TwiMLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg0_297 = 143602217;
            com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
            enqueue.toUrl();
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_12 = // StatementAdd: add invocation of a method
            enqueue.getWorkflowSid();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_12.codePointAt(__DSPOT_arg0_297);
            org.junit.Assert.fail("testUrl_sd38_sd1614 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testXml */
    /* amplification of testXml_sd4189 */
    @org.junit.Test(timeout = 10000)
    public void testXml_sd4189_sd5590_failAssert0() throws com.twilio.twiml.TwiMLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg3_1843 = -62197962;
            byte[] __DSPOT_arg2_1842 = new byte[0];
            int __DSPOT_arg1_1841 = 1862598240;
            int __DSPOT_arg0_1840 = 280142576;
            com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
            enqueue.toXml();
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_12 = // StatementAdd: add invocation of a method
            enqueue.getQueueName();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_12.getBytes(__DSPOT_arg0_1840, __DSPOT_arg1_1841, __DSPOT_arg2_1842, __DSPOT_arg3_1843);
            org.junit.Assert.fail("testXml_sd4189_sd5590 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testXml */
    /* amplification of testXml_sd4190 */
    @org.junit.Test(timeout = 10000)
    public void testXml_sd4190_sd5658_failAssert2() throws com.twilio.twiml.TwiMLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg0_1893 = -662478396;
            com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
            enqueue.toXml();
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_12 = // StatementAdd: add invocation of a method
            enqueue.getWaitUrl();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_12.codePointAt(__DSPOT_arg0_1893);
            org.junit.Assert.fail("testXml_sd4190_sd5658 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testXml */
    /* amplification of testXml_sd4192 */
    @org.junit.Test(timeout = 10000)
    public void testXml_sd4192_sd5782_failAssert1() throws com.twilio.twiml.TwiMLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg3_1978 = 1882328181;
            char[] __DSPOT_arg2_1977 = new char[]{ '%', '>', '#' };
            int __DSPOT_arg1_1976 = 1875217173;
            int __DSPOT_arg0_1975 = 509680883;
            com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
            enqueue.toXml();
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_12 = // StatementAdd: add invocation of a method
            enqueue.getWorkflowSid();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_12.getChars(__DSPOT_arg0_1975, __DSPOT_arg1_1976, __DSPOT_arg2_1977, __DSPOT_arg3_1978);
            org.junit.Assert.fail("testXml_sd4192_sd5782 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }
}

