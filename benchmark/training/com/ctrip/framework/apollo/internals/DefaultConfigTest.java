package com.ctrip.framework.apollo.internals;


import ConfigSourceType.NONE;
import PropertyChangeType.ADDED;
import PropertyChangeType.DELETED;
import PropertyChangeType.MODIFIED;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.build.MockInjector;
import com.ctrip.framework.apollo.enums.ConfigSourceType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.util.concurrent.SettableFuture;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigTest {
    private File someResourceDir;

    private String someNamespace;

    private ConfigRepository configRepository;

    private Properties someProperties;

    private ConfigSourceType someSourceType;

    @Test
    public void testGetPropertyWithAllPropertyHierarchy() throws Exception {
        String someKey = "someKey";
        String someSystemPropertyValue = "system-property-value";
        String anotherKey = "anotherKey";
        String someLocalFileValue = "local-file-value";
        String lastKey = "lastKey";
        String someResourceValue = "resource-value";
        // set up system property
        System.setProperty(someKey, someSystemPropertyValue);
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someKey, someLocalFileValue);
        someProperties.setProperty(anotherKey, someLocalFileValue);
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        someSourceType = ConfigSourceType.LOCAL;
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        // set up resource file
        File resourceFile = new File(someResourceDir, ((someNamespace) + ".properties"));
        Files.write(((someKey + "=") + someResourceValue), resourceFile, Charsets.UTF_8);
        Files.append(System.getProperty("line.separator"), resourceFile, Charsets.UTF_8);
        Files.append(((anotherKey + "=") + someResourceValue), resourceFile, Charsets.UTF_8);
        Files.append(System.getProperty("line.separator"), resourceFile, Charsets.UTF_8);
        Files.append(((lastKey + "=") + someResourceValue), resourceFile, Charsets.UTF_8);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        String someKeyValue = defaultConfig.getProperty(someKey, null);
        String anotherKeyValue = defaultConfig.getProperty(anotherKey, null);
        String lastKeyValue = defaultConfig.getProperty(lastKey, null);
        // clean up
        System.clearProperty(someKey);
        Assert.assertEquals(someSystemPropertyValue, someKeyValue);
        Assert.assertEquals(someLocalFileValue, anotherKeyValue);
        Assert.assertEquals(someResourceValue, lastKeyValue);
        Assert.assertEquals(someSourceType, defaultConfig.getSourceType());
    }

    @Test
    public void testGetIntProperty() throws Exception {
        String someStringKey = "someStringKey";
        String someStringValue = "someStringValue";
        String someKey = "someKey";
        Integer someValue = 2;
        Integer someDefaultValue = -1;
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someStringKey, someStringValue);
        someProperties.setProperty(someKey, String.valueOf(someValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Assert.assertEquals(someDefaultValue, defaultConfig.getIntProperty(someStringKey, someDefaultValue));
    }

    @Test
    public void testGetIntPropertyMultipleTimesWithCache() throws Exception {
        String someKey = "someKey";
        Integer someValue = 2;
        Integer someDefaultValue = -1;
        // set up config repo
        someProperties = Mockito.mock(Properties.class);
        Mockito.when(someProperties.getProperty(someKey)).thenReturn(String.valueOf(someValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Mockito.verify(someProperties, Mockito.times(1)).getProperty(someKey);
    }

    @Test
    public void testGetIntPropertyMultipleTimesWithPropertyChanges() throws Exception {
        String someKey = "someKey";
        Integer someValue = 2;
        Integer anotherValue = 3;
        Integer someDefaultValue = -1;
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someKey, String.valueOf(someValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Properties anotherProperties = new Properties();
        anotherProperties.setProperty(someKey, String.valueOf(anotherValue));
        defaultConfig.onRepositoryChange(someNamespace, anotherProperties);
        Assert.assertEquals(anotherValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
    }

    @Test
    public void testGetIntPropertyMultipleTimesWithSmallCache() throws Exception {
        String someKey = "someKey";
        Integer someValue = 2;
        String anotherKey = "anotherKey";
        Integer anotherValue = 3;
        Integer someDefaultValue = -1;
        MockInjector.setInstance(ConfigUtil.class, new DefaultConfigTest.MockConfigUtilWithSmallCache());
        // set up config repo
        someProperties = Mockito.mock(Properties.class);
        Mockito.when(someProperties.getProperty(someKey)).thenReturn(String.valueOf(someValue));
        Mockito.when(someProperties.getProperty(anotherKey)).thenReturn(String.valueOf(anotherValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Mockito.verify(someProperties, Mockito.times(1)).getProperty(someKey);
        Assert.assertEquals(anotherValue, defaultConfig.getIntProperty(anotherKey, someDefaultValue));
        Assert.assertEquals(anotherValue, defaultConfig.getIntProperty(anotherKey, someDefaultValue));
        Mockito.verify(someProperties, Mockito.times(1)).getProperty(anotherKey);
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Mockito.verify(someProperties, Mockito.times(2)).getProperty(someKey);
    }

    @Test
    public void testGetIntPropertyMultipleTimesWithShortExpireTime() throws Exception {
        String someKey = "someKey";
        Integer someValue = 2;
        Integer someDefaultValue = -1;
        MockInjector.setInstance(ConfigUtil.class, new DefaultConfigTest.MockConfigUtilWithShortExpireTime());
        // set up config repo
        someProperties = Mockito.mock(Properties.class);
        Mockito.when(someProperties.getProperty(someKey)).thenReturn(String.valueOf(someValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Mockito.verify(someProperties, Mockito.times(1)).getProperty(someKey);
        TimeUnit.MILLISECONDS.sleep(50);
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Assert.assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
        Mockito.verify(someProperties, Mockito.times(2)).getProperty(someKey);
    }

    @Test
    public void testGetLongProperty() throws Exception {
        String someStringKey = "someStringKey";
        String someStringValue = "someStringValue";
        String someKey = "someKey";
        Long someValue = 2L;
        Long someDefaultValue = -1L;
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someStringKey, someStringValue);
        someProperties.setProperty(someKey, String.valueOf(someValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, defaultConfig.getLongProperty(someKey, someDefaultValue));
        Assert.assertEquals(someDefaultValue, defaultConfig.getLongProperty(someStringKey, someDefaultValue));
    }

    @Test
    public void testGetShortProperty() throws Exception {
        String someStringKey = "someStringKey";
        String someStringValue = "someStringValue";
        String someKey = "someKey";
        Short someValue = 2;
        Short someDefaultValue = -1;
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someStringKey, someStringValue);
        someProperties.setProperty(someKey, String.valueOf(someValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, defaultConfig.getShortProperty(someKey, someDefaultValue));
        Assert.assertEquals(someDefaultValue, defaultConfig.getShortProperty(someStringKey, someDefaultValue));
    }

    @Test
    public void testGetFloatProperty() throws Exception {
        String someStringKey = "someStringKey";
        String someStringValue = "someStringValue";
        String someKey = "someKey";
        Float someValue = 2.2F;
        Float someDefaultValue = -1.0F;
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someStringKey, someStringValue);
        someProperties.setProperty(someKey, String.valueOf(someValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, defaultConfig.getFloatProperty(someKey, someDefaultValue), 0.001);
        Assert.assertEquals(someDefaultValue, defaultConfig.getFloatProperty(someStringKey, someDefaultValue), 0.001);
    }

    @Test
    public void testGetDoubleProperty() throws Exception {
        String someStringKey = "someStringKey";
        String someStringValue = "someStringValue";
        String someKey = "someKey";
        Double someValue = 2.2;
        Double someDefaultValue = -1.0;
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someStringKey, someStringValue);
        someProperties.setProperty(someKey, String.valueOf(someValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, defaultConfig.getDoubleProperty(someKey, someDefaultValue), 0.001);
        Assert.assertEquals(someDefaultValue, defaultConfig.getDoubleProperty(someStringKey, someDefaultValue), 0.001);
    }

    @Test
    public void testGetByteProperty() throws Exception {
        String someStringKey = "someStringKey";
        String someStringValue = "someStringValue";
        String someKey = "someKey";
        Byte someValue = 10;
        Byte someDefaultValue = -1;
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someStringKey, someStringValue);
        someProperties.setProperty(someKey, String.valueOf(someValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, defaultConfig.getByteProperty(someKey, someDefaultValue));
        Assert.assertEquals(someDefaultValue, defaultConfig.getByteProperty(someStringKey, someDefaultValue));
    }

    @Test
    public void testGetBooleanProperty() throws Exception {
        String someStringKey = "someStringKey";
        String someStringValue = "someStringValue";
        String someKey = "someKey";
        Boolean someValue = true;
        Boolean someDefaultValue = false;
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someStringKey, someStringValue);
        someProperties.setProperty(someKey, String.valueOf(someValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someValue, defaultConfig.getBooleanProperty(someKey, someDefaultValue));
        Assert.assertEquals(someDefaultValue, defaultConfig.getBooleanProperty(someStringKey, someDefaultValue));
    }

    @Test
    public void testGetArrayProperty() throws Exception {
        String someKey = "someKey";
        String someDelimiter = ",";
        String someInvalidDelimiter = "{";
        String[] values = new String[]{ "a", "b", "c" };
        String someValue = Joiner.on(someDelimiter).join(values);
        String[] someDefaultValue = new String[]{ "1", "2" };
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someKey, someValue);
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertArrayEquals(values, defaultConfig.getArrayProperty(someKey, someDelimiter, someDefaultValue));
        Assert.assertArrayEquals(someDefaultValue, defaultConfig.getArrayProperty(someKey, someInvalidDelimiter, someDefaultValue));
    }

    @Test
    public void testGetArrayPropertyMultipleTimesWithCache() throws Exception {
        String someKey = "someKey";
        String someDelimiter = ",";
        String someInvalidDelimiter = "{";
        String[] values = new String[]{ "a", "b", "c" };
        String someValue = Joiner.on(someDelimiter).join(values);
        String[] someDefaultValue = new String[]{ "1", "2" };
        // set up config repo
        someProperties = Mockito.mock(Properties.class);
        Mockito.when(someProperties.getProperty(someKey)).thenReturn(someValue);
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertArrayEquals(values, defaultConfig.getArrayProperty(someKey, someDelimiter, someDefaultValue));
        Assert.assertArrayEquals(values, defaultConfig.getArrayProperty(someKey, someDelimiter, someDefaultValue));
        Mockito.verify(someProperties, Mockito.times(1)).getProperty(someKey);
        Assert.assertArrayEquals(someDefaultValue, defaultConfig.getArrayProperty(someKey, someInvalidDelimiter, someDefaultValue));
        Assert.assertArrayEquals(someDefaultValue, defaultConfig.getArrayProperty(someKey, someInvalidDelimiter, someDefaultValue));
        Mockito.verify(someProperties, Mockito.times(3)).getProperty(someKey);
    }

    @Test
    public void testGetArrayPropertyMultipleTimesWithCacheAndValueChanges() throws Exception {
        String someKey = "someKey";
        String someDelimiter = ",";
        String[] values = new String[]{ "a", "b", "c" };
        String[] anotherValues = new String[]{ "b", "c", "d" };
        String someValue = Joiner.on(someDelimiter).join(values);
        String anotherValue = Joiner.on(someDelimiter).join(anotherValues);
        String[] someDefaultValue = new String[]{ "1", "2" };
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someKey, someValue);
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        Properties anotherProperties = new Properties();
        anotherProperties.setProperty(someKey, anotherValue);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertArrayEquals(values, defaultConfig.getArrayProperty(someKey, someDelimiter, someDefaultValue));
        defaultConfig.onRepositoryChange(someNamespace, anotherProperties);
        Assert.assertArrayEquals(anotherValues, defaultConfig.getArrayProperty(someKey, someDelimiter, someDefaultValue));
    }

    @Test
    public void testGetDatePropertyWithFormat() throws Exception {
        Date someDefaultValue = new Date();
        Date shortDate = assembleDate(2016, 9, 28, 0, 0, 0, 0);
        Date mediumDate = assembleDate(2016, 9, 28, 15, 10, 10, 0);
        Date longDate = assembleDate(2016, 9, 28, 15, 10, 10, 123);
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty("shortDateProperty", "2016-09-28");
        someProperties.setProperty("mediumDateProperty", "2016-09-28 15:10:10");
        someProperties.setProperty("longDateProperty", "2016-09-28 15:10:10.123");
        someProperties.setProperty("stringProperty", "someString");
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        checkDatePropertyWithFormat(defaultConfig, shortDate, "shortDateProperty", "yyyy-MM-dd", someDefaultValue);
        checkDatePropertyWithFormat(defaultConfig, mediumDate, "mediumDateProperty", "yyyy-MM-dd HH:mm:ss", someDefaultValue);
        checkDatePropertyWithFormat(defaultConfig, shortDate, "mediumDateProperty", "yyyy-MM-dd", someDefaultValue);
        checkDatePropertyWithFormat(defaultConfig, longDate, "longDateProperty", "yyyy-MM-dd HH:mm:ss.SSS", someDefaultValue);
        checkDatePropertyWithFormat(defaultConfig, mediumDate, "longDateProperty", "yyyy-MM-dd HH:mm:ss", someDefaultValue);
        checkDatePropertyWithFormat(defaultConfig, shortDate, "longDateProperty", "yyyy-MM-dd", someDefaultValue);
        checkDatePropertyWithFormat(defaultConfig, someDefaultValue, "stringProperty", "yyyy-MM-dd", someDefaultValue);
    }

    @Test
    public void testGetDatePropertyWithNoFormat() throws Exception {
        Date someDefaultValue = new Date();
        Date shortDate = assembleDate(2016, 9, 28, 0, 0, 0, 0);
        Date mediumDate = assembleDate(2016, 9, 28, 15, 10, 10, 0);
        Date longDate = assembleDate(2016, 9, 28, 15, 10, 10, 123);
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty("shortDateProperty", "2016-09-28");
        someProperties.setProperty("mediumDateProperty", "2016-09-28 15:10:10");
        someProperties.setProperty("longDateProperty", "2016-09-28 15:10:10.123");
        someProperties.setProperty("stringProperty", "someString");
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        checkDatePropertyWithoutFormat(defaultConfig, shortDate, "shortDateProperty", someDefaultValue);
        checkDatePropertyWithoutFormat(defaultConfig, mediumDate, "mediumDateProperty", someDefaultValue);
        checkDatePropertyWithoutFormat(defaultConfig, longDate, "longDateProperty", someDefaultValue);
        checkDatePropertyWithoutFormat(defaultConfig, someDefaultValue, "stringProperty", someDefaultValue);
    }

    @Test
    public void testGetEnumProperty() throws Exception {
        DefaultConfigTest.SomeEnum someDefaultValue = DefaultConfigTest.SomeEnum.defaultValue;
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty("enumProperty", "someValue");
        someProperties.setProperty("stringProperty", "someString");
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(DefaultConfigTest.SomeEnum.someValue, defaultConfig.getEnumProperty("enumProperty", DefaultConfigTest.SomeEnum.class, someDefaultValue));
        Assert.assertEquals(someDefaultValue, defaultConfig.getEnumProperty("stringProperty", DefaultConfigTest.SomeEnum.class, someDefaultValue));
    }

    @Test
    public void testGetDurationProperty() throws Exception {
        long someDefaultValue = 1000;
        long result = ((((((2 * 24) * 3600) * 1000) + ((3 * 3600) * 1000)) + ((4 * 60) * 1000)) + (5 * 1000)) + 123;
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty("durationProperty", "2D3H4m5S123ms");
        someProperties.setProperty("stringProperty", "someString");
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(result, defaultConfig.getDurationProperty("durationProperty", someDefaultValue));
        Assert.assertEquals(someDefaultValue, defaultConfig.getDurationProperty("stringProperty", someDefaultValue));
    }

    @Test
    public void testOnRepositoryChange() throws Exception {
        String someKey = "someKey";
        String someSystemPropertyValue = "system-property-value";
        String anotherKey = "anotherKey";
        String someLocalFileValue = "local-file-value";
        String keyToBeDeleted = "keyToBeDeleted";
        String keyToBeDeletedValue = "keyToBeDeletedValue";
        String yetAnotherKey = "yetAnotherKey";
        String yetAnotherValue = "yetAnotherValue";
        String yetAnotherResourceValue = "yetAnotherResourceValue";
        // set up system property
        System.setProperty(someKey, someSystemPropertyValue);
        // set up config repo
        someProperties = new Properties();
        someProperties.putAll(ImmutableMap.of(someKey, someLocalFileValue, anotherKey, someLocalFileValue, keyToBeDeleted, keyToBeDeletedValue, yetAnotherKey, yetAnotherValue));
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        someSourceType = ConfigSourceType.LOCAL;
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        // set up resource file
        File resourceFile = new File(someResourceDir, ((someNamespace) + ".properties"));
        Files.append(((yetAnotherKey + "=") + yetAnotherResourceValue), resourceFile, Charsets.UTF_8);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(someSourceType, defaultConfig.getSourceType());
        final SettableFuture<ConfigChangeEvent> configChangeFuture = SettableFuture.create();
        ConfigChangeListener someListener = new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                configChangeFuture.set(changeEvent);
            }
        };
        defaultConfig.addChangeListener(someListener);
        Properties newProperties = new Properties();
        String someKeyNewValue = "new-some-value";
        String anotherKeyNewValue = "another-new-value";
        String newKey = "newKey";
        String newValue = "newValue";
        newProperties.putAll(ImmutableMap.of(someKey, someKeyNewValue, anotherKey, anotherKeyNewValue, newKey, newValue));
        ConfigSourceType anotherSourceType = ConfigSourceType.REMOTE;
        Mockito.when(configRepository.getSourceType()).thenReturn(anotherSourceType);
        defaultConfig.onRepositoryChange(someNamespace, newProperties);
        ConfigChangeEvent changeEvent = configChangeFuture.get(500, TimeUnit.MILLISECONDS);
        // clean up
        System.clearProperty(someKey);
        Assert.assertEquals(someNamespace, changeEvent.getNamespace());
        Assert.assertEquals(4, changeEvent.changedKeys().size());
        ConfigChange anotherKeyChange = changeEvent.getChange(anotherKey);
        Assert.assertEquals(someLocalFileValue, anotherKeyChange.getOldValue());
        Assert.assertEquals(anotherKeyNewValue, anotherKeyChange.getNewValue());
        Assert.assertEquals(MODIFIED, anotherKeyChange.getChangeType());
        ConfigChange yetAnotherKeyChange = changeEvent.getChange(yetAnotherKey);
        Assert.assertEquals(yetAnotherValue, yetAnotherKeyChange.getOldValue());
        Assert.assertEquals(yetAnotherResourceValue, yetAnotherKeyChange.getNewValue());
        Assert.assertEquals(MODIFIED, yetAnotherKeyChange.getChangeType());
        ConfigChange keyToBeDeletedChange = changeEvent.getChange(keyToBeDeleted);
        Assert.assertEquals(keyToBeDeletedValue, keyToBeDeletedChange.getOldValue());
        Assert.assertEquals(null, keyToBeDeletedChange.getNewValue());
        Assert.assertEquals(DELETED, keyToBeDeletedChange.getChangeType());
        ConfigChange newKeyChange = changeEvent.getChange(newKey);
        Assert.assertEquals(null, newKeyChange.getOldValue());
        Assert.assertEquals(newValue, newKeyChange.getNewValue());
        Assert.assertEquals(ADDED, newKeyChange.getChangeType());
        Assert.assertEquals(anotherSourceType, defaultConfig.getSourceType());
    }

    @Test
    public void testFireConfigChangeWithInterestedKeys() throws Exception {
        String someKeyChanged = "someKeyChanged";
        String anotherKeyChanged = "anotherKeyChanged";
        String someKeyNotChanged = "someKeyNotChanged";
        String someNamespace = "someNamespace";
        Map<String, ConfigChange> changes = Maps.newHashMap();
        changes.put(someKeyChanged, Mockito.mock(ConfigChange.class));
        changes.put(anotherKeyChanged, Mockito.mock(ConfigChange.class));
        ConfigChangeEvent someChangeEvent = new ConfigChangeEvent(someNamespace, changes);
        final SettableFuture<ConfigChangeEvent> interestedInAllKeysFuture = SettableFuture.create();
        ConfigChangeListener interestedInAllKeys = new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                interestedInAllKeysFuture.set(changeEvent);
            }
        };
        final SettableFuture<ConfigChangeEvent> interestedInSomeKeyFuture = SettableFuture.create();
        ConfigChangeListener interestedInSomeKey = new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                interestedInSomeKeyFuture.set(changeEvent);
            }
        };
        final SettableFuture<ConfigChangeEvent> interestedInSomeKeyNotChangedFuture = SettableFuture.create();
        ConfigChangeListener interestedInSomeKeyNotChanged = new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                interestedInSomeKeyNotChangedFuture.set(changeEvent);
            }
        };
        DefaultConfig config = new DefaultConfig(someNamespace, Mockito.mock(ConfigRepository.class));
        config.addChangeListener(interestedInAllKeys);
        config.addChangeListener(interestedInSomeKey, Sets.newHashSet(someKeyChanged));
        config.addChangeListener(interestedInSomeKeyNotChanged, Sets.newHashSet(someKeyNotChanged));
        config.fireConfigChange(someChangeEvent);
        ConfigChangeEvent changeEvent = interestedInAllKeysFuture.get(500, TimeUnit.MILLISECONDS);
        Assert.assertEquals(someChangeEvent, changeEvent);
        Assert.assertEquals(someChangeEvent, interestedInSomeKeyFuture.get(500, TimeUnit.MILLISECONDS));
        Assert.assertFalse(interestedInSomeKeyNotChangedFuture.isDone());
    }

    @Test
    public void testRemoveChangeListener() throws Exception {
        String someNamespace = "someNamespace";
        final ConfigChangeEvent someConfigChangEvent = Mockito.mock(ConfigChangeEvent.class);
        ConfigChangeEvent anotherConfigChangEvent = Mockito.mock(ConfigChangeEvent.class);
        final SettableFuture<ConfigChangeEvent> someListenerFuture1 = SettableFuture.create();
        final SettableFuture<ConfigChangeEvent> someListenerFuture2 = SettableFuture.create();
        ConfigChangeListener someListener = new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                if (someConfigChangEvent == changeEvent) {
                    someListenerFuture1.set(changeEvent);
                } else {
                    someListenerFuture2.set(changeEvent);
                }
            }
        };
        final SettableFuture<ConfigChangeEvent> anotherListenerFuture1 = SettableFuture.create();
        final SettableFuture<ConfigChangeEvent> anotherListenerFuture2 = SettableFuture.create();
        ConfigChangeListener anotherListener = new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                if (someConfigChangEvent == changeEvent) {
                    anotherListenerFuture1.set(changeEvent);
                } else {
                    anotherListenerFuture2.set(changeEvent);
                }
            }
        };
        ConfigChangeListener yetAnotherListener = Mockito.mock(ConfigChangeListener.class);
        DefaultConfig config = new DefaultConfig(someNamespace, Mockito.mock(ConfigRepository.class));
        config.addChangeListener(someListener);
        config.addChangeListener(anotherListener);
        config.fireConfigChange(someConfigChangEvent);
        Assert.assertEquals(someConfigChangEvent, someListenerFuture1.get(500, TimeUnit.MILLISECONDS));
        Assert.assertEquals(someConfigChangEvent, anotherListenerFuture1.get(500, TimeUnit.MILLISECONDS));
        Assert.assertFalse(config.removeChangeListener(yetAnotherListener));
        Assert.assertTrue(config.removeChangeListener(someListener));
        config.fireConfigChange(anotherConfigChangEvent);
        Assert.assertEquals(anotherConfigChangEvent, anotherListenerFuture2.get(500, TimeUnit.MILLISECONDS));
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertFalse(someListenerFuture2.isDone());
    }

    @Test
    public void testGetPropertyNames() {
        String someKeyPrefix = "someKey";
        String someValuePrefix = "someValue";
        // set up config repo
        someProperties = new Properties();
        for (int i = 0; i < 10; i++) {
            someProperties.setProperty((someKeyPrefix + i), (someValuePrefix + i));
        }
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Set<String> propertyNames = defaultConfig.getPropertyNames();
        Assert.assertEquals(10, propertyNames.size());
        Assert.assertEquals(someProperties.stringPropertyNames(), propertyNames);
    }

    @Test
    public void testGetPropertyNamesWithNullProp() {
        Mockito.when(configRepository.getConfig()).thenReturn(null);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Set<String> propertyNames = defaultConfig.getPropertyNames();
        Assert.assertEquals(Collections.emptySet(), propertyNames);
    }

    @Test
    public void testGetPropertyWithFunction() throws Exception {
        String someKey = "someKey";
        String someValue = "a,b,c";
        String someNullKey = "someNullKey";
        // set up config repo
        someProperties = new Properties();
        someProperties.setProperty(someKey, someValue);
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Assert.assertEquals(defaultConfig.getProperty(someKey, new Function<String, List<String>>() {
            @Override
            public List<String> apply(String s) {
                return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(s);
            }
        }, Lists.<String>newArrayList()), Lists.newArrayList("a", "b", "c"));
        Assert.assertEquals(defaultConfig.getProperty(someNullKey, new Function<String, List<String>>() {
            @Override
            public List<String> apply(String s) {
                return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(s);
            }
        }, Lists.<String>newArrayList()), Lists.newArrayList());
    }

    @Test
    public void testLoadFromRepositoryFailedAndThenRecovered() {
        String someKey = "someKey";
        String someValue = "someValue";
        String someDefaultValue = "someDefaultValue";
        ConfigSourceType someSourceType = ConfigSourceType.REMOTE;
        Mockito.when(configRepository.getConfig()).thenThrow(Mockito.mock(RuntimeException.class));
        DefaultConfig defaultConfig = new DefaultConfig(someNamespace, configRepository);
        Mockito.verify(configRepository, Mockito.times(1)).addChangeListener(defaultConfig);
        Assert.assertEquals(NONE, defaultConfig.getSourceType());
        Assert.assertEquals(someDefaultValue, defaultConfig.getProperty(someKey, someDefaultValue));
        someProperties = new Properties();
        someProperties.setProperty(someKey, someValue);
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        defaultConfig.onRepositoryChange(someNamespace, someProperties);
        Assert.assertEquals(someSourceType, defaultConfig.getSourceType());
        Assert.assertEquals(someValue, defaultConfig.getProperty(someKey, someDefaultValue));
    }

    private enum SomeEnum {

        someValue,
        defaultValue;}

    public static class MockConfigUtil extends ConfigUtil {
        @Override
        public long getMaxConfigCacheSize() {
            return 10;
        }

        @Override
        public long getConfigCacheExpireTime() {
            return 1;
        }

        @Override
        public TimeUnit getConfigCacheExpireTimeUnit() {
            return TimeUnit.MINUTES;
        }
    }

    public static class MockConfigUtilWithSmallCache extends DefaultConfigTest.MockConfigUtil {
        @Override
        public long getMaxConfigCacheSize() {
            return 1;
        }
    }

    public static class MockConfigUtilWithShortExpireTime extends DefaultConfigTest.MockConfigUtil {
        @Override
        public long getConfigCacheExpireTime() {
            return 50;
        }

        @Override
        public TimeUnit getConfigCacheExpireTimeUnit() {
            return TimeUnit.MILLISECONDS;
        }
    }
}

