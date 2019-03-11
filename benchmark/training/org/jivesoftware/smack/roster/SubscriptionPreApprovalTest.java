/**
 * Copyright ? 2015 Tom?? Havlas
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


import ItemType.none;
import Presence.Type.subscribed;
import SubscriptionPreApproval.INSTANCE;
import Type.set;
import org.jivesoftware.smack.DummyConnection;
import org.jivesoftware.smack.SmackException.FeatureNotSupportedException;
import org.jivesoftware.smack.im.InitSmackIm;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.packet.RosterPacket.Item;
import org.jivesoftware.smack.roster.packet.RosterPacket.ItemType;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;


/**
 * Tests that verifies the correct behavior of the pre-approval implementation.
 *
 * @see <a href="http://xmpp.org/rfcs/rfc6121.html#sub-preapproval">Pre-Approving a Subscription Request</a>
 * @author Tom?? Havlas
 */
public class SubscriptionPreApprovalTest extends InitSmackIm {
    private DummyConnection connection;

    private Roster roster;

    private RosterTest.TestRosterListener rosterListener;

    @Test(expected = FeatureNotSupportedException.class)
    public void testPreApprovalNotSupported() throws Throwable {
        final Jid contactJID = JidCreate.from("preapproval@example.com");
        roster.preApprove(contactJID.asBareJid());
    }

    @Test
    public void testPreApproveAndCreate() throws Throwable {
        final BareJid contactJID = JidCreate.bareFrom("preapproval@example.com");
        final String contactName = "PreApproval";
        final String[] contactGroup = new String[]{  };
        connection.enableStreamFeature(INSTANCE);
        final SubscriptionPreApprovalTest.PreApproveAndCreateEntryResponder serverSimulator = new SubscriptionPreApprovalTest.PreApproveAndCreateEntryResponder() {
            @Override
            void verifyRosterUpdateRequest(final RosterPacket updateRequest) {
                final Item item = updateRequest.getRosterItems().iterator().next();
                Assert.assertSame("The provided JID doesn't match the requested!", contactJID, item.getJid());
                Assert.assertSame("The provided name doesn't match the requested!", contactName, item.getName());
                Assert.assertSame("The provided group number doesn't match the requested!", 0, item.getGroupNames().size());
            }

            @Override
            void verifyPreApprovalRequest(Presence preApproval) {
                Assert.assertSame("The provided name doesn't match the requested!", contactJID, preApproval.getTo());
                Assert.assertSame("The provided presence type is incorrect!", subscribed, preApproval.getType());
            }
        };
        serverSimulator.start();
        roster.preApproveAndCreateEntry(contactJID, contactName, contactGroup);
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
        Assert.assertSame("The new contact should be member of exactly one group!", 0, addedEntry.getGroups().size());
    }

    /**
     * This class can be used to simulate the server response for
     * a pre approve request request.
     */
    private abstract class PreApproveAndCreateEntryResponder extends Thread {
        private Throwable exception = null;

        /**
         * Overwrite this method to check if the received roster update request is valid.
         *
         * @param updateRequest
         * 		the request which would be sent to the server.
         */
        abstract void verifyRosterUpdateRequest(RosterPacket updateRequest);

        /**
         * Overwrite this method to check if received pre-approval request is valid
         *
         * @param preApproval
         * 		the request which would be sent to server.
         */
        abstract void verifyPreApprovalRequest(Presence preApproval);

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
                        verifyRosterUpdateRequest(rosterRequest);
                        break;
                    } else
                        if ((packet instanceof Presence) && ((getType()) == (Type.subscribed))) {
                            final Presence approval = ((Presence) (packet));
                            verifyPreApprovalRequest(approval);
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
}

