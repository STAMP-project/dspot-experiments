package org.apereo.cas.support.sms;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link TextMagicSmsSenderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class TextMagicSmsSenderTests {
    @Test
    public void verifyAction() {
        val sender = new TextMagicSmsSender("casuser", "test-token");
        Assertions.assertFalse(sender.send("123456678", "123456678", "Msg"));
    }
}

