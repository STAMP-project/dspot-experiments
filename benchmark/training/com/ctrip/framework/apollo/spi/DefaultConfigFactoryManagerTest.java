package com.ctrip.framework.apollo.spi;


import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.build.MockInjector;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigFactoryManagerTest {
    private DefaultConfigFactoryManager defaultConfigFactoryManager;

    @Test
    public void testGetFactoryFromRegistry() throws Exception {
        ConfigFactory result = defaultConfigFactoryManager.getFactory(DefaultConfigFactoryManagerTest.MockConfigRegistry.NAMESPACE_REGISTERED);
        Assert.assertEquals(DefaultConfigFactoryManagerTest.MockConfigRegistry.REGISTERED_CONFIGFACTORY, result);
    }

    @Test
    public void testGetFactoryFromNamespace() throws Exception {
        String someNamespace = "someName";
        MockInjector.setInstance(ConfigFactory.class, someNamespace, new DefaultConfigFactoryManagerTest.SomeConfigFactory());
        ConfigFactory result = defaultConfigFactoryManager.getFactory(someNamespace);
        Assert.assertThat("When namespace is registered, should return the registerd config factory", result, IsInstanceOf.instanceOf(DefaultConfigFactoryManagerTest.SomeConfigFactory.class));
    }

    @Test
    public void testGetFactoryFromNamespaceMultipleTimes() throws Exception {
        String someNamespace = "someName";
        MockInjector.setInstance(ConfigFactory.class, someNamespace, new DefaultConfigFactoryManagerTest.SomeConfigFactory());
        ConfigFactory result = defaultConfigFactoryManager.getFactory(someNamespace);
        ConfigFactory anotherResult = defaultConfigFactoryManager.getFactory(someNamespace);
        Assert.assertThat("Get config factory with the same namespace multiple times should returnt the same instance", anotherResult, IsEqual.equalTo(result));
    }

    @Test
    public void testGetFactoryFromDefault() throws Exception {
        String someNamespace = "someName";
        MockInjector.setInstance(ConfigFactory.class, new DefaultConfigFactoryManagerTest.AnotherConfigFactory());
        ConfigFactory result = defaultConfigFactoryManager.getFactory(someNamespace);
        Assert.assertThat("When namespace is not registered, should return the default config factory", result, IsInstanceOf.instanceOf(DefaultConfigFactoryManagerTest.AnotherConfigFactory.class));
    }

    public static class MockConfigRegistry implements ConfigRegistry {
        public static String NAMESPACE_REGISTERED = "some-namespace-registered";

        public static ConfigFactory REGISTERED_CONFIGFACTORY = new ConfigFactory() {
            @Override
            public Config create(String namespace) {
                return null;
            }

            @Override
            public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
                return null;
            }
        };

        @Override
        public void register(String namespace, ConfigFactory factory) {
            // do nothing
        }

        @Override
        public ConfigFactory getFactory(String namespace) {
            if (namespace.equals(DefaultConfigFactoryManagerTest.MockConfigRegistry.NAMESPACE_REGISTERED)) {
                return DefaultConfigFactoryManagerTest.MockConfigRegistry.REGISTERED_CONFIGFACTORY;
            }
            return null;
        }
    }

    public static class SomeConfigFactory implements ConfigFactory {
        @Override
        public Config create(String namespace) {
            return null;
        }

        @Override
        public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
            return null;
        }
    }

    public static class AnotherConfigFactory implements ConfigFactory {
        @Override
        public Config create(String namespace) {
            return null;
        }

        @Override
        public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
            return null;
        }
    }
}

