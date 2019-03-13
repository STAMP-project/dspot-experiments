/**
 * Copyright ? 2013-2014 Esko Luontola <www.orfjackal.net>
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda.test;


import java.io.Closeable;
import java.io.IOException;
import org.apache.commons.lang.SystemUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;


public class TryWithResourcesTest {
    @Test
    public void calls_close() throws IOException {
        Closeable closeable = Mockito.mock(Closeable.class);
        try (Closeable c = closeable) {
        }
        Mockito.verify(closeable).close();
    }

    @Test
    public void suppressed_exceptions() {
        Exception thrown;
        try {
            try (TryWithResourcesTest.ThrowSecondaryExceptionOnClose c = new TryWithResourcesTest.ThrowSecondaryExceptionOnClose()) {
                throw new TryWithResourcesTest.PrimaryException();
            }
        } catch (Exception e) {
            thrown = e;
        }
        MatcherAssert.assertThat("thrown", thrown, is(instanceOf(TryWithResourcesTest.PrimaryException.class)));
        MatcherAssert.assertThat("cause", thrown.getCause(), is(nullValue()));
        // On Java 6 and lower we will swallow the suppressed exception, because the API does not exist,
        // but on Java 7 we want to keep the original behavior.
        if (SystemUtils.isJavaVersionAtLeast(1.7F)) {
            MatcherAssert.assertThat("suppressed", thrown.getSuppressed(), arrayContaining(instanceOf(TryWithResourcesTest.SecondaryException.class)));
        }
    }

    private static class PrimaryException extends RuntimeException {}

    private static class SecondaryException extends RuntimeException {}

    private static class ThrowSecondaryExceptionOnClose implements Closeable {
        @Override
        public void close() {
            throw new TryWithResourcesTest.SecondaryException();
        }
    }
}

