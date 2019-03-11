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
package org.jivesoftware.smack.chat;


import ChatManager.MatchMode.BARE_JID;
import ChatManager.MatchMode.NONE;
import ChatManager.MatchMode.SUPPLIED_JID;
import JidTestUtil.DUMMY_AT_EXAMPLE_ORG;
import Type.chat;
import Type.groupchat;
import Type.headline;
import Type.normal;
import org.jivesoftware.smack.DummyConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.test.util.WaitForPacketListener;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings({ "deprecation", "ReferenceEquality" })
public class ChatConnectionTest {
    private DummyConnection dc;

    private ChatManager cm;

    private ChatConnectionTest.TestChatManagerListener listener;

    private WaitForPacketListener waitListener;

    @Test
    public void validateDefaultSetNormalIncludedFalse() {
        ChatManager.setDefaultIsNormalIncluded(false);
        Assert.assertFalse(ChatManager.getInstanceFor(new DummyConnection()).isNormalIncluded());
    }

    @Test
    public void validateDefaultSetNormalIncludedTrue() {
        ChatManager.setDefaultIsNormalIncluded(true);
        Assert.assertTrue(ChatManager.getInstanceFor(new DummyConnection()).isNormalIncluded());
    }

    @Test
    public void validateDefaultSetMatchModeNone() {
        ChatManager.setDefaultMatchMode(NONE);
        Assert.assertEquals(NONE, ChatManager.getInstanceFor(new DummyConnection()).getMatchMode());
    }

    @Test
    public void validateDefaultSetMatchModeEntityBareJid() {
        ChatManager.setDefaultMatchMode(BARE_JID);
        Assert.assertEquals(BARE_JID, ChatManager.getInstanceFor(new DummyConnection()).getMatchMode());
    }

    @Test
    public void validateMessageTypeWithDefaults1() {
        Message incomingChat = ChatConnectionTest.createChatPacket("134", true);
        incomingChat.setType(chat);
        processServerMessage(incomingChat);
        Assert.assertNotNull(listener.getNewChat());
    }

    @Test
    public void validateMessageTypeWithDefaults2() {
        Message incomingChat = ChatConnectionTest.createChatPacket("134", true);
        incomingChat.setType(normal);
        processServerMessage(incomingChat);
        Assert.assertNotNull(listener.getNewChat());
    }

    @Test
    public void validateMessageTypeWithDefaults3() {
        Message incomingChat = ChatConnectionTest.createChatPacket("134", true);
        incomingChat.setType(groupchat);
        processServerMessage(incomingChat);
        Assert.assertNull(listener.getNewChat());
    }

    @Test
    public void validateMessageTypeWithDefaults4() {
        Message incomingChat = ChatConnectionTest.createChatPacket("134", true);
        incomingChat.setType(headline);
        Assert.assertNull(listener.getNewChat());
    }

    @Test
    public void validateMessageTypeWithNoNormal1() {
        cm.setNormalIncluded(false);
        Message incomingChat = ChatConnectionTest.createChatPacket("134", true);
        incomingChat.setType(chat);
        processServerMessage(incomingChat);
        Assert.assertNotNull(listener.getNewChat());
    }

    @Test
    public void validateMessageTypeWithNoNormal2() {
        cm.setNormalIncluded(false);
        Message incomingChat = ChatConnectionTest.createChatPacket("134", true);
        incomingChat.setType(normal);
        processServerMessage(incomingChat);
        Assert.assertNull(listener.getNewChat());
    }

    // No thread behaviour
    @Test
    public void chatMatchedOnJIDWhenNoThreadBareMode() {
        // ChatManager.MatchMode.BARE_JID is the default, so setting required.
        ChatConnectionTest.TestMessageListener msgListener = new ChatConnectionTest.TestMessageListener();
        ChatConnectionTest.TestChatManagerListener listener = new ChatConnectionTest.TestChatManagerListener(msgListener);
        cm.addChatListener(listener);
        Stanza incomingChat = ChatConnectionTest.createChatPacket(null, true);
        processServerMessage(incomingChat);
        Chat newChat = listener.getNewChat();
        Assert.assertNotNull(newChat);
        // Should match on chat with full jid
        incomingChat = ChatConnectionTest.createChatPacket(null, true);
        processServerMessage(incomingChat);
        Assert.assertEquals(2, msgListener.getNumMessages());
        // Should match on chat with bare jid
        incomingChat = ChatConnectionTest.createChatPacket(null, false);
        processServerMessage(incomingChat);
        Assert.assertEquals(3, msgListener.getNumMessages());
    }

