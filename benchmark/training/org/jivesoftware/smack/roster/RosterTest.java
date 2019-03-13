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
package org.jivesoftware.smack.roster;


import Condition.service_unavailable;
import ItemType.none;
import Type.set;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jivesoftware.smack.DummyConnection;
import org.jivesoftware.smack.im.InitSmackIm;
import org.jivesoftware.smack.packet.ErrorIQ;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.packet.RosterPacket.Item;
import org.jivesoftware.smack.roster.packet.RosterPacket.ItemType;
import org.jivesoftware.smack.test.util.TestUtils;
import org.jivesoftware.smack.test.util.WaitForPacketListener;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.xmlpull.v1.XmlPullParser;


/**
 * Tests that verifies the correct behavior of the {@link Roster} implementation.
 *
 * @see Roster
 * @see <a href="http://xmpp.org/rfcs/rfc3921.html#roster">Roster Management</a>
 * @author Guenther Niess
 */
public class RosterTest extends InitSmackIm {
    private DummyConnection connection;

    private Roster roster;

    private RosterTest.TestRosterListener rosterListener;

    /**
     * Test a simple roster initialization according to the example in
     * <a href="http://xmpp.org/rfcs/rfc3921.html#roster-login"
     *     >RFC3921: Retrieving One's Roster on Login</a>.
     */
    @Test
    public void testSimpleRosterInitialization() throws Exception {
        Assert.assertNotNull("Can't get the roster from the provided connection!", roster);
        Assert.assertFalse("Roster shouldn't be already loaded!", roster.isLoaded());
        // Perform roster initialization
        initRoster();
        // Verify roster
        Assert.assertTrue("Roster can't be loaded!", roster.waitUntilLoaded());
        RosterTest.verifyRomeosEntry(roster.getEntry(JidCreate.entityBareFrom("romeo@example.net")));
        RosterTest.verifyMercutiosEntry(roster.getEntry(JidCreate.entityBareFrom("mercutio@example.com")));
        RosterTest.verifyBenvoliosEntry(roster.getEntry(JidCreate.entityBareFrom("benvolio@example.net")));
        Assert.assertSame("Wrong number of roster entries.", 3, roster.getEntries().size());
        // Verify roster listener
        Assert.assertTrue("The roster listener wasn't invoked for Romeo.", rosterListener.addedAddressesContains("romeo@example.net"));
        Assert.assertTrue("The roster listener wasn't invoked for Mercutio.", rosterListener.addedAddressesContains("mercutio@example.com"));
        Assert.assertTrue("The roster listener wasn't invoked for Benvolio.", rosterListener.addedAddressesContains("benvolio@example.net"));
        Assert.assertSame("RosterListeners implies that a item was deleted!", 0, rosterListener.getDeletedAddresses().size());
        Assert.assertSame("RosterListeners implies that a item was updated!", 0, rosterListener.getUpdatedAddresses().size());
    }

