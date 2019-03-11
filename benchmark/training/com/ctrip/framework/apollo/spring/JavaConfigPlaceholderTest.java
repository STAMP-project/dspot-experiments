package com.ctrip.framework.apollo.spring;


import ConfigConsts.NAMESPACE_APPLICATION;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.PropertiesCompatibleConfigFile;
import com.ctrip.framework.apollo.spring.annotation.ApolloJsonValue;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import java.util.List;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class JavaConfigPlaceholderTest extends AbstractSpringIntegrationTest {
    private static final String TIMEOUT_PROPERTY = "timeout";

    private static final int DEFAULT_TIMEOUT = 100;

    private static final String BATCH_PROPERTY = "batch";

    private static final int DEFAULT_BATCH = 200;

    private static final String FX_APOLLO_NAMESPACE = "FX.apollo";

    private static final String JSON_PROPERTY = "jsonProperty";

    private static final String OTHER_JSON_PROPERTY = "otherJsonProperty";

    @Test
    public void testPropertySourceWithNoNamespace() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        check(someTimeout, someBatch, JavaConfigPlaceholderTest.AppConfig1.class);
    }

    @Test
    public void testPropertySourceWithNoConfig() throws Exception {
        Config config = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        check(JavaConfigPlaceholderTest.DEFAULT_TIMEOUT, JavaConfigPlaceholderTest.DEFAULT_BATCH, JavaConfigPlaceholderTest.AppConfig1.class);
    }

    @Test
    public void testApplicationPropertySource() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        check(someTimeout, someBatch, JavaConfigPlaceholderTest.AppConfig2.class);
    }

    @Test
    public void testPropertiesCompatiblePropertySource() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        Properties properties = Mockito.mock(Properties.class);
        Mockito.when(properties.getProperty(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY)).thenReturn(String.valueOf(someTimeout));
        Mockito.when(properties.getProperty(JavaConfigPlaceholderTest.BATCH_PROPERTY)).thenReturn(String.valueOf(someBatch));
        PropertiesCompatibleConfigFile configFile = Mockito.mock(PropertiesCompatibleConfigFile.class);
        Mockito.when(configFile.asProperties()).thenReturn(properties);
        AbstractSpringIntegrationTest.mockConfigFile("application.yaml", configFile);
        check(someTimeout, someBatch, JavaConfigPlaceholderTest.AppConfig9.class);
    }

    @Test
    public void testPropertiesCompatiblePropertySourceWithNonNormalizedCase() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        Properties properties = Mockito.mock(Properties.class);
        Mockito.when(properties.getProperty(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY)).thenReturn(String.valueOf(someTimeout));
        Mockito.when(properties.getProperty(JavaConfigPlaceholderTest.BATCH_PROPERTY)).thenReturn(String.valueOf(someBatch));
        PropertiesCompatibleConfigFile configFile = Mockito.mock(PropertiesCompatibleConfigFile.class);
        Mockito.when(configFile.asProperties()).thenReturn(properties);
        AbstractSpringIntegrationTest.mockConfigFile("application.yaml", configFile);
        check(someTimeout, someBatch, JavaConfigPlaceholderTest.AppConfig10.class);
    }

    @Test
    public void testMultiplePropertySources() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        Config application = Mockito.mock(Config.class);
        Mockito.when(application.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, application);
        Config fxApollo = Mockito.mock(Config.class);
        Mockito.when(application.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(JavaConfigPlaceholderTest.FX_APOLLO_NAMESPACE, fxApollo);
        check(someTimeout, someBatch, JavaConfigPlaceholderTest.AppConfig3.class);
    }

    @Test
    public void testMultiplePropertiesCompatiblePropertySourcesWithSameProperties() throws Exception {
        int someTimeout = 1000;
        int anotherTimeout = someTimeout + 1;
        int someBatch = 2000;
        Properties properties = Mockito.mock(Properties.class);
        Mockito.when(properties.getProperty(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY)).thenReturn(String.valueOf(someTimeout));
        Mockito.when(properties.getProperty(JavaConfigPlaceholderTest.BATCH_PROPERTY)).thenReturn(String.valueOf(someBatch));
        PropertiesCompatibleConfigFile configFile = Mockito.mock(PropertiesCompatibleConfigFile.class);
        Mockito.when(configFile.asProperties()).thenReturn(properties);
        AbstractSpringIntegrationTest.mockConfigFile("application.yml", configFile);
        Config fxApollo = Mockito.mock(Config.class);
        Mockito.when(fxApollo.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(anotherTimeout));
        AbstractSpringIntegrationTest.mockConfig(JavaConfigPlaceholderTest.FX_APOLLO_NAMESPACE, fxApollo);
        check(someTimeout, someBatch, JavaConfigPlaceholderTest.AppConfig11.class);
    }

    @Test
    public void testMultiplePropertySourcesCoverWithSameProperties() throws Exception {
        // Multimap does not maintain the strict input order of namespace.
        int someTimeout = 1000;
        int anotherTimeout = someTimeout + 1;
        int someBatch = 2000;
        Config fxApollo = Mockito.mock(Config.class);
        Mockito.when(fxApollo.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(fxApollo.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(JavaConfigPlaceholderTest.FX_APOLLO_NAMESPACE, fxApollo);
        Config application = Mockito.mock(Config.class);
        Mockito.when(application.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(anotherTimeout));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, application);
        check(someTimeout, someBatch, JavaConfigPlaceholderTest.AppConfig6.class);
    }

    @Test
    public void testMultiplePropertySourcesCoverWithSamePropertiesWithPropertiesCompatiblePropertySource() throws Exception {
        // Multimap does not maintain the strict input order of namespace.
        int someTimeout = 1000;
        int anotherTimeout = someTimeout + 1;
        int someBatch = 2000;
        Config fxApollo = Mockito.mock(Config.class);
        Mockito.when(fxApollo.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(fxApollo.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(JavaConfigPlaceholderTest.FX_APOLLO_NAMESPACE, fxApollo);
        Config application = Mockito.mock(Config.class);
        Mockito.when(application.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(anotherTimeout));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, application);
        check(someTimeout, someBatch, JavaConfigPlaceholderTest.AppConfig6.class);
    }

    @Test
    public void testMultiplePropertySourcesWithSamePropertiesWithWeight() throws Exception {
        int someTimeout = 1000;
        int anotherTimeout = someTimeout + 1;
        int someBatch = 2000;
        Config application = Mockito.mock(Config.class);
        Mockito.when(application.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(application.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, application);
        Config fxApollo = Mockito.mock(Config.class);
        Mockito.when(fxApollo.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(anotherTimeout));
        AbstractSpringIntegrationTest.mockConfig(JavaConfigPlaceholderTest.FX_APOLLO_NAMESPACE, fxApollo);
        check(anotherTimeout, someBatch, JavaConfigPlaceholderTest.AppConfig2.class, JavaConfigPlaceholderTest.AppConfig4.class);
    }

    @Test
    public void testApplicationPropertySourceWithValueInjectedAsParameter() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderTest.AppConfig5.class);
        JavaConfigPlaceholderTest.TestJavaConfigBean2 bean = context.getBean(JavaConfigPlaceholderTest.TestJavaConfigBean2.class);
        Assert.assertEquals(someTimeout, bean.getTimeout());
        Assert.assertEquals(someBatch, bean.getBatch());
    }

    @Test
    public void testApplicationPropertySourceWithValueInjectedAsConstructorArgs() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.TIMEOUT_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someTimeout));
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.BATCH_PROPERTY), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someBatch));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderTest.AppConfig7.class);
        JavaConfigPlaceholderTest.TestJavaConfigBean3 bean = context.getBean(JavaConfigPlaceholderTest.TestJavaConfigBean3.class);
        Assert.assertEquals(someTimeout, bean.getTimeout());
        Assert.assertEquals(someBatch, bean.getBatch());
    }

    @Test
    public void testNestedProperty() throws Exception {
        String a = "a";
        String b = "b";
        int someValue = 1234;
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(a), ArgumentMatchers.anyString())).thenReturn(a);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(b), ArgumentMatchers.anyString())).thenReturn(b);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(String.format("%s.%s", a, b)), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someValue));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderTest.NestedPropertyConfig1.class);
        JavaConfigPlaceholderTest.TestNestedPropertyBean bean = context.getBean(JavaConfigPlaceholderTest.TestNestedPropertyBean.class);
        Assert.assertEquals(someValue, bean.getNestedProperty());
    }

    @Test
    public void testNestedPropertyWithDefaultValue() throws Exception {
        String a = "a";
        String b = "b";
        String c = "c";
        int someValue = 1234;
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(a), ArgumentMatchers.anyString())).thenReturn(a);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(b), ArgumentMatchers.anyString())).thenReturn(b);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(c), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someValue));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderTest.NestedPropertyConfig1.class);
        JavaConfigPlaceholderTest.TestNestedPropertyBean bean = context.getBean(JavaConfigPlaceholderTest.TestNestedPropertyBean.class);
        Assert.assertEquals(someValue, bean.getNestedProperty());
    }

    @Test
    public void testNestedPropertyWithNestedDefaultValue() throws Exception {
        String a = "a";
        String b = "b";
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(a), ArgumentMatchers.anyString())).thenReturn(a);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(b), ArgumentMatchers.anyString())).thenReturn(b);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderTest.NestedPropertyConfig1.class);
        JavaConfigPlaceholderTest.TestNestedPropertyBean bean = context.getBean(JavaConfigPlaceholderTest.TestNestedPropertyBean.class);
        Assert.assertEquals(100, bean.getNestedProperty());
    }

    @Test
    public void testMultipleNestedProperty() throws Exception {
        String a = "a";
        String b = "b";
        String nestedKey = "c.d";
        String nestedProperty = String.format("${%s}", nestedKey);
        int someValue = 1234;
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(a), ArgumentMatchers.anyString())).thenReturn(a);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(b), ArgumentMatchers.anyString())).thenReturn(b);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(String.format("%s.%s", a, b)), ArgumentMatchers.anyString())).thenReturn(nestedProperty);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(nestedKey), ArgumentMatchers.anyString())).thenReturn(String.valueOf(someValue));
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderTest.NestedPropertyConfig1.class);
        JavaConfigPlaceholderTest.TestNestedPropertyBean bean = context.getBean(JavaConfigPlaceholderTest.TestNestedPropertyBean.class);
        Assert.assertEquals(someValue, bean.getNestedProperty());
    }

    @Test
    public void testMultipleNestedPropertyWithDefaultValue() throws Exception {
        String a = "a";
        String b = "b";
        String nestedKey = "c.d";
        int someValue = 1234;
        String nestedProperty = String.format("${%s:%d}", nestedKey, someValue);
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(a), ArgumentMatchers.anyString())).thenReturn(a);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(b), ArgumentMatchers.anyString())).thenReturn(b);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(String.format("%s.%s", a, b)), ArgumentMatchers.anyString())).thenReturn(nestedProperty);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderTest.NestedPropertyConfig1.class);
        JavaConfigPlaceholderTest.TestNestedPropertyBean bean = context.getBean(JavaConfigPlaceholderTest.TestNestedPropertyBean.class);
        Assert.assertEquals(someValue, bean.getNestedProperty());
    }

    @Test
    public void testApolloJsonValue() {
        String someJson = "[{\"a\":\"astring\", \"b\":10},{\"a\":\"astring2\", \"b\":20}]";
        String otherJson = "[{\"a\":\"otherString\", \"b\":10},{\"a\":\"astring2\", \"b\":20}]";
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.JSON_PROPERTY), ArgumentMatchers.anyString())).thenReturn(someJson);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.OTHER_JSON_PROPERTY), ArgumentMatchers.anyString())).thenReturn(otherJson);
        Mockito.when(config.getProperty(ArgumentMatchers.eq("a"), ArgumentMatchers.anyString())).thenReturn(JavaConfigPlaceholderTest.JSON_PROPERTY);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderTest.AppConfig8.class);
        JavaConfigPlaceholderTest.TestJsonPropertyBean testJsonPropertyBean = context.getBean(JavaConfigPlaceholderTest.TestJsonPropertyBean.class);
        Assert.assertEquals(2, testJsonPropertyBean.getJsonBeanList().size());
        Assert.assertEquals("astring", testJsonPropertyBean.getJsonBeanList().get(0).getA());
        Assert.assertEquals(10, testJsonPropertyBean.getJsonBeanList().get(0).getB());
        Assert.assertEquals("astring2", testJsonPropertyBean.getJsonBeanList().get(1).getA());
        Assert.assertEquals(20, testJsonPropertyBean.getJsonBeanList().get(1).getB());
        Assert.assertEquals(testJsonPropertyBean.getJsonBeanList(), testJsonPropertyBean.getEmbeddedJsonBeanList());
        Assert.assertEquals("otherString", testJsonPropertyBean.getOtherJsonBeanList().get(0).getA());
        Assert.assertEquals(10, testJsonPropertyBean.getOtherJsonBeanList().get(0).getB());
        Assert.assertEquals("astring2", testJsonPropertyBean.getOtherJsonBeanList().get(1).getA());
        Assert.assertEquals(20, testJsonPropertyBean.getOtherJsonBeanList().get(1).getB());
    }

    @Test(expected = BeanCreationException.class)
    public void testApolloJsonValueWithInvalidJson() throws Exception {
        String someInvalidJson = "someInvalidJson";
        Config config = Mockito.mock(Config.class);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.JSON_PROPERTY), ArgumentMatchers.anyString())).thenReturn(someInvalidJson);
        Mockito.when(config.getProperty(ArgumentMatchers.eq(JavaConfigPlaceholderTest.OTHER_JSON_PROPERTY), ArgumentMatchers.anyString())).thenReturn(someInvalidJson);
        Mockito.when(config.getProperty(ArgumentMatchers.eq("a"), ArgumentMatchers.anyString())).thenReturn(JavaConfigPlaceholderTest.JSON_PROPERTY);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        new AnnotationConfigApplicationContext(JavaConfigPlaceholderTest.AppConfig8.class).getBean(JavaConfigPlaceholderTest.TestJsonPropertyBean.class);
    }

    @Test(expected = BeanCreationException.class)
    public void testApolloJsonValueWithNoPropertyValue() throws Exception {
        Config config = Mockito.mock(Config.class);
        AbstractSpringIntegrationTest.mockConfig(NAMESPACE_APPLICATION, config);
        new AnnotationConfigApplicationContext(JavaConfigPlaceholderTest.AppConfig8.class);
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig1 {
        @Bean
        JavaConfigPlaceholderTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderTest.TestJavaConfigBean();
        }
    }

    @Configuration
    @EnableApolloConfig("application")
    static class AppConfig2 {
        @Bean
        JavaConfigPlaceholderTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderTest.TestJavaConfigBean();
        }
    }

    @Configuration
    @EnableApolloConfig({ "application", "FX.apollo" })
    static class AppConfig3 {
        @Bean
        JavaConfigPlaceholderTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderTest.TestJavaConfigBean();
        }
    }

    @Configuration
    @EnableApolloConfig(value = "FX.apollo", order = 10)
    static class AppConfig4 {}

    @Configuration
    @EnableApolloConfig
    static class AppConfig5 {
        @Bean
        JavaConfigPlaceholderTest.TestJavaConfigBean2 testJavaConfigBean2(@Value("${timeout:100}")
        int timeout, @Value("${batch:200}")
        int batch) {
            JavaConfigPlaceholderTest.TestJavaConfigBean2 bean = new JavaConfigPlaceholderTest.TestJavaConfigBean2();
            bean.setTimeout(timeout);
            bean.setBatch(batch);
            return bean;
        }
    }

    @Configuration
    @EnableApolloConfig({ "FX.apollo", "application" })
    static class AppConfig6 {
        @Bean
        JavaConfigPlaceholderTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderTest.TestJavaConfigBean();
        }
    }

    @Configuration
    @ComponentScan(includeFilters = { @Filter(type = FilterType.ANNOTATION, value = { Component.class }) }, excludeFilters = { @Filter(type = FilterType.ANNOTATION, value = { Configuration.class }) })
    @EnableApolloConfig
    static class AppConfig7 {}

    @Configuration
    @EnableApolloConfig
    static class NestedPropertyConfig1 {
        @Bean
        JavaConfigPlaceholderTest.TestNestedPropertyBean testNestedPropertyBean() {
            return new JavaConfigPlaceholderTest.TestNestedPropertyBean();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig8 {
        @Bean
        JavaConfigPlaceholderTest.TestJsonPropertyBean testJavaConfigBean() {
            return new JavaConfigPlaceholderTest.TestJsonPropertyBean();
        }
    }

    @Configuration
    @EnableApolloConfig("application.yaml")
    static class AppConfig9 {
        @Bean
        JavaConfigPlaceholderTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderTest.TestJavaConfigBean();
        }
    }

    @Configuration
    @EnableApolloConfig("application.yaMl")
    static class AppConfig10 {
        @Bean
        JavaConfigPlaceholderTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderTest.TestJavaConfigBean();
        }
    }

    @Configuration
    @EnableApolloConfig({ "application.yml", "FX.apollo" })
    static class AppConfig11 {
        @Bean
        JavaConfigPlaceholderTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderTest.TestJavaConfigBean();
        }
    }

    @Component
    static class TestJavaConfigBean {
        @Value("${timeout:100}")
        private int timeout;

        private int batch;

        @Value("${batch:200}")
        public void setBatch(int batch) {
            this.batch = batch;
        }

        public int getTimeout() {
            return timeout;
        }

        public int getBatch() {
            return batch;
        }
    }

    static class TestJavaConfigBean2 {
        private int timeout;

        private int batch;

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getBatch() {
            return batch;
        }

        public void setBatch(int batch) {
            this.batch = batch;
        }
    }

    @Component
    static class TestJavaConfigBean3 {
        private final int timeout;

        private final int batch;

        @Autowired
        public TestJavaConfigBean3(@Value("${timeout:100}")
        int timeout, @Value("${batch:200}")
        int batch) {
            this.timeout = timeout;
            this.batch = batch;
        }

        public int getTimeout() {
            return timeout;
        }

        public int getBatch() {
            return batch;
        }
    }

    static class TestNestedPropertyBean {
        @Value("${${a}.${b}:${c:100}}")
        private int nestedProperty;

        public int getNestedProperty() {
            return nestedProperty;
        }
    }

    static class TestJsonPropertyBean {
        @ApolloJsonValue("${jsonProperty}")
        private List<JavaConfigPlaceholderTest.JsonBean> jsonBeanList;

        private List<JavaConfigPlaceholderTest.JsonBean> otherJsonBeanList;

        @ApolloJsonValue("${${a}}")
        private List<JavaConfigPlaceholderTest.JsonBean> embeddedJsonBeanList;

        public List<JavaConfigPlaceholderTest.JsonBean> getJsonBeanList() {
            return jsonBeanList;
        }

        @ApolloJsonValue("${otherJsonProperty}")
        public void setOtherJsonBeanList(List<JavaConfigPlaceholderTest.JsonBean> otherJsonBeanList) {
            this.otherJsonBeanList = otherJsonBeanList;
        }

        public List<JavaConfigPlaceholderTest.JsonBean> getOtherJsonBeanList() {
            return otherJsonBeanList;
        }

        public List<JavaConfigPlaceholderTest.JsonBean> getEmbeddedJsonBeanList() {
            return embeddedJsonBeanList;
        }
    }

    static class JsonBean {
        private String a;

        private int b;

        String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            JavaConfigPlaceholderTest.JsonBean jsonBean = ((JavaConfigPlaceholderTest.JsonBean) (o));
            if ((b) != (jsonBean.b)) {
                return false;
            }
            return (a) != null ? a.equals(jsonBean.a) : (jsonBean.a) == null;
        }

        @Override
        public int hashCode() {
            int result = ((a) != null) ? a.hashCode() : 0;
            result = (31 * result) + (b);
            return result;
        }
    }
}

