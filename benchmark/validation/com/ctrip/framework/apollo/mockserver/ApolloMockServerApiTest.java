package com.ctrip.framework.apollo.mockserver;


import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.common.util.concurrent.SettableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class ApolloMockServerApiTest {
    private static final String anotherNamespace = "anotherNamespace";

    @ClassRule
    public static EmbeddedApollo embeddedApollo = new EmbeddedApollo();

    @Test
    public void testGetProperty() throws Exception {
        Config applicationConfig = ConfigService.getAppConfig();
        Assert.assertEquals("value1", applicationConfig.getProperty("key1", null));
        Assert.assertEquals("value2", applicationConfig.getProperty("key2", null));
    }

    @Test
    public void testUpdateProperties() throws Exception {
        String someNewValue = "someNewValue";
        Config otherConfig = ConfigService.getConfig(ApolloMockServerApiTest.anotherNamespace);
        final SettableFuture<ConfigChangeEvent> future = SettableFuture.create();
        otherConfig.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                future.set(changeEvent);
            }
        });
        Assert.assertEquals("otherValue1", otherConfig.getProperty("key1", null));
        Assert.assertEquals("otherValue2", otherConfig.getProperty("key2", null));
        ApolloMockServerApiTest.embeddedApollo.addOrModifyProperty(ApolloMockServerApiTest.anotherNamespace, "key1", someNewValue);
        ConfigChangeEvent changeEvent = future.get(5, TimeUnit.SECONDS);
        Assert.assertEquals(someNewValue, otherConfig.getProperty("key1", null));
        Assert.assertEquals("otherValue2", otherConfig.getProperty("key2", null));
        Assert.assertTrue(changeEvent.isChanged("key1"));
    }
}

