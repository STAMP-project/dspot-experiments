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


public class ImplementationCompoundComposableTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Implementation.Composable first;

    @Mock
    private Implementation.Composable second;

    @Mock
    private Implementation.Composable third;

    @Mock
    private Implementation.Composable forth;

    @Mock
    private InstrumentedType instrumentedType;

    @Mock
    private Implementation.Target implementationTarget;

    @Mock
    private ByteCodeAppender byteCodeAppender;

    private Implementation.Composable compound;

    @Test
    public void testPrepare() throws Exception {
        Mockito.when(second.andThen(third)).thenReturn(forth);
        Mockito.when(first.prepare(instrumentedType)).thenReturn(instrumentedType);
        Mockito.when(forth.prepare(instrumentedType)).thenReturn(instrumentedType);
        MatcherAssert.assertThat(compound.andThen(third).prepare(instrumentedType), CoreMatchers.is(instrumentedType));
        Mockito.verify(first).prepare(instrumentedType);
        Mockito.verify(forth).prepare(instrumentedType);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(forth);
        Mockito.verify(second).andThen(third);
        Mockito.verifyNoMoreInteractions(second);
        Mockito.verifyZeroInteractions(third);
    }

    @Test
    public void testAppend() throws Exception {
        Mockito.when(second.andThen(third)).thenReturn(forth);
        Mockito.when(first.appender(implementationTarget)).thenReturn(byteCodeAppender);
        Mockito.when(forth.appender(implementationTarget)).thenReturn(byteCodeAppender);
        MatcherAssert.assertThat(compound.andThen(third).appender(implementationTarget), CoreMatchers.notNullValue());
        Mockito.verify(first).appender(implementationTarget);
        Mockito.verify(forth).appender(implementationTarget);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(forth);
        Mockito.verify(second).andThen(third);
        Mockito.verifyNoMoreInteractions(second);
        Mockito.verifyZeroInteractions(third);
    }
}

