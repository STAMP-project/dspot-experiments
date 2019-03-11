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
package org.jivesoftware.smack.roster.rosterstore;


import ItemType.both;
import ItemType.none;
import JidTestUtil.BARE_JID_1;
import JidTestUtil.BARE_JID_2;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.packet.RosterPacket.Item;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.JidTestUtil;


/**
 * Tests the implementation of {@link DirectoryRosterStore}.
 *
 * @author Lars Noschinski
 */
public class DirectoryRosterStoreTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Tests that opening an uninitialized directory fails.
     */
    @Test
    public void testStoreUninitialized() throws IOException {
        File storeDir = tmpFolder.newFolder();
        Assert.assertNull(DirectoryRosterStore.open(storeDir));
    }

    /**
     * Tests that an initialized directory is empty.
     */
    @Test
    public void testStoreInitializedEmpty() throws IOException {
        File storeDir = tmpFolder.newFolder();
        DirectoryRosterStore store = DirectoryRosterStore.init(storeDir);
        Assert.assertNotNull("Initialization returns store", store);
        Assert.assertEquals("Freshly initialized store must have empty version", "", store.getRosterVersion());
        Assert.assertEquals("Freshly initialized store must have no entries", 0, store.getEntries().size());
    }

    /**
     * Tests adding and removing entries.
     */
    @Test
    public void testStoreAddRemove() throws IOException {
        File storeDir = tmpFolder.newFolder();
        DirectoryRosterStore store = DirectoryRosterStore.init(storeDir);
        Assert.assertEquals("Initial roster version", "", store.getRosterVersion());
        BareJid userName = JidTestUtil.DUMMY_AT_EXAMPLE_ORG;
        final RosterPacket.Item item1 = new Item(userName, null);
        final String version1 = "1";
        store.addEntry(item1, version1);
        Assert.assertEquals("Adding entry sets version correctly", version1, store.getRosterVersion());
        RosterPacket.Item storedItem = store.getEntry(userName);
        Assert.assertNotNull("Added entry not found found", storedItem);
        Assert.assertEquals("User of added entry", item1.getJid(), storedItem.getJid());
        Assert.assertEquals("Name of added entry", item1.getName(), storedItem.getName());
        Assert.assertEquals("Groups", item1.getGroupNames(), storedItem.getGroupNames());
        Assert.assertEquals("ItemType of added entry", item1.getItemType(), storedItem.getItemType());
        Assert.assertEquals("ItemStatus of added entry", item1.isSubscriptionPending(), storedItem.isSubscriptionPending());
        Assert.assertEquals("Approved of added entry", item1.isApproved(), storedItem.isApproved());
        final String version2 = "2";
        final RosterPacket.Item item2 = new Item(userName, "Ursula Example");
        item2.addGroupName("users");
        item2.addGroupName("examples");
        item2.setSubscriptionPending(true);
        item2.setItemType(none);
        item2.setApproved(true);
        store.addEntry(item2, version2);
        Assert.assertEquals("Updating entry sets version correctly", version2, store.getRosterVersion());
        storedItem = store.getEntry(userName);
        Assert.assertNotNull("Added entry not found", storedItem);
        Assert.assertEquals("User of added entry", item2.getJid(), storedItem.getJid());
        Assert.assertEquals("Name of added entry", item2.getName(), storedItem.getName());
        Assert.assertEquals("Groups", item2.getGroupNames(), storedItem.getGroupNames());
        Assert.assertEquals("ItemType of added entry", item2.getItemType(), storedItem.getItemType());
        Assert.assertEquals("ItemStatus of added entry", item2.isSubscriptionPending(), storedItem.isSubscriptionPending());
        Assert.assertEquals("Approved of added entry", item2.isApproved(), storedItem.isApproved());
        List<Item> entries = store.getEntries();
        Assert.assertEquals("Number of entries", 1, entries.size());
        final RosterPacket.Item item3 = new Item(JidTestUtil.BARE_JID_1, "Foo Bar");
        item3.addGroupName("The Foo Fighters");
        item3.addGroupName("Bar Friends");
        item3.setSubscriptionPending(true);
        item3.setItemType(both);
        final RosterPacket.Item item4 = new Item(JidTestUtil.BARE_JID_2, "Baba Baz");
        item4.addGroupName("The Foo Fighters");
        item4.addGroupName("Bar Friends");
        item4.setSubscriptionPending(false);
        item4.setItemType(both);
        item4.setApproved(true);
        ArrayList<Item> items34 = new ArrayList<RosterPacket.Item>();
        items34.add(item3);
        items34.add(item4);
        String version3 = "3";
        store.resetEntries(items34, version3);
        storedItem = store.getEntry(BARE_JID_1);
        Assert.assertNotNull("Added entry not found", storedItem);
        Assert.assertEquals("User of added entry", item3.getJid(), storedItem.getJid());
        Assert.assertEquals("Name of added entry", item3.getName(), storedItem.getName());
        Assert.assertEquals("Groups", item3.getGroupNames(), storedItem.getGroupNames());
        Assert.assertEquals("ItemType of added entry", item3.getItemType(), storedItem.getItemType());
        Assert.assertEquals("ItemStatus of added entry", item3.isSubscriptionPending(), storedItem.isSubscriptionPending());
        Assert.assertEquals("Approved of added entry", item3.isApproved(), storedItem.isApproved());
        storedItem = store.getEntry(BARE_JID_2);
        Assert.assertNotNull("Added entry not found", storedItem);
        Assert.assertEquals("User of added entry", item4.getJid(), storedItem.getJid());
        Assert.assertEquals("Name of added entry", item4.getName(), storedItem.getName());
        Assert.assertEquals("Groups", item4.getGroupNames(), storedItem.getGroupNames());
        Assert.assertEquals("ItemType of added entry", item4.getItemType(), storedItem.getItemType());
        Assert.assertEquals("ItemStatus of added entry", item4.isSubscriptionPending(), storedItem.isSubscriptionPending());
        Assert.assertEquals("Approved of added entry", item4.isApproved(), storedItem.isApproved());
        entries = store.getEntries();
        Assert.assertEquals("Number of entries", 2, entries.size());
        String version4 = "4";
        store.removeEntry(BARE_JID_2, version4);
        Assert.assertEquals("Removing entry sets version correctly", version4, store.getRosterVersion());
        Assert.assertNull("Removed entry is gone", store.getEntry(userName));
        entries = store.getEntries();
        Assert.assertEquals("Number of entries", 1, entries.size());
    }
}

