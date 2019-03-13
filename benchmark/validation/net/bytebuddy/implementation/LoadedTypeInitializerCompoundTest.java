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


public class LoadedTypeInitializerCompoundTest {
    private static final Class<?> TYPE = Object.class;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private LoadedTypeInitializer first;

    @Mock
    private LoadedTypeInitializer second;

    @Mock
    private InstrumentedType instrumentedType;

    @Mock
    private ByteCodeAppender byteCodeAppender;

    private LoadedTypeInitializer compound;

    @Test
    public void testIsAlive() throws Exception {
        Mockito.when(first.isAlive()).thenReturn(true);
        MatcherAssert.assertThat(compound.isAlive(), CoreMatchers.is(true));
        Mockito.verify(first).isAlive();
        Mockito.verifyNoMoreInteractions(first);
    }

    @Test
    public void testIsNotAlive() throws Exception {
        MatcherAssert.assertThat(compound.isAlive(), CoreMatchers.is(false));
        Mockito.verify(first).isAlive();
        Mockito.verify(second).isAlive();
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testApply() throws Exception {
        compound.onLoad(LoadedTypeInitializerCompoundTest.TYPE);
        Mockito.verify(first).onLoad(LoadedTypeInitializerCompoundTest.TYPE);
        Mockito.verify(second).onLoad(LoadedTypeInitializerCompoundTest.TYPE);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verifyNoMoreInteractions(second);
    }
}

