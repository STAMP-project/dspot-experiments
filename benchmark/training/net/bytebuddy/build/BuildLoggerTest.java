package net.bytebuddy.build;


import BuildLogger.NoOp.INSTANCE;
import BuildLogger.StreamWriting;
import java.io.PrintStream;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;


public class BuildLoggerTest {
    private static final String FOO = "foo";

    @Test
    public void testNonOperational() {
        MatcherAssert.assertThat(INSTANCE.isDebugEnabled(), Is.is(false));
        MatcherAssert.assertThat(INSTANCE.isInfoEnabled(), Is.is(false));
        MatcherAssert.assertThat(INSTANCE.isWarnEnabled(), Is.is(false));
        MatcherAssert.assertThat(INSTANCE.isErrorEnabled(), Is.is(false));
        INSTANCE.debug(BuildLoggerTest.FOO);
        INSTANCE.debug(BuildLoggerTest.FOO, new RuntimeException());
        INSTANCE.info(BuildLoggerTest.FOO);
        INSTANCE.info(BuildLoggerTest.FOO, new RuntimeException());
        INSTANCE.warn(BuildLoggerTest.FOO);
        INSTANCE.warn(BuildLoggerTest.FOO, new RuntimeException());
        INSTANCE.error(BuildLoggerTest.FOO);
        INSTANCE.error(BuildLoggerTest.FOO, new RuntimeException());
    }

    @Test
    public void testAdapterNonOperational() {
        BuildLogger buildLogger = new BuildLogger.Adapter() {};
        MatcherAssert.assertThat(buildLogger.isDebugEnabled(), Is.is(false));
        MatcherAssert.assertThat(buildLogger.isInfoEnabled(), Is.is(false));
        MatcherAssert.assertThat(buildLogger.isWarnEnabled(), Is.is(false));
        MatcherAssert.assertThat(buildLogger.isErrorEnabled(), Is.is(false));
        buildLogger.debug(BuildLoggerTest.FOO);
        buildLogger.debug(BuildLoggerTest.FOO, new RuntimeException());
        buildLogger.info(BuildLoggerTest.FOO);
        buildLogger.info(BuildLoggerTest.FOO, new RuntimeException());
        buildLogger.warn(BuildLoggerTest.FOO);
        buildLogger.warn(BuildLoggerTest.FOO, new RuntimeException());
        buildLogger.error(BuildLoggerTest.FOO);
        buildLogger.error(BuildLoggerTest.FOO, new RuntimeException());
    }

    @Test
    public void testStreamWriting() {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        BuildLogger buildLogger = new BuildLogger.StreamWriting(printStream);
        MatcherAssert.assertThat(buildLogger.isDebugEnabled(), Is.is(true));
        MatcherAssert.assertThat(buildLogger.isInfoEnabled(), Is.is(true));
        MatcherAssert.assertThat(buildLogger.isWarnEnabled(), Is.is(true));
        MatcherAssert.assertThat(buildLogger.isErrorEnabled(), Is.is(true));
        Throwable throwable = Mockito.mock(Throwable.class);
        buildLogger.debug(BuildLoggerTest.FOO);
        buildLogger.debug(BuildLoggerTest.FOO, throwable);
        buildLogger.info(BuildLoggerTest.FOO);
        buildLogger.info(BuildLoggerTest.FOO, throwable);
        buildLogger.warn(BuildLoggerTest.FOO);
        buildLogger.warn(BuildLoggerTest.FOO, throwable);
        buildLogger.error(BuildLoggerTest.FOO);
        buildLogger.error(BuildLoggerTest.FOO, throwable);
        Mockito.verify(printStream, Mockito.times(8)).print(BuildLoggerTest.FOO);
        Mockito.verifyNoMoreInteractions(printStream);
        Mockito.verify(throwable, Mockito.times(4)).printStackTrace(printStream);
        Mockito.verifyNoMoreInteractions(throwable);
    }

    @Test
    public void testStreamWritingDefaults() {
        MatcherAssert.assertThat(StreamWriting.toSystemOut(), FieldByFieldComparison.hasPrototype(((BuildLogger) (new BuildLogger.StreamWriting(System.out)))));
        MatcherAssert.assertThat(StreamWriting.toSystemError(), FieldByFieldComparison.hasPrototype(((BuildLogger) (new BuildLogger.StreamWriting(System.err)))));
    }