    /**
     * Test adding a roster item according to the example in
     * <a href="http://xmpp.org/rfcs/rfc3921.html#roster-add"
     *     >RFC3921: Adding a Roster Item</a>.
     */
    @Test
    public void testAddRosterItem() throws Throwable {
        // Constants for the new contact
        final BareJid contactJID = JidCreate.entityBareFrom("nurse@example.com");
        final String contactName = "Nurse";
        final String[] contactGroup = new String[]{ "Servants" };
        // Setup
        Assert.assertNotNull("Can't get the roster from the provided connection!", roster);
        initRoster();
        rosterListener.reset();
        // Adding the new roster item
        final RosterTest.RosterUpdateResponder serverSimulator = new RosterTest.RosterUpdateResponder() {
            @Override
            void verifyUpdateRequest(final RosterPacket updateRequest) {
                final Item item = updateRequest.getRosterItems().iterator().next();
                Assert.assertEquals("The provided JID doesn't match the requested!", contactJID, item.getJid());
                Assert.assertSame("The provided name doesn't match the requested!", contactName, item.getName());
                Assert.assertSame("The provided group number doesn't match the requested!", contactGroup.length, item.getGroupNames().size());
                Assert.assertSame("The provided group doesn't match the requested!", contactGroup[0], item.getGroupNames().iterator().next());
            }
        };
        serverSimulator.start();
        roster.createEntry(contactJID, contactName, contactGroup);
        serverSimulator.join();
        // Check if an error occurred within the simulator
        final Throwable exception = serverSimulator.getException();
        if (exception != null) {
            throw exception;
        }
        waitUntilInvocationOrTimeout();
        // Verify the roster entry of the new contact
        final RosterEntry addedEntry = roster.getEntry(contactJID);
        Assert.assertNotNull("The new contact wasn't added to the roster!", addedEntry);
        Assert.assertTrue("The roster listener wasn't invoked for the new contact!", rosterListener.getAddedAddresses().contains(contactJID));
        Assert.assertSame("Setup wrong name for the new contact!", contactName, addedEntry.getName());
        Assert.assertSame("Setup wrong default subscription status!", none, addedEntry.getType());
        Assert.assertSame("The new contact should be member of exactly one group!", 1, addedEntry.getGroups().size());
        Assert.assertSame("Setup wrong group name for the added contact!", contactGroup[0], addedEntry.getGroups().iterator().next().getName());
        // Verify the unchanged roster items
        RosterTest.verifyRomeosEntry(roster.getEntry(JidCreate.entityBareFrom("romeo@example.net")));
        RosterTest.verifyMercutiosEntry(roster.getEntry(JidCreate.entityBareFrom("mercutio@example.com")));
        RosterTest.verifyBenvoliosEntry(roster.getEntry(JidCreate.entityBareFrom("benvolio@example.net")));
        Assert.assertSame("Wrong number of roster entries.", 4, roster.getEntries().size());
    }

    /**
     * Test updating a roster item according to the example in
     * <a href="http://xmpp.org/rfcs/rfc3921.html#roster-update"
     *     >RFC3921: Updating a Roster Item</a>.
     */
    @Test
    public void testUpdateRosterItem() throws Throwable {
        // Constants for the updated contact
        final BareJid contactJID = JidCreate.entityBareFrom("romeo@example.net");
        final String contactName = "Romeo";
        final String[] contactGroups = new String[]{ "Friends", "Lovers" };
        // Setup
        Assert.assertNotNull("Can't get the roster from the provided connection!", roster);
        initRoster();
        rosterListener.reset();
        // Updating the roster item
        final RosterTest.RosterUpdateResponder serverSimulator = new RosterTest.RosterUpdateResponder() {
            @Override
            void verifyUpdateRequest(final RosterPacket updateRequest) {
                final Item item = updateRequest.getRosterItems().iterator().next();
                Assert.assertEquals("The provided JID doesn't match the requested!", contactJID, item.getJid());
                Assert.assertSame("The provided name doesn't match the requested!", contactName, item.getName());
                Assert.assertTrue((("The updated contact doesn't belong to the requested groups (" + (contactGroups[0])) + ")!"), item.getGroupNames().contains(contactGroups[0]));
                Assert.assertTrue((("The updated contact doesn't belong to the requested groups (" + (contactGroups[1])) + ")!"), item.getGroupNames().contains(contactGroups[1]));
                Assert.assertSame("The provided group number doesn't match the requested!", contactGroups.length, item.getGroupNames().size());
            }
        };
        serverSimulator.start();
        roster.createGroup(contactGroups[1]).addEntry(roster.getEntry(contactJID));
        serverSimulator.join();
        // Check if an error occurred within the simulator
        final Throwable exception = serverSimulator.getException();
        if (exception != null) {
            throw exception;
        }
        waitUntilInvocationOrTimeout();
        // Verify the roster entry of the updated contact
        final RosterEntry addedEntry = roster.getEntry(contactJID);
        Assert.assertNotNull("The contact was deleted from the roster!", addedEntry);
        Assert.assertTrue("The roster listener wasn't invoked for the updated contact!", rosterListener.getUpdatedAddresses().contains(contactJID));
        Assert.assertSame("Setup wrong name for the changed contact!", contactName, addedEntry.getName());
        Assert.assertTrue((("The updated contact doesn't belong to the requested groups (" + (contactGroups[0])) + ")!"), roster.getGroup(contactGroups[0]).contains(addedEntry));
        Assert.assertTrue((("The updated contact doesn't belong to the requested groups (" + (contactGroups[1])) + ")!"), roster.getGroup(contactGroups[1]).contains(addedEntry));
        Assert.assertSame("The updated contact should be member of two groups!", contactGroups.length, addedEntry.getGroups().size());
        // Verify the unchanged roster items
        RosterTest.verifyMercutiosEntry(roster.getEntry(JidCreate.entityBareFrom("mercutio@example.com")));
        RosterTest.verifyBenvoliosEntry(roster.getEntry(JidCreate.entityBareFrom("benvolio@example.net")));
        Assert.assertSame((("Wrong number of roster entries (" + (roster.getEntries())) + ")."), 3, roster.getEntries().size());
    }

