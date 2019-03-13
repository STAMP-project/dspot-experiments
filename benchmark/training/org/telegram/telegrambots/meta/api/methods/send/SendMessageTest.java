package org.telegram.telegrambots.meta.api.methods.send;


import org.junit.Assert;
import org.junit.Test;


public class SendMessageTest {
    @Test
    public void comparison() throws Exception {
        SendMessage sm1 = new SendMessage().setChatId(1L).setText("Hello World");
        SendMessage sm2 = new SendMessage().setChatId(1L).setText("Hello World");
        SendMessage noMessage = new SendMessage().setChatId(1L);
        SendMessage disabledNotification = new SendMessage().setChatId(1L).setText("Hello World").disableNotification();
        Assert.assertTrue(sm1.equals(sm2));
        Assert.assertFalse(sm1.equals(noMessage));
        Assert.assertFalse(sm1.equals(disabledNotification));
        Assert.assertTrue(((sm1.hashCode()) == (sm2.hashCode())));
        Assert.assertFalse(((sm1.hashCode()) == (noMessage.hashCode())));
        Assert.assertFalse(((sm1.hashCode()) == (disabledNotification.hashCode())));
    }
}

