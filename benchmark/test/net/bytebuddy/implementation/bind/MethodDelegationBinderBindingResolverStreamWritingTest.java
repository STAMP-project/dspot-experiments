package net.bytebuddy.implementation.bind;


import java.io.PrintStream;
import java.util.Collections;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.BindingResolver.Default.INSTANCE;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.BindingResolver.StreamWriting.toSystemError;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.BindingResolver.StreamWriting.toSystemOut;


public class MethodDelegationBinderBindingResolverStreamWritingTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription source;

    @Mock
    private MethodDescription target;

    @Mock
    private MethodDelegationBinder.MethodBinding methodBinding;

    @Mock
    private MethodDelegationBinder.AmbiguityResolver ambiguityResolver;

    @Test
    public void testStreamWriting() throws Exception {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        MethodDelegationBinder.BindingResolver delegate = Mockito.mock(MethodDelegationBinder.BindingResolver.class);
        Mockito.when(delegate.resolve(ambiguityResolver, source, Collections.singletonList(methodBinding))).thenReturn(methodBinding);
        Assert.assertThat(new MethodDelegationBinder.BindingResolver.StreamWriting(delegate, printStream).resolve(ambiguityResolver, source, Collections.singletonList(methodBinding)), CoreMatchers.is(methodBinding));
        Mockito.verify(printStream).println(((("Binding " + (source)) + " as delegation to ") + (target)));
    }

    @Test
    public void testSystemOut() throws Exception {
        Assert.assertThat(toSystemOut(), FieldByFieldComparison.hasPrototype(((MethodDelegationBinder.BindingResolver) (new MethodDelegationBinder.BindingResolver.StreamWriting(INSTANCE, System.out)))));
    }

    @Test
    public void testSystemError() throws Exception {
        Assert.assertThat(toSystemError(), FieldByFieldComparison.hasPrototype(((MethodDelegationBinder.BindingResolver) (new MethodDelegationBinder.BindingResolver.StreamWriting(INSTANCE, System.err)))));
    }
}

