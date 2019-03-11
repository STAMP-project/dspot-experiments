package com.ctrip.framework.apollo.mockserver;


import PropertyChangeType.DELETED;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.google.common.util.concurrent.SettableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Create by zhangzheng on 8/16/18 Email:zhangzheng@youzan.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ApolloMockServerSpringIntegrationTest.TestConfiguration.class)
public class ApolloMockServerSpringIntegrationTest {
    private static final String otherNamespace = "otherNamespace";

    @ClassRule
    public static EmbeddedApollo embeddedApollo = new EmbeddedApollo();

    @Autowired
    private ApolloMockServerSpringIntegrationTest.TestBean testBean;

    @Autowired
    private ApolloMockServerSpringIntegrationTest.TestInterestedKeyPrefixesBean testInterestedKeyPrefixesBean;

    @Test
    @DirtiesContext
    public void testPropertyInject() {
        Assert.assertEquals("value1", testBean.key1);
        Assert.assertEquals("value2", testBean.key2);
    }

    @Test
    @DirtiesContext
    public void testListenerTriggeredByAdd() throws InterruptedException, ExecutionException, TimeoutException {
        ApolloMockServerSpringIntegrationTest.embeddedApollo.addOrModifyProperty(ApolloMockServerSpringIntegrationTest.otherNamespace, "someKey", "someValue");
        ConfigChangeEvent changeEvent = testBean.futureData.get(5000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(ApolloMockServerSpringIntegrationTest.otherNamespace, changeEvent.getNamespace());
        Assert.assertEquals("someValue", changeEvent.getChange("someKey").getNewValue());
    }

    @Test
    @DirtiesContext
    public void testListenerTriggeredByDel() throws InterruptedException, ExecutionException, TimeoutException {
        ApolloMockServerSpringIntegrationTest.embeddedApollo.deleteProperty(ApolloMockServerSpringIntegrationTest.otherNamespace, "key1");
        ConfigChangeEvent changeEvent = testBean.futureData.get(5000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(ApolloMockServerSpringIntegrationTest.otherNamespace, changeEvent.getNamespace());
        Assert.assertEquals(DELETED, changeEvent.getChange("key1").getChangeType());
    }

    @Test
    @DirtiesContext
    public void shouldNotifyOnInterestedPatterns() throws Exception {
        ApolloMockServerSpringIntegrationTest.embeddedApollo.addOrModifyProperty(ApolloMockServerSpringIntegrationTest.otherNamespace, "server.port", "8080");
        ApolloMockServerSpringIntegrationTest.embeddedApollo.addOrModifyProperty(ApolloMockServerSpringIntegrationTest.otherNamespace, "server.path", "/apollo");
        ApolloMockServerSpringIntegrationTest.embeddedApollo.addOrModifyProperty(ApolloMockServerSpringIntegrationTest.otherNamespace, "spring.application.name", "whatever");
        ConfigChangeEvent changeEvent = testInterestedKeyPrefixesBean.futureData.get(5000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(ApolloMockServerSpringIntegrationTest.otherNamespace, changeEvent.getNamespace());
        Assert.assertEquals("8080", changeEvent.getChange("server.port").getNewValue());
        Assert.assertEquals("/apollo", changeEvent.getChange("server.path").getNewValue());
    }

    @Test(expected = TimeoutException.class)
    @DirtiesContext
    public void shouldNotNotifyOnUninterestedPatterns() throws Exception {
        ApolloMockServerSpringIntegrationTest.embeddedApollo.addOrModifyProperty(ApolloMockServerSpringIntegrationTest.otherNamespace, "spring.application.name", "apollo");
        testInterestedKeyPrefixesBean.futureData.get(5000, TimeUnit.MILLISECONDS);
    }

    @EnableApolloConfig
    @Configuration
    static class TestConfiguration {
        @Bean
        public ApolloMockServerSpringIntegrationTest.TestBean testBean() {
            return new ApolloMockServerSpringIntegrationTest.TestBean();
        }

        @Bean
        public ApolloMockServerSpringIntegrationTest.TestInterestedKeyPrefixesBean testInterestedKeyPrefixesBean() {
            return new ApolloMockServerSpringIntegrationTest.TestInterestedKeyPrefixesBean();
        }
    }

    private static class TestBean {
        @Value("${key1:default}")
        private String key1;

        @Value("${key2:default}")
        private String key2;

        private SettableFuture<ConfigChangeEvent> futureData = SettableFuture.create();

        @ApolloConfigChangeListener(ApolloMockServerSpringIntegrationTest.otherNamespace)
        private void onChange(ConfigChangeEvent changeEvent) {
            futureData.set(changeEvent);
        }
    }

    private static class TestInterestedKeyPrefixesBean {
        private SettableFuture<ConfigChangeEvent> futureData = SettableFuture.create();

        @ApolloConfigChangeListener(value = ApolloMockServerSpringIntegrationTest.otherNamespace, interestedKeyPrefixes = "server.")
        private void onChange(ConfigChangeEvent changeEvent) {
            futureData.set(changeEvent);
        }
    }
}

