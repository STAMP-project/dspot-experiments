package com.ctrip.framework.apollo.internals;


import PropertyChangeType.MODIFIED;
import com.ctrip.framework.apollo.ConfigFileChangeListener;
import com.ctrip.framework.apollo.model.ConfigFileChangeEvent;
import com.google.common.util.concurrent.SettableFuture;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class PropertiesConfigFileTest {
    private String someNamespace;

    @Mock
    private ConfigRepository configRepository;

    @Test
    public void testWhenHasContent() throws Exception {
        Properties someProperties = new Properties();
        String someKey = "someKey";
        String someValue = "someValue";
        someProperties.setProperty(someKey, someValue);
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        PropertiesConfigFile configFile = new PropertiesConfigFile(someNamespace, configRepository);
        Assert.assertEquals(ConfigFileFormat.Properties, configFile.getConfigFileFormat());
        Assert.assertEquals(someNamespace, configFile.getNamespace());
        Assert.assertTrue(configFile.hasContent());
        Assert.assertTrue(configFile.getContent().contains(String.format("%s=%s", someKey, someValue)));
    }

    @Test
    public void testWhenHasNoContent() throws Exception {
        Mockito.when(configRepository.getConfig()).thenReturn(null);
        PropertiesConfigFile configFile = new PropertiesConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
    }

    @Test
    public void testWhenConfigRepositoryHasError() throws Exception {
        Mockito.when(configRepository.getConfig()).thenThrow(new RuntimeException("someError"));
        PropertiesConfigFile configFile = new PropertiesConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
    }

    @Test
    public void testOnRepositoryChange() throws Exception {
        Properties someProperties = new Properties();
        String someKey = "someKey";
        String someValue = "someValue";
        String anotherValue = "anotherValue";
        someProperties.setProperty(someKey, someValue);
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        PropertiesConfigFile configFile = new PropertiesConfigFile(someNamespace, configRepository);
        Assert.assertTrue(configFile.getContent().contains(String.format("%s=%s", someKey, someValue)));
        Properties anotherProperties = new Properties();
        anotherProperties.setProperty(someKey, anotherValue);
        final SettableFuture<ConfigFileChangeEvent> configFileChangeFuture = SettableFuture.create();
        ConfigFileChangeListener someListener = new ConfigFileChangeListener() {
            @Override
            public void onChange(ConfigFileChangeEvent changeEvent) {
                configFileChangeFuture.set(changeEvent);
            }
        };
        configFile.addChangeListener(someListener);
        configFile.onRepositoryChange(someNamespace, anotherProperties);
        ConfigFileChangeEvent changeEvent = configFileChangeFuture.get(500, TimeUnit.MILLISECONDS);
        Assert.assertFalse(configFile.getContent().contains(String.format("%s=%s", someKey, someValue)));
        Assert.assertTrue(configFile.getContent().contains(String.format("%s=%s", someKey, anotherValue)));
        Assert.assertEquals(someNamespace, changeEvent.getNamespace());
        Assert.assertTrue(changeEvent.getOldValue().contains(String.format("%s=%s", someKey, someValue)));
        Assert.assertTrue(changeEvent.getNewValue().contains(String.format("%s=%s", someKey, anotherValue)));
        Assert.assertEquals(MODIFIED, changeEvent.getChangeType());
    }

    @Test
    public void testWhenConfigRepositoryHasErrorAndThenRecovered() throws Exception {
        Properties someProperties = new Properties();
        String someKey = "someKey";
        String someValue = "someValue";
        someProperties.setProperty(someKey, someValue);
        Mockito.when(configRepository.getConfig()).thenThrow(new RuntimeException("someError"));
        PropertiesConfigFile configFile = new PropertiesConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
        configFile.onRepositoryChange(someNamespace, someProperties);
        Assert.assertTrue(configFile.hasContent());
        Assert.assertTrue(configFile.getContent().contains(String.format("%s=%s", someKey, someValue)));
    }
}

