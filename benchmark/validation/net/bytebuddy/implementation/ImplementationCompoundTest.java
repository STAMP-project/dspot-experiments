package net.bytebuddy.implementation;


import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ImplementationCompoundTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Implementation first;

    @Mock
    private Implementation second;

    @Mock
    private InstrumentedType instrumentedType;

    @Mock
    private Implementation.Target implementationTarget;

    @Mock
    private ByteCodeAppender byteCodeAppender;

    private Implementation compound;

    @Test
    public void testPrepare() throws Exception {
        Mockito.when(first.prepare(instrumentedType)).thenReturn(instrumentedType);
        Mockito.when(second.prepare(instrumentedType)).thenReturn(instrumentedType);
        MatcherAssert.assertThat(compound.prepare(instrumentedType), CoreMatchers.is(instrumentedType));
        Mockito.verify(first).prepare(instrumentedType);
        Mockito.verify(second).prepare(instrumentedType);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testAppend() throws Exception {
        Mockito.when(first.appender(implementationTarget)).thenReturn(byteCodeAppender);
        Mockito.when(second.appender(implementationTarget)).thenReturn(byteCodeAppender);
        MatcherAssert.assertThat(compound.appender(implementationTarget), CoreMatchers.notNullValue());
        Mockito.verify(first).appender(implementationTarget);
        Mockito.verify(second).appender(implementationTarget);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }
}

