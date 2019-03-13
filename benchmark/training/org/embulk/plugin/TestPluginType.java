package org.embulk.plugin;


import PluginSource.Type.DEFAULT;
import PluginSource.Type.MAVEN;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


public class TestPluginType {
    @Test
    public void testEquals() {
        PluginType type = PluginType.createFromStringForTesting("a");
        Assert.assertTrue((type instanceof DefaultPluginType));
        Assert.assertEquals(DEFAULT, type.getSourceType());
        Assert.assertTrue(type.equals(type));
        Assert.assertTrue(type.equals(PluginType.createFromStringForTesting("a")));
        Assert.assertFalse(type.equals(PluginType.createFromStringForTesting("b")));
    }

    @Test
    public void testMapping1() {
        HashMap<String, String> mapping = new HashMap<String, String>();
        mapping.put("source", "default");
        mapping.put("name", "c");
        PluginType type = PluginType.createFromStringMapForTesting(mapping);
        Assert.assertTrue((type instanceof DefaultPluginType));
        Assert.assertEquals(DEFAULT, type.getSourceType());
        Assert.assertTrue(type.equals(type));
        Assert.assertTrue(type.equals(PluginType.createFromStringForTesting("c")));
        Assert.assertFalse(type.equals(PluginType.createFromStringForTesting("d")));
    }

    @Test
    public void testMapping2() {
        HashMap<String, String> mapping = new HashMap<String, String>();
        mapping.put("source", "maven");
        mapping.put("name", "e");
        mapping.put("group", "org.embulk.foobar");
        mapping.put("version", "0.1.2");
        PluginType type = PluginType.createFromStringMapForTesting(mapping);
        Assert.assertTrue((type instanceof MavenPluginType));
        Assert.assertEquals(MAVEN, type.getSourceType());
        MavenPluginType mavenType = ((MavenPluginType) (type));
        Assert.assertTrue(mavenType.equals(mavenType));
        Assert.assertEquals("e", mavenType.getName());
        Assert.assertEquals("org.embulk.foobar", mavenType.getGroup());
        Assert.assertEquals("0.1.2", mavenType.getVersion());
        Assert.assertNull(mavenType.getClassifier());
        Assert.assertEquals("maven:org.embulk.foobar:e:0.1.2", mavenType.getFullName());
    }

    @Test
    public void testMappingMavenWithClassifier() {
        HashMap<String, String> mapping = new HashMap<String, String>();
        mapping.put("source", "maven");
        mapping.put("name", "e");
        mapping.put("group", "org.embulk.foobar");
        mapping.put("version", "0.1.2");
        mapping.put("classifier", "bar");
        PluginType type = PluginType.createFromStringMapForTesting(mapping);
        Assert.assertTrue((type instanceof MavenPluginType));
        Assert.assertEquals(MAVEN, type.getSourceType());
        MavenPluginType mavenType = ((MavenPluginType) (type));
        Assert.assertTrue(mavenType.equals(mavenType));
        Assert.assertEquals("e", mavenType.getName());
        Assert.assertEquals("org.embulk.foobar", mavenType.getGroup());
        Assert.assertEquals("0.1.2", mavenType.getVersion());
        Assert.assertEquals("bar", mavenType.getClassifier());
        Assert.assertEquals("maven:org.embulk.foobar:e:0.1.2:bar", mavenType.getFullName());
    }
}

