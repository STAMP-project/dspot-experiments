package org.apereo.cas.logout;


import LogoutHttpMessage.LOGOUT_REQUEST_PARAMETER;
import java.net.URL;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link LogoutHttpMessageTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
public class LogoutHttpMessageTests {
    @Test
    public void verifyOperation() throws Exception {
        val message = new LogoutHttpMessage(new URL("https://github.com"), "LogoutMessage", false);
        Assertions.assertTrue(message.getMessage().startsWith(LOGOUT_REQUEST_PARAMETER));
    }
}

