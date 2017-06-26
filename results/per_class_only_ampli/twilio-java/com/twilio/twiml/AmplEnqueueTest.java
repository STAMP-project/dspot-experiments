

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
}

