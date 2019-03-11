package com.ctrip.framework.apollo.spi;


import ConfigFileFormat.JSON;
import ConfigFileFormat.XML;
import ConfigFileFormat.YAML;
import ConfigFileFormat.YML;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.internals.DefaultConfig;
import com.ctrip.framework.apollo.internals.JsonConfigFile;
import com.ctrip.framework.apollo.internals.LocalFileConfigRepository;
import com.ctrip.framework.apollo.internals.PropertiesCompatibleFileConfigRepository;
import com.ctrip.framework.apollo.internals.PropertiesConfigFile;
import com.ctrip.framework.apollo.internals.XmlConfigFile;
import com.ctrip.framework.apollo.internals.YamlConfigFile;
import com.ctrip.framework.apollo.internals.YmlConfigFile;
import com.ctrip.framework.apollo.util.ConfigUtil;
import java.util.Properties;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigFactoryTest {
    private DefaultConfigFactory defaultConfigFactory;

    private static String someAppId;

    private static Env someEnv;

    @Test
    public void testCreate() throws Exception {
        String someNamespace = "someName";
        Properties someProperties = new Properties();
        String someKey = "someKey";
        String someValue = "someValue";
        someProperties.setProperty(someKey, someValue);
        LocalFileConfigRepository someLocalConfigRepo = Mockito.mock(LocalFileConfigRepository.class);
        Mockito.when(someLocalConfigRepo.getConfig()).thenReturn(someProperties);
        Mockito.doReturn(someLocalConfigRepo).when(defaultConfigFactory).createLocalConfigRepository(someNamespace);
        Config result = defaultConfigFactory.create(someNamespace);
        Assert.assertThat("DefaultConfigFactory should create DefaultConfig", result, Is.is(IsInstanceOf.instanceOf(DefaultConfig.class)));
        Assert.assertEquals(someValue, result.getProperty(someKey, null));
    }

    @Test
    public void testCreateLocalConfigRepositoryInLocalDev() throws Exception {
        String someNamespace = "someName";
        DefaultConfigFactoryTest.someEnv = Env.LOCAL;
        LocalFileConfigRepository localFileConfigRepository = defaultConfigFactory.createLocalConfigRepository(someNamespace);
        Assert.assertNull(ReflectionTestUtils.getField(localFileConfigRepository, "m_upstream"));
    }

    @Test
    public void testCreatePropertiesCompatibleFileConfigRepository() throws Exception {
        ConfigFileFormat somePropertiesCompatibleFormat = ConfigFileFormat.YML;
        String someNamespace = ("someName" + ".") + somePropertiesCompatibleFormat;
        Properties someProperties = new Properties();
        String someKey = "someKey";
        String someValue = "someValue";
        someProperties.setProperty(someKey, someValue);
        PropertiesCompatibleFileConfigRepository someRepository = Mockito.mock(PropertiesCompatibleFileConfigRepository.class);
        Mockito.when(someRepository.getConfig()).thenReturn(someProperties);
        Mockito.doReturn(someRepository).when(defaultConfigFactory).createPropertiesCompatibleFileConfigRepository(someNamespace, somePropertiesCompatibleFormat);
        Config result = defaultConfigFactory.create(someNamespace);
        Assert.assertThat("DefaultConfigFactory should create DefaultConfig", result, Is.is(IsInstanceOf.instanceOf(DefaultConfig.class)));
        Assert.assertEquals(someValue, result.getProperty(someKey, null));
    }

    @Test
    public void testCreateConfigFile() throws Exception {
        String someNamespace = "someName";
        String anotherNamespace = "anotherName";
        String yetAnotherNamespace = "yetAnotherNamespace";
        Properties someProperties = new Properties();
        LocalFileConfigRepository someLocalConfigRepo = Mockito.mock(LocalFileConfigRepository.class);
        Mockito.when(someLocalConfigRepo.getConfig()).thenReturn(someProperties);
        Mockito.doReturn(someLocalConfigRepo).when(defaultConfigFactory).createLocalConfigRepository(someNamespace);
        Mockito.doReturn(someLocalConfigRepo).when(defaultConfigFactory).createLocalConfigRepository(anotherNamespace);
        Mockito.doReturn(someLocalConfigRepo).when(defaultConfigFactory).createLocalConfigRepository(yetAnotherNamespace);
        ConfigFile propertyConfigFile = defaultConfigFactory.createConfigFile(someNamespace, ConfigFileFormat.Properties);
        ConfigFile xmlConfigFile = defaultConfigFactory.createConfigFile(anotherNamespace, XML);
        ConfigFile jsonConfigFile = defaultConfigFactory.createConfigFile(yetAnotherNamespace, JSON);
        ConfigFile ymlConfigFile = defaultConfigFactory.createConfigFile(someNamespace, YML);
        ConfigFile yamlConfigFile = defaultConfigFactory.createConfigFile(someNamespace, YAML);
        Assert.assertThat("Should create PropertiesConfigFile for properties format", propertyConfigFile, Is.is(IsInstanceOf.instanceOf(PropertiesConfigFile.class)));
        Assert.assertEquals(someNamespace, propertyConfigFile.getNamespace());
        Assert.assertThat("Should create XmlConfigFile for xml format", xmlConfigFile, Is.is(IsInstanceOf.instanceOf(XmlConfigFile.class)));
        Assert.assertEquals(anotherNamespace, xmlConfigFile.getNamespace());
        Assert.assertThat("Should create JsonConfigFile for json format", jsonConfigFile, Is.is(IsInstanceOf.instanceOf(JsonConfigFile.class)));
        Assert.assertEquals(yetAnotherNamespace, jsonConfigFile.getNamespace());
        Assert.assertThat("Should create YmlConfigFile for yml format", ymlConfigFile, Is.is(IsInstanceOf.instanceOf(YmlConfigFile.class)));
        Assert.assertEquals(someNamespace, ymlConfigFile.getNamespace());
        Assert.assertThat("Should create YamlConfigFile for yaml format", yamlConfigFile, Is.is(IsInstanceOf.instanceOf(YamlConfigFile.class)));
        Assert.assertEquals(someNamespace, yamlConfigFile.getNamespace());
    }

    @Test
    public void testDetermineFileFormat() throws Exception {
        checkFileFormat("abc", ConfigFileFormat.Properties);
        checkFileFormat("abc.properties", ConfigFileFormat.Properties);
        checkFileFormat("abc.pRopErties", ConfigFileFormat.Properties);
        checkFileFormat("abc.xml", XML);
        checkFileFormat("abc.xmL", XML);
        checkFileFormat("abc.json", JSON);
        checkFileFormat("abc.jsOn", JSON);
        checkFileFormat("abc.yaml", YAML);
        checkFileFormat("abc.yAml", YAML);
        checkFileFormat("abc.yml", YML);
        checkFileFormat("abc.yMl", YML);
        checkFileFormat("abc.properties.yml", YML);
    }

    @Test
    public void testTrimNamespaceFormat() throws Exception {
        checkNamespaceName("abc", ConfigFileFormat.Properties, "abc");
        checkNamespaceName("abc.properties", ConfigFileFormat.Properties, "abc");
        checkNamespaceName("abcproperties", ConfigFileFormat.Properties, "abcproperties");
        checkNamespaceName("abc.pRopErties", ConfigFileFormat.Properties, "abc");
        checkNamespaceName("abc.xml", XML, "abc");
        checkNamespaceName("abc.xmL", XML, "abc");
        checkNamespaceName("abc.json", JSON, "abc");
        checkNamespaceName("abc.jsOn", JSON, "abc");
        checkNamespaceName("abc.yaml", YAML, "abc");
        checkNamespaceName("abc.yAml", YAML, "abc");
        checkNamespaceName("abc.yml", YML, "abc");
        checkNamespaceName("abc.yMl", YML, "abc");
        checkNamespaceName("abc.proPerties.yml", YML, "abc.proPerties");
    }

    public static class MockConfigUtil extends ConfigUtil {
        @Override
        public String getAppId() {
            return DefaultConfigFactoryTest.someAppId;
        }

        @Override
        public Env getApolloEnv() {
            return DefaultConfigFactoryTest.someEnv;
        }
    }
}

