package org.stagemonitor.core;


import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.configuration.source.SimpleSource;
import org.stagemonitor.core.configuration.ElasticsearchConfigurationSource;
import org.stagemonitor.core.configuration.RemotePropertiesConfigurationSource;


public class StagemonitorCoreConfigurationSourceInitializerTest {
    private StagemonitorCoreConfigurationSourceInitializer initializer = new StagemonitorCoreConfigurationSourceInitializer();

    private final ConfigurationRegistry configuration = Mockito.mock(ConfigurationRegistry.class);

    private final CorePlugin corePlugin = Mockito.mock(CorePlugin.class);

    @Test(expected = IllegalStateException.class)
    public void testEsDownDeactivate() throws Exception {
        prepareESTest();
        Mockito.when(corePlugin.isDeactivateStagemonitorIfEsConfigSourceIsDown()).thenReturn(true);
        initializer.onConfigurationInitialized(new StagemonitorConfigurationSourceInitializer.ConfigInitializedArguments(configuration));
    }

    @Test
    public void testEsDown() throws Exception {
        prepareESTest();
        Mockito.when(corePlugin.isDeactivateStagemonitorIfEsConfigSourceIsDown()).thenReturn(false);
        initializer.onConfigurationInitialized(new StagemonitorConfigurationSourceInitializer.ConfigInitializedArguments(configuration));
        Mockito.verify(configuration).addConfigurationSourceAfter(ArgumentMatchers.any(ElasticsearchConfigurationSource.class), ArgumentMatchers.eq(SimpleSource.class));
    }

    @Test
    public void testESEnabledAndSpringCloudDisabled() throws IOException {
        prepareESTest();
        initializer.onConfigurationInitialized(new StagemonitorConfigurationSourceInitializer.ConfigInitializedArguments(configuration));
        Mockito.verify(configuration, Mockito.never()).addConfigurationSourceAfter(ArgumentMatchers.any(RemotePropertiesConfigurationSource.class), ArgumentMatchers.eq(SimpleSource.class));
        Mockito.verify(configuration).addConfigurationSourceAfter(ArgumentMatchers.any(ElasticsearchConfigurationSource.class), ArgumentMatchers.eq(SimpleSource.class));
    }

    @Test
    public void testESDisabledAndSpringCloudEnabled() throws IOException {
        Mockito.when(corePlugin.getRemotePropertiesConfigUrls()).thenReturn(Collections.singletonList(new URL("http://localhost/config.json")));
        initializer.onConfigurationInitialized(new StagemonitorConfigurationSourceInitializer.ConfigInitializedArguments(configuration));
        Mockito.verify(configuration).addConfigurationSourceAfter(ArgumentMatchers.any(RemotePropertiesConfigurationSource.class), ArgumentMatchers.eq(SimpleSource.class));
        Mockito.verify(configuration, Mockito.never()).addConfigurationSourceAfter(ArgumentMatchers.any(ElasticsearchConfigurationSource.class), ArgumentMatchers.eq(SimpleSource.class));
    }

    @Test
    public void testSpringCloud_missingServerAddress() throws IOException {
        initializer.onConfigurationInitialized(new StagemonitorConfigurationSourceInitializer.ConfigInitializedArguments(configuration));
        Mockito.verify(configuration, Mockito.never()).addConfigurationSourceAfter(ArgumentMatchers.any(RemotePropertiesConfigurationSource.class), ArgumentMatchers.eq(SimpleSource.class));
    }

    @Test
    public void testCorrectProperties() throws IOException {
        Mockito.when(corePlugin.getRemotePropertiesConfigUrls()).thenReturn(Collections.singletonList(new URL("http://localhost/config.json")));
        initializer.onConfigurationInitialized(new StagemonitorConfigurationSourceInitializer.ConfigInitializedArguments(configuration));
        ArgumentCaptor<RemotePropertiesConfigurationSource> configSourceCaptor = ArgumentCaptor.forClass(RemotePropertiesConfigurationSource.class);
        Mockito.verify(configuration).addConfigurationSourceAfter(configSourceCaptor.capture(), ArgumentMatchers.eq(SimpleSource.class));
        Assert.assertEquals("http://localhost/config.json", configSourceCaptor.getValue().getName());
    }

    @Test
    public void testSpringCloud_multipleConfigUrls() throws IOException {
        Mockito.when(corePlugin.getRemotePropertiesConfigUrls()).thenReturn(Arrays.asList(new URL("http://localhost/config1"), new URL("http://localhost/config2"), new URL("http://some.other/domain")));
        Mockito.when(corePlugin.getApplicationName()).thenReturn("myapplication");
        initializer.onConfigurationInitialized(new StagemonitorConfigurationSourceInitializer.ConfigInitializedArguments(configuration));
        // Expecting 3 config source
        Mockito.verify(configuration, Mockito.times(3)).addConfigurationSourceAfter(ArgumentMatchers.any(RemotePropertiesConfigurationSource.class), ArgumentMatchers.eq(SimpleSource.class));
    }
}

