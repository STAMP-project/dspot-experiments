package com.ctrip.framework.apollo.internals;


import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.enums.ConfigSourceType;
import com.ctrip.framework.apollo.spi.ConfigFactory;
import com.ctrip.framework.apollo.spi.ConfigFactoryManager;
import java.util.Properties;
import java.util.Set;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigManagerTest {
    private DefaultConfigManager defaultConfigManager;

    private static String someConfigContent;

    @Test
    public void testGetConfig() throws Exception {
        String someNamespace = "someName";
        String anotherNamespace = "anotherName";
        String someKey = "someKey";
        Config config = defaultConfigManager.getConfig(someNamespace);
        Config anotherConfig = defaultConfigManager.getConfig(anotherNamespace);
        Assert.assertEquals(((someNamespace + ":") + someKey), config.getProperty(someKey, null));
        Assert.assertEquals(((anotherNamespace + ":") + someKey), anotherConfig.getProperty(someKey, null));
    }

    @Test
    public void testGetConfigMultipleTimesWithSameNamespace() throws Exception {
        String someNamespace = "someName";
        Config config = defaultConfigManager.getConfig(someNamespace);
        Config anotherConfig = defaultConfigManager.getConfig(someNamespace);
        Assert.assertThat("Get config multiple times with the same namespace should return the same config instance", config, IsEqual.equalTo(anotherConfig));
    }

    @Test
    public void testGetConfigFile() throws Exception {
        String someNamespace = "someName";
        ConfigFileFormat someConfigFileFormat = ConfigFileFormat.Properties;
        ConfigFile configFile = defaultConfigManager.getConfigFile(someNamespace, someConfigFileFormat);
        Assert.assertEquals(someConfigFileFormat, configFile.getConfigFileFormat());
        Assert.assertEquals(DefaultConfigManagerTest.someConfigContent, configFile.getContent());
    }

    @Test
    public void testGetConfigFileMultipleTimesWithSameNamespace() throws Exception {
        String someNamespace = "someName";
        ConfigFileFormat someConfigFileFormat = ConfigFileFormat.Properties;
        ConfigFile someConfigFile = defaultConfigManager.getConfigFile(someNamespace, someConfigFileFormat);
        ConfigFile anotherConfigFile = defaultConfigManager.getConfigFile(someNamespace, someConfigFileFormat);
        Assert.assertThat("Get config file multiple times with the same namespace should return the same config file instance", someConfigFile, IsEqual.equalTo(anotherConfigFile));
    }

    public static class MockConfigFactoryManager implements ConfigFactoryManager {
        @Override
        public ConfigFactory getFactory(String namespace) {
            return new ConfigFactory() {
                @Override
                public Config create(final String namespace) {
                    return new AbstractConfig() {
                        @Override
                        public String getProperty(String key, String defaultValue) {
                            return (namespace + ":") + key;
                        }

                        @Override
                        public Set<String> getPropertyNames() {
                            return null;
                        }

                        @Override
                        public ConfigSourceType getSourceType() {
                            return null;
                        }
                    };
                }

                @Override
                public ConfigFile createConfigFile(String namespace, final ConfigFileFormat configFileFormat) {
                    ConfigRepository someConfigRepository = Mockito.mock(ConfigRepository.class);
                    return new AbstractConfigFile(namespace, someConfigRepository) {
                        @Override
                        protected void update(Properties newProperties) {
                        }

                        @Override
                        public String getContent() {
                            return DefaultConfigManagerTest.someConfigContent;
                        }

                        @Override
                        public boolean hasContent() {
                            return true;
                        }

                        @Override
                        public ConfigFileFormat getConfigFileFormat() {
                            return configFileFormat;
                        }
                    };
                }
            };
        }
    }
}

