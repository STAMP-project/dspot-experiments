package com.ctrip.framework.apollo.spring;


import ConfigConsts.NAMESPACE_APPLICATION;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.BeanCreationException;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class XMLConfigAnnotationTest extends AbstractSpringIntegrationTest {
    private static final String FX_APOLLO_NAMESPACE = "FX.apollo";

    @Test
    public void testApolloConfig() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        Config fxApolloConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        AbstractSpringIntegrationTest.mockConfig(XMLConfigAnnotationTest.FX_APOLLO_NAMESPACE, fxApolloConfig);
        XMLConfigAnnotationTest.TestApolloConfigBean1 bean = getBean("spring/XmlConfigAnnotationTest1.xml", XMLConfigAnnotationTest.TestApolloConfigBean1.class);
        Assert.assertEquals(applicationConfig, bean.getConfig());
        Assert.assertEquals(applicationConfig, bean.getAnotherConfig());
        Assert.assertEquals(fxApolloConfig, bean.getYetAnotherConfig());
    }

    @Test(expected = BeanCreationException.class)
    public void testApolloConfigWithWrongFieldType() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        getBean("spring/XmlConfigAnnotationTest2.xml", XMLConfigAnnotationTest.TestApolloConfigBean2.class);
    }

    @Test
    public void testApolloConfigChangeListener() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        Config fxApolloConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        AbstractSpringIntegrationTest.mockConfig(XMLConfigAnnotationTest.FX_APOLLO_NAMESPACE, fxApolloConfig);
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
        XMLConfigAnnotationTest.TestApolloConfigChangeListenerBean1 bean = getBean("spring/XmlConfigAnnotationTest3.xml", XMLConfigAnnotationTest.TestApolloConfigChangeListenerBean1.class);
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
        getBean("spring/XmlConfigAnnotationTest4.xml", XMLConfigAnnotationTest.TestApolloConfigChangeListenerBean2.class);
    }

    @Test(expected = BeanCreationException.class)
    public void testApolloConfigChangeListenerWithWrongParamCount() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        getBean("spring/XmlConfigAnnotationTest5.xml", XMLConfigAnnotationTest.TestApolloConfigChangeListenerBean3.class);
    }

    @Test
    public void testApolloConfigChangeListenerWithInterestedKeys() throws Exception {
        Config applicationConfig = Mockito.mock(Config.class);
        Config fxApolloConfig = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, applicationConfig);
        AbstractSpringIntegrationTest.mockConfig(XMLConfigAnnotationTest.FX_APOLLO_NAMESPACE, fxApolloConfig);
        XMLConfigAnnotationTest.TestApolloConfigChangeListenerWithInterestedKeysBean bean = getBean("spring/XmlConfigAnnotationTest6.xml", XMLConfigAnnotationTest.TestApolloConfigChangeListenerWithInterestedKeysBean.class);
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

    public static class TestApolloConfigBean1 {
        @ApolloConfig
        private Config config;

        @ApolloConfig(ConfigConsts.NAMESPACE_APPLICATION)
        private Config anotherConfig;

        @ApolloConfig(XMLConfigAnnotationTest.FX_APOLLO_NAMESPACE)
        private Config yetAnotherConfig;

        public Config getConfig() {
            return config;
        }

        public Config getAnotherConfig() {
            return anotherConfig;
        }

        public Config getYetAnotherConfig() {
            return yetAnotherConfig;
        }
    }

    public static class TestApolloConfigBean2 {
        @ApolloConfig
        private String config;
    }

    public static class TestApolloConfigChangeListenerBean1 {
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

        @ApolloConfigChangeListener({ ConfigConsts.NAMESPACE_APPLICATION, XMLConfigAnnotationTest.FX_APOLLO_NAMESPACE })
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

    public static class TestApolloConfigChangeListenerBean2 {
        @ApolloConfigChangeListener
        private void onChange(String event) {
        }
    }

    public static class TestApolloConfigChangeListenerBean3 {
        @ApolloConfigChangeListener
        private void onChange(ConfigChangeEvent event, String someParam) {
        }
    }

    static class TestApolloConfigChangeListenerWithInterestedKeysBean {
        @ApolloConfigChangeListener(interestedKeys = { "someKey" })
        private void someOnChange(ConfigChangeEvent changeEvent) {
        }

        @ApolloConfigChangeListener(value = { ConfigConsts.NAMESPACE_APPLICATION, XMLConfigAnnotationTest.FX_APOLLO_NAMESPACE }, interestedKeys = { "anotherKey" })
        private void anotherOnChange(ConfigChangeEvent changeEvent) {
        }
    }
}

