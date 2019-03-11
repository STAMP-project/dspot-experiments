package org.bukkit.configuration;


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.Assert;
import org.junit.Test;


public abstract class ConfigurationSectionTest {
    @Test
    public void testGetKeys() {
        ConfigurationSection section = getConfigurationSection();
        section.set("key", true);
        section.set("subsection.subkey", true);
        section.set("subsection.subkey2", true);
        section.set("subsection.subsubsection.key", true);
        section.set("key2", true);
        section.set("42", true);
        Assert.assertArrayEquals(new String[]{ "key", "subsection", "key2", "42" }, section.getKeys(false).toArray());
        Assert.assertArrayEquals(new String[]{ "key", "subsection", "subsection.subkey", "subsection.subkey2", "subsection.subsubsection", "subsection.subsubsection.key", "key2", "42" }, section.getKeys(true).toArray());
        Assert.assertArrayEquals(new String[]{ "subkey", "subkey2", "subsubsection", "subsubsection.key" }, section.getConfigurationSection("subsection").getKeys(true).toArray());
    }

    @Test
    public void testGetKeysWithDefaults() {
        ConfigurationSection section = getConfigurationSection();
        section.getRoot().options().copyDefaults(true);
        section.set("key", true);
        section.addDefault("subsection.subkey", true);
        section.addDefault("subsection.subkey2", true);
        section.addDefault("subsection.subsubsection.key", true);
        section.addDefault("key2", true);
        Assert.assertArrayEquals(new String[]{ "subsection", "key2", "key" }, section.getKeys(false).toArray());
        Assert.assertArrayEquals(new String[]{ "subsection", "subsection.subkey", "subsection.subkey2", "subsection.subsubsection", "subsection.subsubsection.key", "key2", "key" }, section.getKeys(true).toArray());
        Assert.assertArrayEquals(new String[]{ "subkey", "subkey2", "subsubsection", "subsubsection.key" }, section.getConfigurationSection("subsection").getKeys(true).toArray());
    }

    @Test
    public void testGetValues() {
        ConfigurationSection section = getConfigurationSection();
        section.set("bool", true);
        section.set("subsection.string", "test");
        section.set("subsection.long", Long.MAX_VALUE);
        section.set("int", 42);
        Map<String, Object> shallowValues = section.getValues(false);
        Assert.assertArrayEquals(new String[]{ "bool", "subsection", "int" }, shallowValues.keySet().toArray());
        Assert.assertArrayEquals(new Object[]{ true, section.getConfigurationSection("subsection"), 42 }, shallowValues.values().toArray());
        Map<String, Object> deepValues = section.getValues(true);
        Assert.assertArrayEquals(new String[]{ "bool", "subsection", "subsection.string", "subsection.long", "int" }, deepValues.keySet().toArray());
        Assert.assertArrayEquals(new Object[]{ true, section.getConfigurationSection("subsection"), "test", Long.MAX_VALUE, 42 }, deepValues.values().toArray());
    }

    @Test
    public void testGetValuesWithDefaults() {
        ConfigurationSection section = getConfigurationSection();
        section.getRoot().options().copyDefaults(true);
        section.set("bool", true);
        section.set("subsection.string", "test");
        section.addDefault("subsection.long", Long.MAX_VALUE);
        section.addDefault("int", 42);
        Map<String, Object> shallowValues = section.getValues(false);
        Assert.assertArrayEquals(new String[]{ "subsection", "int", "bool" }, shallowValues.keySet().toArray());
        Assert.assertArrayEquals(new Object[]{ section.getConfigurationSection("subsection"), 42, true }, shallowValues.values().toArray());
        Map<String, Object> deepValues = section.getValues(true);
        Assert.assertArrayEquals(new String[]{ "subsection", "subsection.long", "int", "bool", "subsection.string" }, deepValues.keySet().toArray());
        Assert.assertArrayEquals(new Object[]{ section.getConfigurationSection("subsection"), Long.MAX_VALUE, 42, true, "test" }, deepValues.values().toArray());
    }

