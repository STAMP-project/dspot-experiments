

package com.twilio.twiml;


/**
 * Test class for {@link Body}.
 */
public class AmplBodyTest {
    @org.junit.Test
    public void testXml() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Body body = new com.twilio.twiml.Body("This body!");
        org.junit.Assert.assertEquals("<Body>This body!</Body>", body.toXml());
    }

    @org.junit.Test
    public void testUrl() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Body body = new com.twilio.twiml.Body("This body!");
        org.junit.Assert.assertEquals("%3CBody%3EThis+body%21%3C%2FBody%3E", body.toUrl());
    }

    /* amplification of com.twilio.twiml.BodyTest#testUrl */
    @org.junit.Test(timeout = 10000)
    public void testUrl_cf12() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Body body = new com.twilio.twiml.Body("This body!");
        // AssertGenerator replace invocation
        java.lang.String o_testUrl_cf12__3 = // StatementAdderMethod cloned existing statement
body.getBody();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testUrl_cf12__3, "This body!");
        org.junit.Assert.assertEquals("%3CBody%3EThis+body%21%3C%2FBody%3E", body.toUrl());
    }

    /* amplification of com.twilio.twiml.BodyTest#testXml */
    @org.junit.Test(timeout = 10000)
    public void testXml_cf55() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Body body = new com.twilio.twiml.Body("This body!");
        // AssertGenerator replace invocation
        java.lang.String o_testXml_cf55__3 = // StatementAdderMethod cloned existing statement
body.getBody();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testXml_cf55__3, "This body!");
        org.junit.Assert.assertEquals("<Body>This body!</Body>", body.toXml());
    }
}

