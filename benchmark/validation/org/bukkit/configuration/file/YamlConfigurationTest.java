package org.bukkit.configuration.file;


import org.junit.Assert;
import org.junit.Test;


public class YamlConfigurationTest extends FileConfigurationTest {
    @Test
    public void testSaveToStringWithIndent() {
        YamlConfiguration config = getConfig();
        config.options().indent(9);
        config.set("section.key", 1);
        String result = config.saveToString();
        String expected = "section:\n         key: 1\n";
        Assert.assertEquals(expected, result);
    }
}

