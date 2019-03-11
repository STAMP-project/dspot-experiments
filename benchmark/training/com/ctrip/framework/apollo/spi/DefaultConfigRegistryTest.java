package com.ctrip.framework.apollo.spi;


import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigRegistryTest {
    private DefaultConfigRegistry defaultConfigRegistry;

    @Test
    public void testGetFactory() throws Exception {
        String someNamespace = "someName";
        ConfigFactory someConfigFactory = new DefaultConfigRegistryTest.MockConfigFactory();
        defaultConfigRegistry.register(someNamespace, someConfigFactory);
        Assert.assertThat("Should return the registered config factory", defaultConfigRegistry.getFactory(someNamespace), IsEqual.equalTo(someConfigFactory));
    }

    @Test
    public void testGetFactoryWithNamespaceUnregistered() throws Exception {
        String someUnregisteredNamespace = "someName";
        Assert.assertNull(defaultConfigRegistry.getFactory(someUnregisteredNamespace));
    }

    public static class MockConfigFactory implements ConfigFactory {
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

