package net.bytebuddy.agent.builder;


import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.util.Collections;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.Listener.NoOp.INSTANCE;
import static net.bytebuddy.agent.builder.AgentBuilder.Listener.StreamWriting.toSystemError;
import static net.bytebuddy.agent.builder.AgentBuilder.Listener.StreamWriting.toSystemOut;


public class AgentBuilderListenerTest {
    private static final String FOO = "foo";

    private static final boolean LOADED = true;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private AgentBuilder.Listener first;

    @Mock
    private AgentBuilder.Listener second;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private JavaModule module;

    @Mock
    private DynamicType dynamicType;

    @Mock
    private Throwable throwable;

    @Test
    public void testNoOp() throws Exception {
        INSTANCE.onDiscovery(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        INSTANCE.onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        Mockito.verifyZeroInteractions(dynamicType);
        INSTANCE.onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        Mockito.verifyZeroInteractions(throwable);
        INSTANCE.onIgnored(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED);
        INSTANCE.onComplete(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
    }

    @Test
    public void testPseudoAdapter() throws Exception {
        AgentBuilder.Listener listener = new AgentBuilderListenerTest.PseudoAdapter();
        listener.onDiscovery(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        listener.onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        Mockito.verifyZeroInteractions(dynamicType);
        listener.onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        Mockito.verifyZeroInteractions(throwable);
        listener.onIgnored(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED);
        listener.onComplete(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
    }

    @Test
    public void testCompoundOnDiscovery() throws Exception {
        new AgentBuilder.Listener.Compound(first, second).onDiscovery(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verify(first).onDiscovery(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).onDiscovery(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testCompoundOnTransformation() throws Exception {
        new AgentBuilder.Listener.Compound(first, second).onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        Mockito.verify(first).onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testCompoundOnError() throws Exception {
        new AgentBuilder.Listener.Compound(first, second).onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        Mockito.verify(first).onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testCompoundOnIgnored() throws Exception {
        new AgentBuilder.Listener.Compound(first, second).onIgnored(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verify(first).onIgnored(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).onIgnored(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testCompoundOnComplete() throws Exception {
        new AgentBuilder.Listener.Compound(first, second).onComplete(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verify(first).onComplete(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).onComplete(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testStreamWritingOnDiscovery() throws Exception {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.StreamWriting(printStream);
        listener.onDiscovery(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verify(printStream).printf("[Byte Buddy] DISCOVERY %s [%s, %s, loaded=%b]%n", AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(printStream);
    }

    @Test
    public void testStreamWritingOnTransformation() throws Exception {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.StreamWriting(printStream);
        listener.onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        Mockito.verify(printStream).printf("[Byte Buddy] TRANSFORM %s [%s, %s, loaded=%b]%n", AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(printStream);
    }

    @Test
    public void testStreamWritingOnError() throws Exception {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.StreamWriting(printStream);
        listener.onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        Mockito.verify(printStream).printf("[Byte Buddy] ERROR %s [%s, %s, loaded=%b]%n", AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(printStream);
        Mockito.verify(throwable).printStackTrace(printStream);
        Mockito.verifyNoMoreInteractions(throwable);
    }

    @Test
    public void testStreamWritingOnComplete() throws Exception {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.StreamWriting(printStream);
        listener.onComplete(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verify(printStream).printf("[Byte Buddy] COMPLETE %s [%s, %s, loaded=%b]%n", AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(printStream);
    }

    @Test
    public void testStreamWritingOnIgnore() throws Exception {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.StreamWriting(printStream);
        listener.onIgnored(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verify(printStream).printf("[Byte Buddy] IGNORE %s [%s, %s, loaded=%b]%n", AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(printStream);
    }

    @Test
    public void testStreamWritingStandardOutput() throws Exception {
        MatcherAssert.assertThat(toSystemOut(), FieldByFieldComparison.hasPrototype(((AgentBuilder.Listener) (new AgentBuilder.Listener.StreamWriting(System.out)))));
    }

    @Test
    public void testStreamWritingStandardError() throws Exception {
        MatcherAssert.assertThat(toSystemError(), FieldByFieldComparison.hasPrototype(((AgentBuilder.Listener) (new AgentBuilder.Listener.StreamWriting(System.err)))));
    }

    @Test
    public void testStreamWritingTransformationsOnly() throws Exception {
        PrintStream target = Mockito.mock(PrintStream.class);
        MatcherAssert.assertThat(withTransformationsOnly(), FieldByFieldComparison.hasPrototype(((AgentBuilder.Listener) (new AgentBuilder.Listener.WithTransformationsOnly(new AgentBuilder.Listener.StreamWriting(target))))));
    }

    @Test
    public void testStreamWritingErrorOnly() throws Exception {
        PrintStream target = Mockito.mock(PrintStream.class);
        MatcherAssert.assertThat(withErrorsOnly(), FieldByFieldComparison.hasPrototype(((AgentBuilder.Listener) (new AgentBuilder.Listener.WithErrorsOnly(new AgentBuilder.Listener.StreamWriting(target))))));
    }

    @Test
    public void testTransformationsOnly() {
        AgentBuilder.Listener delegate = Mockito.mock(AgentBuilder.Listener.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.WithTransformationsOnly(delegate);
        listener.onDiscovery(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        listener.onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        listener.onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        listener.onIgnored(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED);
        listener.onComplete(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verify(delegate).onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        Mockito.verify(delegate).onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        Mockito.verifyNoMoreInteractions(delegate);
    }

    @Test
    public void testErrorsOnly() {
        AgentBuilder.Listener delegate = Mockito.mock(AgentBuilder.Listener.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.WithErrorsOnly(delegate);
        listener.onDiscovery(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        listener.onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        listener.onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        listener.onIgnored(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED);
        listener.onComplete(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verify(delegate).onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        Mockito.verifyNoMoreInteractions(delegate);
    }

    @Test
    public void testFilteringDoesNotMatch() throws Exception {
        AgentBuilder.Listener delegate = Mockito.mock(AgentBuilder.Listener.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.Filtering(ElementMatchers.none(), delegate);
        listener.onDiscovery(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        listener.onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        listener.onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        listener.onIgnored(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED);
        listener.onComplete(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyZeroInteractions(delegate);
    }

    @Test
    public void testFilteringMatch() throws Exception {
        AgentBuilder.Listener delegate = Mockito.mock(AgentBuilder.Listener.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.Filtering(ElementMatchers.any(), delegate);
        listener.onDiscovery(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        listener.onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        listener.onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        listener.onIgnored(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED);
        listener.onComplete(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verify(delegate).onDiscovery(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verify(delegate).onTransformation(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED, dynamicType);
        Mockito.verify(delegate).onError(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED, throwable);
        Mockito.verify(delegate).onIgnored(typeDescription, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verify(delegate).onComplete(AgentBuilderListenerTest.FOO, classLoader, module, AgentBuilderListenerTest.LOADED);
        Mockito.verifyNoMoreInteractions(delegate);
    }

    @Test
    public void testReadEdgeAddingListenerNotSupported() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.ModuleReadEdgeCompleting(instrumentation, false, Collections.<JavaModule>emptySet());
        listener.onTransformation(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), JavaModule.UNSUPPORTED, AgentBuilderListenerTest.LOADED, Mockito.mock(DynamicType.class));
    }

    @Test
    public void testReadEdgeAddingListenerUnnamed() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        JavaModule source = Mockito.mock(JavaModule.class);
        JavaModule target = Mockito.mock(JavaModule.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.ModuleReadEdgeCompleting(instrumentation, false, Collections.singleton(target));
        listener.onTransformation(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), source, AgentBuilderListenerTest.LOADED, Mockito.mock(DynamicType.class));
        Mockito.verify(source).isNamed();
        Mockito.verifyNoMoreInteractions(source);
        Mockito.verifyZeroInteractions(target);
    }

    @Test
    public void testReadEdgeAddingListenerCanRead() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        JavaModule source = Mockito.mock(JavaModule.class);
        JavaModule target = Mockito.mock(JavaModule.class);
        Mockito.when(source.isNamed()).thenReturn(true);
        Mockito.when(source.canRead(target)).thenReturn(true);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.ModuleReadEdgeCompleting(instrumentation, false, Collections.singleton(target));
        listener.onTransformation(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), source, AgentBuilderListenerTest.LOADED, Mockito.mock(DynamicType.class));
        Mockito.verify(source).isNamed();
        Mockito.verify(source).canRead(target);
        Mockito.verifyNoMoreInteractions(source);
        Mockito.verifyZeroInteractions(target);
    }

    @Test
    public void testReadEdgeAddingListenerNamedCannotRead() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        JavaModule source = Mockito.mock(JavaModule.class);
        JavaModule target = Mockito.mock(JavaModule.class);
        Mockito.when(source.isNamed()).thenReturn(true);
        Mockito.when(source.canRead(target)).thenReturn(false);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.ModuleReadEdgeCompleting(instrumentation, false, Collections.singleton(target));
        listener.onTransformation(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), source, AgentBuilderListenerTest.LOADED, Mockito.mock(DynamicType.class));
        Mockito.verify(source).isNamed();
        Mockito.verify(source).canRead(target);
        Mockito.verify(source).addReads(instrumentation, target);
        Mockito.verifyNoMoreInteractions(source);
        Mockito.verifyZeroInteractions(target);
    }

    @Test
    public void testReadEdgeAddingListenerDuplexNotSupported() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.ModuleReadEdgeCompleting(instrumentation, true, Collections.<JavaModule>emptySet());
        listener.onTransformation(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), JavaModule.UNSUPPORTED, AgentBuilderListenerTest.LOADED, Mockito.mock(DynamicType.class));
    }

    @Test
    public void testReadEdgeAddingListenerDuplexUnnamed() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        JavaModule source = Mockito.mock(JavaModule.class);
        JavaModule target = Mockito.mock(JavaModule.class);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.ModuleReadEdgeCompleting(instrumentation, true, Collections.singleton(target));
        listener.onTransformation(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), source, AgentBuilderListenerTest.LOADED, Mockito.mock(DynamicType.class));
        Mockito.verify(source).isNamed();
        Mockito.verifyNoMoreInteractions(source);
        Mockito.verifyZeroInteractions(target);
    }

    @Test
    public void testReadEdgeAddingListenerDuplexCanRead() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        JavaModule source = Mockito.mock(JavaModule.class);
        JavaModule target = Mockito.mock(JavaModule.class);
        Mockito.when(source.isNamed()).thenReturn(true);
        Mockito.when(source.canRead(target)).thenReturn(true);
        Mockito.when(target.canRead(source)).thenReturn(true);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.ModuleReadEdgeCompleting(instrumentation, true, Collections.singleton(target));
        listener.onTransformation(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), source, AgentBuilderListenerTest.LOADED, Mockito.mock(DynamicType.class));
        Mockito.verify(source).isNamed();
        Mockito.verify(source).canRead(target);
        Mockito.verifyNoMoreInteractions(source);
        Mockito.verify(target).canRead(source);
        Mockito.verifyNoMoreInteractions(target);
    }

    @Test
    public void testReadEdgeAddingListenerNamedDuplexCannotRead() throws Exception {
        Instrumentation instrumentation = Mockito.mock(Instrumentation.class);
        JavaModule source = Mockito.mock(JavaModule.class);
        JavaModule target = Mockito.mock(JavaModule.class);
        Mockito.when(source.isNamed()).thenReturn(true);
        Mockito.when(source.canRead(target)).thenReturn(false);
        Mockito.when(target.canRead(source)).thenReturn(false);
        AgentBuilder.Listener listener = new AgentBuilder.Listener.ModuleReadEdgeCompleting(instrumentation, true, Collections.singleton(target));
        listener.onTransformation(Mockito.mock(TypeDescription.class), Mockito.mock(ClassLoader.class), source, AgentBuilderListenerTest.LOADED, Mockito.mock(DynamicType.class));
        Mockito.verify(source).isNamed();
        Mockito.verify(source).canRead(target);
        Mockito.verify(source).addReads(instrumentation, target);
        Mockito.verifyNoMoreInteractions(source);
        Mockito.verify(target).canRead(source);
        Mockito.verify(target).addReads(instrumentation, source);
        Mockito.verifyNoMoreInteractions(target);
    }

    /* empty */
    private static class PseudoAdapter extends AgentBuilder.Listener.Adapter {}
}

