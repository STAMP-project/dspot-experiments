/**
 * Copyright ? 2014 Florian Schmaus
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
package org.jivesoftware.smackx.rsm.provider;


import RSMSet.ELEMENT;
import RSMSet.NAMESPACE;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.junit.Assert;
import org.junit.Test;


public class RSMSetProviderTest extends InitExtensions {
    @Test
    public void testRsmSetProvider() throws Exception {
        // @formatter:off
        final String rsmset = "<iq type='get' id='iqget'>" + ((((((((((("<pubsub xmlns='http://jabber.org/protocol/pubsub'>" + "<set xmlns='http://jabber.org/protocol/rsm'>") + "<after>aftervalue</after>") + "<before>beforevalue</before>") + "<count>1</count>") + "<first index='2'>foo@bar.com</first>") + "<index>3</index>") + "<last>lastvalue</last>") + "<max>4</max>") + "</set>") + "</pubsub>") + "</iq>");
        // @formatter:on
        IQ iqWithRsm = PacketParserUtils.parseStanza(rsmset);
        RSMSet rsm = iqWithRsm.getExtension(ELEMENT, NAMESPACE);
        Assert.assertNotNull(rsm);
        Assert.assertEquals("aftervalue", rsm.getAfter());
        Assert.assertEquals("beforevalue", rsm.getBefore());
        Assert.assertEquals(1, rsm.getCount());
        Assert.assertEquals(2, rsm.getFirstIndex());
        Assert.assertEquals("foo@bar.com", rsm.getFirst());
        Assert.assertEquals(3, rsm.getIndex());
        Assert.assertEquals("lastvalue", rsm.getLast());
        Assert.assertEquals(4, rsm.getMax());
    }
}

