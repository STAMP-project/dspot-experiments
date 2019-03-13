package com.ctrip.framework.apollo.internals;


import com.ctrip.framework.apollo.PropertiesCompatibleConfigFile;
import com.ctrip.framework.apollo.enums.ConfigSourceType;
import com.ctrip.framework.apollo.model.ConfigFileChangeEvent;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PropertiesCompatibleFileConfigRepositoryTest {
    @Mock
    private PropertiesCompatibleConfigFile configFile;

    private String someNamespaceName;

    @Mock
    private Properties someProperties;

    @Test
    public void testGetConfig() throws Exception {
        PropertiesCompatibleFileConfigRepository configFileRepository = new PropertiesCompatibleFileConfigRepository(configFile);
        Assert.assertSame(someProperties, configFileRepository.getConfig());
        Mockito.verify(configFile, Mockito.times(1)).addChangeListener(configFileRepository);
    }

    @Test
    public void testGetConfigFailedAndThenRecovered() throws Exception {
        RuntimeException someException = new RuntimeException("some exception");
        Mockito.when(configFile.asProperties()).thenThrow(someException);
        PropertiesCompatibleFileConfigRepository configFileRepository = new PropertiesCompatibleFileConfigRepository(configFile);
        Throwable exceptionThrown = null;
        try {
            configFileRepository.getConfig();
        } catch (Throwable ex) {
            exceptionThrown = ex;
        }
        Assert.assertSame(someException, exceptionThrown);
        // recovered
        Mockito.reset(configFile);
        Properties someProperties = Mockito.mock(Properties.class);
        Mockito.when(configFile.asProperties()).thenReturn(someProperties);
        Assert.assertSame(someProperties, configFileRepository.getConfig());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetConfigWithConfigFileReturnNullProperties() throws Exception {
        Mockito.when(configFile.asProperties()).thenReturn(null);
        PropertiesCompatibleFileConfigRepository configFileRepository = new PropertiesCompatibleFileConfigRepository(configFile);
        configFileRepository.getConfig();
    }

    @Test
    public void testGetSourceType() throws Exception {
        ConfigSourceType someType = ConfigSourceType.REMOTE;
        Mockito.when(configFile.getSourceType()).thenReturn(someType);
        PropertiesCompatibleFileConfigRepository configFileRepository = new PropertiesCompatibleFileConfigRepository(configFile);
        Assert.assertSame(someType, configFileRepository.getSourceType());
    }

    @Test
    public void testOnChange() throws Exception {
        Properties anotherProperties = Mockito.mock(Properties.class);
        ConfigFileChangeEvent someChangeEvent = Mockito.mock(ConfigFileChangeEvent.class);
        RepositoryChangeListener someListener = Mockito.mock(RepositoryChangeListener.class);
        PropertiesCompatibleFileConfigRepository configFileRepository = new PropertiesCompatibleFileConfigRepository(configFile);
        configFileRepository.addChangeListener(someListener);
        Assert.assertSame(someProperties, configFileRepository.getConfig());
        Mockito.when(configFile.asProperties()).thenReturn(anotherProperties);
        configFileRepository.onChange(someChangeEvent);
        Assert.assertSame(anotherProperties, configFileRepository.getConfig());
        Mockito.verify(someListener, Mockito.times(1)).onRepositoryChange(someNamespaceName, anotherProperties);
    }
}

