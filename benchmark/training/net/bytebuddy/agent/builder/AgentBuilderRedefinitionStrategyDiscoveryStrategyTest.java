package net.bytebuddy.agent.builder;


import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.Reiterating.INSTANCE;


public class AgentBuilderRedefinitionStrategyDiscoveryStrategyTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Instrumentation instrumentation;

    @Test
    public void testSinglePass() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ String.class, Integer.class });
        Iterator<Iterable<Class<?>>> types = INSTANCE.resolve(instrumentation).iterator();
        Assert.assertThat(types.hasNext(), CoreMatchers.is(true));
        Assert.assertThat(types.next(), CoreMatchers.<Iterable<Class<?>>>equalTo(Arrays.<Class<?>>asList(String.class, Integer.class)));
        Assert.assertThat(types.hasNext(), CoreMatchers.is(false));
    }

    @Test
    public void testReiteration() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ String.class, Integer.class }, new Class<?>[]{ String.class, Integer.class, Void.class });
        Iterator<Iterable<Class<?>>> types = INSTANCE.resolve(instrumentation).iterator();
        Assert.assertThat(types.hasNext(), CoreMatchers.is(true));
        Assert.assertThat(types.next(), CoreMatchers.<Iterable<Class<?>>>equalTo(Arrays.<Class<?>>asList(String.class, Integer.class)));
        Assert.assertThat(types.hasNext(), CoreMatchers.is(true));
        Assert.assertThat(types.next(), CoreMatchers.<Iterable<Class<?>>>equalTo(Collections.<Class<?>>singletonList(Void.class)));
        Assert.assertThat(types.hasNext(), CoreMatchers.is(false));
    }

    @Test(expected = NoSuchElementException.class)
    public void testReiterationNoMoreElement() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[0]);
        INSTANCE.resolve(instrumentation).iterator().next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReiterationNoRemoval() throws Exception {
        Mockito.when(instrumentation.getAllLoadedClasses()).thenReturn(new Class<?>[]{ Void.class });
        INSTANCE.resolve(instrumentation).iterator().remove();
    }

    @Test
    public void testExplicit() throws Exception {
        Iterator<Iterable<Class<?>>> types = new AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.Explicit(String.class, Integer.class).resolve(instrumentation).iterator();
        Assert.assertThat(types.hasNext(), CoreMatchers.is(true));
        Assert.assertThat(types.next(), CoreMatchers.<Iterable<Class<?>>>equalTo(new HashSet<Class<?>>(Arrays.<Class<?>>asList(String.class, Integer.class))));
        Assert.assertThat(types.hasNext(), CoreMatchers.is(false));
    }
}