    @Test
    public void testCompoundInactive() {
        BuildLogger delegate = Mockito.mock(BuildLogger.class);
        BuildLogger buildLogger = new BuildLogger.Compound(delegate);
        MatcherAssert.assertThat(buildLogger.isDebugEnabled(), Is.is(false));
        MatcherAssert.assertThat(buildLogger.isInfoEnabled(), Is.is(false));
        MatcherAssert.assertThat(buildLogger.isWarnEnabled(), Is.is(false));
        MatcherAssert.assertThat(buildLogger.isErrorEnabled(), Is.is(false));
        Throwable throwable = new Throwable();
        buildLogger.debug(BuildLoggerTest.FOO);
        buildLogger.debug(BuildLoggerTest.FOO, throwable);
        buildLogger.info(BuildLoggerTest.FOO);
        buildLogger.info(BuildLoggerTest.FOO, throwable);
        buildLogger.warn(BuildLoggerTest.FOO);
        buildLogger.warn(BuildLoggerTest.FOO, throwable);
        buildLogger.error(BuildLoggerTest.FOO);
        buildLogger.error(BuildLoggerTest.FOO, throwable);
        Mockito.verify(delegate, Mockito.never()).debug(BuildLoggerTest.FOO);
        Mockito.verify(delegate, Mockito.never()).debug(BuildLoggerTest.FOO, throwable);
        Mockito.verify(delegate, Mockito.never()).info(BuildLoggerTest.FOO);
        Mockito.verify(delegate, Mockito.never()).info(BuildLoggerTest.FOO, throwable);
        Mockito.verify(delegate, Mockito.never()).warn(BuildLoggerTest.FOO);
        Mockito.verify(delegate, Mockito.never()).warn(BuildLoggerTest.FOO, throwable);
        Mockito.verify(delegate, Mockito.never()).error(BuildLoggerTest.FOO);
        Mockito.verify(delegate, Mockito.never()).error(BuildLoggerTest.FOO, throwable);
    }

    @Test
    public void testCompoundActive() {
        BuildLogger delegate = Mockito.mock(BuildLogger.class);
        Mockito.when(delegate.isDebugEnabled()).thenReturn(true);
        Mockito.when(delegate.isInfoEnabled()).thenReturn(true);
        Mockito.when(delegate.isWarnEnabled()).thenReturn(true);
        Mockito.when(delegate.isErrorEnabled()).thenReturn(true);
        BuildLogger buildLogger = new BuildLogger.Compound(delegate);
        MatcherAssert.assertThat(buildLogger.isDebugEnabled(), Is.is(true));
        MatcherAssert.assertThat(buildLogger.isInfoEnabled(), Is.is(true));
        MatcherAssert.assertThat(buildLogger.isWarnEnabled(), Is.is(true));
        MatcherAssert.assertThat(buildLogger.isErrorEnabled(), Is.is(true));
        Throwable throwable = new Throwable();
        buildLogger.debug(BuildLoggerTest.FOO);
        buildLogger.debug(BuildLoggerTest.FOO, throwable);
        buildLogger.info(BuildLoggerTest.FOO);
        buildLogger.info(BuildLoggerTest.FOO, throwable);
        buildLogger.warn(BuildLoggerTest.FOO);
        buildLogger.warn(BuildLoggerTest.FOO, throwable);
        buildLogger.error(BuildLoggerTest.FOO);
        buildLogger.error(BuildLoggerTest.FOO, throwable);
        Mockito.verify(delegate).debug(BuildLoggerTest.FOO);
        Mockito.verify(delegate).debug(BuildLoggerTest.FOO, throwable);
        Mockito.verify(delegate).info(BuildLoggerTest.FOO);
        Mockito.verify(delegate).info(BuildLoggerTest.FOO, throwable);
        Mockito.verify(delegate).warn(BuildLoggerTest.FOO);
        Mockito.verify(delegate).warn(BuildLoggerTest.FOO, throwable);
        Mockito.verify(delegate).error(BuildLoggerTest.FOO);
        Mockito.verify(delegate).error(BuildLoggerTest.FOO, throwable);
    }
}

