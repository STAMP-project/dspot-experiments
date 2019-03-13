package org.nd4j.tools;


import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for PropertyParser
 *
 * @author gagatust
 */
public class PropertyParserTest {
    public PropertyParserTest() {
    }

    /**
     * Test of parseString method, of class PropertyParser.
     */
    @Test
    public void testParseString() {
        System.out.println("parseString");
        String expResult;
        String result;
        Properties props = new Properties();
        props.put("value1", "sTr1");
        props.put("value2", "str_2");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = "sTr1";
        result = instance.parseString("value1");
        Assert.assertEquals(expResult, result);
        expResult = "str_2";
        result = instance.parseString("value2");
        Assert.assertEquals(expResult, result);
        expResult = "";
        result = instance.parseString("empty");
        Assert.assertEquals(expResult, result);
        expResult = "abc";
        result = instance.parseString("str");
        Assert.assertEquals(expResult, result);
        expResult = "true";
        result = instance.parseString("boolean");
        Assert.assertEquals(expResult, result);
        expResult = "24.98";
        result = instance.parseString("float");
        Assert.assertEquals(expResult, result);
        expResult = "12";
        result = instance.parseString("int");
        Assert.assertEquals(expResult, result);
        expResult = "a";
        result = instance.parseString("char");
        Assert.assertEquals(expResult, result);
        try {
            instance.parseString("nonexistent");
            Assert.fail("no exception");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test of parseInt method, of class PropertyParser.
     */
    @Test
    public void testParseInt() {
        System.out.println("parseInt");
        int expResult;
        int result;
        Properties props = new Properties();
        props.put("value1", "432");
        props.put("value2", "-242");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 432;
        result = instance.parseInt("value1");
        Assert.assertEquals(expResult, result);
        expResult = -242;
        result = instance.parseInt("value2");
        Assert.assertEquals(expResult, result);
        try {
            instance.parseInt("empty");
            Assert.fail("no exception");
        } catch (NumberFormatException e) {
        }
        try {
            instance.parseInt("str");
            Assert.fail("no exception");
        } catch (NumberFormatException e) {
        }
        try {
            instance.parseInt("boolean");
            Assert.assertEquals(expResult, result);
            Assert.fail("no exception");
        } catch (NumberFormatException e) {
        }
        try {
            instance.parseInt("float");
            Assert.fail("no exception");
        } catch (NumberFormatException e) {
        }
        expResult = 12;
        result = instance.parseInt("int");
        Assert.assertEquals(expResult, result);
        try {
            instance.parseInt("char");
            Assert.fail("no exception");
        } catch (NumberFormatException e) {
        }
        try {
            expResult = 0;
            result = instance.parseInt("nonexistent");
            Assert.fail("no exception");
            Assert.assertEquals(expResult, result);
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test of parseBoolean method, of class PropertyParser.
     */
    @Test
    public void testParseBoolean() {
        System.out.println("parseBoolean");
        boolean expResult;
        boolean result;
        Properties props = new Properties();
        props.put("value1", "true");
        props.put("value2", "false");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = true;
        result = instance.parseBoolean("value1");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.parseBoolean("value2");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.parseBoolean("empty");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.parseBoolean("str");
        Assert.assertEquals(expResult, result);
        expResult = true;
        result = instance.parseBoolean("boolean");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.parseBoolean("float");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.parseBoolean("int");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.parseBoolean("char");
        Assert.assertEquals(expResult, result);
        try {
            expResult = false;
            result = instance.parseBoolean("nonexistent");
            Assert.fail("no exception");
            Assert.assertEquals(expResult, result);
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test of parseDouble method, of class PropertyParser.
     */
    @Test
    public void testParseFloat() {
        System.out.println("parseFloat");
        double expResult;
        double result;
        Properties props = new Properties();
        props.put("value1", "12345.6789");
        props.put("value2", "-9000.001");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 12345.679F;
        result = instance.parseFloat("value1");
        Assert.assertEquals(expResult, result, 0);
        expResult = -9000.001F;
        result = instance.parseFloat("value2");
        Assert.assertEquals(expResult, result, 0);
        try {
            instance.parseFloat("empty");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseFloat("str");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseFloat("boolean");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        expResult = 24.98F;
        result = instance.parseFloat("float");
        Assert.assertEquals(expResult, result, 0);
        expResult = 12.0F;
        result = instance.parseFloat("int");
        Assert.assertEquals(expResult, result, 0);
        try {
            instance.parseFloat("char");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseFloat("nonexistent");
            Assert.fail("no exception");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test of parseDouble method, of class PropertyParser.
     */
    @Test
    public void testParseDouble() {
        System.out.println("parseDouble");
        double expResult;
        double result;
        Properties props = new Properties();
        props.put("value1", "12345.6789");
        props.put("value2", "-9000.001");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 12345.6789;
        result = instance.parseDouble("value1");
        Assert.assertEquals(expResult, result, 0);
        expResult = -9000.001;
        result = instance.parseDouble("value2");
        Assert.assertEquals(expResult, result, 0);
        try {
            instance.parseDouble("empty");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseDouble("str");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseDouble("boolean");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        expResult = 24.98;
        result = instance.parseDouble("float");
        Assert.assertEquals(expResult, result, 0);
        expResult = 12;
        result = instance.parseDouble("int");
        Assert.assertEquals(expResult, result, 0);
        try {
            instance.parseDouble("char");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseDouble("nonexistent");
            Assert.fail("no exception");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test of parseLong method, of class PropertyParser.
     */
    @Test
    public void testParseLong() {
        System.out.println("parseLong");
        long expResult;
        long result;
        Properties props = new Properties();
        props.put("value1", "12345678900");
        props.put("value2", "-9000001");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 12345678900L;
        result = instance.parseLong("value1");
        Assert.assertEquals(expResult, result);
        expResult = -9000001L;
        result = instance.parseLong("value2");
        Assert.assertEquals(expResult, result);
        try {
            instance.parseLong("empty");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseLong("str");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseLong("boolean");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseLong("float");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        expResult = 12L;
        result = instance.parseLong("int");
        Assert.assertEquals(expResult, result);
        try {
            instance.parseLong("char");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseLong("nonexistent");
            Assert.fail("no exception");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test of parseChar method, of class PropertyParser.
     */
    @Test
    public void testParseChar() {
        System.out.println("parseChar");
        char expResult;
        char result;
        Properties props = new Properties();
        props.put("value1", "b");
        props.put("value2", "c");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 'b';
        result = instance.parseChar("value1");
        Assert.assertEquals(expResult, result);
        expResult = 'c';
        result = instance.parseChar("value2");
        Assert.assertEquals(expResult, result);
        try {
            instance.parseChar("empty");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseChar("str");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseChar("boolean");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseChar("float");
        } catch (IllegalArgumentException e) {
        }
        try {
            instance.parseChar("int");
        } catch (IllegalArgumentException e) {
        }
        expResult = 'a';
        result = instance.parseChar("char");
        Assert.assertEquals(expResult, result);
        try {
            instance.parseChar("nonexistent");
            Assert.fail("no exception");
            Assert.assertEquals(expResult, result);
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test of toString method, of class PropertyParser.
     */
    @Test
    public void testToString_String() {
        System.out.println("toString");
        String expResult;
        String result;
        Properties props = new Properties();
        props.put("value1", "sTr1");
        props.put("value2", "str_2");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = "sTr1";
        result = instance.toString("value1");
        Assert.assertEquals(expResult, result);
        expResult = "str_2";
        result = instance.toString("value2");
        Assert.assertEquals(expResult, result);
        expResult = "";
        result = instance.toString("empty");
        Assert.assertEquals(expResult, result);
        expResult = "abc";
        result = instance.toString("str");
        Assert.assertEquals(expResult, result);
        expResult = "true";
        result = instance.toString("boolean");
        Assert.assertEquals(expResult, result);
        expResult = "24.98";
        result = instance.toString("float");
        Assert.assertEquals(expResult, result);
        expResult = "12";
        result = instance.toString("int");
        Assert.assertEquals(expResult, result);
        expResult = "a";
        result = instance.toString("char");
        Assert.assertEquals(expResult, result);
        expResult = "";
        result = instance.toString("nonexistent");
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of toInt method, of class PropertyParser.
     */
    @Test
    public void testToInt_String() {
        System.out.println("toInt");
        int expResult;
        int result;
        Properties props = new Properties();
        props.put("value1", "123");
        props.put("value2", "-54");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 123;
        result = instance.toInt("value1");
        Assert.assertEquals(expResult, result);
        expResult = -54;
        result = instance.toInt("value2");
        Assert.assertEquals(expResult, result);
        expResult = 0;
        result = instance.toInt("empty");
        Assert.assertEquals(expResult, result);
        expResult = 0;
        result = instance.toInt("str");
        Assert.assertEquals(expResult, result);
        expResult = 0;
        result = instance.toInt("boolean");
        Assert.assertEquals(expResult, result);
        expResult = 0;
        result = instance.toInt("float");
        Assert.assertEquals(expResult, result);
        expResult = 12;
        result = instance.toInt("int");
        Assert.assertEquals(expResult, result);
        expResult = 0;
        result = instance.toInt("char");
        Assert.assertEquals(expResult, result);
        expResult = 0;
        result = instance.toInt("nonexistent");
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of toBoolean method, of class PropertyParser.
     */
    @Test
    public void testToBoolean_String() {
        System.out.println("toBoolean");
        boolean expResult;
        boolean result;
        Properties props = new Properties();
        props.put("value1", "true");
        props.put("value2", "false");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = true;
        result = instance.toBoolean("value1");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("value2");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("empty");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("str");
        Assert.assertEquals(expResult, result);
        expResult = true;
        result = instance.toBoolean("boolean");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("float");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("int");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("char");
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("nonexistent");
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of toDouble method, of class PropertyParser.
     */
    @Test
    public void testToFloat_String() {
        System.out.println("toFloat");
        float expResult;
        float result;
        Properties props = new Properties();
        props.put("value1", "12345.6789");
        props.put("value2", "-9000.001");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 12345.679F;
        result = instance.toFloat("value1");
        Assert.assertEquals(expResult, result, 0.0F);
        expResult = -9000.001F;
        result = instance.toFloat("value2");
        Assert.assertEquals(expResult, result, 0.0F);
        expResult = 0.0F;
        result = instance.toFloat("empty");
        Assert.assertEquals(expResult, result, 0.0F);
        expResult = 0.0F;
        result = instance.toFloat("str");
        Assert.assertEquals(expResult, result, 0.0F);
        expResult = 0.0F;
        result = instance.toFloat("boolean");
        Assert.assertEquals(expResult, result, 0.0F);
        expResult = 24.98F;
        result = instance.toFloat("float");
        Assert.assertEquals(expResult, result, 0.0F);
        expResult = 12.0F;
        result = instance.toFloat("int");
        Assert.assertEquals(expResult, result, 0.0F);
        expResult = 0.0F;
        result = instance.toFloat("char");
        Assert.assertEquals(expResult, result, 0.0F);
        expResult = 0.0F;
        result = instance.toFloat("nonexistent");
        Assert.assertEquals(expResult, result, 0.0F);
    }

    /**
     * Test of toDouble method, of class PropertyParser.
     */
    @Test
    public void testToDouble_String() {
        System.out.println("toDouble");
        double expResult;
        double result;
        Properties props = new Properties();
        props.put("value1", "12345.6789");
        props.put("value2", "-9000.001");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 12345.6789;
        result = instance.toDouble("value1");
        Assert.assertEquals(expResult, result, 0);
        expResult = -9000.001;
        result = instance.toDouble("value2");
        Assert.assertEquals(expResult, result, 0);
        expResult = 0;
        result = instance.toDouble("empty");
        Assert.assertEquals(expResult, result, 0);
        expResult = 0;
        result = instance.toDouble("str");
        Assert.assertEquals(expResult, result, 0);
        expResult = 0;
        result = instance.toDouble("boolean");
        Assert.assertEquals(expResult, result, 0);
        expResult = 24.98;
        result = instance.toDouble("float");
        Assert.assertEquals(expResult, result, 0);
        expResult = 12;
        result = instance.toDouble("int");
        Assert.assertEquals(expResult, result, 0);
        expResult = 0;
        result = instance.toDouble("char");
        Assert.assertEquals(expResult, result, 0);
        expResult = 0;
        result = instance.toDouble("nonexistent");
        Assert.assertEquals(expResult, result, 0);
    }

    /**
     * Test of toLong method, of class PropertyParser.
     */
    @Test
    public void testToLong_String() {
        System.out.println("toLong");
        long expResult;
        long result;
        Properties props = new Properties();
        props.put("value1", "12345678900");
        props.put("value2", "-9000001");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 12345678900L;
        result = instance.toLong("value1");
        Assert.assertEquals(expResult, result);
        expResult = -9000001L;
        result = instance.toLong("value2");
        Assert.assertEquals(expResult, result);
        expResult = 0L;
        result = instance.toLong("empty");
        Assert.assertEquals(expResult, result);
        expResult = 0L;
        result = instance.toLong("str");
        Assert.assertEquals(expResult, result);
        expResult = 0L;
        result = instance.toLong("boolean");
        Assert.assertEquals(expResult, result);
        expResult = 0L;
        result = instance.toLong("float");
        Assert.assertEquals(expResult, result);
        expResult = 12L;
        result = instance.toLong("int");
        Assert.assertEquals(expResult, result);
        expResult = 0L;
        result = instance.toLong("char");
        Assert.assertEquals(expResult, result);
        expResult = 0L;
        result = instance.toLong("nonexistent");
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of toChar method, of class PropertyParser.
     */
    @Test
    public void testToChar_String() {
        System.out.println("toChar");
        char expResult;
        char result;
        Properties props = new Properties();
        props.put("value1", "f");
        props.put("value2", "w");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 'f';
        result = instance.toChar("value1");
        Assert.assertEquals(expResult, result);
        expResult = 'w';
        result = instance.toChar("value2");
        Assert.assertEquals(expResult, result);
        expResult = '\u0000';
        result = instance.toChar("empty");
        Assert.assertEquals(expResult, result);
        expResult = '\u0000';
        result = instance.toChar("str");
        Assert.assertEquals(expResult, result);
        expResult = '\u0000';
        result = instance.toChar("boolean");
        Assert.assertEquals(expResult, result);
        expResult = '\u0000';
        result = instance.toChar("float");
        Assert.assertEquals(expResult, result);
        expResult = '\u0000';
        result = instance.toChar("int");
        Assert.assertEquals(expResult, result);
        expResult = 'a';
        result = instance.toChar("char");
        Assert.assertEquals(expResult, result);
        expResult = '\u0000';
        result = instance.toChar("nonexistent");
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class PropertyParser.
     */
    @Test
    public void testToString_String_String() {
        System.out.println("toString");
        String expResult;
        String result;
        Properties props = new Properties();
        props.put("value1", "sTr1");
        props.put("value2", "str_2");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = "sTr1";
        result = instance.toString("value1", "defStr");
        Assert.assertEquals(expResult, result);
        expResult = "str_2";
        result = instance.toString("value2", "defStr");
        Assert.assertEquals(expResult, result);
        expResult = "";
        result = instance.toString("empty", "defStr");
        Assert.assertEquals(expResult, result);
        expResult = "abc";
        result = instance.toString("str", "defStr");
        Assert.assertEquals(expResult, result);
        expResult = "true";
        result = instance.toString("boolean", "defStr");
        Assert.assertEquals(expResult, result);
        expResult = "24.98";
        result = instance.toString("float", "defStr");
        Assert.assertEquals(expResult, result);
        expResult = "12";
        result = instance.toString("int", "defStr");
        Assert.assertEquals(expResult, result);
        expResult = "a";
        result = instance.toString("char", "defStr");
        Assert.assertEquals(expResult, result);
        expResult = "defStr";
        result = instance.toString("nonexistent", "defStr");
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of toInt method, of class PropertyParser.
     */
    @Test
    public void testToInt_String_int() {
        System.out.println("toInt");
        int expResult;
        int result;
        Properties props = new Properties();
        props.put("value1", "123");
        props.put("value2", "-54");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 123;
        result = instance.toInt("value1", 17);
        Assert.assertEquals(expResult, result);
        expResult = -54;
        result = instance.toInt("value2", 17);
        Assert.assertEquals(expResult, result);
        expResult = 17;
        result = instance.toInt("empty", 17);
        Assert.assertEquals(expResult, result);
        expResult = 17;
        result = instance.toInt("str", 17);
        Assert.assertEquals(expResult, result);
        expResult = 17;
        result = instance.toInt("boolean", 17);
        Assert.assertEquals(expResult, result);
        expResult = 17;
        result = instance.toInt("float", 17);
        Assert.assertEquals(expResult, result);
        expResult = 12;
        result = instance.toInt("int", 17);
        Assert.assertEquals(expResult, result);
        expResult = 17;
        result = instance.toInt("char", 17);
        Assert.assertEquals(expResult, result);
        expResult = 17;
        result = instance.toInt("nonexistent", 17);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of toBoolean method, of class PropertyParser.
     */
    @Test
    public void testToBoolean_String_boolean() {
        System.out.println("toBoolean");
        boolean expResult;
        boolean result;
        Properties props = new Properties();
        props.put("value1", "true");
        props.put("value2", "false");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = true;
        result = instance.toBoolean("value1", true);
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("value2", true);
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("empty", true);
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("str", true);
        Assert.assertEquals(expResult, result);
        expResult = true;
        result = instance.toBoolean("boolean", true);
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("float", true);
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("int", true);
        Assert.assertEquals(expResult, result);
        expResult = false;
        result = instance.toBoolean("char", true);
        Assert.assertEquals(expResult, result);
        expResult = true;
        result = instance.toBoolean("nonexistent", true);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of toDouble method, of class PropertyParser.
     */
    @Test
    public void testToFloat_String_float() {
        System.out.println("toFloat");
        float expResult;
        float result;
        Properties props = new Properties();
        props.put("value1", "12345.6789");
        props.put("value2", "-9000.001");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 12345.679F;
        result = instance.toFloat("value1", 0.123F);
        Assert.assertEquals(expResult, result, 0);
        expResult = -9000.001F;
        result = instance.toFloat("value2", 0.123F);
        Assert.assertEquals(expResult, result, 0);
        expResult = 0.123F;
        result = instance.toFloat("empty", 0.123F);
        Assert.assertEquals(expResult, result, 0);
        expResult = 0.123F;
        result = instance.toFloat("str", 0.123F);
        Assert.assertEquals(expResult, result, 0);
        expResult = 0.123F;
        result = instance.toFloat("boolean", 0.123F);
        Assert.assertEquals(expResult, result, 0);
        expResult = 24.98F;
        result = instance.toFloat("float", 0.123F);
        Assert.assertEquals(expResult, result, 0);
        expResult = 12;
        result = instance.toFloat("int", 0.123F);
        Assert.assertEquals(expResult, result, 0);
        expResult = 0.123F;
        result = instance.toFloat("char", 0.123F);
        Assert.assertEquals(expResult, result, 0);
        expResult = 0.123F;
        result = instance.toFloat("nonexistent", 0.123F);
        Assert.assertEquals(expResult, result, 0);
    }

    /**
     * Test of toDouble method, of class PropertyParser.
     */
    @Test
    public void testToDouble_String_double() {
        System.out.println("toDouble");
        double expResult;
        double result;
        Properties props = new Properties();
        props.put("value1", "12345.6789");
        props.put("value2", "-9000.001");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 12345.6789;
        result = instance.toDouble("value1", 0.123);
        Assert.assertEquals(expResult, result, 0);
        expResult = -9000.001;
        result = instance.toDouble("value2", 0.123);
        Assert.assertEquals(expResult, result, 0);
        expResult = 0.123;
        result = instance.toDouble("empty", 0.123);
        Assert.assertEquals(expResult, result, 0);
        expResult = 0.123;
        result = instance.toDouble("str", 0.123);
        Assert.assertEquals(expResult, result, 0);
        expResult = 0.123;
        result = instance.toDouble("boolean", 0.123);
        Assert.assertEquals(expResult, result, 0);
        expResult = 24.98;
        result = instance.toDouble("float", 0.123);
        Assert.assertEquals(expResult, result, 0);
        expResult = 12;
        result = instance.toDouble("int", 0.123);
        Assert.assertEquals(expResult, result, 0);
        expResult = 0.123;
        result = instance.toDouble("char", 0.123);
        Assert.assertEquals(expResult, result, 0);
        expResult = 0.123;
        result = instance.toDouble("nonexistent", 0.123);
        Assert.assertEquals(expResult, result, 0);
    }

    /**
     * Test of toLong method, of class PropertyParser.
     */
    @Test
    public void testToLong_String_long() {
        System.out.println("toLong");
        long expResult;
        long result;
        Properties props = new Properties();
        props.put("value1", "12345678900");
        props.put("value2", "-9000001");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 12345678900L;
        result = instance.toLong("value1", 3L);
        Assert.assertEquals(expResult, result);
        expResult = -9000001L;
        result = instance.toLong("value2", 3L);
        Assert.assertEquals(expResult, result);
        expResult = 3L;
        result = instance.toLong("empty", 3L);
        Assert.assertEquals(expResult, result);
        expResult = 3L;
        result = instance.toLong("str", 3L);
        Assert.assertEquals(expResult, result);
        expResult = 3L;
        result = instance.toLong("boolean", 3L);
        Assert.assertEquals(expResult, result);
        expResult = 3L;
        result = instance.toLong("float", 3L);
        Assert.assertEquals(expResult, result);
        expResult = 12L;
        result = instance.toLong("int", 3L);
        Assert.assertEquals(expResult, result);
        expResult = 3L;
        result = instance.toLong("char", 3L);
        Assert.assertEquals(expResult, result);
        expResult = 3L;
        result = instance.toLong("nonexistent", 3L);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of toChar method, of class PropertyParser.
     */
    @Test
    public void testToChar_String_char() {
        System.out.println("toChar");
        char expResult;
        char result;
        Properties props = new Properties();
        props.put("value1", "f");
        props.put("value2", "w");
        props.put("empty", "");
        props.put("str", "abc");
        props.put("boolean", "true");
        props.put("float", "24.98");
        props.put("int", "12");
        props.put("char", "a");
        PropertyParser instance = new PropertyParser(props);
        expResult = 'f';
        result = instance.toChar("value1", 't');
        Assert.assertEquals(expResult, result);
        expResult = 'w';
        result = instance.toChar("value2", 't');
        Assert.assertEquals(expResult, result);
        expResult = 't';
        result = instance.toChar("empty", 't');
        Assert.assertEquals(expResult, result);
        expResult = 't';
        result = instance.toChar("str", 't');
        Assert.assertEquals(expResult, result);
        expResult = 't';
        result = instance.toChar("boolean", 't');
        Assert.assertEquals(expResult, result);
        expResult = 't';
        result = instance.toChar("float", 't');
        Assert.assertEquals(expResult, result);
        expResult = 't';
        result = instance.toChar("int", 't');
        Assert.assertEquals(expResult, result);
        expResult = 'a';
        result = instance.toChar("char", 't');
        Assert.assertEquals(expResult, result);
        expResult = 't';
        result = instance.toChar("nonexistent", 't');
        Assert.assertEquals(expResult, result);
    }
}

