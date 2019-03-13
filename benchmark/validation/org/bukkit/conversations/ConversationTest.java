package org.bukkit.conversations;


import org.junit.Assert;
import org.junit.Test;

import static Prompt.END_OF_CONVERSATION;


/**
 *
 */
public class ConversationTest {
    @Test
    public void testBaseConversationFlow() {
        FakeConversable forWhom = new FakeConversable();
        Conversation conversation = new Conversation(null, forWhom, new ConversationTest.FirstPrompt());
        // Conversation not yet begun
        Assert.assertNull(forWhom.lastSentMessage);
        Assert.assertEquals(conversation.getForWhom(), forWhom);
        Assert.assertTrue(conversation.isModal());
        // Begin the conversation
        conversation.begin();
        Assert.assertEquals("FirstPrompt", forWhom.lastSentMessage);
        Assert.assertEquals(conversation, forWhom.begunConversation);
        // Send the first input
        conversation.acceptInput("FirstInput");
        Assert.assertEquals("SecondPrompt", forWhom.lastSentMessage);
        Assert.assertEquals(conversation, forWhom.abandonedConverstion);
    }

    @Test
    public void testConversationFactory() {
        FakeConversable forWhom = new FakeConversable();
        NullConversationPrefix prefix = new NullConversationPrefix();
        ConversationFactory factory = new ConversationFactory(null).withFirstPrompt(new ConversationTest.FirstPrompt()).withModality(false).withPrefix(prefix);
        Conversation conversation = factory.buildConversation(forWhom);
        // Conversation not yet begun
        Assert.assertNull(forWhom.lastSentMessage);
        Assert.assertEquals(conversation.getForWhom(), forWhom);
        Assert.assertFalse(conversation.isModal());
        Assert.assertEquals(conversation.getPrefix(), prefix);
        // Begin the conversation
        conversation.begin();
        Assert.assertEquals("FirstPrompt", forWhom.lastSentMessage);
        Assert.assertEquals(conversation, forWhom.begunConversation);
        // Send the first input
        conversation.acceptInput("FirstInput");
        Assert.assertEquals("SecondPrompt", forWhom.lastSentMessage);
        Assert.assertEquals(conversation, forWhom.abandonedConverstion);
    }

    @Test
    public void testEscapeSequence() {
        FakeConversable forWhom = new FakeConversable();
        Conversation conversation = new Conversation(null, forWhom, new ConversationTest.FirstPrompt());
        conversation.addConversationCanceller(new ExactMatchConversationCanceller("bananas"));
        // Begin the conversation
        conversation.begin();
        Assert.assertEquals("FirstPrompt", forWhom.lastSentMessage);
        Assert.assertEquals(conversation, forWhom.begunConversation);
        // Send the first input
        conversation.acceptInput("bananas");
        Assert.assertEquals("bananas", forWhom.lastSentMessage);
        Assert.assertEquals(conversation, forWhom.abandonedConverstion);
    }

    @Test
    public void testNotPlayer() {
        FakeConversable forWhom = new FakeConversable();
        NullConversationPrefix prefix = new NullConversationPrefix();
        ConversationFactory factory = new ConversationFactory(null).thatExcludesNonPlayersWithMessage("bye");
        Conversation conversation = factory.buildConversation(forWhom);
        // Begin the conversation
        conversation.begin();
        Assert.assertEquals("bye", forWhom.lastSentMessage);
        Assert.assertEquals(conversation, forWhom.begunConversation);
        Assert.assertEquals(conversation, forWhom.abandonedConverstion);
    }

    private class FirstPrompt extends StringPrompt {
        public String getPromptText(ConversationContext context) {
            return "FirstPrompt";
        }

        public Prompt acceptInput(ConversationContext context, String input) {
            Assert.assertEquals("FirstInput", input);
            context.setSessionData("data", 10);
            return new ConversationTest.SecondPrompt();
        }
    }

    private class SecondPrompt extends MessagePrompt {
        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return END_OF_CONVERSATION;
        }

        public String getPromptText(ConversationContext context) {
            // Assert that session data passes from one prompt to the next
            Assert.assertEquals(context.getSessionData("data"), 10);
            return "SecondPrompt";
        }
    }
}

