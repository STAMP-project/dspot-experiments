package com.ctrip.framework.apollo.spring;


import ConfigConsts.NAMESPACE_APPLICATION;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.internals.YamlConfigFile;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.SettableFuture;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class JavaConfigAnnotationTest extends AbstractSpringIntegrationTest {
    private static final String FX_APOLLO_NAMESPACE = "FX.apollo";

    private static final String APPLICATION_YAML_NAMESPACE = "application.yaml";

    @Test
    public void testApolloConfig() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        Config fxApolloConfig = Mockito.mock(Config.class);
        String someKey = "someKey";
        String someValue = "someValue";
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        AbstractSpringIntegrationTest.mockConfig(JavaConfigAnnotationTest.FX_APOLLO_NAMESPACE, fxApolloConfig);
        AbstractSpringIntegrationTest.prepareYamlConfigFile(JavaConfigAnnotationTest.APPLICATION_YAML_NAMESPACE, AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case9.yml"));
        JavaConfigAnnotationTest.TestApolloConfigBean1 bean = getBean(JavaConfigAnnotationTest.TestApolloConfigBean1.class, JavaConfigAnnotationTest.AppConfig1.class);
        Assert.assertEquals(applicationConfig, bean.getConfig());
        Assert.assertEquals(applicationConfig, bean.getAnotherConfig());
        Assert.assertEquals(fxApolloConfig, bean.getYetAnotherConfig());
        Config yamlConfig = bean.getYamlConfig();
        Assert.assertEquals(someValue, yamlConfig.getProperty(someKey, null));
    }

    @Test(expected = BeanCreationException.class)
    public void testApolloConfigWithWrongFieldType() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        getBean(JavaConfigAnnotationTest.TestApolloConfigBean2.class, JavaConfigAnnotationTest.AppConfig2.class);
    }

    @Test
    public void testApolloConfigWithInheritance() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        Config fxApolloConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        AbstractSpringIntegrationTest.mockConfig(JavaConfigAnnotationTest.FX_APOLLO_NAMESPACE, fxApolloConfig);
        JavaConfigAnnotationTest.TestApolloChildConfigBean bean = getBean(JavaConfigAnnotationTest.TestApolloChildConfigBean.class, JavaConfigAnnotationTest.AppConfig6.class);
        Assert.assertEquals(applicationConfig, bean.getConfig());
        Assert.assertEquals(applicationConfig, bean.getAnotherConfig());
        Assert.assertEquals(fxApolloConfig, bean.getYetAnotherConfig());
        Assert.assertEquals(applicationConfig, bean.getSomeConfig());
    }

    @Test
    public void testApolloConfigChangeListener() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        Config fxApolloConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        AbstractSpringIntegrationTest.mockConfig(JavaConfigAnnotationTest.FX_APOLLO_NAMESPACE, fxApolloConfig);
        final List<ConfigChangeListener> applicationListeners = Lists.newArrayList();
        final List<ConfigChangeListener> fxApolloListeners = Lists.newArrayList();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                applicationListeners.add(getArgumentAt(0, ConfigChangeListener.class));
                return Void.class;
            }
        }).when(applicationConfig).addChangeListener(ArgumentMatchers.any(ConfigChangeListener.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                fxApolloListeners.add(getArgumentAt(0, ConfigChangeListener.class));
                return Void.class;
            }
        }).when(fxApolloConfig).addChangeListener(ArgumentMatchers.any(ConfigChangeListener.class));
        ConfigChangeEvent someEvent = Mockito.mock(ConfigChangeEvent.class);
        ConfigChangeEvent anotherEvent = Mockito.mock(ConfigChangeEvent.class);
        JavaConfigAnnotationTest.TestApolloConfigChangeListenerBean1 bean = getBean(JavaConfigAnnotationTest.TestApolloConfigChangeListenerBean1.class, JavaConfigAnnotationTest.AppConfig3.class);
        // PropertySourcesProcessor add listeners to listen config changed of all namespace
        Assert.assertEquals(4, applicationListeners.size());
        Assert.assertEquals(1, fxApolloListeners.size());
        for (ConfigChangeListener listener : applicationListeners) {
            listener.onChange(someEvent);
        }
        Assert.assertEquals(someEvent, bean.getChangeEvent1());
        Assert.assertEquals(someEvent, bean.getChangeEvent2());
        Assert.assertEquals(someEvent, bean.getChangeEvent3());
        for (ConfigChangeListener listener : fxApolloListeners) {
            listener.onChange(anotherEvent);
        }
        Assert.assertEquals(someEvent, bean.getChangeEvent1());
        Assert.assertEquals(someEvent, bean.getChangeEvent2());
        Assert.assertEquals(anotherEvent, bean.getChangeEvent3());
    }

    @Test(expected = BeanCreationException.class)
    public void testApolloConfigChangeListenerWithWrongParamType() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        getBean(JavaConfigAnnotationTest.TestApolloConfigChangeListenerBean2.class, JavaConfigAnnotationTest.AppConfig4.class);
    }

    @Test(expected = BeanCreationException.class)
    public void testApolloConfigChangeListenerWithWrongParamCount() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        getBean(JavaConfigAnnotationTest.TestApolloConfigChangeListenerBean3.class, JavaConfigAnnotationTest.AppConfig5.class);
    }

    @Test
    public void testApolloConfigChangeListenerWithInheritance() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        Config fxApolloConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        AbstractSpringIntegrationTest.mockConfig(JavaConfigAnnotationTest.FX_APOLLO_NAMESPACE, fxApolloConfig);
        final List<ConfigChangeListener> applicationListeners = Lists.newArrayList();
        final List<ConfigChangeListener> fxApolloListeners = Lists.newArrayList();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                applicationListeners.add(getArgumentAt(0, ConfigChangeListener.class));
                return Void.class;
            }
        }).when(applicationConfig).addChangeListener(ArgumentMatchers.any(ConfigChangeListener.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                fxApolloListeners.add(getArgumentAt(0, ConfigChangeListener.class));
                return Void.class;
            }
        }).when(fxApolloConfig).addChangeListener(ArgumentMatchers.any(ConfigChangeListener.class));
        ConfigChangeEvent someEvent = Mockito.mock(ConfigChangeEvent.class);
        ConfigChangeEvent anotherEvent = Mockito.mock(ConfigChangeEvent.class);
        JavaConfigAnnotationTest.TestApolloChildConfigChangeListener bean = getBean(JavaConfigAnnotationTest.TestApolloChildConfigChangeListener.class, JavaConfigAnnotationTest.AppConfig7.class);
        // PropertySourcesProcessor add listeners to listen config changed of all namespace
        Assert.assertEquals(5, applicationListeners.size());
        Assert.assertEquals(1, fxApolloListeners.size());
        for (ConfigChangeListener listener : applicationListeners) {
            listener.onChange(someEvent);
        }
        Assert.assertEquals(someEvent, bean.getChangeEvent1());
        Assert.assertEquals(someEvent, bean.getChangeEvent2());
        Assert.assertEquals(someEvent, bean.getChangeEvent3());
        Assert.assertEquals(someEvent, bean.getSomeChangeEvent());
        for (ConfigChangeListener listener : fxApolloListeners) {
            listener.onChange(anotherEvent);
        }
        Assert.assertEquals(someEvent, bean.getChangeEvent1());
        Assert.assertEquals(someEvent, bean.getChangeEvent2());
        Assert.assertEquals(anotherEvent, bean.getChangeEvent3());
        Assert.assertEquals(someEvent, bean.getSomeChangeEvent());
    }

    @Test
    public void testApolloConfigChangeListenerWithInterestedKeys() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        Config fxApolloConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        AbstractSpringIntegrationTest.mockConfig(JavaConfigAnnotationTest.FX_APOLLO_NAMESPACE, fxApolloConfig);
        JavaConfigAnnotationTest.TestApolloConfigChangeListenerWithInterestedKeysBean bean = getBean(JavaConfigAnnotationTest.TestApolloConfigChangeListenerWithInterestedKeysBean.class, JavaConfigAnnotationTest.AppConfig8.class);
        final ArgumentCaptor<Set> applicationConfigInterestedKeys = ArgumentCaptor.forClass(Set.class);
        final ArgumentCaptor<Set> fxApolloConfigInterestedKeys = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(applicationConfig, Mockito.times(2)).addChangeListener(ArgumentMatchers.any(ConfigChangeListener.class), applicationConfigInterestedKeys.capture(), ArgumentMatchers.anySetOf(String.class));
        Mockito.verify(fxApolloConfig, Mockito.times(1)).addChangeListener(ArgumentMatchers.any(ConfigChangeListener.class), fxApolloConfigInterestedKeys.capture(), ArgumentMatchers.anySetOf(String.class));
        Assert.assertEquals(2, applicationConfigInterestedKeys.getAllValues().size());
        Set<String> result = Sets.newHashSet();
        for (Set interestedKeys : applicationConfigInterestedKeys.getAllValues()) {
            result.addAll(interestedKeys);
        }
        Assert.assertEquals(Sets.newHashSet("someKey", "anotherKey"), result);
        Assert.assertEquals(1, fxApolloConfigInterestedKeys.getAllValues().size());
        Assert.assertEquals(Arrays.asList(Sets.newHashSet("anotherKey")), fxApolloConfigInterestedKeys.getAllValues());
    }

    @Test
    public void testApolloConfigChangeListenerWithYamlFile() throws Exception {
        String someKey = "someKey";
        String someValue = "someValue";
        String anotherValue = "anotherValue";
        YamlConfigFile configFile = AbstractSpringIntegrationTest.prepareYamlConfigFile(JavaConfigAnnotationTest.APPLICATION_YAML_NAMESPACE, AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case9.yml"));
        JavaConfigAnnotationTest.TestApolloConfigChangeListenerWithYamlFile bean = getBean(JavaConfigAnnotationTest.TestApolloConfigChangeListenerWithYamlFile.class, JavaConfigAnnotationTest.AppConfig9.class);
        Config yamlConfig = bean.getYamlConfig();
        SettableFuture<ConfigChangeEvent> future = bean.getConfigChangeEventFuture();
        Assert.assertEquals(someValue, yamlConfig.getProperty(someKey, null));
        Assert.assertFalse(future.isDone());
        configFile.onRepositoryChange(JavaConfigAnnotationTest.APPLICATION_YAML_NAMESPACE, AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case9-new.yml"));
        ConfigChangeEvent configChangeEvent = future.get(100, TimeUnit.MILLISECONDS);
        ConfigChange change = configChangeEvent.getChange(someKey);
        Assert.assertEquals(someValue, change.getOldValue());
        Assert.assertEquals(anotherValue, change.getNewValue());
        Assert.assertEquals(anotherValue, yamlConfig.getProperty(someKey, null));
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig1 {
        @Bean
        public JavaConfigAnnotationTest.TestApolloConfigBean1 bean() {
            return new JavaConfigAnnotationTest.TestApolloConfigBean1();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig2 {
        @Bean
        public JavaConfigAnnotationTest.TestApolloConfigBean2 bean() {
            return new JavaConfigAnnotationTest.TestApolloConfigBean2();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig3 {
        @Bean
        public JavaConfigAnnotationTest.TestApolloConfigChangeListenerBean1 bean() {
            return new JavaConfigAnnotationTest.TestApolloConfigChangeListenerBean1();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig4 {
        @Bean
        public JavaConfigAnnotationTest.TestApolloConfigChangeListenerBean2 bean() {
            return new JavaConfigAnnotationTest.TestApolloConfigChangeListenerBean2();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig5 {
        @Bean
        public JavaConfigAnnotationTest.TestApolloConfigChangeListenerBean3 bean() {
            return new JavaConfigAnnotationTest.TestApolloConfigChangeListenerBean3();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig6 {
        @Bean
        public JavaConfigAnnotationTest.TestApolloChildConfigBean bean() {
            return new JavaConfigAnnotationTest.TestApolloChildConfigBean();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig7 {
        @Bean
        public JavaConfigAnnotationTest.TestApolloChildConfigChangeListener bean() {
            return new JavaConfigAnnotationTest.TestApolloChildConfigChangeListener();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig8 {
        @Bean
        public JavaConfigAnnotationTest.TestApolloConfigChangeListenerWithInterestedKeysBean bean() {
            return new JavaConfigAnnotationTest.TestApolloConfigChangeListenerWithInterestedKeysBean();
        }
    }

    @Configuration
    @EnableApolloConfig(JavaConfigAnnotationTest.APPLICATION_YAML_NAMESPACE)
    static class AppConfig9 {
        @Bean
        public JavaConfigAnnotationTest.TestApolloConfigChangeListenerWithYamlFile bean() {
            return new JavaConfigAnnotationTest.TestApolloConfigChangeListenerWithYamlFile();
        }
    }

    static class TestApolloConfigBean1 {
        @ApolloConfig
        private Config config;

        @ApolloConfig(ConfigConsts.NAMESPACE_APPLICATION)
        private Config anotherConfig;

        @ApolloConfig(JavaConfigAnnotationTest.FX_APOLLO_NAMESPACE)
        private Config yetAnotherConfig;

        @ApolloConfig(JavaConfigAnnotationTest.APPLICATION_YAML_NAMESPACE)
        private Config yamlConfig;

        public Config getConfig() {
            return config;
        }

        public Config getAnotherConfig() {
            return anotherConfig;
        }

        public Config getYetAnotherConfig() {
            return yetAnotherConfig;
        }

        public Config getYamlConfig() {
            return yamlConfig;
        }
    }

    static class TestApolloConfigBean2 {
        @ApolloConfig
        private String config;
    }

    static class TestApolloChildConfigBean extends JavaConfigAnnotationTest.TestApolloConfigBean1 {
        @ApolloConfig
        private Config someConfig;

        public Config getSomeConfig() {
            return someConfig;
        }
    }

    static class TestApolloConfigChangeListenerBean1 {
        private ConfigChangeEvent changeEvent1;

        private ConfigChangeEvent changeEvent2;

        private ConfigChangeEvent changeEvent3;

        @ApolloConfigChangeListener
        private void onChange1(ConfigChangeEvent changeEvent) {
            this.changeEvent1 = changeEvent;
        }

        @ApolloConfigChangeListener(ConfigConsts.NAMESPACE_APPLICATION)
        private void onChange2(ConfigChangeEvent changeEvent) {
            this.changeEvent2 = changeEvent;
        }

        @ApolloConfigChangeListener({ ConfigConsts.NAMESPACE_APPLICATION, JavaConfigAnnotationTest.FX_APOLLO_NAMESPACE })
        private void onChange3(ConfigChangeEvent changeEvent) {
            this.changeEvent3 = changeEvent;
        }

        public ConfigChangeEvent getChangeEvent1() {
            return changeEvent1;
        }

        public ConfigChangeEvent getChangeEvent2() {
            return changeEvent2;
        }

        public ConfigChangeEvent getChangeEvent3() {
            return changeEvent3;
        }
    }

    static class TestApolloConfigChangeListenerBean2 {
        @ApolloConfigChangeListener
        private void onChange(String event) {
        }
    }

    static class TestApolloConfigChangeListenerBean3 {
        @ApolloConfigChangeListener
        private void onChange(ConfigChangeEvent event, String someParam) {
        }
    }

    static class TestApolloChildConfigChangeListener extends JavaConfigAnnotationTest.TestApolloConfigChangeListenerBean1 {
        private ConfigChangeEvent someChangeEvent;

        @ApolloConfigChangeListener
        private void someOnChange(ConfigChangeEvent changeEvent) {
            this.someChangeEvent = changeEvent;
        }

        public ConfigChangeEvent getSomeChangeEvent() {
            return someChangeEvent;
        }
    }

    static class TestApolloConfigChangeListenerWithInterestedKeysBean {
        @ApolloConfigChangeListener(interestedKeys = { "someKey" })
        private void someOnChange(ConfigChangeEvent changeEvent) {
        }

        @ApolloConfigChangeListener(value = { ConfigConsts.NAMESPACE_APPLICATION, JavaConfigAnnotationTest.FX_APOLLO_NAMESPACE }, interestedKeys = { "anotherKey" })
        private void anotherOnChange(ConfigChangeEvent changeEvent) {
        }
    }

    static class TestApolloConfigChangeListenerWithYamlFile {
        private SettableFuture<ConfigChangeEvent> configChangeEventFuture = SettableFuture.create();

        @ApolloConfig(JavaConfigAnnotationTest.APPLICATION_YAML_NAMESPACE)
        private Config yamlConfig;

        @ApolloConfigChangeListener(JavaConfigAnnotationTest.APPLICATION_YAML_NAMESPACE)
        private void onChange(ConfigChangeEvent event) {
            configChangeEventFuture.set(event);
        }

        public SettableFuture<ConfigChangeEvent> getConfigChangeEventFuture() {
            return configChangeEventFuture;
        }

        public Config getYamlConfig() {
            return yamlConfig;
        }
    }
}