    @Test
    public void testContains() {
        ConfigurationSection section = getConfigurationSection();
        section.set("exists", true);
        Assert.assertTrue(section.contains("exists"));
        Assert.assertFalse(section.contains("doesnt-exist"));
    }

    @Test
    public void testIsSet() {
        ConfigurationSection section = getConfigurationSection();
        section.set("notDefault", true);
        section.getRoot().addDefault("default", true);
        section.getRoot().addDefault("defaultAndSet", true);
        section.set("defaultAndSet", true);
        Assert.assertTrue(section.isSet("notDefault"));
        Assert.assertFalse(section.isSet("default"));
        Assert.assertTrue(section.isSet("defaultAndSet"));
    }

    @Test
    public void testGetCurrentPath() {
        ConfigurationSection section = getConfigurationSection();
        Assert.assertEquals(section.getName(), section.getCurrentPath());
    }

    @Test
    public void testGetName() {
        ConfigurationSection section = getConfigurationSection().createSection("subsection");
        Assert.assertEquals("subsection", section.getName());
        Assert.assertEquals("", section.getRoot().getName());
    }

    @Test
    public void testGetRoot() {
        ConfigurationSection section = getConfigurationSection();
        Assert.assertNotNull(section.getRoot());
        Assert.assertTrue(section.getRoot().contains(section.getCurrentPath()));
    }

    @Test
    public void testGetParent() {
        ConfigurationSection section = getConfigurationSection();
        ConfigurationSection subsection = section.createSection("subsection");
        Assert.assertEquals(section.getRoot(), section.getParent());
        Assert.assertEquals(section, subsection.getParent());
    }

    @Test
    public void testGet_String() {
        ConfigurationSection section = getConfigurationSection();
        section.set("exists", "hello world");
        Assert.assertEquals("hello world", section.getString("exists"));
        Assert.assertNull(section.getString("doesntExist"));
    }

    @Test
    public void testGet_String_Object() {
        ConfigurationSection section = getConfigurationSection();
        section.set("exists", "Set Value");
        Assert.assertEquals("Set Value", section.get("exists", "Default Value"));
        Assert.assertEquals("Default Value", section.get("doesntExist", "Default Value"));
    }

    @Test
    public void testSet() {
        ConfigurationSection section = getConfigurationSection();
        section.set("exists", "hello world");
        Assert.assertTrue(section.contains("exists"));
        Assert.assertTrue(section.isSet("exists"));
        Assert.assertEquals("hello world", section.get("exists"));
        section.set("exists", null);
        Assert.assertFalse(section.contains("exists"));
        Assert.assertFalse(section.isSet("exists"));
    }

    @Test
    public void testCreateSection() {
        ConfigurationSection section = getConfigurationSection();
        ConfigurationSection subsection = section.createSection("subsection");
        Assert.assertEquals("subsection", subsection.getName());
    }

    @Test
    public void testSectionMap() {
        ConfigurationSection config = getConfigurationSection();
        Map<String, Object> testMap = new LinkedHashMap<String, Object>();
        testMap.put("string", "Hello World");
        testMap.put("integer", 15);
        config.createSection("test.path", testMap);
        Assert.assertEquals(testMap, config.getConfigurationSection("test.path").getValues(false));
    }

