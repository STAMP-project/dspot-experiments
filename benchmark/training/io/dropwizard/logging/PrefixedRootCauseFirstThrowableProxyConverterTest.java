package io.dropwizard.logging;


import ch.qos.logback.classic.spi.ThrowableProxy;
import io.dropwizard.util.Strings;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link PrefixedRootCauseFirstThrowableProxyConverter}.
 */
public class PrefixedRootCauseFirstThrowableProxyConverterTest {
    private final PrefixedRootCauseFirstThrowableProxyConverter converter = new PrefixedRootCauseFirstThrowableProxyConverter();

    private final ThrowableProxy proxy = new ThrowableProxy(getException());

    @Test
    public void prefixesExceptionsWithExclamationMarks() {
        final List<String> stackTrace = Arrays.stream(converter.throwableProxyToString(proxy).split(System.lineSeparator())).filter(( s) -> !(Strings.isNullOrEmpty(s))).collect(Collectors.toList());
        assertThat(stackTrace).isNotEmpty().allSatisfy(( line) -> assertThat(line).startsWith("!"));
    }

    @Test
    public void placesRootCauseIsFirst() {
        assertThat(converter.throwableProxyToString(proxy)).matches(Pattern.compile((".+" + ((("java\\.net\\.SocketTimeoutException: Timed-out reading from socket.+" + "java\\.io\\.IOException: Fairly general error doing some IO.+") + "java\\.lang\\.RuntimeException: Very general error doing something") + ".+")), Pattern.DOTALL));
    }
}