    /**
     * Test deleting a roster item according to the example in
     * <a href="http://xmpp.org/rfcs/rfc3921.html#roster-delete"
     *     >RFC3921: Deleting a Roster Item</a>.
     */
    @Test
    public void testDeleteRosterItem() throws Throwable {
        // The contact which should be deleted
        final BareJid contactJID = JidCreate.entityBareFrom("romeo@example.net");
        // Setup
        Assert.assertNotNull("Can't get the roster from the provided connection!", roster);
        initRoster();
        rosterListener.reset();
        // Delete a roster item
        final RosterTest.RosterUpdateResponder serverSimulator = new RosterTest.RosterUpdateResponder() {
            @Override
            void verifyUpdateRequest(final RosterPacket updateRequest) {
                final Item item = updateRequest.getRosterItems().iterator().next();
                Assert.assertEquals("The provided JID doesn't match the requested!", contactJID, item.getJid());
            }
        };
        serverSimulator.start();
        roster.removeEntry(roster.getEntry(contactJID));
        serverSimulator.join();
        // Check if an error occurred within the simulator
        final Throwable exception = serverSimulator.getException();
        if (exception != null) {
            throw exception;
        }
        waitUntilInvocationOrTimeout();
        // Verify
        final RosterEntry deletedEntry = roster.getEntry(contactJID);
        Assert.assertNull("The contact wasn't deleted from the roster!", deletedEntry);
        Assert.assertTrue("The roster listener wasn't invoked for the deleted contact!", rosterListener.getDeletedAddresses().contains(contactJID));
        RosterTest.verifyMercutiosEntry(roster.getEntry(JidCreate.entityBareFrom("mercutio@example.com")));
        RosterTest.verifyBenvoliosEntry(roster.getEntry(JidCreate.entityBareFrom("benvolio@example.net")));
        Assert.assertSame((("Wrong number of roster entries (" + (roster.getEntries())) + ")."), 2, roster.getEntries().size());
    }