    @Test
    public void testGetString_String() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        String value = "Hello World";
        section.set(key, value);
        Assert.assertEquals(value, section.getString(key));
        Assert.assertNull(section.getString("doesntExist"));
    }

    @Test
    public void testGetString_String_String() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        String value = "Hello World";
        String def = "Default Value";
        section.set(key, value);
        Assert.assertEquals(value, section.getString(key, def));
        Assert.assertEquals(def, section.getString("doesntExist", def));
    }

    @Test
    public void testIsString() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        String value = "Hello World";
        section.set(key, value);
        Assert.assertTrue(section.isString(key));
        Assert.assertFalse(section.isString("doesntExist"));
    }

    @Test
    public void testGetInt_String() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        int value = Integer.MAX_VALUE;
        section.set(key, value);
        Assert.assertEquals(value, section.getInt(key));
        Assert.assertNull(section.getString("doesntExist"));
    }

    @Test
    public void testGetInt_String_Int() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        int value = Integer.MAX_VALUE;
        int def = Integer.MIN_VALUE;
        section.set(key, value);
        Assert.assertEquals(value, section.getInt(key, def));
        Assert.assertEquals(def, section.getInt("doesntExist", def));
    }

    @Test
    public void testIsInt() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        int value = Integer.MAX_VALUE;
        section.set(key, value);
        Assert.assertTrue(section.isInt(key));
        Assert.assertFalse(section.isInt("doesntExist"));
    }

    @Test
    public void testGetBoolean_String() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        boolean value = true;
        section.set(key, value);
        Assert.assertEquals(value, section.getBoolean(key));
        Assert.assertNull(section.getString("doesntExist"));
    }

    @Test
    public void testGetBoolean_String_Boolean() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        boolean value = true;
        boolean def = false;
        section.set(key, value);
        Assert.assertEquals(value, section.getBoolean(key, def));
        Assert.assertEquals(def, section.getBoolean("doesntExist", def));
    }

    @Test
    public void testIsBoolean() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        boolean value = true;
        section.set(key, value);
        Assert.assertTrue(section.isBoolean(key));
        Assert.assertFalse(section.isBoolean("doesntExist"));
    }

    @Test
    public void testGetDouble_String() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        double value = Double.MAX_VALUE;
        section.set(key, value);
        Assert.assertEquals(value, section.getDouble(key), 1);
        Assert.assertNull(section.getString("doesntExist"));
    }

    @Test
    public void testGetDoubleFromInt() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        double value = 123;
        section.set(key, ((int) (value)));
        Assert.assertEquals(value, section.getDouble(key), 1);
        Assert.assertNull(section.getString("doesntExist"));
    }

    @Test
    public void testGetDouble_String_Double() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        double value = Double.MAX_VALUE;
        double def = Double.MIN_VALUE;
        section.set(key, value);
        Assert.assertEquals(value, section.getDouble(key, def), 1);
        Assert.assertEquals(def, section.getDouble("doesntExist", def), 1);
    }

    @Test
    public void testIsDouble() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        double value = Double.MAX_VALUE;
        section.set(key, value);
        Assert.assertTrue(section.isDouble(key));
        Assert.assertFalse(section.isDouble("doesntExist"));
    }

    @Test
    public void testGetLong_String() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        long value = Long.MAX_VALUE;
        section.set(key, value);
        Assert.assertEquals(value, section.getLong(key));
        Assert.assertNull(section.getString("doesntExist"));
    }

    @Test
    public void testGetLong_String_Long() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        long value = Long.MAX_VALUE;
        long def = Long.MIN_VALUE;
        section.set(key, value);
        Assert.assertEquals(value, section.getLong(key, def));
        Assert.assertEquals(def, section.getLong("doesntExist", def));
    }

    @Test
    public void testIsLong() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        long value = Long.MAX_VALUE;
        section.set(key, value);
        Assert.assertTrue(section.isLong(key));
        Assert.assertFalse(section.isLong("doesntExist"));
    }

    @Test
    public void testGetList_String() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("one", 1);
        map.put("two", "two");
        map.put("three", 3.14);
        List<Object> value = Arrays.asList("One", "Two", "Three", 4, "5", 6.0, true, "false", map);
        section.set(key, value);
        Assert.assertEquals(value, section.getList(key));
        Assert.assertEquals(Arrays.asList(((Object) ("One")), "Two", "Three", "4", "5", "6.0", "true", "false"), section.getStringList(key));
        Assert.assertEquals(Arrays.asList(((Object) (4)), 5, 6), section.getIntegerList(key));
        Assert.assertEquals(Arrays.asList(((Object) (true)), false), section.getBooleanList(key));
        Assert.assertEquals(Arrays.asList(((Object) (4.0)), 5.0, 6.0), section.getDoubleList(key));
        Assert.assertEquals(Arrays.asList(((Object) (4.0F)), 5.0F, 6.0F), section.getFloatList(key));
        Assert.assertEquals(Arrays.asList(((Object) (4L)), 5L, 6L), section.getLongList(key));
        Assert.assertEquals(Arrays.asList(((Object) ((byte) (4))), ((byte) (5)), ((byte) (6))), section.getByteList(key));
        Assert.assertEquals(Arrays.asList(((Object) ((short) (4))), ((short) (5)), ((short) (6))), section.getShortList(key));
        Assert.assertEquals(map, section.getMapList(key).get(0));
        Assert.assertNull(section.getString("doesntExist"));
    }

    @Test
    public void testGetList_String_List() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        List<String> value = Arrays.asList("One", "Two", "Three");
        List<String> def = Arrays.asList("A", "B", "C");
        section.set(key, value);
        Assert.assertEquals(value, section.getList(key, def));
        Assert.assertEquals(def, section.getList("doesntExist", def));
    }

    @Test
    public void testIsList() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        List<String> value = Arrays.asList("One", "Two", "Three");
        section.set(key, value);
        Assert.assertTrue(section.isList(key));
        Assert.assertFalse(section.isList("doesntExist"));
    }

    @Test
    public void testGetVector_String() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        Vector value = new Vector(Double.MIN_VALUE, Double.MAX_VALUE, 5);
        section.set(key, value);
        Assert.assertEquals(value, section.getVector(key));
        Assert.assertNull(section.getString("doesntExist"));
    }

    @Test
    public void testGetVector_String_Vector() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        Vector value = new Vector(Double.MIN_VALUE, Double.MAX_VALUE, 5);
        Vector def = new Vector(100, Double.MIN_VALUE, Double.MAX_VALUE);
        section.set(key, value);
        Assert.assertEquals(value, section.getVector(key, def));
        Assert.assertEquals(def, section.getVector("doesntExist", def));
    }

    @Test
    public void testIsVector() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        Vector value = new Vector(Double.MIN_VALUE, Double.MAX_VALUE, 5);
        section.set(key, value);
        Assert.assertTrue(section.isVector(key));
        Assert.assertFalse(section.isVector("doesntExist"));
    }

    @Test
    public void testGetItemStack_String() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        ItemStack value = new ItemStack(Material.WOOD, 50, ((short) (2)));
        section.set(key, value);
        Assert.assertEquals(value, section.getItemStack(key));
        Assert.assertNull(section.getString("doesntExist"));
    }

    @Test
    public void testGetItemStack_String_ItemStack() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        ItemStack value = new ItemStack(Material.WOOD, 50, ((short) (2)));
        ItemStack def = new ItemStack(Material.STONE, 1);
        section.set(key, value);
        Assert.assertEquals(value, section.getItemStack(key, def));
        Assert.assertEquals(def, section.getItemStack("doesntExist", def));
    }

    @Test
    public void testIsItemStack() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        ItemStack value = new ItemStack(Material.WOOD, 50, ((short) (2)));
        section.set(key, value);
        Assert.assertTrue(section.isItemStack(key));
        Assert.assertFalse(section.isItemStack("doesntExist"));
    }

    @Test
    public void testGetConfigurationSection() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        ConfigurationSection subsection = section.createSection(key);
        Assert.assertEquals(subsection, section.getConfigurationSection(key));
    }

    @Test
    public void testIsConfigurationSection() {
        ConfigurationSection section = getConfigurationSection();
        String key = "exists";
        section.createSection(key);
        Assert.assertTrue(section.isConfigurationSection(key));
        Assert.assertFalse(section.isConfigurationSection("doesntExist"));
    }

    public enum TestEnum {

        HELLO,
        WORLD,
        BANANAS;}
}

