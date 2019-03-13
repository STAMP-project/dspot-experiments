package org.apereo.cas.support.sms;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link TwilioSmsSenderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class TwilioSmsSenderTests {
    @Test
    public void verifyAction() {
        val s = new TwilioSmsSender("accountid", "token");
        Assertions.assertFalse(s.send("123456789", "123456789", "Msg"));
    }
}