    /**
     * Test a simple roster push according to the example in
     * <a href="http://xmpp.org/internet-drafts/draft-ietf-xmpp-3921bis-03.html#roster-syntax-actions-push"
     *     >RFC3921bis-03: Roster Push</a>.
     */
    @Test
    public void testSimpleRosterPush() throws Throwable {
        final BareJid contactJID = JidCreate.entityBareFrom("nurse@example.com");
        Assert.assertNotNull("Can't get the roster from the provided connection!", roster);
        final StringBuilder sb = new StringBuilder();
        sb.append("<iq id=\"rostertest1\" type=\"set\" ").append("to=\"").append(connection.getUser()).append("\">").append("<query xmlns=\"jabber:iq:roster\">").append("<item jid=\"").append(contactJID).append("\"/>").append("</query>").append("</iq>");
        final XmlPullParser parser = TestUtils.getIQParser(sb.toString());
        final IQ rosterPush = PacketParserUtils.parseIQ(parser);
        initRoster();
        rosterListener.reset();
        // Simulate receiving the roster push
        connection.processStanza(rosterPush);
        waitUntilInvocationOrTimeout();
        // Verify the roster entry of the new contact
        final RosterEntry addedEntry = roster.getEntry(contactJID);
        Assert.assertNotNull("The new contact wasn't added to the roster!", addedEntry);
        Assert.assertTrue("The roster listener wasn't invoked for the new contact!", rosterListener.getAddedAddresses().contains(contactJID));
        Assert.assertSame("Setup wrong default subscription status!", none, addedEntry.getType());
        Assert.assertSame("The new contact shouldn't be member of any group!", 0, addedEntry.getGroups().size());
        // Verify the unchanged roster items
        RosterTest.verifyRomeosEntry(roster.getEntry(JidCreate.entityBareFrom("romeo@example.net")));
        RosterTest.verifyMercutiosEntry(roster.getEntry(JidCreate.entityBareFrom("mercutio@example.com")));
        RosterTest.verifyBenvoliosEntry(roster.getEntry(JidCreate.entityBareFrom("benvolio@example.net")));
        Assert.assertSame("Wrong number of roster entries.", 4, roster.getEntries().size());
    }

    /**
     * Tests that roster pushes with invalid from are ignored.
     *
     * @throws XmppStringprepException
     * 		
     * @see <a href="http://xmpp.org/rfcs/rfc6121.html#roster-syntax-actions-push">RFC 6121, Section 2.1.6</a>
     */
    @Test
    public void testIgnoreInvalidFrom() throws XmppStringprepException {
        final BareJid spammerJid = JidCreate.entityBareFrom("spam@example.com");
        RosterPacket packet = new RosterPacket();
        packet.setType(set);
        packet.setTo(connection.getUser());
        packet.setFrom(JidCreate.entityBareFrom("mallory@example.com"));
        packet.addRosterItem(new Item(spammerJid, "Cool products!"));
        final String requestId = packet.getStanzaId();
        // Simulate receiving the roster push
        connection.processStanza(packet);
        // Smack should reply with an error IQ
        ErrorIQ errorIQ = connection.getSentPacket();
        Assert.assertEquals(requestId, errorIQ.getStanzaId());
        Assert.assertEquals(service_unavailable, errorIQ.getError().getCondition());
        Assert.assertNull("Contact was added to roster", Roster.getInstanceFor(connection).getEntry(spammerJid));
    }

    /**
     * Test if adding an user with an empty group is equivalent with providing
     * no group.
     *
     * @see <a href="http://www.igniterealtime.org/issues/browse/SMACK-294">SMACK-294</a>
     */
    @Test(timeout = 5000)
    public void testAddEmptyGroupEntry() throws Throwable {
        // Constants for the new contact
        final BareJid contactJID = JidCreate.entityBareFrom("nurse@example.com");
        final String contactName = "Nurse";
        final String[] contactGroup = new String[]{ "" };
        // Setup
        Assert.assertNotNull("Can't get the roster from the provided connection!", roster);
        initRoster();
        rosterListener.reset();
        // Adding the new roster item
        final RosterTest.RosterUpdateResponder serverSimulator = new RosterTest.RosterUpdateResponder() {
            @Override
            void verifyUpdateRequest(final RosterPacket updateRequest) {
                final Item item = updateRequest.getRosterItems().iterator().next();
                Assert.assertSame("The provided JID doesn't match the requested!", contactJID, item.getJid());
                Assert.assertSame("The provided name doesn't match the requested!", contactName, item.getName());
                Assert.assertSame("Shouldn't provide an empty group element!", 0, item.getGroupNames().size());
            }
        };
        serverSimulator.start();
        roster.createEntry(contactJID, contactName, contactGroup);
        serverSimulator.join();
        // Check if an error occurred within the simulator
        final Throwable exception = serverSimulator.getException();
        if (exception != null) {
            throw exception;
        }
        waitUntilInvocationOrTimeout();
        // Verify the roster entry of the new contact
        final RosterEntry addedEntry = roster.getEntry(contactJID);
        Assert.assertNotNull("The new contact wasn't added to the roster!", addedEntry);
        Assert.assertTrue("The roster listener wasn't invoked for the new contact!", rosterListener.getAddedAddresses().contains(contactJID));
        Assert.assertSame("Setup wrong name for the new contact!", contactName, addedEntry.getName());
        Assert.assertSame("Setup wrong default subscription status!", none, addedEntry.getType());
        Assert.assertSame("The new contact shouldn't be member of any group!", 0, addedEntry.getGroups().size());
        // Verify the unchanged roster items
        RosterTest.verifyRomeosEntry(roster.getEntry(JidCreate.entityBareFrom("romeo@example.net")));
        RosterTest.verifyMercutiosEntry(roster.getEntry(JidCreate.entityBareFrom("mercutio@example.com")));
        RosterTest.verifyBenvoliosEntry(roster.getEntry(JidCreate.entityBareFrom("benvolio@example.net")));
        Assert.assertSame("Wrong number of roster entries.", 4, roster.getEntries().size());
    }

