package org.embulk.config;


import java.io.IOException;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


public class TestConfigLoader {
    private ConfigLoader loader;

    @Test
    public void testFromEmptyJson() throws IOException {
        ConfigSource config = loader.fromJson(TestConfigLoader.newInputStream("{\"type\":\"test\",\"data\":1}"));
        Assert.assertEquals("test", config.get(String.class, "type"));
        Assert.assertEquals(1, ((int) (config.get(Integer.class, "data"))));
    }

    @Test
    public void testFromYamlProperties() throws IOException {
        Properties props = new Properties();
        props.setProperty("type", "test");
        props.setProperty("data", "1");
        ConfigSource config = loader.fromPropertiesYamlLiteral(props, "");
        Assert.assertEquals("test", config.get(String.class, "type"));
        Assert.assertEquals(1, ((int) (config.get(Integer.class, "data"))));
    }

    @Test
    public void testFromYamlPropertiesNested() throws IOException {
        Properties props = new Properties();
        props.setProperty("type", "test");
        props.setProperty("columns.k1", "1");
        props.setProperty("values.myval.data", "2");
        ConfigSource config = loader.fromPropertiesYamlLiteral(props, "");
        System.out.println(("config: " + config));
        Assert.assertEquals("test", config.get(String.class, "type"));
        Assert.assertEquals(1, ((int) (config.getNested("columns").get(Integer.class, "k1"))));
        Assert.assertEquals(2, ((int) (config.getNested("values").getNested("myval").get(Integer.class, "data"))));
    }
}

