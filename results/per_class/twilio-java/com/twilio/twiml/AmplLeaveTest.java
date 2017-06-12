

package com.twilio.twiml;


/**
 * Test class for {@link Leave}.
 */
public class AmplLeaveTest {
    @org.junit.Test
    public void testXml() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Leave leave = new com.twilio.twiml.Leave();
        org.junit.Assert.assertEquals("<Leave/>", leave.toXml());
    }

    @org.junit.Test
    public void testUrl() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Leave leave = new com.twilio.twiml.Leave();
        org.junit.Assert.assertEquals("%3CLeave%2F%3E", leave.toUrl());
    }
}

