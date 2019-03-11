package com.ctrip.framework.apollo.internals;


import ConfigFileFormat.XML;
import PropertyChangeType.ADDED;
import PropertyChangeType.DELETED;
import PropertyChangeType.MODIFIED;
import com.ctrip.framework.apollo.ConfigFileChangeListener;
import com.ctrip.framework.apollo.core.ConfigConsts;
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
public class XmlConfigFileTest {
    private String someNamespace;

    @Mock
    private ConfigRepository configRepository;

    @Test
    public void testWhenHasContent() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someValue = "someValue";
        someProperties.setProperty(key, someValue);
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);
        Assert.assertEquals(XML, configFile.getConfigFileFormat());
        Assert.assertEquals(someNamespace, configFile.getNamespace());
        Assert.assertTrue(configFile.hasContent());
        Assert.assertEquals(someValue, configFile.getContent());
    }

    @Test
    public void testWhenHasNoContent() throws Exception {
        Mockito.when(configRepository.getConfig()).thenReturn(null);
        XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
    }

    @Test
    public void testWhenConfigRepositoryHasError() throws Exception {
        Mockito.when(configRepository.getConfig()).thenThrow(new RuntimeException("someError"));
        XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
    }

    @Test
    public void testOnRepositoryChange() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someValue = "someValue";
        String anotherValue = "anotherValue";
        someProperties.setProperty(key, someValue);
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);
        Assert.assertEquals(someValue, configFile.getContent());
        Properties anotherProperties = new Properties();
        anotherProperties.setProperty(key, anotherValue);
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
        Assert.assertEquals(anotherValue, configFile.getContent());
        Assert.assertEquals(someNamespace, changeEvent.getNamespace());
        Assert.assertEquals(someValue, changeEvent.getOldValue());
        Assert.assertEquals(anotherValue, changeEvent.getNewValue());
        Assert.assertEquals(MODIFIED, changeEvent.getChangeType());
    }

    @Test
    public void testOnRepositoryChangeWithContentAdded() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someValue = "someValue";
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);
        Assert.assertEquals(null, configFile.getContent());
        Properties anotherProperties = new Properties();
        anotherProperties.setProperty(key, someValue);
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
        Assert.assertEquals(someValue, configFile.getContent());
        Assert.assertEquals(someNamespace, changeEvent.getNamespace());
        Assert.assertEquals(null, changeEvent.getOldValue());
        Assert.assertEquals(someValue, changeEvent.getNewValue());
        Assert.assertEquals(ADDED, changeEvent.getChangeType());
    }

    @Test
    public void testOnRepositoryChangeWithContentDeleted() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someValue = "someValue";
        someProperties.setProperty(key, someValue);
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);
        Assert.assertEquals(someValue, configFile.getContent());
        Properties anotherProperties = new Properties();
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
        Assert.assertEquals(null, configFile.getContent());
        Assert.assertEquals(someNamespace, changeEvent.getNamespace());
        Assert.assertEquals(someValue, changeEvent.getOldValue());
        Assert.assertEquals(null, changeEvent.getNewValue());
        Assert.assertEquals(DELETED, changeEvent.getChangeType());
    }

    @Test
    public void testWhenConfigRepositoryHasErrorAndThenRecovered() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someValue = "someValue";
        someProperties.setProperty(key, someValue);
        Mockito.when(configRepository.getConfig()).thenThrow(new RuntimeException("someError"));
        XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
        configFile.onRepositoryChange(someNamespace, someProperties);
        Assert.assertTrue(configFile.hasContent());
        Assert.assertEquals(someValue, configFile.getContent());
    }
}

