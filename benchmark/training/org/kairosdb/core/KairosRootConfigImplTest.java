package org.kairosdb.core;


import ConfigFormat.HOCON;
import ConfigFormat.PROPERTIES;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class KairosRootConfigImplTest {
    private static final String PROPERITES_CONFIG_FILE = "config.properties";

    private static final String JSON_CONFIG_FILE = "config.json";

    private static final String HOCON_CONFIG_FILE = "config.conf";

    private static final String RESERVED_HOCON_CHARS = "${}[]:+=#`^?!@*// ";

    private static Map<String, String> defaultProperties;

    private static Map<String, String> properties;

    private KairosRootConfig m_config;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void test_getProperty() {
        m_config.load(ImmutableMap.of("key", "value"));
        Assert.assertEquals("value", m_config.getProperty("key"));
    }

    @Test
    public void test_getProperty_shouldReturnUpdatedValue() {
        m_config.load(ImmutableMap.of("key", "initialValue"));
        m_config.load(ImmutableMap.of("key", "updatedValue"));
        Assert.assertEquals("updatedValue", m_config.getProperty("key"));
    }

    @Test
    public void test_getProperty_shouldReturnNull() {
        Assert.assertNull(m_config.getProperty("key"));
    }

    @Test
    public void test_stringPropertyNames() {
        m_config.load(KairosRootConfigImplTest.defaultProperties);
        Assert.assertEquals(KairosRootConfigImplTest.defaultProperties.keySet(), Sets.newHashSet(m_config));
    }

    @Test
    public void test_load_propertiesFile() throws IOException, URISyntaxException {
        m_config.load(getFile(KairosRootConfigImplTest.PROPERITES_CONFIG_FILE));
        KairosRootConfigImplTest.verifyContains(KairosRootConfigImplTest.properties, m_config);
    }

    /* @Test
    public void test_load_JSONFile() throws IOException, URISyntaxException
    {
    m_config.load(getFile(JSON_CONFIG_FILE));
    verifyContains(properties, m_config);
    }
     */
    @Test
    public void test_load_HOCONFile() throws IOException, URISyntaxException {
        m_config.load(getFile(KairosRootConfigImplTest.HOCON_CONFIG_FILE));
        KairosRootConfigImplTest.verifyContains(KairosRootConfigImplTest.properties, m_config);
    }

    @Test
    public void test_load_propertiesStream() throws IOException {
        try (InputStream is = Resources.getResource(KairosRootConfigImplTest.PROPERITES_CONFIG_FILE).openStream()) {
            m_config.load(is, PROPERTIES);
        }
        KairosRootConfigImplTest.verifyContains(KairosRootConfigImplTest.properties, m_config);
    }

    /* @Test
    public void test_load_JSONStream() throws IOException
    {
    try (InputStream is = Resources.getResource(JSON_CONFIG_FILE).openStream())
    {
    m_config.load(is, ConfigFormat.JSON);
    }
    verifyContains(properties, m_config);
    }
     */
    @Test
    public void test_load_HOCONStream() throws IOException {
        try (InputStream is = Resources.getResource(KairosRootConfigImplTest.HOCON_CONFIG_FILE).openStream()) {
            m_config.load(is, HOCON);
        }
        KairosRootConfigImplTest.verifyContains(KairosRootConfigImplTest.properties, m_config);
    }

    @Test
    public void test_load_file_shouldOverrideExistingProperties() throws IOException, URISyntaxException {
        m_config.load(KairosRootConfigImplTest.defaultProperties);
        m_config.load(getFile(KairosRootConfigImplTest.HOCON_CONFIG_FILE));
        Map<String, String> expectedProperties = KairosRootConfigImplTest.merge(KairosRootConfigImplTest.defaultProperties, KairosRootConfigImplTest.properties);
        KairosRootConfigImplTest.verifyContains(expectedProperties, m_config);
    }

    @Test
    public void test_load_stream_shouldOverrideExistingProperties() throws IOException {
        m_config.load(KairosRootConfigImplTest.defaultProperties);
        try (InputStream is = Resources.getResource(KairosRootConfigImplTest.HOCON_CONFIG_FILE).openStream()) {
            m_config.load(is, HOCON);
        }
        Map<String, String> expectedProperties = KairosRootConfigImplTest.merge(KairosRootConfigImplTest.defaultProperties, KairosRootConfigImplTest.properties);
        KairosRootConfigImplTest.verifyContains(expectedProperties, m_config);
    }

    @Test
    public void test_load_file_shouldUsePropertiesFormat() throws IOException {
        // Loading config data that is only valid using Java Properties format
        String configString = "key=" + (KairosRootConfigImplTest.RESERVED_HOCON_CHARS);
        m_config.load(makeFile("temp.properties", configString));
    }

    /* @Test(expected = ConfigException.Parse.class)
    public void test_load_file_shouldUseJSONFormat() throws IOException
    {
    // Loading config data that is only invalid using JSON format
    String configString = "key=value";
    m_config.load(makeFile("temp.json", configString));
    }
     */
    @Test
    public void test_load_file_shouldUseHOCONFormat() throws IOException {
        // Loading config data that is only valid using HOCON format
        String configString = "{key: value}";
        m_config.load(makeFile("temp.conf", configString));
        Assert.assertEquals("value", m_config.getProperty("key"));
    }

    @Test
    public void test_load_stream_shouldUsePropertiesFormat() throws IOException {
        // Loading config data that is only valid using Java Properties format
        String config = "key=" + (KairosRootConfigImplTest.RESERVED_HOCON_CHARS);
        try (InputStream is = IOUtils.toInputStream(config, "UTF-8")) {
            m_config.load(is, PROPERTIES);
        }
    }

    /* @Test(expected = ConfigException.Parse.class)
    public void test_load_stream_shouldUseJSONFormat() throws IOException
    {
    // Loading config data that is only invalid using JSON format
    String config = "key=value";
    try (InputStream is = IOUtils.toInputStream(config, "UTF-8"))
    {
    m_config.load(is, ConfigFormat.JSON);
    }
    }
     */
    @Test
    public void test_load_stream_shouldUseHOCONFormat() throws IOException {
        // Loading config data that is only valid using HOCON format
        String config = "{key: value}";
        try (InputStream is = IOUtils.toInputStream(config, "UTF-8")) {
            m_config.load(is, HOCON);
        }
        Assert.assertEquals("value", m_config.getProperty("key"));
    }

    @Test
    public void isSupportedFormat() {
        Assert.assertTrue(m_config.isSupportedFormat(KairosRootConfig.ConfigFormat.PROPERTIES));
        // assertTrue(m_config.isSupportedFormat(KairosConfig.ConfigFormat.JSON));
        Assert.assertTrue(m_config.isSupportedFormat(KairosRootConfig.ConfigFormat.HOCON));
    }
}

