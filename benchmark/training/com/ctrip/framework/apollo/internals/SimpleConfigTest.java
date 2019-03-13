package com.ctrip.framework.apollo.internals;


import ConfigSourceType.NONE;
import PropertyChangeType.ADDED;
import PropertyChangeType.DELETED;
import PropertyChangeType.MODIFIED;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.enums.ConfigSourceType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.common.collect.ImmutableMap;
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
public class SimpleConfigTest {
    private String someNamespace;

    @Mock
    private ConfigRepository configRepository;

    private ConfigSourceType someSourceType;

    @Test
    public void testGetProperty() throws Exception {
        Properties someProperties = new Properties();
        String someKey = "someKey";
        String someValue = "someValue";
        someProperties.setProperty(someKey, someValue);
        someSourceType = ConfigSourceType.LOCAL;
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        SimpleConfig config = new SimpleConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, config.getProperty(someKey, null));
        Assert.assertEquals(someSourceType, config.getSourceType());
    }

    @Test
    public void testLoadConfigFromConfigRepositoryError() throws Exception {
        String someKey = "someKey";
        String anyValue = "anyValue" + (Math.random());
        Mockito.when(configRepository.getConfig()).thenThrow(Mockito.mock(RuntimeException.class));
        Config config = new SimpleConfig(someNamespace, configRepository);
        Assert.assertEquals(anyValue, config.getProperty(someKey, anyValue));
        Assert.assertEquals(NONE, config.getSourceType());
    }

    @Test
    public void testOnRepositoryChange() throws Exception {
        Properties someProperties = new Properties();
        String someKey = "someKey";
        String someValue = "someValue";
        String anotherKey = "anotherKey";
        String anotherValue = "anotherValue";
        someProperties.putAll(ImmutableMap.of(someKey, someValue, anotherKey, anotherValue));
        Properties anotherProperties = new Properties();
        String newKey = "newKey";
        String newValue = "newValue";
        String someValueNew = "someValueNew";
        anotherProperties.putAll(ImmutableMap.of(someKey, someValueNew, newKey, newValue));
        someSourceType = ConfigSourceType.LOCAL;
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        final SettableFuture<ConfigChangeEvent> configChangeFuture = SettableFuture.create();
        ConfigChangeListener someListener = new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                configChangeFuture.set(changeEvent);
            }
        };
        SimpleConfig config = new SimpleConfig(someNamespace, configRepository);
        Assert.assertEquals(someSourceType, config.getSourceType());
        config.addChangeListener(someListener);
        ConfigSourceType anotherSourceType = ConfigSourceType.REMOTE;
        Mockito.when(configRepository.getSourceType()).thenReturn(anotherSourceType);
        config.onRepositoryChange(someNamespace, anotherProperties);
        ConfigChangeEvent changeEvent = configChangeFuture.get(500, TimeUnit.MILLISECONDS);
        Assert.assertEquals(someNamespace, changeEvent.getNamespace());
        Assert.assertEquals(3, changeEvent.changedKeys().size());
        ConfigChange someKeyChange = changeEvent.getChange(someKey);
        Assert.assertEquals(someValue, someKeyChange.getOldValue());
        Assert.assertEquals(someValueNew, someKeyChange.getNewValue());
        Assert.assertEquals(MODIFIED, someKeyChange.getChangeType());
        ConfigChange anotherKeyChange = changeEvent.getChange(anotherKey);
        Assert.assertEquals(anotherValue, anotherKeyChange.getOldValue());
        Assert.assertEquals(null, anotherKeyChange.getNewValue());
        Assert.assertEquals(DELETED, anotherKeyChange.getChangeType());
        ConfigChange newKeyChange = changeEvent.getChange(newKey);
        Assert.assertEquals(null, newKeyChange.getOldValue());
        Assert.assertEquals(newValue, newKeyChange.getNewValue());
        Assert.assertEquals(ADDED, newKeyChange.getChangeType());
        Assert.assertEquals(anotherSourceType, config.getSourceType());
    }
}

