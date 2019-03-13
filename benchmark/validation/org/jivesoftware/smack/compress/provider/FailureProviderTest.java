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
package org.jivesoftware.smack.compress.provider;


import Condition.bad_request;
import Failure.CompressFailureError.processing_failed;
import Failure.CompressFailureError.setup_failed;
import FailureProvider.INSTANCE;
import org.jivesoftware.smack.compress.packet.Failure;
import org.jivesoftware.smack.packet.StanzaError;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;


public class FailureProviderTest {
    @Test
    public void simpleFailureTest() throws Exception {
        final String xml = "<failure xmlns='http://jabber.org/protocol/compress'><processing-failed/></failure>";
        final XmlPullParser parser = PacketParserUtils.getParserFor(xml);
        final Failure failure = INSTANCE.parse(parser);
        Assert.assertEquals(processing_failed, failure.getCompressFailureError());
    }

    @Test
    public void withStanzaErrrorFailureTest() throws Exception {
        final String xml = "<failure xmlns='http://jabber.org/protocol/compress'>" + (((("<setup-failed/>" + "<error xmlns='jabber:client' type='modify'>") + "<bad-request xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>") + "</error>") + "</failure>");
        final XmlPullParser parser = PacketParserUtils.getParserFor(xml);
        final Failure failure = INSTANCE.parse(parser);
        Assert.assertEquals(setup_failed, failure.getCompressFailureError());
        final StanzaError error = failure.getStanzaError();
        Assert.assertEquals(bad_request, error.getCondition());
    }
}

