/**
 * Copyright 2016 Fernando Ramirez
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
package org.jivesoftware.smackx.muclight;


import MUCLightAffiliation.member;
import MUCLightAffiliation.none;
import MUCLightAffiliation.owner;
import java.util.HashMap;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.muclight.element.MUCLightElements.AffiliationsChangeExtension;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;


public class MUCLightAffiliationsChangeExtensionTest {
    private static final String exampleMessageStanza = "<message xmlns='jabber:client' to='coven@muclight.shakespeare.lit' id='member1' type='groupchat'>" + ((((("<x xmlns='urn:xmpp:muclight:0#affiliations'>" + "<user affiliation='owner'>sarasa2@shakespeare.lit</user>") + "<user affiliation='member'>sarasa1@shakespeare.lit</user>") + "<user affiliation='none'>sarasa3@shakespeare.lit</user>") + "</x>") + "</message>");

    private static final String exampleMessageStanzaWithVersion = "<message xmlns='jabber:client' " + ((((((("to='coven@muclight.shakespeare.lit' id='member1' type='groupchat'>" + "<x xmlns='urn:xmpp:muclight:0#affiliations'>") + "<version>qwerty</version>") + "<user affiliation='member'>sarasa1@shakespeare.lit</user>") + "<user affiliation='none'>sarasa3@shakespeare.lit</user>") + "</x>") + "<body></body>") + "</message>");

    private static final String exampleMessageStanzaWithPrevVersion = "<message xmlns='jabber:client' " + (((((((("to='coven@muclight.shakespeare.lit' id='member1' type='groupchat'>" + "<x xmlns='urn:xmpp:muclight:0#affiliations'>") + "<prev-version>njiokm</prev-version>") + "<version>qwerty</version>") + "<user affiliation='owner'>sarasa2@shakespeare.lit</user>") + "<user affiliation='member'>sarasa1@shakespeare.lit</user>") + "</x>") + "<body></body>") + "</message>");

    @Test
    public void checkAffiliationsChangeExtension() throws Exception {
        Message changeAffiliationsMessage = PacketParserUtils.parseStanza(MUCLightAffiliationsChangeExtensionTest.exampleMessageStanza);
        AffiliationsChangeExtension affiliationsChangeExtension = AffiliationsChangeExtension.from(changeAffiliationsMessage);
        HashMap<Jid, MUCLightAffiliation> affiliations = affiliationsChangeExtension.getAffiliations();
        Assert.assertEquals(affiliations.size(), 3);
        Assert.assertEquals(affiliations.get(JidCreate.from("sarasa2@shakespeare.lit")), owner);
        Assert.assertEquals(affiliations.get(JidCreate.from("sarasa1@shakespeare.lit")), member);
        Assert.assertEquals(affiliations.get(JidCreate.from("sarasa3@shakespeare.lit")), none);
    }

    @Test
    public void checkAffiliationsChangeExtensionWithVersion() throws Exception {
        Message changeAffiliationsMessage = PacketParserUtils.parseStanza(MUCLightAffiliationsChangeExtensionTest.exampleMessageStanzaWithVersion);
        AffiliationsChangeExtension affiliationsChangeExtension = AffiliationsChangeExtension.from(changeAffiliationsMessage);
        HashMap<Jid, MUCLightAffiliation> affiliations = affiliationsChangeExtension.getAffiliations();
        Assert.assertEquals(affiliations.size(), 2);
        Assert.assertEquals(affiliations.get(JidCreate.from("sarasa1@shakespeare.lit")), member);
        Assert.assertEquals(affiliations.get(JidCreate.from("sarasa3@shakespeare.lit")), none);
        Assert.assertEquals(affiliationsChangeExtension.getVersion(), "qwerty");
    }

    @Test
    public void checkAffiliationsChangeExtensionWithPrevVersion() throws Exception {
        Message changeAffiliationsMessage = PacketParserUtils.parseStanza(MUCLightAffiliationsChangeExtensionTest.exampleMessageStanzaWithPrevVersion);
        AffiliationsChangeExtension affiliationsChangeExtension = AffiliationsChangeExtension.from(changeAffiliationsMessage);
        HashMap<Jid, MUCLightAffiliation> affiliations = affiliationsChangeExtension.getAffiliations();
        Assert.assertEquals(affiliations.size(), 2);
        Assert.assertEquals(affiliations.get(JidCreate.from("sarasa2@shakespeare.lit")), owner);
        Assert.assertEquals(affiliations.get(JidCreate.from("sarasa1@shakespeare.lit")), member);
        Assert.assertEquals(affiliationsChangeExtension.getPrevVersion(), "njiokm");
        Assert.assertEquals(affiliationsChangeExtension.getVersion(), "qwerty");
    }
}