    @Test
    public void chatMatchedOnJIDWhenNoThreadJidMode() {
        ChatConnectionTest.TestMessageListener msgListener = new ChatConnectionTest.TestMessageListener();
        ChatConnectionTest.TestChatManagerListener listener = new ChatConnectionTest.TestChatManagerListener(msgListener);
        cm.setMatchMode(SUPPLIED_JID);
        cm.addChatListener(listener);
        Stanza incomingChat = ChatConnectionTest.createChatPacket(null, true);
        processServerMessage(incomingChat);
        Chat newChat = listener.getNewChat();
        Assert.assertNotNull(newChat);
        cm.removeChatListener(listener);
        // Should match on chat with full jid
        incomingChat = ChatConnectionTest.createChatPacket(null, true);
        processServerMessage(incomingChat);
        Assert.assertEquals(2, msgListener.getNumMessages());
        // Should not match on chat with bare jid
        ChatConnectionTest.TestChatManagerListener listener2 = new ChatConnectionTest.TestChatManagerListener();
        cm.addChatListener(listener2);
        incomingChat = ChatConnectionTest.createChatPacket(null, false);
        processServerMessage(incomingChat);
        Assert.assertEquals(2, msgListener.getNumMessages());
        Assert.assertNotNull(listener2.getNewChat());
    }

    @Test
    public void chatMatchedOnJIDWhenNoThreadNoneMode() {
        ChatConnectionTest.TestMessageListener msgListener = new ChatConnectionTest.TestMessageListener();
        ChatConnectionTest.TestChatManagerListener listener = new ChatConnectionTest.TestChatManagerListener(msgListener);
        cm.setMatchMode(NONE);
        cm.addChatListener(listener);
        Stanza incomingChat = ChatConnectionTest.createChatPacket(null, true);
        processServerMessage(incomingChat);
        Chat newChat = listener.getNewChat();
        Assert.assertNotNull(newChat);
        Assert.assertEquals(1, msgListener.getNumMessages());
        cm.removeChatListener(listener);
        // Should not match on chat with full jid
        ChatConnectionTest.TestChatManagerListener listener2 = new ChatConnectionTest.TestChatManagerListener();
        cm.addChatListener(listener2);
        incomingChat = ChatConnectionTest.createChatPacket(null, true);
        processServerMessage(incomingChat);
        Assert.assertEquals(1, msgListener.getNumMessages());
        Assert.assertNotNull(newChat);
        cm.removeChatListener(listener2);
        // Should not match on chat with bare jid
        ChatConnectionTest.TestChatManagerListener listener3 = new ChatConnectionTest.TestChatManagerListener();
        cm.addChatListener(listener3);
        incomingChat = ChatConnectionTest.createChatPacket(null, false);
        processServerMessage(incomingChat);
        Assert.assertEquals(1, msgListener.getNumMessages());
        Assert.assertNotNull(listener3.getNewChat());
    }

    /**
     * Confirm that an existing chat created with a base jid is matched to an incoming chat message that has no thread
     * id and the user is a full jid.
     */
    @Test
    public void chatFoundWhenNoThreadEntityFullJid() {
        Chat outgoing = cm.createChat(DUMMY_AT_EXAMPLE_ORG, null);
        Stanza incomingChat = ChatConnectionTest.createChatPacket(null, true);
        processServerMessage(incomingChat);
        Chat newChat = listener.getNewChat();
        Assert.assertNotNull(newChat);
        Assert.assertTrue((newChat == outgoing));
    }

    /**
     * Confirm that an existing chat created with a base jid is matched to an incoming chat message that has no thread
     * id and the user is a base jid.
     */
    @Test
    public void chatFoundWhenNoThreadBaseJid() {
        Chat outgoing = cm.createChat(DUMMY_AT_EXAMPLE_ORG, null);
        Stanza incomingChat = ChatConnectionTest.createChatPacket(null, false);
        processServerMessage(incomingChat);
        Chat newChat = listener.getNewChat();
        Assert.assertNotNull(newChat);
        Assert.assertTrue((newChat == outgoing));
    }

