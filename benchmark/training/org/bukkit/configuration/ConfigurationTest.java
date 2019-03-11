package org.bukkit.configuration;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public abstract class ConfigurationTest {
    /**
     * Test of addDefault method, of class Configuration.
     */
    @Test
    public void testAddDefault() {
        Configuration config = getConfig();
        Map<String, Object> values = getTestValues();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String path = entry.getKey();
            Object object = entry.getValue();
            config.addDefault(path, object);
            Assert.assertEquals(object, config.get(path));
            Assert.assertTrue(config.contains(path));
            Assert.assertFalse(config.isSet(path));
            Assert.assertTrue(config.getDefaults().isSet(path));
        }
    }

    /**
     * Test of addDefaults method, of class Configuration.
     */
    @Test
    public void testAddDefaults_Map() {
        Configuration config = getConfig();
        Map<String, Object> values = getTestValues();
        config.addDefaults(values);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String path = entry.getKey();
            Object object = entry.getValue();
            Assert.assertEquals(object, config.get(path));
            Assert.assertTrue(config.contains(path));
            Assert.assertFalse(config.isSet(path));
            Assert.assertTrue(config.getDefaults().isSet(path));
        }
    }

    /**
     * Test of addDefaults method, of class Configuration.
     */
    @Test
    public void testAddDefaults_Configuration() {
        Configuration config = getConfig();
        Map<String, Object> values = getTestValues();
        Configuration defaults = getConfig();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            defaults.set(entry.getKey(), entry.getValue());
        }
        config.addDefaults(defaults);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String path = entry.getKey();
            Object object = entry.getValue();
            Assert.assertEquals(object, config.get(path));
            Assert.assertTrue(config.contains(path));
            Assert.assertFalse(config.isSet(path));
            Assert.assertTrue(config.getDefaults().isSet(path));
        }
    }

    /**
     * Test of setDefaults method, of class Configuration.
     */
    @Test
    public void testSetDefaults() {
        Configuration config = getConfig();
        Map<String, Object> values = getTestValues();
        Configuration defaults = getConfig();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            defaults.set(entry.getKey(), entry.getValue());
        }
        config.setDefaults(defaults);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String path = entry.getKey();
            Object object = entry.getValue();
            Assert.assertEquals(object, config.get(path));
            Assert.assertTrue(config.contains(path));
            Assert.assertFalse(config.isSet(path));
            Assert.assertTrue(config.getDefaults().isSet(path));
        }
    }

    /**
     * Test creation of ConfigurationSection
     */
    @Test
    public void testCreateSection() {
        Configuration config = getConfig();
        Set<String> set = new HashSet<String>();
        set.add("this");
        set.add("this.test.sub");
        set.add("this.test");
        set.add("this.test.other");
        config.createSection("this.test.sub");
        config.createSection("this.test.other");
        Assert.assertEquals(set, config.getKeys(true));
    }

    /**
     * Test of getDefaults method, of class Configuration.
     */
    @Test
    public void testGetDefaults() {
        Configuration config = getConfig();
        Configuration defaults = getConfig();
        config.setDefaults(defaults);
        Assert.assertEquals(defaults, config.getDefaults());
    }
}

