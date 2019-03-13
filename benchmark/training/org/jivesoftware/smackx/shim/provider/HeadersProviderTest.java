/**
 * Copyright 2014-2018 Florian Schmaus
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
package org.jivesoftware.smackx.shim.provider;


import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.test.util.TestUtils;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.shim.packet.Header;
import org.jivesoftware.smackx.shim.packet.HeadersExtension;
import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;


public class HeadersProviderTest {
    @Test
    public void headersInMessageTest() throws Exception {
        // @formatter:off
        final String messageStanza = "<message xmlns='jabber:client' from='romeo@shakespeare.lit/orchard' to='juliet@capulet.com' type='chat'>" + (((("<body>Wherefore are thou?!?</body>" + "<headers xmlns='http://jabber.org/protocol/shim'>") + "<header name='Urgency'>high</header>") + "</headers>") + "</message>");
        // @formatter:on
        XmlPullParser parser = TestUtils.getMessageParser(messageStanza);
        Message message = PacketParserUtils.parseMessage(parser);
        HeadersExtension headers = HeadersExtension.from(message);
        Header header = headers.getHeaders().get(0);
        Assert.assertEquals("Urgency", header.getName());
        Assert.assertEquals("high", header.getValue());
    }
}

