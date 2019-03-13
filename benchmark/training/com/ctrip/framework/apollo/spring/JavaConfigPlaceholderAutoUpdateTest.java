package com.ctrip.framework.apollo.spring;


import ConfigConsts.NAMESPACE_APPLICATION;
import com.ctrip.framework.apollo.build.MockInjector;
import com.ctrip.framework.apollo.internals.SimpleConfig;
import com.ctrip.framework.apollo.internals.YamlConfigFile;
import com.ctrip.framework.apollo.spring.annotation.ApolloJsonValue;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.primitives.Ints;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;


public class JavaConfigPlaceholderAutoUpdateTest extends AbstractSpringIntegrationTest {
    private static final String TIMEOUT_PROPERTY = "timeout";

    private static final int DEFAULT_TIMEOUT = 100;

    private static final String BATCH_PROPERTY = "batch";

    private static final int DEFAULT_BATCH = 200;

    private static final String FX_APOLLO_NAMESPACE = "FX.apollo";

    private static final String SOME_KEY_PROPERTY = "someKey";

    private static final String ANOTHER_KEY_PROPERTY = "anotherKey";

    @Test
    public void testAutoUpdateWithOneNamespace() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        int newBatch = 2001;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(initialBatch));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig1.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(newTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(newBatch));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(newTimeout, bean.getTimeout());
        Assert.assertEquals(newBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithOneYamlFile() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        int newBatch = 2001;
        YamlConfigFile configFile = AbstractSpringIntegrationTest.prepareYamlConfigFile("application.yaml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case1.yaml"));
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig12.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        configFile.onRepositoryChange("application.yaml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case1-new.yaml"));
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(newTimeout, bean.getTimeout());
        Assert.assertEquals(newBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithValueAndXmlProperty() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        int newBatch = 2001;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(initialBatch));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig8.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean javaConfigBean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        XmlConfigPlaceholderTest.TestXmlBean xmlBean = context.getBean(XmlConfigPlaceholderTest.TestXmlBean.class);
        Assert.assertEquals(initialTimeout, javaConfigBean.getTimeout());
        Assert.assertEquals(initialBatch, javaConfigBean.getBatch());
        Assert.assertEquals(initialTimeout, xmlBean.getTimeout());
        Assert.assertEquals(initialBatch, xmlBean.getBatch());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(newTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(newBatch));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(newTimeout, javaConfigBean.getTimeout());
        Assert.assertEquals(newBatch, javaConfigBean.getBatch());
        Assert.assertEquals(newTimeout, xmlBean.getTimeout());
        Assert.assertEquals(newBatch, xmlBean.getBatch());
    }

    @Test
    public void testAutoUpdateWithYamlFileWithValueAndXmlProperty() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        int newBatch = 2001;
        YamlConfigFile configFile = AbstractSpringIntegrationTest.prepareYamlConfigFile("application.yaml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case1.yaml"));
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig13.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean javaConfigBean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        XmlConfigPlaceholderTest.TestXmlBean xmlBean = context.getBean(XmlConfigPlaceholderTest.TestXmlBean.class);
        Assert.assertEquals(initialTimeout, javaConfigBean.getTimeout());
        Assert.assertEquals(initialBatch, javaConfigBean.getBatch());
        Assert.assertEquals(initialTimeout, xmlBean.getTimeout());
        Assert.assertEquals(initialBatch, xmlBean.getBatch());
        configFile.onRepositoryChange("application.yaml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case1-new.yaml"));
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(newTimeout, javaConfigBean.getTimeout());
        Assert.assertEquals(newBatch, javaConfigBean.getBatch());
        Assert.assertEquals(newTimeout, xmlBean.getTimeout());
        Assert.assertEquals(newBatch, xmlBean.getBatch());
    }

    @Test
    public void testAutoUpdateDisabled() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        int newBatch = 2001;
        AbstractSpringIntegrationTest.MockConfigUtil mockConfigUtil = new AbstractSpringIntegrationTest.MockConfigUtil();
        mockConfigUtil.setAutoUpdateInjectedSpringProperties(false);
        MockInjector.setInstance(ConfigUtil.class, mockConfigUtil);
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(initialBatch));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig1.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(newTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(newBatch));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithMultipleNamespaces() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        int newBatch = 2001;
        Properties applicationProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout));
        Properties fxApolloProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(initialBatch));
        SimpleConfig applicationConfig = prepareConfig(NAMESPACE_APPLICATION, applicationProperties);
        SimpleConfig fxApolloConfig = prepareConfig(JavaConfigPlaceholderAutoUpdateTest.FX_APOLLO_NAMESPACE, fxApolloProperties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig2.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        Properties newApplicationProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(newTimeout));
        applicationConfig.onRepositoryChange(NAMESPACE_APPLICATION, newApplicationProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(newTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        Properties newFxApolloProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(newBatch));
        fxApolloConfig.onRepositoryChange(JavaConfigPlaceholderAutoUpdateTest.FX_APOLLO_NAMESPACE, newFxApolloProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(newTimeout, bean.getTimeout());
        Assert.assertEquals(newBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithMultipleNamespacesWithSameProperties() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        int anotherBatch = 3000;
        int someNewTimeout = 1001;
        int someNewBatch = 2001;
        Properties applicationProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(someBatch));
        Properties fxApolloProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(someTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(anotherBatch));
        prepareConfig(NAMESPACE_APPLICATION, applicationProperties);
        SimpleConfig fxApolloConfig = prepareConfig(JavaConfigPlaceholderAutoUpdateTest.FX_APOLLO_NAMESPACE, fxApolloProperties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig2.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(someTimeout, bean.getTimeout());
        Assert.assertEquals(someBatch, bean.getBatch());
        Properties newFxApolloProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(someNewTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(someNewBatch));
        fxApolloConfig.onRepositoryChange(JavaConfigPlaceholderAutoUpdateTest.FX_APOLLO_NAMESPACE, newFxApolloProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(someNewTimeout, bean.getTimeout());
        Assert.assertEquals(someBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithMultipleNamespacesWithSamePropertiesWithYamlFile() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        int anotherBatch = 3000;
        int someNewBatch = 2001;
        YamlConfigFile configFile = AbstractSpringIntegrationTest.prepareYamlConfigFile("application.yml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case2.yml"));
        Properties fxApolloProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(someTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(anotherBatch));
        prepareConfig(JavaConfigPlaceholderAutoUpdateTest.FX_APOLLO_NAMESPACE, fxApolloProperties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig14.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(someTimeout, bean.getTimeout());
        Assert.assertEquals(someBatch, bean.getBatch());
        configFile.onRepositoryChange("application.yml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case2-new.yml"));
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(someTimeout, bean.getTimeout());
        Assert.assertEquals(someNewBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithNewProperties() throws Exception {
        int initialTimeout = 1000;
        int newTimeout = 1001;
        int newBatch = 2001;
        Properties applicationProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout));
        SimpleConfig applicationConfig = prepareConfig(NAMESPACE_APPLICATION, applicationProperties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig1.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(JavaConfigPlaceholderAutoUpdateTest.DEFAULT_BATCH, bean.getBatch());
        Properties newApplicationProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(newTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(newBatch));
        applicationConfig.onRepositoryChange(NAMESPACE_APPLICATION, newApplicationProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(newTimeout, bean.getTimeout());
        Assert.assertEquals(newBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithNewPropertiesWithYamlFile() throws Exception {
        int initialTimeout = 1000;
        int newTimeout = 1001;
        int newBatch = 2001;
        YamlConfigFile configFile = AbstractSpringIntegrationTest.prepareYamlConfigFile("application.yaml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case3.yaml"));
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig12.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(JavaConfigPlaceholderAutoUpdateTest.DEFAULT_BATCH, bean.getBatch());
        configFile.onRepositoryChange("application.yaml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case3-new.yaml"));
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(newTimeout, bean.getTimeout());
        Assert.assertEquals(newBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithIrrelevantProperties() throws Exception {
        int initialTimeout = 1000;
        String someIrrelevantKey = "someIrrelevantKey";
        String someIrrelevantValue = "someIrrelevantValue";
        String anotherIrrelevantKey = "anotherIrrelevantKey";
        String anotherIrrelevantValue = "anotherIrrelevantValue";
        Properties applicationProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), someIrrelevantKey, someIrrelevantValue);
        SimpleConfig applicationConfig = prepareConfig(NAMESPACE_APPLICATION, applicationProperties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig1.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(JavaConfigPlaceholderAutoUpdateTest.DEFAULT_BATCH, bean.getBatch());
        Properties newApplicationProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), anotherIrrelevantKey, String.valueOf(anotherIrrelevantValue));
        applicationConfig.onRepositoryChange(NAMESPACE_APPLICATION, newApplicationProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(JavaConfigPlaceholderAutoUpdateTest.DEFAULT_BATCH, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithDeletedProperties() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(initialBatch));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig1.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        Properties newProperties = new Properties();
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(JavaConfigPlaceholderAutoUpdateTest.DEFAULT_TIMEOUT, bean.getTimeout());
        Assert.assertEquals(JavaConfigPlaceholderAutoUpdateTest.DEFAULT_BATCH, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithDeletedPropertiesWithYamlFile() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        YamlConfigFile configFile = AbstractSpringIntegrationTest.prepareYamlConfigFile("application.yaml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case4.yaml"));
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig12.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        configFile.onRepositoryChange("application.yaml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case4-new.yaml"));
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(JavaConfigPlaceholderAutoUpdateTest.DEFAULT_TIMEOUT, bean.getTimeout());
        Assert.assertEquals(JavaConfigPlaceholderAutoUpdateTest.DEFAULT_BATCH, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithMultipleNamespacesWithSamePropertiesDeleted() throws Exception {
        int someTimeout = 1000;
        int someBatch = 2000;
        int anotherBatch = 3000;
        Properties applicationProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(someBatch));
        Properties fxApolloProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(someTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(anotherBatch));
        SimpleConfig applicationConfig = prepareConfig(NAMESPACE_APPLICATION, applicationProperties);
        prepareConfig(JavaConfigPlaceholderAutoUpdateTest.FX_APOLLO_NAMESPACE, fxApolloProperties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig2.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(someTimeout, bean.getTimeout());
        Assert.assertEquals(someBatch, bean.getBatch());
        Properties newProperties = new Properties();
        applicationConfig.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(someTimeout, bean.getTimeout());
        Assert.assertEquals(anotherBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithDeletedPropertiesWithNoDefaultValue() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(initialBatch));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig6.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean5 bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean5.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(newTimeout));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(300);
        Assert.assertEquals(newTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithTypeMismatch() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        String newBatch = "newBatch";
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(initialBatch));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig1.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(newTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, newBatch);
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(300);
        Assert.assertEquals(newTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithTypeMismatchWithYamlFile() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        YamlConfigFile configFile = AbstractSpringIntegrationTest.prepareYamlConfigFile("application.yaml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case5.yaml"));
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig12.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        configFile.onRepositoryChange("application.yaml", AbstractSpringIntegrationTest.readYamlContentAsConfigFileProperties("case5-new.yaml"));
        TimeUnit.MILLISECONDS.sleep(300);
        Assert.assertEquals(newTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithValueInjectedAsParameter() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        int newBatch = 2001;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(initialBatch));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig3.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean2 bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean2.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(newTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(newBatch));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        // Does not support this scenario
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
    }

    @Test
    public void testApplicationPropertySourceWithValueInjectedInConfiguration() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        int newBatch = 2001;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(initialBatch));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig7.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean2 bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean2.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(newTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(newBatch));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        // Does not support this scenario
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithValueInjectedAsConstructorArgs() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        int newBatch = 2001;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(initialBatch));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig4.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean3 bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean3.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(newTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(newBatch));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        // Does not support this scenario
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithInvalidSetter() throws Exception {
        int initialTimeout = 1000;
        int initialBatch = 2000;
        int newTimeout = 1001;
        int newBatch = 2001;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(initialTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(initialBatch));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig5.class);
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean4 bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean4.class);
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.TIMEOUT_PROPERTY, String.valueOf(newTimeout), JavaConfigPlaceholderAutoUpdateTest.BATCH_PROPERTY, String.valueOf(newBatch));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        // Does not support this scenario
        Assert.assertEquals(initialTimeout, bean.getTimeout());
        Assert.assertEquals(initialBatch, bean.getBatch());
    }

    @Test
    public void testAutoUpdateWithNestedProperty() throws Exception {
        String someKeyValue = "someKeyValue";
        String anotherKeyValue = "anotherKeyValue";
        String newKeyValue = "newKeyValue";
        int someValue = 1234;
        int someNewValue = 2345;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.SOME_KEY_PROPERTY, someKeyValue, JavaConfigPlaceholderAutoUpdateTest.ANOTHER_KEY_PROPERTY, anotherKeyValue, String.format("%s.%s", someKeyValue, anotherKeyValue), String.valueOf(someValue));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.NestedPropertyConfig1.class);
        JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBean.class);
        Assert.assertEquals(someValue, bean.getNestedProperty());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.SOME_KEY_PROPERTY, newKeyValue, JavaConfigPlaceholderAutoUpdateTest.ANOTHER_KEY_PROPERTY, anotherKeyValue, String.format("%s.%s", newKeyValue, anotherKeyValue), String.valueOf(someNewValue));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(someNewValue, bean.getNestedProperty());
    }

    @Test
    public void testAutoUpdateWithNotSupportedNestedProperty() throws Exception {
        String someKeyValue = "someKeyValue";
        String anotherKeyValue = "anotherKeyValue";
        int someValue = 1234;
        int someNewValue = 2345;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.SOME_KEY_PROPERTY, someKeyValue, JavaConfigPlaceholderAutoUpdateTest.ANOTHER_KEY_PROPERTY, anotherKeyValue, String.format("%s.%s", someKeyValue, anotherKeyValue), String.valueOf(someValue));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.NestedPropertyConfig1.class);
        JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBean.class);
        Assert.assertEquals(someValue, bean.getNestedProperty());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.SOME_KEY_PROPERTY, someKeyValue, JavaConfigPlaceholderAutoUpdateTest.ANOTHER_KEY_PROPERTY, anotherKeyValue, String.format("%s.%s", someKeyValue, anotherKeyValue), String.valueOf(someNewValue));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        // Does not support this scenario
        Assert.assertEquals(someValue, bean.getNestedProperty());
    }

    @Test
    public void testAutoUpdateWithNestedPropertyWithDefaultValue() throws Exception {
        String someKeyValue = "someKeyValue";
        String someNewKeyValue = "someNewKeyValue";
        int someValue = 1234;
        int someNewValue = 2345;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.SOME_KEY_PROPERTY, someKeyValue, JavaConfigPlaceholderAutoUpdateTest.ANOTHER_KEY_PROPERTY, String.valueOf(someValue));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.NestedPropertyConfig2.class);
        JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBeanWithDefaultValue bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBeanWithDefaultValue.class);
        Assert.assertEquals(someValue, bean.getNestedProperty());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.SOME_KEY_PROPERTY, someNewKeyValue, JavaConfigPlaceholderAutoUpdateTest.ANOTHER_KEY_PROPERTY, String.valueOf(someValue), someNewKeyValue, String.valueOf(someNewValue));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(someNewValue, bean.getNestedProperty());
    }

    @Test
    public void testAutoUpdateWithMultipleNestedProperty() throws Exception {
        String someKeyValue = "someKeyValue";
        String someNewKeyValue = "someNewKeyValue";
        String anotherKeyValue = "anotherKeyValue";
        String someNestedKey = "someNestedKey";
        String someNestedPlaceholder = String.format("${%s}", someNestedKey);
        String anotherNestedKey = "anotherNestedKey";
        String anotherNestedPlaceholder = String.format("${%s}", anotherNestedKey);
        int someValue = 1234;
        int someNewValue = 2345;
        Properties properties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.SOME_KEY_PROPERTY, someKeyValue, JavaConfigPlaceholderAutoUpdateTest.ANOTHER_KEY_PROPERTY, anotherKeyValue, someKeyValue, someNestedPlaceholder);
        properties.setProperty(someNestedKey, String.valueOf(someValue));
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.NestedPropertyConfig2.class);
        JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBeanWithDefaultValue bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBeanWithDefaultValue.class);
        Assert.assertEquals(someValue, bean.getNestedProperty());
        Properties newProperties = assembleProperties(JavaConfigPlaceholderAutoUpdateTest.SOME_KEY_PROPERTY, someNewKeyValue, JavaConfigPlaceholderAutoUpdateTest.ANOTHER_KEY_PROPERTY, anotherKeyValue, someNewKeyValue, anotherNestedPlaceholder);
        newProperties.setProperty(anotherNestedKey, String.valueOf(someNewValue));
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(someNewValue, bean.getNestedProperty());
    }

    @Test
    public void testAutoUpdateWithAllKindsOfDataTypes() throws Exception {
        int someInt = 1000;
        int someNewInt = 1001;
        int[] someIntArray = new int[]{ 1, 2, 3, 4 };
        int[] someNewIntArray = new int[]{ 5, 6, 7, 8 };
        long someLong = 2000L;
        long someNewLong = 2001L;
        short someShort = 3000;
        short someNewShort = 3001;
        float someFloat = 1.2F;
        float someNewFloat = 2.2F;
        double someDouble = 3.1;
        double someNewDouble = 4.1;
        byte someByte = 123;
        byte someNewByte = 124;
        boolean someBoolean = true;
        boolean someNewBoolean = !someBoolean;
        String someString = "someString";
        String someNewString = "someNewString";
        String someJsonProperty = "[{\"a\":\"astring\", \"b\":10},{\"a\":\"astring2\", \"b\":20}]";
        String someNewJsonProperty = "[{\"a\":\"newString\", \"b\":20},{\"a\":\"astring2\", \"b\":20}]";
        String someDateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
        Date someDate = assembleDate(2018, 2, 23, 20, 1, 2, 123);
        Date someNewDate = assembleDate(2018, 2, 23, 21, 2, 3, 345);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(someDateFormat, Locale.US);
        Properties properties = new Properties();
        properties.setProperty("intProperty", String.valueOf(someInt));
        properties.setProperty("intArrayProperty", Ints.join(", ", someIntArray));
        properties.setProperty("longProperty", String.valueOf(someLong));
        properties.setProperty("shortProperty", String.valueOf(someShort));
        properties.setProperty("floatProperty", String.valueOf(someFloat));
        properties.setProperty("doubleProperty", String.valueOf(someDouble));
        properties.setProperty("byteProperty", String.valueOf(someByte));
        properties.setProperty("booleanProperty", String.valueOf(someBoolean));
        properties.setProperty("stringProperty", String.valueOf(someString));
        properties.setProperty("dateFormat", String.valueOf(someDateFormat));
        properties.setProperty("dateProperty", simpleDateFormat.format(someDate));
        properties.setProperty("jsonProperty", someJsonProperty);
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig9.class);
        JavaConfigPlaceholderAutoUpdateTest.TestAllKindsOfDataTypesBean bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestAllKindsOfDataTypesBean.class);
        Assert.assertEquals(someInt, bean.getIntProperty());
        Assert.assertArrayEquals(someIntArray, bean.getIntArrayProperty());
        Assert.assertEquals(someLong, bean.getLongProperty());
        Assert.assertEquals(someShort, bean.getShortProperty());
        Assert.assertEquals(someFloat, bean.getFloatProperty(), 0.001F);
        Assert.assertEquals(someDouble, bean.getDoubleProperty(), 0.001);
        Assert.assertEquals(someByte, bean.getByteProperty());
        Assert.assertEquals(someBoolean, bean.getBooleanProperty());
        Assert.assertEquals(someString, bean.getStringProperty());
        Assert.assertEquals(someDate, bean.getDateProperty());
        Assert.assertEquals("astring", bean.getJsonBeanList().get(0).getA());
        Assert.assertEquals(10, bean.getJsonBeanList().get(0).getB());
        Properties newProperties = new Properties();
        newProperties.setProperty("intProperty", String.valueOf(someNewInt));
        newProperties.setProperty("intArrayProperty", Ints.join(", ", someNewIntArray));
        newProperties.setProperty("longProperty", String.valueOf(someNewLong));
        newProperties.setProperty("shortProperty", String.valueOf(someNewShort));
        newProperties.setProperty("floatProperty", String.valueOf(someNewFloat));
        newProperties.setProperty("doubleProperty", String.valueOf(someNewDouble));
        newProperties.setProperty("byteProperty", String.valueOf(someNewByte));
        newProperties.setProperty("booleanProperty", String.valueOf(someNewBoolean));
        newProperties.setProperty("stringProperty", String.valueOf(someNewString));
        newProperties.setProperty("dateFormat", String.valueOf(someDateFormat));
        newProperties.setProperty("dateProperty", simpleDateFormat.format(someNewDate));
        newProperties.setProperty("jsonProperty", someNewJsonProperty);
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        Assert.assertEquals(someNewInt, bean.getIntProperty());
        Assert.assertArrayEquals(someNewIntArray, bean.getIntArrayProperty());
        Assert.assertEquals(someNewLong, bean.getLongProperty());
        Assert.assertEquals(someNewShort, bean.getShortProperty());
        Assert.assertEquals(someNewFloat, bean.getFloatProperty(), 0.001F);
        Assert.assertEquals(someNewDouble, bean.getDoubleProperty(), 0.001);
        Assert.assertEquals(someNewByte, bean.getByteProperty());
        Assert.assertEquals(someNewBoolean, bean.getBooleanProperty());
        Assert.assertEquals(someNewString, bean.getStringProperty());
        Assert.assertEquals(someNewDate, bean.getDateProperty());
        Assert.assertEquals("newString", bean.getJsonBeanList().get(0).getA());
        Assert.assertEquals(20, bean.getJsonBeanList().get(0).getB());
    }

    @Test
    public void testAutoUpdateJsonValueWithInvalidValue() throws Exception {
        String someValidValue = "{\"a\":\"someString\", \"b\":10}";
        String someInvalidValue = "someInvalidValue";
        Properties properties = assembleProperties("jsonProperty", someValidValue);
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig10.class);
        JavaConfigPlaceholderAutoUpdateTest.TestApolloJsonValue bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestApolloJsonValue.class);
        JavaConfigPlaceholderTest.JsonBean jsonBean = bean.getJsonBean();
        Assert.assertEquals("someString", jsonBean.getA());
        Assert.assertEquals(10, jsonBean.getB());
        Properties newProperties = assembleProperties("jsonProperty", someInvalidValue);
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(300);
        // should not change anything
        Assert.assertTrue((jsonBean == (bean.getJsonBean())));
    }

    @Test
    public void testAutoUpdateJsonValueWithNoValueAndNoDefaultValue() throws Exception {
        String someValidValue = "{\"a\":\"someString\", \"b\":10}";
        Properties properties = assembleProperties("jsonProperty", someValidValue);
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig10.class);
        JavaConfigPlaceholderAutoUpdateTest.TestApolloJsonValue bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestApolloJsonValue.class);
        JavaConfigPlaceholderTest.JsonBean jsonBean = bean.getJsonBean();
        Assert.assertEquals("someString", jsonBean.getA());
        Assert.assertEquals(10, jsonBean.getB());
        Properties newProperties = new Properties();
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(300);
        // should not change anything
        Assert.assertTrue((jsonBean == (bean.getJsonBean())));
    }

    @Test
    public void testAutoUpdateJsonValueWithNoValueAndDefaultValue() throws Exception {
        String someValidValue = "{\"a\":\"someString\", \"b\":10}";
        Properties properties = assembleProperties("jsonProperty", someValidValue);
        SimpleConfig config = prepareConfig(NAMESPACE_APPLICATION, properties);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JavaConfigPlaceholderAutoUpdateTest.AppConfig11.class);
        JavaConfigPlaceholderAutoUpdateTest.TestApolloJsonValueWithDefaultValue bean = context.getBean(JavaConfigPlaceholderAutoUpdateTest.TestApolloJsonValueWithDefaultValue.class);
        JavaConfigPlaceholderTest.JsonBean jsonBean = bean.getJsonBean();
        Assert.assertEquals("someString", jsonBean.getA());
        Assert.assertEquals(10, jsonBean.getB());
        Properties newProperties = new Properties();
        config.onRepositoryChange(NAMESPACE_APPLICATION, newProperties);
        TimeUnit.MILLISECONDS.sleep(100);
        JavaConfigPlaceholderTest.JsonBean newJsonBean = bean.getJsonBean();
        Assert.assertEquals("defaultString", newJsonBean.getA());
        Assert.assertEquals(1, newJsonBean.getB());
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig1 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean();
        }
    }

    @Configuration
    @EnableApolloConfig({ "application", "FX.apollo" })
    static class AppConfig2 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig3 {
        /**
         * This case won't get auto updated
         */
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean2 testJavaConfigBean2(@Value("${timeout:100}")
        int timeout, @Value("${batch:200}")
        int batch) {
            JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean2 bean = new JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean2();
            bean.setTimeout(timeout);
            bean.setBatch(batch);
            return bean;
        }
    }

    @Configuration
    @ComponentScan(includeFilters = { @Filter(type = FilterType.ANNOTATION, value = { Component.class }) }, excludeFilters = { @Filter(type = FilterType.ANNOTATION, value = { Configuration.class }) })
    @EnableApolloConfig
    static class AppConfig4 {}

    @Configuration
    @EnableApolloConfig
    static class AppConfig5 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean4 testJavaConfigBean() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean4();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig6 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean5 testJavaConfigBean() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean5();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig7 {
        @Value("${batch}")
        private int batch;

        @Bean
        @Value("${timeout}")
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean2 testJavaConfigBean2(int timeout) {
            JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean2 bean = new JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean2();
            bean.setTimeout(timeout);
            bean.setBatch(batch);
            return bean;
        }
    }

    @Configuration
    @EnableApolloConfig
    @ImportResource("spring/XmlConfigPlaceholderTest1.xml")
    static class AppConfig8 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig9 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestAllKindsOfDataTypesBean testAllKindsOfDataTypesBean() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestAllKindsOfDataTypesBean();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class NestedPropertyConfig1 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBean testNestedPropertyBean() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBean();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class NestedPropertyConfig2 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBeanWithDefaultValue testNestedPropertyBean() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestNestedPropertyBeanWithDefaultValue();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig10 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestApolloJsonValue testApolloJsonValue() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestApolloJsonValue();
        }
    }

    @Configuration
    @EnableApolloConfig
    static class AppConfig11 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestApolloJsonValueWithDefaultValue testApolloJsonValue() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestApolloJsonValueWithDefaultValue();
        }
    }

    @Configuration
    @EnableApolloConfig("application.yaMl")
    static class AppConfig12 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean();
        }
    }

    @Configuration
    @EnableApolloConfig("application.yaml")
    @ImportResource("spring/XmlConfigPlaceholderTest11.xml")
    static class AppConfig13 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean();
        }
    }

    @Configuration
    @EnableApolloConfig({ "application.yml", "FX.apollo" })
    static class AppConfig14 {
        @Bean
        JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean testJavaConfigBean() {
            return new JavaConfigPlaceholderAutoUpdateTest.TestJavaConfigBean();
        }
    }

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

    /**
     * This case won't get auto updated
     */
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

    /**
     * This case won't get auto updated
     */
    static class TestJavaConfigBean4 {
        private int timeout;

        private int batch;

        @Value("${batch:200}")
        public void setValues(int batch, @Value("${timeout:100}")
        int timeout) {
            this.batch = batch;
            this.timeout = timeout;
        }

        public int getTimeout() {
            return timeout;
        }

        public int getBatch() {
            return batch;
        }
    }

    static class TestJavaConfigBean5 {
        @Value("${timeout}")
        private int timeout;

        private int batch;

        @Value("${batch}")
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

    static class TestNestedPropertyBean {
        @Value("${${someKey}.${anotherKey}}")
        private int nestedProperty;

        public int getNestedProperty() {
            return nestedProperty;
        }
    }

    static class TestNestedPropertyBeanWithDefaultValue {
        @Value("${${someKey}:${anotherKey}}")
        private int nestedProperty;

        public int getNestedProperty() {
            return nestedProperty;
        }
    }

    static class TestAllKindsOfDataTypesBean {
        @Value("${intProperty}")
        private int intProperty;

        @Value("${intArrayProperty}")
        private int[] intArrayProperty;

        @Value("${longProperty}")
        private long longProperty;

        @Value("${shortProperty}")
        private short shortProperty;

        @Value("${floatProperty}")
        private float floatProperty;

        @Value("${doubleProperty}")
        private double doubleProperty;

        @Value("${byteProperty}")
        private byte byteProperty;

        @Value("${booleanProperty}")
        private boolean booleanProperty;

        @Value("${stringProperty}")
        private String stringProperty;

        @Value("#{new java.text.SimpleDateFormat('${dateFormat}').parse('${dateProperty}')}")
        private Date dateProperty;

        @ApolloJsonValue("${jsonProperty}")
        private List<JavaConfigPlaceholderTest.JsonBean> jsonBeanList;

        public int getIntProperty() {
            return intProperty;
        }

        public int[] getIntArrayProperty() {
            return intArrayProperty;
        }

        public long getLongProperty() {
            return longProperty;
        }

        public short getShortProperty() {
            return shortProperty;
        }

        public float getFloatProperty() {
            return floatProperty;
        }

        public double getDoubleProperty() {
            return doubleProperty;
        }

        public byte getByteProperty() {
            return byteProperty;
        }

        public boolean getBooleanProperty() {
            return booleanProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public Date getDateProperty() {
            return dateProperty;
        }

        public List<JavaConfigPlaceholderTest.JsonBean> getJsonBeanList() {
            return jsonBeanList;
        }
    }

    static class TestApolloJsonValue {
        @ApolloJsonValue("${jsonProperty}")
        private JavaConfigPlaceholderTest.JsonBean jsonBean;

        public JavaConfigPlaceholderTest.JsonBean getJsonBean() {
            return jsonBean;
        }
    }

    static class TestApolloJsonValueWithDefaultValue {
        @ApolloJsonValue("${jsonProperty:{\"a\":\"defaultString\", \"b\":1}}")
        private JavaConfigPlaceholderTest.JsonBean jsonBean;

        public JavaConfigPlaceholderTest.JsonBean getJsonBean() {
            return jsonBean;
        }
    }
}

