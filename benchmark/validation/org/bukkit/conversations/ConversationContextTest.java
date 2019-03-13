package org.bukkit.conversations;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ConversationContextTest {
    @Test
    public void TestFromWhom() {
        Conversable conversable = new FakeConversable();
        ConversationContext context = new ConversationContext(null, conversable, new HashMap<Object, Object>());
        Assert.assertEquals(conversable, context.getForWhom());
    }

    @Test
    public void TestPlugin() {
        Conversable conversable = new FakeConversable();
        ConversationContext context = new ConversationContext(null, conversable, new HashMap<Object, Object>());
        Assert.assertEquals(null, context.getPlugin());
    }

    @Test
    public void TestSessionData() {
        Conversable conversable = new FakeConversable();
        Map session = new HashMap();
        session.put("key", "value");
        ConversationContext context = new ConversationContext(null, conversable, session);
        Assert.assertEquals("value", context.getSessionData("key"));
    }
}

