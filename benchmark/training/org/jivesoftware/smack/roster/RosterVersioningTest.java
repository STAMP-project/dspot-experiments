/**
 * Copyright the original author or authors
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
package org.jivesoftware.smack.roster;


import ItemType.remove;
import Type.result;
import Type.set;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import org.jivesoftware.smack.DummyConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.packet.RosterPacket.Item;
import org.jivesoftware.smack.roster.rosterstore.DirectoryRosterStore;
import org.jivesoftware.smack.roster.rosterstore.RosterStore;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;


/**
 * Tests that verify the correct behavior of the {@link Roster} implementation
 * with regard to roster versioning.
 *
 * @see Roster
 * @see <a href="http://xmpp.org/rfcs/rfc6121.html#roster">Managing the Roster</a>
 * @author Fabian Schuetz
 * @author Lars Noschinski
 */
public class RosterVersioningTest {
    private DummyConnection connection;

    private Roster roster;

    private RosterTest.TestRosterListener rosterListener;

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Tests that receiving an empty roster result causes the roster to be populated
     * by all entries of the roster store.
     *
     * @throws SmackException
     * 		
     * @throws XMPPException
     * 		
     */
    @Test(timeout = 300000)
    public void testEqualVersionStored() throws IOException, InterruptedException, SmackException, XMPPException {
        answerWithEmptyRosterResult();
        roster.waitUntilLoaded();
        Collection<RosterEntry> entries = roster.getEntries();
        Assert.assertSame("Size of the roster", 3, entries.size());
        HashSet<Item> items = new HashSet<>();
        for (RosterEntry entry : entries) {
            items.add(RosterEntry.toRosterItem(entry));
        }
        RosterStore store = DirectoryRosterStore.init(tmpFolder.newFolder());
        RosterVersioningTest.populateStore(store);
        Assert.assertEquals("Elements of the roster", new HashSet(store.getEntries()), items);
        for (RosterEntry entry : entries) {
            Assert.assertTrue(((("joe stevens".equals(entry.getName())) || ("geoff hurley".equals(entry.getName()))) || ("higgins mcmann".equals(entry.getName()))));
        }
        Collection<RosterGroup> groups = roster.getGroups();
        Assert.assertSame(3, groups.size());
        for (RosterGroup group : groups) {
            Assert.assertTrue(((("all".equals(group.getName())) || ("friends".equals(group.getName()))) || ("partners".equals(group.getName()))));
        }
    }

    /**
     * Tests that a non-empty roster result empties the store.
     *
     * @throws SmackException
     * 		
     * @throws XMPPException
     * 		
     * @throws XmppStringprepException
     * 		
     */
    @Test(timeout = 5000)
    public void testOtherVersionStored() throws SmackException, XMPPException, XmppStringprepException {
        Item vaglafItem = RosterVersioningTest.vaglafItem();
        // We expect that the roster request is the only packet sent. This is not part of the specification,
        // but a shortcut in the test implementation.
        Stanza sentPacket = connection.getSentPacket();
        if (sentPacket instanceof RosterPacket) {
            RosterPacket sentRP = ((RosterPacket) (sentPacket));
            RosterPacket answer = new RosterPacket();
            answer.setStanzaId(sentRP.getStanzaId());
            answer.setType(result);
            answer.setTo(sentRP.getFrom());
            answer.setVersion("newVersion");
            answer.addRosterItem(vaglafItem);
            rosterListener.reset();
            connection.processStanza(answer);
            waitUntilInvocationOrTimeout();
        } else {
            Assert.assertTrue("Expected to get a RosterPacket ", false);
        }
        Roster roster = Roster.getInstanceFor(connection);
        Assert.assertEquals("Size of roster", 1, roster.getEntries().size());
        RosterEntry entry = roster.getEntry(vaglafItem.getJid());
        Assert.assertNotNull("Roster contains vaglaf entry", entry);
        Assert.assertEquals("vaglaf entry in roster equals the sent entry", vaglafItem, RosterEntry.toRosterItem(entry));
        RosterStore store = roster.getRosterStore();
        Assert.assertEquals("Size of store", 1, store.getEntries().size());
        Item item = store.getEntry(vaglafItem.getJid());
        Assert.assertNotNull("Store contains vaglaf entry", item);
        Assert.assertEquals("vaglaf entry in store equals the sent entry", vaglafItem, item);
    }

    /**
     * Test roster versioning with roster pushes.
     */
    @SuppressWarnings("UndefinedEquals")
    @Test(timeout = 5000)
    public void testRosterVersioningWithCachedRosterAndPushes() throws Throwable {
        answerWithEmptyRosterResult();
        waitAndReset();
        RosterStore store = roster.getRosterStore();
        // Simulate a roster push adding vaglaf
        {
            RosterPacket rosterPush = new RosterPacket();
            rosterPush.setTo(JidCreate.from("rostertest@example.com/home"));
            rosterPush.setType(set);
            rosterPush.setVersion("v97");
            Item pushedItem = RosterVersioningTest.vaglafItem();
            rosterPush.addRosterItem(pushedItem);
            rosterListener.reset();
            connection.processStanza(rosterPush);
            waitAndReset();
            Assert.assertEquals("Expect store version after push", "v97", store.getRosterVersion());
            Item storedItem = store.getEntry(JidCreate.from("vaglaf@example.com"));
            Assert.assertNotNull("Expect vaglaf to be added", storedItem);
            Assert.assertEquals("Expect vaglaf to be equal to pushed item", pushedItem, storedItem);
            Collection<Item> rosterItems = new HashSet<>();
            for (RosterEntry entry : roster.getEntries()) {
                rosterItems.add(RosterEntry.toRosterItem(entry));
            }
            Assert.assertEquals(rosterItems, new HashSet(store.getEntries()));
        }
        // Simulate a roster push removing vaglaf
        {
            RosterPacket rosterPush = new RosterPacket();
            rosterPush.setTo(JidCreate.from("rostertest@example.com/home"));
            rosterPush.setType(set);
            rosterPush.setVersion("v98");
            Item item = new Item(JidCreate.entityBareFrom("vaglaf@example.com"), "vaglaf the only");
            item.setItemType(remove);
            rosterPush.addRosterItem(item);
            rosterListener.reset();
            connection.processStanza(rosterPush);
            waitAndReset();
            Assert.assertNull("Store doses not contain vaglaf", store.getEntry(JidCreate.entityBareFrom("vaglaf@example.com")));
            Assert.assertEquals("Expect store version after push", "v98", store.getRosterVersion());
        }
    }
}

