package net.bytebuddy.build;


import java.io.PrintStream;
import java.util.Collections;
import java.util.jar.Manifest;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PluginEngineListenerTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription definingType;

    @Mock
    private Plugin plugin;

    @Mock
    private Throwable throwable;

    @Mock
    private Manifest manifest;

    @Test
    public void testNoOp() {
        Engine.onTransformation(typeDescription, plugin);
        Engine.onTransformation(typeDescription, Collections.singletonList(plugin));
        Engine.onIgnored(typeDescription, plugin);
        Engine.onIgnored(typeDescription, Collections.singletonList(plugin));
        Engine.onError(typeDescription, plugin, throwable);
        Engine.onError(typeDescription, Collections.singletonList(throwable));
        Engine.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        Engine.onError(plugin, throwable);
        Engine.onLiveInitializer(typeDescription, definingType);
        Engine.onComplete(typeDescription);
        Engine.onUnresolved(PluginEngineListenerTest.FOO);
        Engine.onManifest(manifest);
        Engine.onResource(PluginEngineListenerTest.BAR);
        Mockito.verifyZeroInteractions(typeDescription);
        Mockito.verifyZeroInteractions(definingType);
        Mockito.verifyZeroInteractions(plugin);
        Mockito.verifyZeroInteractions(throwable);
    }

    @Test
    public void testAdapterNoOp() {
        Plugin.Engine.Listener listener = new Plugin.Engine.Listener.Adapter() {};
        listener.onTransformation(typeDescription, plugin);
        listener.onTransformation(typeDescription, Collections.singletonList(plugin));
        listener.onIgnored(typeDescription, plugin);
        listener.onIgnored(typeDescription, Collections.singletonList(plugin));
        listener.onError(typeDescription, plugin, throwable);
        listener.onError(typeDescription, Collections.singletonList(throwable));
        listener.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        listener.onError(plugin, throwable);
        listener.onLiveInitializer(typeDescription, definingType);
        listener.onComplete(typeDescription);
        listener.onUnresolved(PluginEngineListenerTest.FOO);
        listener.onManifest(manifest);
        listener.onResource(PluginEngineListenerTest.BAR);
        Mockito.verifyZeroInteractions(typeDescription);
        Mockito.verifyZeroInteractions(definingType);
        Mockito.verifyZeroInteractions(plugin);
        Mockito.verifyZeroInteractions(throwable);
    }

    @Test
    public void testStreamWriting() {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        Plugin.Engine.Listener listener = new Plugin.Engine.Listener.StreamWriting(printStream);
        listener.onTransformation(typeDescription, plugin);
        listener.onTransformation(typeDescription, Collections.singletonList(plugin));
        listener.onIgnored(typeDescription, plugin);
        listener.onIgnored(typeDescription, Collections.singletonList(plugin));
        listener.onError(typeDescription, plugin, throwable);
        listener.onError(typeDescription, Collections.singletonList(throwable));
        listener.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        listener.onError(plugin, throwable);
        listener.onLiveInitializer(typeDescription, definingType);
        listener.onComplete(typeDescription);
        listener.onUnresolved(PluginEngineListenerTest.FOO);
        listener.onManifest(manifest);
        listener.onResource(PluginEngineListenerTest.BAR);
        Mockito.verifyZeroInteractions(typeDescription);
        Mockito.verifyZeroInteractions(definingType);
        Mockito.verifyZeroInteractions(plugin);
        Mockito.verify(throwable, Mockito.times(2)).printStackTrace(printStream);
        Mockito.verifyNoMoreInteractions(throwable);
        Mockito.verify(printStream).printf("[Byte Buddy] TRANSFORM %s for %s", typeDescription, plugin);
        Mockito.verify(printStream).printf("[Byte Buddy] IGNORE %s for %s", typeDescription, plugin);
        Mockito.verify(printStream).printf("[Byte Buddy] ERROR %s for %s", typeDescription, plugin);
        Mockito.verify(printStream).printf("[Byte Buddy] ERROR %s", plugin);
        Mockito.verify(printStream).printf("[Byte Buddy] LIVE %s on %s", typeDescription, definingType);
        Mockito.verify(printStream).printf("[Byte Buddy] COMPLETE %s", typeDescription);
        Mockito.verify(printStream).printf("[Byte Buddy] UNRESOLVED %s", PluginEngineListenerTest.FOO);
        Mockito.verify(printStream).printf("[Byte Buddy] MANIFEST %b", true);
        Mockito.verify(printStream).printf("[Byte Buddy] RESOURCE %s", PluginEngineListenerTest.BAR);
        Mockito.verifyNoMoreInteractions(printStream);
    }

    @Test
    public void testStreamWritingDefaults() {
        MatcherAssert.assertThat(Engine.toSystemOut(), FieldByFieldComparison.hasPrototype(new Plugin.Engine.Listener.StreamWriting(System.out)));
        MatcherAssert.assertThat(Engine.toSystemError(), FieldByFieldComparison.hasPrototype(new Plugin.Engine.Listener.StreamWriting(System.err)));
        MatcherAssert.assertThat(Engine.toSystemOut().withTransformationsOnly(), FieldByFieldComparison.hasPrototype(((Plugin.Engine.Listener) (new Plugin.Engine.Listener.WithTransformationsOnly(new Plugin.Engine.Listener.StreamWriting(System.out))))));
        MatcherAssert.assertThat(Engine.toSystemError().withErrorsOnly(), FieldByFieldComparison.hasPrototype(((Plugin.Engine.Listener) (new Plugin.Engine.Listener.WithErrorsOnly(new Plugin.Engine.Listener.StreamWriting(System.err))))));
    }

    @Test
    public void testForErrorHandler() {
        Plugin.Engine.ErrorHandler delegate = Mockito.mock(.class);
        Plugin.Engine.Listener listener = new Plugin.Engine.Listener.ForErrorHandler(delegate);
        listener.onTransformation(typeDescription, plugin);
        listener.onTransformation(typeDescription, Collections.singletonList(plugin));
        listener.onIgnored(typeDescription, plugin);
        listener.onIgnored(typeDescription, Collections.singletonList(plugin));
        listener.onError(typeDescription, plugin, throwable);
        listener.onError(typeDescription, Collections.singletonList(throwable));
        listener.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        listener.onError(plugin, throwable);
        listener.onLiveInitializer(typeDescription, definingType);
        listener.onComplete(typeDescription);
        listener.onUnresolved(PluginEngineListenerTest.FOO);
        listener.onManifest(manifest);
        listener.onResource(PluginEngineListenerTest.BAR);
        Mockito.verifyZeroInteractions(typeDescription);
        Mockito.verifyZeroInteractions(definingType);
        Mockito.verifyZeroInteractions(plugin);
        Mockito.verifyZeroInteractions(throwable);
        Mockito.verify(delegate).onError(typeDescription, plugin, throwable);
        Mockito.verify(delegate).onError(typeDescription, Collections.singletonList(throwable));
        Mockito.verify(delegate).onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        Mockito.verify(delegate).onError(plugin, throwable);
        Mockito.verify(delegate).onLiveInitializer(typeDescription, definingType);
        Mockito.verify(delegate).onUnresolved(PluginEngineListenerTest.FOO);
        Mockito.verify(delegate).onManifest(manifest);
        Mockito.verify(delegate).onResource(PluginEngineListenerTest.BAR);
        Mockito.verifyNoMoreInteractions(delegate);
    }

    @Test
    public void testWithTransformationsOnly() {
        Plugin.Engine.Listener delegate = Mockito.mock(.class);
        Plugin.Engine.Listener listener = new Plugin.Engine.Listener.WithTransformationsOnly(delegate);
        listener.onTransformation(typeDescription, plugin);
        listener.onTransformation(typeDescription, Collections.singletonList(plugin));
        listener.onIgnored(typeDescription, plugin);
        listener.onIgnored(typeDescription, Collections.singletonList(plugin));
        listener.onError(typeDescription, plugin, throwable);
        listener.onError(typeDescription, Collections.singletonList(throwable));
        listener.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        listener.onError(plugin, throwable);
        listener.onLiveInitializer(typeDescription, definingType);
        listener.onComplete(typeDescription);
        listener.onUnresolved(PluginEngineListenerTest.FOO);
        listener.onManifest(manifest);
        listener.onResource(PluginEngineListenerTest.BAR);
        Mockito.verifyZeroInteractions(typeDescription);
        Mockito.verifyZeroInteractions(definingType);
        Mockito.verifyZeroInteractions(plugin);
        Mockito.verifyZeroInteractions(throwable);
        Mockito.verify(delegate).onTransformation(typeDescription, plugin);
        Mockito.verify(delegate).onTransformation(typeDescription, Collections.singletonList(plugin));
        Mockito.verify(delegate).onError(typeDescription, plugin, throwable);
        Mockito.verify(delegate).onError(typeDescription, Collections.singletonList(throwable));
        Mockito.verify(delegate).onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        Mockito.verify(delegate).onError(plugin, throwable);
        Mockito.verifyNoMoreInteractions(delegate);
    }

    @Test
    public void testWithErrorsOnly() {
        Plugin.Engine.Listener delegate = Mockito.mock(.class);
        Plugin.Engine.Listener listener = new Plugin.Engine.Listener.WithErrorsOnly(delegate);
        listener.onTransformation(typeDescription, plugin);
        listener.onTransformation(typeDescription, Collections.singletonList(plugin));
        listener.onIgnored(typeDescription, plugin);
        listener.onIgnored(typeDescription, Collections.singletonList(plugin));
        listener.onError(typeDescription, plugin, throwable);
        listener.onError(typeDescription, Collections.singletonList(throwable));
        listener.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        listener.onError(plugin, throwable);
        listener.onLiveInitializer(typeDescription, definingType);
        listener.onComplete(typeDescription);
        listener.onUnresolved(PluginEngineListenerTest.FOO);
        listener.onManifest(manifest);
        listener.onResource(PluginEngineListenerTest.BAR);
        Mockito.verifyZeroInteractions(typeDescription);
        Mockito.verifyZeroInteractions(definingType);
        Mockito.verifyZeroInteractions(plugin);
        Mockito.verifyZeroInteractions(throwable);
        Mockito.verify(delegate).onError(typeDescription, plugin, throwable);
        Mockito.verify(delegate).onError(typeDescription, Collections.singletonList(throwable));
        Mockito.verify(delegate).onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        Mockito.verify(delegate).onError(plugin, throwable);
        Mockito.verifyNoMoreInteractions(delegate);
    }

    @Test
    public void testCompound() {
        Plugin.Engine.Listener delegate = Mockito.mock(.class);
        Plugin.Engine.Listener listener = new Plugin.Engine.Listener.Compound(delegate);
        listener.onTransformation(typeDescription, plugin);
        listener.onTransformation(typeDescription, Collections.singletonList(plugin));
        listener.onIgnored(typeDescription, plugin);
        listener.onIgnored(typeDescription, Collections.singletonList(plugin));
        listener.onError(typeDescription, plugin, throwable);
        listener.onError(typeDescription, Collections.singletonList(throwable));
        listener.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        listener.onError(plugin, throwable);
        listener.onLiveInitializer(typeDescription, definingType);
        listener.onComplete(typeDescription);
        listener.onUnresolved(PluginEngineListenerTest.FOO);
        listener.onManifest(manifest);
        listener.onResource(PluginEngineListenerTest.BAR);
        Mockito.verifyZeroInteractions(typeDescription);
        Mockito.verifyZeroInteractions(definingType);
        Mockito.verifyZeroInteractions(plugin);
        Mockito.verifyZeroInteractions(throwable);
        Mockito.verify(delegate).onTransformation(typeDescription, plugin);
        Mockito.verify(delegate).onTransformation(typeDescription, Collections.singletonList(plugin));
        Mockito.verify(delegate).onIgnored(typeDescription, plugin);
        Mockito.verify(delegate).onIgnored(typeDescription, Collections.singletonList(plugin));
        Mockito.verify(delegate).onError(typeDescription, plugin, throwable);
        Mockito.verify(delegate).onError(typeDescription, Collections.singletonList(throwable));
        Mockito.verify(delegate).onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        Mockito.verify(delegate).onError(plugin, throwable);
        Mockito.verify(delegate).onLiveInitializer(typeDescription, definingType);
        Mockito.verify(delegate).onComplete(typeDescription);
        Mockito.verify(delegate).onUnresolved(PluginEngineListenerTest.FOO);
        Mockito.verify(delegate).onManifest(manifest);
        Mockito.verify(delegate).onResource(PluginEngineListenerTest.BAR);
        Mockito.verifyNoMoreInteractions(delegate);
    }
}

