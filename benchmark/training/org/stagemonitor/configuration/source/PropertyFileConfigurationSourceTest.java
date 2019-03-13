package org.stagemonitor.configuration.source;


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class PropertyFileConfigurationSourceTest {
    @Test
    public void testLoadFromClasspath() throws Exception {
        PropertyFileConfigurationSource source = new PropertyFileConfigurationSource("test.properties");
        Assert.assertEquals("bar", source.getValue("foo"));
    }

    @Test
    public void testLoadFromJar() throws Exception {
        PropertyFileConfigurationSource source = new PropertyFileConfigurationSource("META-INF/maven/org.slf4j/slf4j-api/pom.properties");
        Assert.assertNotNull(source.getValue("version"));
        Assert.assertFalse(source.isSavingPossible());
    }

    @Test(expected = IOException.class)
    public void testSaveToJar() throws Exception {
        PropertyFileConfigurationSource source = new PropertyFileConfigurationSource("META-INF/maven/org.slf4j/slf4j-api/pom.properties");
        source.save("foo", "bar");
    }

    @Test
    public void testLoadFromFileSystem() throws Exception {
        File properties = File.createTempFile("filesystem-test", ".properties");
        properties.deleteOnExit();
        PropertyFileConfigurationSource source = new PropertyFileConfigurationSource(properties.getAbsolutePath());
        source.save("foo2", "bar2");
        Assert.assertEquals("bar2", source.getValue("foo2"));
        Assert.assertTrue(source.isSavingPossible());
    }
}

