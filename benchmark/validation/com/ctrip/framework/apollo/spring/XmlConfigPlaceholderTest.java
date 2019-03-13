package com.ctrip.framework.apollo.spring;


import ConfigConsts.NAMESPACE_APPLICATION;
import com.ctrip.framework.apollo.Config;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.xml.XmlBeanDefinitionStoreException;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class XmlConfigPlaceholderTest extends AbstractSpringIntegrationTest {
    private static final String TIMEOUT_PROPERTY = "timeout";

    private static final int DEFAULT_TIMEOUT = 100;

    private static final String BATCH_PROPERTY = "batch";

    private static final int DEFAULT_BATCH = 200;

    private static final String FX_APOLLO_NAMESPACE = "FX.apollo";

    @Test
    public void testPropertySourceWithNoNamespace() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(config.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        check("spring/XmlConfigPlaceholderTest1.xml", someTimeout, someBatch);
    }

    @Test
    public void testPropertySourceWithNoConfig() throws Exception {
        Config config = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        check("spring/XmlConfigPlaceholderTest1.xml", XmlConfigPlaceholderTest.DEFAULT_TIMEOUT, XmlConfigPlaceholderTest.DEFAULT_BATCH);
    }

    @Test
    public void testApplicationPropertySource() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(config.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        check("spring/XmlConfigPlaceholderTest2.xml", someTimeout, someBatch);
    }

    @Test
    public void testMultiplePropertySources() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        Config application = Mockito.mock(Config.class);
        Mockito.when(application.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, application);
        Config fxApollo = Mockito.mock(Config.class);
        Mockito.when(application.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(XmlConfigPlaceholderTest.FX_APOLLO_NAMESPACE, fxApollo);
        check("spring/XmlConfigPlaceholderTest3.xml", someTimeout, someBatch);
    }

    @Test
    public void testMultiplePropertySourcesWithSameProperties() throws Exception {
        int someTimeout = 1000;
        int anotherTimeout = someTimeout + 1;
        int someBatch = 2000;
        Config application = Mockito.mock(Config.class);
        Mockito.when(application.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(application.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, application);
        Config fxApollo = Mockito.mock(Config.class);
        Mockito.when(fxApollo.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(anotherTimeout));
        AbstractSpringIntegrationTest.mockConfig(XmlConfigPlaceholderTest.FX_APOLLO_NAMESPACE, fxApollo);
        check("spring/XmlConfigPlaceholderTest3.xml", someTimeout, someBatch);
    }

    @Test
    public void testMultiplePropertySourcesWithSameProperties2() throws Exception {
        int someTimeout = 1000;
        int anotherTimeout = someTimeout + 1;
        int someBatch = 2000;
        Config application = Mockito.mock(Config.class);
        Mockito.when(application.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(application.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, application);
        Config fxApollo = Mockito.mock(Config.class);
        Mockito.when(fxApollo.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(anotherTimeout));
        AbstractSpringIntegrationTest.mockConfig(XmlConfigPlaceholderTest.FX_APOLLO_NAMESPACE, fxApollo);
        check("spring/XmlConfigPlaceholderTest6.xml", anotherTimeout, someBatch);
    }

    @Test
    public void testMultiplePropertySourcesWithSamePropertiesWithWeight() throws Exception {
        int someTimeout = 1000;
        int anotherTimeout = someTimeout + 1;
        int someBatch = 2000;
        Config application = Mockito.mock(Config.class);
        Mockito.when(application.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(application.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, application);
        Config fxApollo = Mockito.mock(Config.class);
        Mockito.when(fxApollo.getProperty(ArgumentMatchers.eq(XmlConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(anotherTimeout));
        AbstractSpringIntegrationTest.mockConfig(XmlConfigPlaceholderTest.FX_APOLLO_NAMESPACE, fxApollo);
        check("spring/XmlConfigPlaceholderTest4.xml", anotherTimeout, someBatch);
    }

    @Test(expected = XmlBeanDefinitionStoreException.class)
    public void testWithInvalidWeight() throws Exception {
        check("spring/XmlConfigPlaceholderTest5.xml", XmlConfigPlaceholderTest.DEFAULT_TIMEOUT, XmlConfigPlaceholderTest.DEFAULT_BATCH);
    }

    public static class TestXmlBean {
        private int timeout;

        private int batch;

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getTimeout() {
            return timeout;
        }

        public int getBatch() {
            return batch;
        }

        public void setBatch(int batch) {
            this.batch = batch;
        }
    }
}

