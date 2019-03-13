package net.bytebuddy.build;


import java.util.Collections;
import java.util.jar.Manifest;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PluginEngineErrorHandlerTest {
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

    @Test(expected = IllegalStateException.class)
    public void testFailingFailFast() {
        Engine.onError(typeDescription, plugin, throwable);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFailingFailFastDoesNotSupportFailAfterType() {
        Engine.onError(typeDescription, Collections.singletonList(throwable));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFailingFailFastDoesNotSupportFailLast() {
        Engine.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
    }

    @Test(expected = IllegalStateException.class)
    public void testFailingFailFastPluginError() {
        Engine.onError(plugin, throwable);
    }

    @Test
    public void testFailingFailAfterTypeDoesNotFailFast() {
        Engine.onError(typeDescription, plugin, throwable);
    }

    @Test(expected = IllegalStateException.class)
    public void testFailingFailAfterType() {
        Engine.onError(typeDescription, Collections.singletonList(throwable));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFailingFailAfterTypeDoesNotSupportFailLast() {
        Engine.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
    }

    @Test(expected = IllegalStateException.class)
    public void testFailingFailAfterTypePluginError() {
        Engine.onError(plugin, throwable);
    }

    @Test
    public void testFailingFailLastDoesNotFailFast() {
        Engine.onError(typeDescription, plugin, throwable);
    }

    @Test
    public void testFailingFailLastDoesNotFailAfterType() {
        Engine.onError(typeDescription, Collections.singletonList(throwable));
    }

    @Test(expected = IllegalStateException.class)
    public void testFailingFailLast() {
        Engine.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
    }

    @Test(expected = IllegalStateException.class)
    public void testFailingFailLastPluginError() {
        Engine.onError(plugin, throwable);
    }

    @Test
    public void testFailingDoesNotFailOnUnrelated() {
        Engine.onLiveInitializer(typeDescription, definingType);
        Engine.onUnresolved(PluginEngineErrorHandlerTest.FOO);
        Engine.onManifest(manifest);
        Engine.onResource(PluginEngineErrorHandlerTest.BAR);
        Engine.onLiveInitializer(typeDescription, definingType);
        Engine.onUnresolved(PluginEngineErrorHandlerTest.FOO);
        Engine.onManifest(manifest);
        Engine.onResource(PluginEngineErrorHandlerTest.BAR);
        Engine.onLiveInitializer(typeDescription, definingType);
        Engine.onUnresolved(PluginEngineErrorHandlerTest.FOO);
        Engine.onManifest(manifest);
        Engine.onResource(PluginEngineErrorHandlerTest.BAR);
    }

    @Test(expected = IllegalStateException.class)
    public void testEnforcingFailOnUnresolved() {
        Engine.onUnresolved(PluginEngineErrorHandlerTest.FOO);
    }

    @Test(expected = IllegalStateException.class)
    public void testEnforcingFailOnLiveInitializer() {
        Engine.onLiveInitializer(typeDescription, definingType);
    }

    @Test(expected = IllegalStateException.class)
    public void testEnforcingFailOnResource() {
        Engine.onResource(PluginEngineErrorHandlerTest.BAR);
    }

    @Test(expected = IllegalStateException.class)
    public void testEnforcingFailOnNoManifest() {
        Engine.onManifest(null);
    }

    @Test
    public void testEnforcingDoesNotFailOnUnrelated() {
        Engine.onError(typeDescription, plugin, throwable);
        Engine.onError(typeDescription, Collections.singletonList(throwable));
        Engine.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        Engine.onError(plugin, throwable);
        Engine.onLiveInitializer(typeDescription, definingType);
        Engine.onManifest(manifest);
        Engine.onResource(PluginEngineErrorHandlerTest.BAR);
        Engine.onError(typeDescription, plugin, throwable);
        Engine.onError(typeDescription, Collections.singletonList(throwable));
        Engine.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        Engine.onError(plugin, throwable);
        Engine.onUnresolved(PluginEngineErrorHandlerTest.FOO);
        Engine.onManifest(manifest);
        Engine.onResource(PluginEngineErrorHandlerTest.BAR);
        Engine.onError(typeDescription, plugin, throwable);
        Engine.onError(typeDescription, Collections.singletonList(throwable));
        Engine.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        Engine.onError(plugin, throwable);
        Engine.onLiveInitializer(typeDescription, definingType);
        Engine.onManifest(manifest);
        Engine.onUnresolved(PluginEngineErrorHandlerTest.FOO);
        Engine.onError(typeDescription, plugin, throwable);
        Engine.onError(typeDescription, Collections.singletonList(throwable));
        Engine.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        Engine.onError(plugin, throwable);
        Engine.onLiveInitializer(typeDescription, definingType);
        Engine.onUnresolved(PluginEngineErrorHandlerTest.FOO);
        Engine.onManifest(manifest);
        Engine.onResource(PluginEngineErrorHandlerTest.BAR);
    }

    @Test
    public void testCompound() {
        Plugin.Engine.ErrorHandler delegate = Mockito.mock(.class);
        Plugin.Engine.ErrorHandler errorHandler = new Plugin.Engine.ErrorHandler.Compound(delegate);
        errorHandler.onError(typeDescription, plugin, throwable);
        errorHandler.onError(typeDescription, Collections.singletonList(throwable));
        errorHandler.onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        errorHandler.onError(plugin, throwable);
        errorHandler.onLiveInitializer(typeDescription, definingType);
        errorHandler.onManifest(manifest);
        errorHandler.onUnresolved(PluginEngineErrorHandlerTest.FOO);
        Mockito.verifyZeroInteractions(typeDescription);
        Mockito.verifyZeroInteractions(definingType);
        Mockito.verifyZeroInteractions(plugin);
        Mockito.verifyZeroInteractions(throwable);
        Mockito.verify(delegate).onError(typeDescription, plugin, throwable);
        Mockito.verify(delegate).onError(typeDescription, Collections.singletonList(throwable));
        Mockito.verify(delegate).onError(Collections.singletonMap(typeDescription, Collections.singletonList(throwable)));
        Mockito.verify(delegate).onError(plugin, throwable);
        Mockito.verify(delegate).onLiveInitializer(typeDescription, definingType);
        Mockito.verify(delegate).onManifest(manifest);
        Mockito.verify(delegate).onUnresolved(PluginEngineErrorHandlerTest.FOO);
        Mockito.verifyNoMoreInteractions(delegate);
    }
}

