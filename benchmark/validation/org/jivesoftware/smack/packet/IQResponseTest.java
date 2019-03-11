/**
 * Copyright 2010 Jive Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smack.packet;


import IQ.Type.error;
import IQ.Type.result;
import IQ.Type.set;
import StanzaError.Builder;
import StanzaError.Condition.bad_request;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;


/**
 * Tests that verifies the correct behavior of creating result and error IQ packets.
 *
 * @see <a href="http://xmpp.org/rfcs/rfc3920.html#stanzas-semantics-iq">IQ Semantics</a>
 * @author Guenther Niess
 */
public class IQResponseTest {
    private static final String ELEMENT = "child";

    private static final String NAMESPACE = "http://igniterealtime.org/protocol/test";

    /**
     * Test creating a simple and empty IQ response.
     *
     * @throws XmppStringprepException
     * 		
     */
    @Test
    public void testGeneratingSimpleResponse() throws XmppStringprepException {
        final IQ request = new TestIQ(IQResponseTest.ELEMENT, IQResponseTest.NAMESPACE);
        request.setFrom(JidCreate.from("sender@test/Smack"));
        request.setTo(JidCreate.from("receiver@test/Smack"));
        final IQ result = IQ.createResultIQ(request);
        Assert.assertEquals(result, result.getType());
        Assert.assertNotNull(result.getStanzaId());
        Assert.assertEquals(request.getStanzaId(), result.getStanzaId());
        Assert.assertEquals(request.getFrom(), result.getTo());
        Assert.assertEquals(request.getTo(), result.getFrom());
        Assert.assertEquals("", result.getChildElementXML().toString());
    }

    /**
     * Test creating a error response based on an IQ request.
     *
     * @throws XmppStringprepException
     * 		
     */
    @Test
    public void testGeneratingValidErrorResponse() throws XmppStringprepException {
        final StanzaError.Builder error = StanzaError.getBuilder(bad_request);
        final IQ request = new TestIQ(IQResponseTest.ELEMENT, IQResponseTest.NAMESPACE);
        request.setType(set);
        request.setFrom(JidCreate.from("sender@test/Smack"));
        request.setTo(JidCreate.from("receiver@test/Smack"));
        final IQ result = IQ.createErrorResponse(request, error);
        Assert.assertEquals(error, result.getType());
        Assert.assertNotNull(result.getStanzaId());
        Assert.assertEquals(request.getStanzaId(), result.getStanzaId());
        Assert.assertEquals(request.getFrom(), result.getTo());
        Assert.assertEquals(error.build().toXML().toString(), result.getError().toXML().toString());
        // TODO this test was never valid
        // assertEquals(CHILD_ELEMENT, result.getChildElementXML());
    }

    /**
     * According to <a href="http://xmpp.org/rfcs/rfc3920.html#stanzas-semantics-iq"
     * >RFC3920: IQ Semantics</a> we shouldn't respond to an IQ of type result.
     *
     * @throws XmppStringprepException
     * 		
     */
    @Test
    public void testGeneratingResponseBasedOnResult() throws XmppStringprepException {
        final IQ request = new TestIQ(IQResponseTest.ELEMENT, IQResponseTest.NAMESPACE);
        request.setType(result);
        request.setFrom(JidCreate.from("sender@test/Smack"));
        request.setTo(JidCreate.from("receiver@test/Smack"));
        try {
            IQ.createResultIQ(request);
        } catch (IllegalArgumentException e) {
            return;
        }
        Assert.fail("It shouldn't be possible to generate a response for a result IQ!");
    }

    /**
     * According to <a href="http://xmpp.org/rfcs/rfc3920.html#stanzas-semantics-iq"
     * >RFC3920: IQ Semantics</a> we shouldn't respond to an IQ of type error.
     *
     * @throws XmppStringprepException
     * 		
     */
    @Test
    public void testGeneratingErrorBasedOnError() throws XmppStringprepException {
        final StanzaError.Builder error = StanzaError.getBuilder(bad_request);
        final IQ request = new TestIQ(IQResponseTest.ELEMENT, IQResponseTest.NAMESPACE);
        request.setType(error);
        request.setFrom(JidCreate.from("sender@test/Smack"));
        request.setTo(JidCreate.from("receiver@test/Smack"));
        request.setError(error);
        try {
            IQ.createErrorResponse(request, error);
        } catch (IllegalArgumentException e) {
            return;
        }
        Assert.fail("It shouldn't be possible to generate a response for a error IQ!");
    }
}