    /**
     * Test processing a roster push with an empty group is equivalent with providing
     * no group.
     *
     * @see <a href="http://www.igniterealtime.org/issues/browse/SMACK-294">SMACK-294</a>
     */
    @Test
    public void testEmptyGroupRosterPush() throws Throwable {
        final BareJid contactJID = JidCreate.entityBareFrom("nurse@example.com");
        Assert.assertNotNull("Can't get the roster from the provided connection!", roster);
        final StringBuilder sb = new StringBuilder();
        sb.append("<iq id=\"rostertest2\" type=\"set\" ").append("to=\"").append(connection.getUser()).append("\">").append("<query xmlns=\"jabber:iq:roster\">").append("<item jid=\"").append(contactJID).append("\">").append("<group></group>").append("</item>").append("</query>").append("</iq>");
        final XmlPullParser parser = TestUtils.getIQParser(sb.toString());
        final IQ rosterPush = PacketParserUtils.parseIQ(parser);
        initRoster();
        rosterListener.reset();
        // Simulate receiving the roster push
        connection.processStanza(rosterPush);
        waitUntilInvocationOrTimeout();
        // Verify the roster entry of the new contact
        final RosterEntry addedEntry = roster.getEntry(contactJID);
        Assert.assertNotNull("The new contact wasn't added to the roster!", addedEntry);
        Assert.assertTrue("The roster listener wasn't invoked for the new contact!", rosterListener.getAddedAddresses().contains(contactJID));
        Assert.assertSame("Setup wrong default subscription status!", none, addedEntry.getType());
        Assert.assertSame("The new contact shouldn't be member of any group!", 0, addedEntry.getGroups().size());
        // Verify the unchanged roster items
        RosterTest.verifyRomeosEntry(roster.getEntry(JidCreate.entityBareFrom("romeo@example.net")));
        RosterTest.verifyMercutiosEntry(roster.getEntry(JidCreate.entityBareFrom("mercutio@example.com")));
        RosterTest.verifyBenvoliosEntry(roster.getEntry(JidCreate.entityBareFrom("benvolio@example.net")));
        Assert.assertSame("Wrong number of roster entries.", 4, roster.getEntries().size());
    }

    /**
     * This class can be used to simulate the server response for
     * a roster update request.
     */
    private abstract class RosterUpdateResponder extends Thread {
        private Throwable exception = null;

        /**
         * Overwrite this method to check if the received update request is valid.
         *
         * @param updateRequest
         * 		the request which would be sent to the server.
         */
        abstract void verifyUpdateRequest(RosterPacket updateRequest);

