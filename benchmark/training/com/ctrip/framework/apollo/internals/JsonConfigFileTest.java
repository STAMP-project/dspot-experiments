package com.ctrip.framework.apollo.internals;


import ConfigFileFormat.JSON;
import ConfigSourceType.NONE;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.enums.ConfigSourceType;
import java.util.Properties;
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
public class JsonConfigFileTest {
    private String someNamespace;

    @Mock
    private ConfigRepository configRepository;

    private ConfigSourceType someSourceType;

    @Test
    public void testWhenHasContent() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someValue = "someValue";
        someProperties.setProperty(key, someValue);
        someSourceType = ConfigSourceType.LOCAL;
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        JsonConfigFile configFile = new JsonConfigFile(someNamespace, configRepository);
        Assert.assertEquals(JSON, configFile.getConfigFileFormat());
        Assert.assertEquals(someNamespace, configFile.getNamespace());
        Assert.assertTrue(configFile.hasContent());
        Assert.assertEquals(someValue, configFile.getContent());
        Assert.assertEquals(someSourceType, configFile.getSourceType());
    }

    @Test
    public void testWhenHasNoContent() throws Exception {
        Mockito.when(configRepository.getConfig()).thenReturn(null);
        JsonConfigFile configFile = new JsonConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
    }

    @Test
    public void testWhenConfigRepositoryHasError() throws Exception {
        Mockito.when(configRepository.getConfig()).thenThrow(new RuntimeException("someError"));
        JsonConfigFile configFile = new JsonConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
        Assert.assertEquals(NONE, configFile.getSourceType());
    }

    @Test
    public void testOnRepositoryChange() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someValue = "someValue";
        String anotherValue = "anotherValue";
        someProperties.setProperty(key, someValue);
        someSourceType = ConfigSourceType.LOCAL;
        Mockito.when(configRepository.getConfig()).thenReturn(someProperties);
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        JsonConfigFile configFile = new JsonConfigFile(someNamespace, configRepository);
        Assert.assertEquals(someValue, configFile.getContent());
        Assert.assertEquals(someSourceType, configFile.getSourceType());
        Properties anotherProperties = new Properties();
        anotherProperties.setProperty(key, anotherValue);
        ConfigSourceType anotherSourceType = ConfigSourceType.REMOTE;
        Mockito.when(configRepository.getSourceType()).thenReturn(anotherSourceType);
        configFile.onRepositoryChange(someNamespace, anotherProperties);
        Assert.assertEquals(anotherValue, configFile.getContent());
        Assert.assertEquals(anotherSourceType, configFile.getSourceType());
    }

    @Test
    public void testWhenConfigRepositoryHasErrorAndThenRecovered() throws Exception {
        Properties someProperties = new Properties();
        String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
        String someValue = "someValue";
        someProperties.setProperty(key, someValue);
        someSourceType = ConfigSourceType.LOCAL;
        Mockito.when(configRepository.getConfig()).thenThrow(new RuntimeException("someError"));
        Mockito.when(configRepository.getSourceType()).thenReturn(someSourceType);
        JsonConfigFile configFile = new JsonConfigFile(someNamespace, configRepository);
        Assert.assertFalse(configFile.hasContent());
        Assert.assertNull(configFile.getContent());
        Assert.assertEquals(NONE, configFile.getSourceType());
        configFile.onRepositoryChange(someNamespace, someProperties);
        Assert.assertTrue(configFile.hasContent());
        Assert.assertEquals(someValue, configFile.getContent());
        Assert.assertEquals(someSourceType, configFile.getSourceType());
    }
}

