package com.netflix.config;


import com.netflix.config.sources.TypesafeConfigurationSource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class TypesafePollingSourceTest {
    private TypesafePollingSourceTest.TestableTypesafeConfigurationSource source = new TypesafePollingSourceTest.TestableTypesafeConfigurationSource();

    @Test
    public void archaiusSeesTypesafeUpdates() throws InterruptedException {
        DynamicPropertyFactory props = props();
        Assert.assertThat(props.getIntProperty("top", (-1)).get(), IsEqual.equalTo(3));
        source.setConf("reference-test-updated");
        Thread.sleep(200);
        Assert.assertThat(props.getIntProperty("top", (-1)).get(), IsEqual.equalTo(7));
    }

    private static class TestableTypesafeConfigurationSource extends TypesafeConfigurationSource {
        private String conf;

        private void setConf(String conf) {
            this.conf = conf;
        }

        @Override
        protected Config config() {
            return ConfigFactory.load(conf);
        }
    }
}