        @Override
        public void run() {
            try {
                while (true) {
                    final Stanza packet = connection.getSentPacket();
                    if ((packet instanceof RosterPacket) && ((getType()) == (Type.set))) {
                        final RosterPacket rosterRequest = ((RosterPacket) (packet));
                        // Prepare and process the roster push
                        final RosterPacket rosterPush = new RosterPacket();
                        final Item item = rosterRequest.getRosterItems().iterator().next();
                        if ((item.getItemType()) != (ItemType.remove)) {
                            item.setItemType(none);
                        }
                        rosterPush.setType(set);
                        rosterPush.setTo(connection.getUser());
                        rosterPush.addRosterItem(item);
                        connection.processStanza(rosterPush);
                        // Create and process the IQ response
                        final IQ response = IQ.createResultIQ(rosterRequest);
                        connection.processStanza(response);
                        // Verify the roster update request
                        if ((rosterRequest.getRosterItemCount()) != 1) {
                            throw new AssertionError("A roster set MUST contain one and only one <item/> element.");
                        }
                        verifyUpdateRequest(rosterRequest);
                        break;
                    }
                } 
            } catch (Throwable e) {
                exception = e;
                Assert.fail(e.getMessage());
            }
        }

        /**
         * Returns the exception or error if something went wrong.
         *
         * @return the Throwable exception or error that occurred.
         */
        public Throwable getException() {
            return exception;
        }
    }

    /**
     * This class can be used to check if the RosterListener was invoked.
     */
    public static class TestRosterListener extends WaitForPacketListener implements RosterListener {
        private final List<Jid> addressesAdded = new CopyOnWriteArrayList<>();

        private final List<Jid> addressesDeleted = new CopyOnWriteArrayList<>();

        private final List<Jid> addressesUpdated = new CopyOnWriteArrayList<>();

        @Override
        public synchronized void entriesAdded(Collection<Jid> addresses) {
            addressesAdded.addAll(addresses);
            reportInvoked();
        }

        @Override
        public synchronized void entriesDeleted(Collection<Jid> addresses) {
            addressesDeleted.addAll(addresses);
            reportInvoked();
        }

        @Override
        public synchronized void entriesUpdated(Collection<Jid> addresses) {
            addressesUpdated.addAll(addresses);
            reportInvoked();
        }

        @Override
        public void presenceChanged(Presence presence) {
            reportInvoked();
        }

        /**
         * Get a collection of JIDs of the added roster items.
         *
         * @return the collection of addresses which were added.
         */
        public Collection<Jid> getAddedAddresses() {
            return Collections.unmodifiableCollection(addressesAdded);
        }

        /**
         * Get a collection of JIDs of the deleted roster items.
         *
         * @return the collection of addresses which were deleted.
         */
        public Collection<Jid> getDeletedAddresses() {
            return Collections.unmodifiableCollection(addressesDeleted);
        }

        /**
         * Get a collection of JIDs of the updated roster items.
         *
         * @return the collection of addresses which were updated.
         */
        public Collection<Jid> getUpdatedAddresses() {
            return Collections.unmodifiableCollection(addressesUpdated);
        }

        public boolean addedAddressesContains(String jidString) {
            Jid jid;
            try {
                jid = JidCreate.from(jidString);
            } catch (XmppStringprepException e) {
                throw new IllegalArgumentException(e);
            }
            return addressesAdded.contains(jid);
        }

        public boolean deletedAddressesContains(String jidString) {
            Jid jid;
            try {
                jid = JidCreate.from(jidString);
            } catch (XmppStringprepException e) {
                throw new IllegalArgumentException(e);
            }
            return addressesDeleted.contains(jid);
        }

        public boolean updatedAddressesContains(String jidString) {
            Jid jid;
            try {
                jid = JidCreate.from(jidString);
            } catch (XmppStringprepException e) {
                throw new IllegalArgumentException(e);
            }
            return addressesUpdated.contains(jid);
        }

        /**
         * Reset the lists of added, deleted or updated items.
         */
        @Override
        public synchronized void reset() {
            super.reset();
            addressesAdded.clear();
            addressesDeleted.clear();
            addressesUpdated.clear();
        }
    }
}

