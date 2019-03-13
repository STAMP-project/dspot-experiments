/**
 * Copyright 2011 Robin Collier
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
package org.jivesoftware.smack.filter;


import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.JidTestUtil;


/**
 * From matches filter test.
 *
 * @author Robin Collier
 */
public class FromMatchesFilterTest {
    private static final Jid BASE_JID1 = JidTestUtil.BARE_JID_1;

    private static final EntityFullJid FULL_JID1_R1 = JidTestUtil.FULL_JID_1_RESOURCE_1;

    private static final EntityFullJid FULL_JID1_R2 = JidTestUtil.FULL_JID_1_RESOURCE_2;

    private static final Jid BASE_JID2 = JidTestUtil.BARE_JID_2;

    private static final Jid FULL_JID2 = JidTestUtil.FULL_JID_2_RESOURCE_1;

    private static final Jid BASE_JID3 = JidTestUtil.DUMMY_AT_EXAMPLE_ORG;

    private static final Jid SERVICE_JID1 = JidTestUtil.MUC_EXAMPLE_ORG;

    private static final Jid SERVICE_JID2 = JidTestUtil.PUBSUB_EXAMPLE_ORG;

    @Test
    public void autoCompareMatchingEntityFullJid() {
        FromMatchesFilter filter = FromMatchesFilter.create(FromMatchesFilterTest.FULL_JID1_R1);
        Stanza packet = new Message();
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID1);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID3);
        Assert.assertFalse(filter.accept(packet));
    }

    @Test
    public void autoCompareMatchingBaseJid() {
        FromMatchesFilter filter = FromMatchesFilter.create(FromMatchesFilterTest.BASE_JID1);
        Stanza packet = new Message();
        packet.setFrom(FromMatchesFilterTest.BASE_JID1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R2);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID3);
        Assert.assertFalse(filter.accept(packet));
    }

    @Test
    public void autoCompareMatchingServiceJid() {
        FromMatchesFilter filter = FromMatchesFilter.create(FromMatchesFilterTest.SERVICE_JID1);
        Stanza packet = new Message();
        packet.setFrom(FromMatchesFilterTest.SERVICE_JID1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.SERVICE_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID1);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R1);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID3);
        Assert.assertFalse(filter.accept(packet));
    }

    @Test
    public void bareCompareMatchingEntityFullJid() {
        FromMatchesFilter filter = FromMatchesFilter.createBare(FromMatchesFilterTest.FULL_JID1_R1);
        Stanza packet = new Message();
        packet.setFrom(FromMatchesFilterTest.BASE_JID1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R2);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID3);
        Assert.assertFalse(filter.accept(packet));
    }

    @Test
    public void bareCompareMatchingBaseJid() {
        FromMatchesFilter filter = FromMatchesFilter.createBare(FromMatchesFilterTest.BASE_JID1);
        Stanza packet = new Message();
        packet.setFrom(FromMatchesFilterTest.BASE_JID1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R2);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID3);
        Assert.assertFalse(filter.accept(packet));
    }

    @Test
    public void bareCompareMatchingServiceJid() {
        FromMatchesFilter filter = FromMatchesFilter.createBare(FromMatchesFilterTest.SERVICE_JID1);
        Stanza packet = new Message();
        packet.setFrom(FromMatchesFilterTest.SERVICE_JID1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.SERVICE_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID1);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R1);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID3);
        Assert.assertFalse(filter.accept(packet));
    }

    @Test
    public void fullCompareMatchingEntityFullJid() {
        FromMatchesFilter filter = FromMatchesFilter.createFull(FromMatchesFilterTest.FULL_JID1_R1);
        Stanza packet = new Message();
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID1);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID3);
        Assert.assertFalse(filter.accept(packet));
    }

    @Test
    public void fullCompareMatchingBaseJid() {
        FromMatchesFilter filter = FromMatchesFilter.createFull(FromMatchesFilterTest.BASE_JID1);
        Stanza packet = new Message();
        packet.setFrom(FromMatchesFilterTest.BASE_JID1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R1);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID3);
        Assert.assertFalse(filter.accept(packet));
    }

    @Test
    public void fullCompareMatchingServiceJid() {
        FromMatchesFilter filter = FromMatchesFilter.createFull(FromMatchesFilterTest.SERVICE_JID1);
        Stanza packet = new Message();
        packet.setFrom(FromMatchesFilterTest.SERVICE_JID1);
        Assert.assertTrue(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.SERVICE_JID2);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID1);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.FULL_JID1_R1);
        Assert.assertFalse(filter.accept(packet));
        packet.setFrom(FromMatchesFilterTest.BASE_JID3);
        Assert.assertFalse(filter.accept(packet));
    }
}

