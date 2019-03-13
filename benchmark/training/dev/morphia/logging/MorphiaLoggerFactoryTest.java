package dev.morphia.logging;


import dev.morphia.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author us@thomas-daily.de
 */
public class MorphiaLoggerFactoryTest extends TestBase {
    @Test
    public void testOverrideLoggerWithCustomOne() throws Exception {
        // given
        MorphiaLoggerFactory.reset();
        MorphiaLoggerFactory.registerLogger(MorphiaLoggerFactoryTest.TestLoggerFactory.class);
        // when
        final Logger logger = MorphiaLoggerFactory.get(Object.class);
        // then
        Assert.assertThat(((MorphiaLoggerFactoryTest.TestLogger) (logger)), CoreMatchers.isA(MorphiaLoggerFactoryTest.TestLogger.class));
    }

    static class TestLoggerFactory implements LoggerFactory {
        @Override
        public Logger get(final Class<?> c) {
            return new MorphiaLoggerFactoryTest.TestLogger();
        }
    }

    private static class TestLogger implements Logger {
        @Override
        public void debug(final String msg) {
        }

        @Override
        public void debug(final String format, final Object... arg) {
        }

        @Override
        public void debug(final String msg, final Throwable t) {
        }

        @Override
        public void error(final String msg) {
        }

        @Override
        public void error(final String format, final Object... arg) {
        }

        @Override
        public void error(final String msg, final Throwable t) {
        }

        @Override
        public void info(final String msg) {
        }

        @Override
        public void info(final String format, final Object... arg) {
        }

        @Override
        public void info(final String msg, final Throwable t) {
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public boolean isWarningEnabled() {
            return false;
        }

        @Override
        public void trace(final String msg) {
        }

        @Override
        public void trace(final String format, final Object... arg) {
        }

        @Override
        public void trace(final String msg, final Throwable t) {
        }

        @Override
        public void warning(final String msg) {
        }

        @Override
        public void warning(final String format, final Object... arg) {
        }

        @Override
        public void warning(final String msg, final Throwable t) {
        }
    }
}

