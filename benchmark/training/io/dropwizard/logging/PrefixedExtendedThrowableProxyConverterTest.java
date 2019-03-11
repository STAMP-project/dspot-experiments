package io.dropwizard.logging;


import ch.qos.logback.classic.spi.ThrowableProxy;
import java.io.IOException;
import org.junit.jupiter.api.Test;


public class PrefixedExtendedThrowableProxyConverterTest {
    private final PrefixedExtendedThrowableProxyConverter converter = new PrefixedExtendedThrowableProxyConverter();

    private final ThrowableProxy proxy = new ThrowableProxy(new IOException("noo"));

    @Test
    public void prefixesExceptionsWithExclamationMarks() throws Exception {
        assertThat(converter.throwableProxyToString(proxy)).startsWith(String.format(("! java.io.IOException: noo%n" + "! at io.dropwizard.logging.PrefixedExtendedThrowableProxyConverterTest.<init>(PrefixedExtendedThrowableProxyConverterTest.java:14)%n")));
    }
}

