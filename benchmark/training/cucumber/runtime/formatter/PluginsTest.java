package cucumber.runtime.formatter;


import cucumber.api.event.ConcurrentEventListener;
import cucumber.api.event.Event;
import cucumber.api.event.EventHandler;
import cucumber.api.event.EventListener;
import cucumber.api.event.EventPublisher;
import cucumber.api.formatter.ColorAware;
import cucumber.api.formatter.StrictAware;
import cucumber.runner.CanonicalOrderEventPublisher;
import cucumber.runtime.RuntimeOptions;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;


public class PluginsTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    private EventPublisher rootEventPublisher;

    private PluginFactory pluginFactory = new PluginFactory();

    @Captor
    private ArgumentCaptor<EventPublisher> eventPublisher;

    @Test
    public void shouldSetStrictOnPlugin() {
        RuntimeOptions runtimeOptions = new RuntimeOptions("--strict");
        Plugins plugins = new Plugins(ClassLoader.getSystemClassLoader(), pluginFactory, rootEventPublisher, runtimeOptions);
        StrictAware plugin = Mockito.mock(StrictAware.class);
        plugins.addPlugin(plugin);
        Mockito.verify(plugin).setStrict(true);
    }

    @Test
    public void shouldSetMonochromeOnPlugin() {
        RuntimeOptions runtimeOptions = new RuntimeOptions("--monochrome");
        Plugins plugins = new Plugins(ClassLoader.getSystemClassLoader(), pluginFactory, rootEventPublisher, runtimeOptions);
        ColorAware plugin = Mockito.mock(ColorAware.class);
        plugins.addPlugin(plugin);
        Mockito.verify(plugin).setMonochrome(true);
    }

    @Test
    public void shouldSetConcurrentEventListener() {
        RuntimeOptions runtimeOptions = new RuntimeOptions(Collections.<String>emptyList());
        Plugins plugins = new Plugins(ClassLoader.getSystemClassLoader(), pluginFactory, rootEventPublisher, runtimeOptions);
        ConcurrentEventListener plugin = Mockito.mock(ConcurrentEventListener.class);
        plugins.addPlugin(plugin);
        Mockito.verify(plugin, Mockito.times(1)).setEventPublisher(rootEventPublisher);
    }

    @Test
    public void shouldSetNonConcurrentEventListener() {
        RuntimeOptions runtimeOptions = new RuntimeOptions(Collections.<String>emptyList());
        Plugins plugins = new Plugins(ClassLoader.getSystemClassLoader(), pluginFactory, rootEventPublisher, runtimeOptions);
        EventListener plugin = Mockito.mock(EventListener.class);
        plugins.addPlugin(plugin);
        Mockito.verify(plugin, Mockito.times(1)).setEventPublisher(eventPublisher.capture());
        Assert.assertEquals(CanonicalOrderEventPublisher.class, eventPublisher.getValue().getClass());
    }

    @Test
    public void shouldRegisterCanonicalOrderEventPublisherWithRootEventPublisher() {
        RuntimeOptions runtimeOptions = new RuntimeOptions(Collections.<String>emptyList());
        Plugins plugins = new Plugins(ClassLoader.getSystemClassLoader(), pluginFactory, rootEventPublisher, runtimeOptions);
        EventListener plugin = Mockito.mock(EventListener.class);
        plugins.addPlugin(plugin);
        Mockito.verify(rootEventPublisher, Mockito.times(1)).registerHandlerFor(ArgumentMatchers.eq(Event.class), ArgumentMatchers.<EventHandler<Event>>any());
    }
}