    /**
     * Confirm that an existing chat created with a base jid is matched to an incoming chat message that has the same id
     * and the user is a full jid.
     */
    @Test
    public void chatFoundWithSameThreadEntityFullJid() {
        Chat outgoing = cm.createChat(DUMMY_AT_EXAMPLE_ORG, null);
        Stanza incomingChat = ChatConnectionTest.createChatPacket(outgoing.getThreadID(), true);
        processServerMessage(incomingChat);
        Chat newChat = listener.getNewChat();
        Assert.assertNotNull(newChat);
        Assert.assertTrue((newChat == outgoing));
    }

    /**
     * Confirm that an existing chat created with a base jid is matched to an incoming chat message that has the same id
     * and the user is a base jid.
     */
    @Test
    public void chatFoundWithSameThreadBaseJid() {
        Chat outgoing = cm.createChat(DUMMY_AT_EXAMPLE_ORG, null);
        Stanza incomingChat = ChatConnectionTest.createChatPacket(outgoing.getThreadID(), false);
        processServerMessage(incomingChat);
        Chat newChat = listener.getNewChat();
        Assert.assertNotNull(newChat);
        Assert.assertTrue((newChat == outgoing));
    }

    /**
     * Confirm that an existing chat created with a base jid is not matched to an incoming chat message that has a
     * different id and the same user as a base jid.
     */
    @Test
    public void chatNotFoundWithDiffThreadBaseJid() {
        Chat outgoing = cm.createChat(DUMMY_AT_EXAMPLE_ORG, null);
        Stanza incomingChat = ChatConnectionTest.createChatPacket(((outgoing.getThreadID()) + "ff"), false);
        processServerMessage(incomingChat);
        Chat newChat = listener.getNewChat();
        Assert.assertNotNull(newChat);
        Assert.assertFalse((newChat == outgoing));
    }

    /**
     * Confirm that an existing chat created with a base jid is not matched to an incoming chat message that has a
     * different id and the same base jid.
     */
    @Test
    public void chatNotFoundWithDiffThreadEntityFullJid() {
        Chat outgoing = cm.createChat(DUMMY_AT_EXAMPLE_ORG, null);
        Stanza incomingChat = ChatConnectionTest.createChatPacket(((outgoing.getThreadID()) + "ff"), true);
        processServerMessage(incomingChat);
        Chat newChat = listener.getNewChat();
        Assert.assertNotNull(newChat);
        Assert.assertFalse((newChat == outgoing));
    }

    @Test
    public void chatNotMatchedWithTypeNormal() {
        cm.setNormalIncluded(false);
        Message incomingChat = ChatConnectionTest.createChatPacket(null, false);
        incomingChat.setType(normal);
        processServerMessage(incomingChat);
        Assert.assertNull(listener.getNewChat());
    }

    static class TestChatManagerListener extends WaitForPacketListener implements ChatManagerListener {
        private Chat newChat;

        private ChatMessageListener listener;

        TestChatManagerListener(ChatConnectionTest.TestMessageListener msgListener) {
            listener = msgListener;
        }

        TestChatManagerListener() {
        }

        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            newChat = chat;
            if ((listener) != null)
                newChat.addMessageListener(listener);

            reportInvoked();
        }

        public Chat getNewChat() {
            return newChat;
        }
    }

    private static class TestChatServer extends Thread {
        private final Stanza chatPacket;

        private final DummyConnection con;

        TestChatServer(Stanza chatMsg, DummyConnection connection) {
            chatPacket = chatMsg;
            con = connection;
        }

        @Override
        public void run() {
            con.processStanza(chatPacket);
        }
    }

    private static class TestMessageListener implements ChatMessageListener {
        private Chat msgChat;

        private int counter = 0;

        @Override
        public void processMessage(Chat chat, Message message) {
            msgChat = chat;
            (counter)++;
        }

        @SuppressWarnings("unused")
        public Chat getChat() {
            return msgChat;
        }

        public int getNumMessages() {
            return counter;
        }
    }
}

