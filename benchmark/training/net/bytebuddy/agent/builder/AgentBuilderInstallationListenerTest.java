package net.bytebuddy.agent.builder;


import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.InstallationListener.NoOp.INSTANCE;
import static net.bytebuddy.agent.builder.AgentBuilder.InstallationListener.StreamWriting.toSystemError;
import static net.bytebuddy.agent.builder.AgentBuilder.InstallationListener.StreamWriting.toSystemOut;


public class AgentBuilderInstallationListenerTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Instrumentation instrumentation;

    @Mock
    private ResettableClassFileTransformer classFileTransformer;

    @Mock
    private Throwable throwable;

    @Test
    public void testNoOpListener() throws Exception {
        INSTANCE.onBeforeInstall(instrumentation, classFileTransformer);
        INSTANCE.onInstall(instrumentation, classFileTransformer);
        MatcherAssert.assertThat(INSTANCE.onError(instrumentation, classFileTransformer, throwable), CoreMatchers.is(throwable));
        INSTANCE.onReset(instrumentation, classFileTransformer);
        Mockito.verifyZeroInteractions(instrumentation, classFileTransformer, throwable);
    }

    @Test
    public void testPseudoAdapter() throws Exception {
        AgentBuilder.InstallationListener pseudoAdapter = new AgentBuilderInstallationListenerTest.PseudoAdapter();
        pseudoAdapter.onBeforeInstall(instrumentation, classFileTransformer);
        pseudoAdapter.onInstall(instrumentation, classFileTransformer);
        MatcherAssert.assertThat(pseudoAdapter.onError(instrumentation, classFileTransformer, throwable), CoreMatchers.is(throwable));
        pseudoAdapter.onReset(instrumentation, classFileTransformer);
        Mockito.verifyZeroInteractions(instrumentation, classFileTransformer, throwable);
    }

    @Test
    public void testErrorSuppressing() throws Exception {
        AgentBuilder.InstallationListener.ErrorSuppressing.INSTANCE.onBeforeInstall(instrumentation, classFileTransformer);
        AgentBuilder.InstallationListener.ErrorSuppressing.INSTANCE.onInstall(instrumentation, classFileTransformer);
        INSTANCE.onReset(instrumentation, classFileTransformer);
        Mockito.verifyZeroInteractions(instrumentation, classFileTransformer, throwable);
    }

    @Test
    public void testErrorSuppressingError() throws Exception {
        MatcherAssert.assertThat(AgentBuilder.InstallationListener.ErrorSuppressing.INSTANCE.onError(instrumentation, classFileTransformer, throwable), CoreMatchers.nullValue(Throwable.class));
    }

    @Test
    public void testStreamWritingListenerBeforeInstall() throws Exception {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        AgentBuilder.InstallationListener installationListener = new AgentBuilder.InstallationListener.StreamWriting(printStream);
        installationListener.onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(printStream).printf("[Byte Buddy] BEFORE_INSTALL %s on %s%n", classFileTransformer, instrumentation);
        Mockito.verifyNoMoreInteractions(printStream);
    }

    @Test
    public void testStreamWritingListenerInstall() throws Exception {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        AgentBuilder.InstallationListener installationListener = new AgentBuilder.InstallationListener.StreamWriting(printStream);
        installationListener.onInstall(instrumentation, classFileTransformer);
        Mockito.verify(printStream).printf("[Byte Buddy] INSTALL %s on %s%n", classFileTransformer, instrumentation);
        Mockito.verifyNoMoreInteractions(printStream);
    }

    @Test
    public void testStreamWritingListenerError() throws Exception {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        AgentBuilder.InstallationListener installationListener = new AgentBuilder.InstallationListener.StreamWriting(printStream);
        MatcherAssert.assertThat(installationListener.onError(instrumentation, classFileTransformer, throwable), CoreMatchers.is(throwable));
        Mockito.verify(printStream).printf("[Byte Buddy] ERROR %s on %s%n", classFileTransformer, instrumentation);
        Mockito.verifyNoMoreInteractions(printStream);
        Mockito.verify(throwable).printStackTrace(printStream);
        Mockito.verifyNoMoreInteractions(throwable);
    }

    @Test
    public void testStreamWritingListenerReset() throws Exception {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        AgentBuilder.InstallationListener installationListener = new AgentBuilder.InstallationListener.StreamWriting(printStream);
        installationListener.onReset(instrumentation, classFileTransformer);
        Mockito.verify(printStream).printf("[Byte Buddy] RESET %s on %s%n", classFileTransformer, instrumentation);
        Mockito.verifyNoMoreInteractions(printStream);
    }

    @Test
    public void testCompoundListenerBeforeInstall() throws Exception {
        AgentBuilder.InstallationListener first = Mockito.mock(AgentBuilder.InstallationListener.class);
        AgentBuilder.InstallationListener second = Mockito.mock(AgentBuilder.InstallationListener.class);
        AgentBuilder.InstallationListener installationListener = new AgentBuilder.InstallationListener.Compound(first, second);
        installationListener.onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(first).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verify(second).onBeforeInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(first, second);
    }

    @Test
    public void testCompoundListenerInstall() throws Exception {
        AgentBuilder.InstallationListener first = Mockito.mock(AgentBuilder.InstallationListener.class);
        AgentBuilder.InstallationListener second = Mockito.mock(AgentBuilder.InstallationListener.class);
        AgentBuilder.InstallationListener installationListener = new AgentBuilder.InstallationListener.Compound(first, second);
        installationListener.onInstall(instrumentation, classFileTransformer);
        Mockito.verify(first).onInstall(instrumentation, classFileTransformer);
        Mockito.verify(second).onInstall(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(first, second);
    }

    @Test
    public void testCompoundListenerError() throws Exception {
        AgentBuilder.InstallationListener first = Mockito.mock(AgentBuilder.InstallationListener.class);
        AgentBuilder.InstallationListener second = Mockito.mock(AgentBuilder.InstallationListener.class);
        Mockito.when(first.onError(instrumentation, classFileTransformer, throwable)).thenReturn(throwable);
        Mockito.when(second.onError(instrumentation, classFileTransformer, throwable)).thenReturn(throwable);
        AgentBuilder.InstallationListener installationListener = new AgentBuilder.InstallationListener.Compound(first, second);
        MatcherAssert.assertThat(installationListener.onError(instrumentation, classFileTransformer, throwable), CoreMatchers.is(throwable));
        Mockito.verify(first).onError(instrumentation, classFileTransformer, throwable);
        Mockito.verify(second).onError(instrumentation, classFileTransformer, throwable);
        Mockito.verifyNoMoreInteractions(first, second);
    }

    @Test
    public void testCompoundListenerErrorHandled() throws Exception {
        AgentBuilder.InstallationListener first = Mockito.mock(AgentBuilder.InstallationListener.class);
        AgentBuilder.InstallationListener second = Mockito.mock(AgentBuilder.InstallationListener.class);
        Mockito.when(first.onError(instrumentation, classFileTransformer, throwable)).thenReturn(null);
        AgentBuilder.InstallationListener installationListener = new AgentBuilder.InstallationListener.Compound(first, second);
        MatcherAssert.assertThat(installationListener.onError(instrumentation, classFileTransformer, throwable), CoreMatchers.nullValue(Throwable.class));
        Mockito.verify(first).onError(instrumentation, classFileTransformer, throwable);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyZeroInteractions(second);
    }

    @Test
    public void testCompoundListenerReset() throws Exception {
        AgentBuilder.InstallationListener first = Mockito.mock(AgentBuilder.InstallationListener.class);
        AgentBuilder.InstallationListener second = Mockito.mock(AgentBuilder.InstallationListener.class);
        AgentBuilder.InstallationListener installationListener = new AgentBuilder.InstallationListener.Compound(first, second);
        installationListener.onReset(instrumentation, classFileTransformer);
        Mockito.verify(first).onReset(instrumentation, classFileTransformer);
        Mockito.verify(second).onReset(instrumentation, classFileTransformer);
        Mockito.verifyNoMoreInteractions(first, second);
    }

    @Test
    public void testStreamWritingToSystem() throws Exception {
        MatcherAssert.assertThat(toSystemOut(), FieldByFieldComparison.hasPrototype(((AgentBuilder.InstallationListener) (new AgentBuilder.InstallationListener.StreamWriting(System.out)))));
        MatcherAssert.assertThat(toSystemError(), FieldByFieldComparison.hasPrototype(((AgentBuilder.InstallationListener) (new AgentBuilder.InstallationListener.StreamWriting(System.err)))));
    }

    /* empty */
    private static class PseudoAdapter extends AgentBuilder.InstallationListener.Adapter {}
}

