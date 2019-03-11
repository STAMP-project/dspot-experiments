package org.bukkit.configuration.file;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import org.bukkit.configuration.MemoryConfigurationTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public abstract class FileConfigurationTest extends MemoryConfigurationTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testSave_File() throws Exception {
        FileConfiguration config = getConfig();
        File file = testFolder.newFile("test.config");
        for (Map.Entry<String, Object> entry : getTestValues().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        config.save(file);
        Assert.assertTrue(file.isFile());
    }

    @Test
    public void testSave_String() throws Exception {
        FileConfiguration config = getConfig();
        File file = testFolder.newFile("test.config");
        for (Map.Entry<String, Object> entry : getTestValues().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        config.save(file.getAbsolutePath());
        Assert.assertTrue(file.isFile());
    }

    @Test
    public void testSaveToString() {
        FileConfiguration config = getConfig();
        for (Map.Entry<String, Object> entry : getTestValues().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        String result = config.saveToString();
        String expected = getTestValuesString();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testLoad_File() throws Exception {
        FileConfiguration config = getConfig();
        File file = testFolder.newFile("test.config");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String saved = getTestValuesString();
        Map<String, Object> values = getTestValues();
        try {
            writer.write(saved);
        } finally {
            writer.close();
        }
        config.load(file);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Assert.assertEquals(entry.getValue(), config.get(entry.getKey()));
        }
        Assert.assertEquals(values.keySet(), config.getKeys(true));
    }

    @Test
    public void testLoad_String() throws Exception {
        FileConfiguration config = getConfig();
        File file = testFolder.newFile("test.config");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String saved = getTestValuesString();
        Map<String, Object> values = getTestValues();
        try {
            writer.write(saved);
        } finally {
            writer.close();
        }
        config.load(file.getAbsolutePath());
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Assert.assertEquals(entry.getValue(), config.get(entry.getKey()));
        }
        Assert.assertEquals(values.keySet(), config.getKeys(true));
    }

    @Test
    public void testLoadFromString() throws Exception {
        FileConfiguration config = getConfig();
        Map<String, Object> values = getTestValues();
        String saved = getTestValuesString();
        config.loadFromString(saved);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Assert.assertEquals(entry.getValue(), config.get(entry.getKey()));
        }
        Assert.assertEquals(values.keySet(), config.getKeys(true));
        Assert.assertEquals(saved, config.saveToString());
    }

    @Test
    public void testSaveToStringWithHeader() {
        FileConfiguration config = getConfig();
        config.options().header(getTestHeaderInput());
        for (Map.Entry<String, Object> entry : getTestValues().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        String result = config.saveToString();
        String expected = ((getTestHeaderResult()) + "\n") + (getTestValuesString());
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testParseHeader() throws Exception {
        FileConfiguration config = getConfig();
        Map<String, Object> values = getTestValues();
        String saved = getTestValuesString();
        String header = getTestHeaderResult();
        String expected = getTestHeaderInput();
        config.loadFromString(((header + "\n") + saved));
        Assert.assertEquals(expected, config.options().header());
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Assert.assertEquals(entry.getValue(), config.get(entry.getKey()));
        }
        Assert.assertEquals(values.keySet(), config.getKeys(true));
        Assert.assertEquals(((header + "\n") + saved), config.saveToString());
    }

    @Test
    public void testCopyHeader() throws Exception {
        FileConfiguration config = getConfig();
        FileConfiguration defaults = getConfig();
        Map<String, Object> values = getTestValues();
        String saved = getTestValuesString();
        String header = getTestHeaderResult();
        String expected = getTestHeaderInput();
        defaults.loadFromString(header);
        config.loadFromString(saved);
        config.setDefaults(defaults);
        Assert.assertNull(config.options().header());
        Assert.assertEquals(expected, defaults.options().header());
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Assert.assertEquals(entry.getValue(), config.get(entry.getKey()));
        }
        Assert.assertEquals(values.keySet(), config.getKeys(true));
        Assert.assertEquals(((header + "\n") + saved), config.saveToString());
        config = getConfig();
        config.loadFromString(((getTestHeaderResult()) + saved));
        Assert.assertEquals(((getTestHeaderResult()) + saved), config.saveToString());
    }

    @Test
    public void testReloadEmptyConfig() throws Exception {
        FileConfiguration config = getConfig();
        Assert.assertEquals("", config.saveToString());
        config = getConfig();
        config.loadFromString("");
        Assert.assertEquals("", config.saveToString());
        config = getConfig();
        config.loadFromString("\n\n");// Should trim the first newlines of a header

        Assert.assertEquals("", config.saveToString());
    }
}

