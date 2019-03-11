package org.apereo.cas.util.http;


import java.net.URL;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Francesco Cina
 * @since 4.1
 */
public class HttpMessageTests {
    @Test
    @SneakyThrows
    public void verifyAsyncArgIsTakenIntoAccount() {
        Assertions.assertTrue(new HttpMessage(new URL("http://www.google.com"), "messageToSend").isAsynchronous());
        Assertions.assertTrue(new HttpMessage(new URL("http://www.google.com"), "messageToSend", true).isAsynchronous());
        Assertions.assertFalse(new HttpMessage(new URL("http://www.google.com"), "messageToSend", false).isAsynchronous());
    }
}

