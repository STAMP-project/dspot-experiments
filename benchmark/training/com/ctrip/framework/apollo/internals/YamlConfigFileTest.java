package com.ctrip.framework.apollo.internals;


import ConfigSourceType.NONE;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.enums.ConfigSourceType;
import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.util.yaml.YamlParser;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class YamlConfigFileTest {
    private String someNamespace;

    @Mock
    private ConfigRepository configRepository;

    @Mock
    private YamlParser yamlParser;

    private ConfigSourceType someSourceType;

    @Test
    public void testWhenHasContent() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someContent = "someKey: 'someValue'";
        someProperties.setProperty(key, someContent);
        someSourceType = ConfigSourceType.LOCAL;
        Properties yamlProperties = new Properties();
        yamlProperties.setProperty("someKey", "someValue");
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        Mockito.when(yamlParser.yamlToProperties(someContent)).thenReturn(yamlProperties);
        YamlConfigFile configFile = new YamlConfigFile(someNamespace, configRepository);
        Assert.assertSame(someContent, configFile.getContent());
        Assert.assertSame(yamlProperties, configFile.asProperties());
    }

    @Test
    public void testWhenHasNoContent() throws Exception {
        Mockito.when(configRepository.getConfig()).thenReturn(null);
        YamlConfigFile configFile = new YamlConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
        Properties properties = configFile.asProperties();
        Assert.assertTrue(properties.isEmpty());
    }

    @Test
    public void testWhenInvalidYamlContent() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someInvalidContent = ",";
        someProperties.setProperty(key, someInvalidContent);
        someSourceType = ConfigSourceType.LOCAL;
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        Mockito.when(yamlParser.yamlToProperties(someInvalidContent)).thenThrow(new RuntimeException("some exception"));
        YamlConfigFile configFile = new YamlConfigFile(someNamespace, configRepository);
        Assert.assertSame(someInvalidContent, configFile.getContent());
        Throwable exceptionThrown = null;
        try {
            configFile.asProperties();
        } catch (Throwable ex) {
            exceptionThrown = ex;
        }
        Assert.assertTrue((exceptionThrown instanceof ApolloConfigException));
        Assert.assertNotNull(exceptionThrown.getCause());
    }

    @Test
    public void testWhenConfigRepositoryHasError() throws Exception {
        Mockito.when(configRepository.getConfig()).thenThrow(new RuntimeException("someError"));
        YamlConfigFile configFile = new YamlConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
        Assert.assertEquals(NONE, configFile.getSourceType());
        Properties properties = configFile.asProperties();
        Assert.assertTrue(properties.isEmpty());
    }

    @Test
    public void testOnRepositoryChange() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someValue = "someKey: 'someValue'";
        String anotherValue = "anotherKey: 'anotherValue'";
        someProperties.setProperty(key, someValue);
        someSourceType = ConfigSourceType.LOCAL;
        Properties someYamlProperties = new Properties();
        someYamlProperties.setProperty("someKey", "someValue");
        Properties anotherYamlProperties = new Properties();
        anotherYamlProperties.setProperty("anotherKey", "anotherValue");
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        Mockito.when(yamlParser.yamlToProperties(someValue)).thenReturn(someYamlProperties);
        Mockito.when(yamlParser.yamlToProperties(anotherValue)).thenReturn(anotherYamlProperties);
        YamlConfigFile configFile = new YamlConfigFile(someNamespace, configRepository);
        Assert.assertEquals(someValue, configFile.getContent());
        Assert.assertEquals(someSourceType, configFile.getSourceType());
        Assert.assertSame(someYamlProperties, configFile.asProperties());
        Properties anotherProperties = new Properties();
        anotherProperties.setProperty(key, anotherValue);
        ConfigSourceType anotherSourceType = ConfigSourceType.REMOTE;
        Mockito.when(configRepository.getSourceType()).thenReturn(anotherSourceType);
        configFile.onRepositoryChange(someNamespace, anotherProperties);
        Assert.assertEquals(anotherValue, configFile.getContent());
        Assert.assertEquals(anotherSourceType, configFile.getSourceType());
        Assert.assertSame(anotherYamlProperties, configFile.asProperties());
    }

    @Test
    public void testWhenConfigRepositoryHasErrorAndThenRecovered() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someValue = "someKey: 'someValue'";
        someProperties.setProperty(key, someValue);
        someSourceType = ConfigSourceType.LOCAL;
        Properties someYamlProperties = new Properties();
        someYamlProperties.setProperty("someKey", "someValue");
        Mockito.when(configRepository.getConfig()).thenThrow(new RuntimeException("someError"));
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        Mockito.when(yamlParser.yamlToProperties(someValue)).thenReturn(someYamlProperties);
        YamlConfigFile configFile = new YamlConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
        Assert.assertEquals(NONE, configFile.getSourceType());
        Assert.assertTrue(configFile.asProperties().isEmpty());
        configFile.onRepositoryChange(someNamespace, someProperties);
        Assert.assertTrue(configFile.hasContent());
        Assert.assertEquals(someValue, configFile.getContent());
        Assert.assertEquals(someSourceType, configFile.getSourceType());
        Assert.assertSame(someYamlProperties, configFile.asProperties());
    }
}

