/**
 * Copyright 2018 Florian Schmaus
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
package org.jivesoftware.smack.compress.packet;


import Condition.bad_request;
import Failure.CompressFailureError;
import java.io.IOException;
import org.jivesoftware.smack.packet.StanzaError;
import org.junit.Test;
import org.xml.sax.SAXException;


public class FailureTest {
    @Test
    public void simpleFailureTest() throws IOException, SAXException {
        Failure failure = new Failure(CompressFailureError.processing_failed);
        CharSequence xml = failure.toXML();
        final String expectedXml = "<failure xmlns='http://jabber.org/protocol/compress'><processing-failed/></failure>";
        assertXMLEqual(expectedXml, xml.toString());
    }

    @Test
    public void withStanzaErrrorFailureTest() throws IOException, SAXException {
        StanzaError stanzaError = StanzaError.getBuilder().setCondition(bad_request).build();
        Failure failure = new Failure(CompressFailureError.setup_failed, stanzaError);
        CharSequence xml = failure.toXML();
        final String expectedXml = "<failure xmlns='http://jabber.org/protocol/compress'>" + (((("<setup-failed/>" + "<error xmlns='jabber:client' type='modify'>") + "<bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>") + "</error>") + "</failure>");
        assertXMLEqual(expectedXml, xml.toString());
    }
}

