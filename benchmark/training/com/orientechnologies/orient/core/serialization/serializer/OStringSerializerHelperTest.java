package com.orientechnologies.orient.core.serialization.serializer;


import OStringSerializerHelper.COLLECTION_SEPARATOR;
import OStringSerializerHelper.LIST_BEGIN;
import OStringSerializerHelper.LIST_END;
import com.orientechnologies.common.io.OIOUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class OStringSerializerHelperTest {
    @Test
    public void test() {
        final List<String> stringItems = new ArrayList<String>();
        final String text = "[\'f\\\'oo\', \'don\\\'t can\\\'t\', \"\\\"bar\\\"\", \'b\\\"a\\\'z\', \"q\\\"u\\\'x\"]";
        final int startPos = 0;
        OStringSerializerHelper.OStringSerializerHelper.getCollection(text, startPos, stringItems, LIST_BEGIN, LIST_END, COLLECTION_SEPARATOR);
        Assert.assertEquals(OIOUtils.getStringContent(stringItems.get(0)), "f'oo");
        Assert.assertEquals(OIOUtils.getStringContent(stringItems.get(1)), "don't can't");
        Assert.assertEquals(OIOUtils.getStringContent(stringItems.get(2)), "\"bar\"");
        Assert.assertEquals(OIOUtils.getStringContent(stringItems.get(3)), "b\"a\'z");
        Assert.assertEquals(OIOUtils.getStringContent(stringItems.get(4)), "q\"u\'x");
    }

    @Test
    public void testSmartTrim() {
        String input = "   t  est   ";
        Assert.assertEquals(smartTrim(input, true, true), "t  est");
        Assert.assertEquals(smartTrim(input, false, true), " t  est");
        Assert.assertEquals(smartTrim(input, true, false), "t  est ");
        Assert.assertEquals(smartTrim(input, false, false), " t  est ");
    }

    @Test
    public void testEncode() {
        Assert.assertEquals(encode("test"), "test");
        Assert.assertEquals(encode("\"test\""), "\\\"test\\\"");
        Assert.assertEquals(encode("\\test\\"), "\\\\test\\\\");
        Assert.assertEquals(encode("test\"test"), "test\\\"test");
        Assert.assertEquals(encode("test\\test"), "test\\\\test");
    }

    @Test
    public void testDecode() {
        Assert.assertEquals(decode("test"), "test");
        Assert.assertEquals(decode("\\\"test\\\""), "\"test\"");
        Assert.assertEquals(decode("\\\\test\\\\"), "\\test\\");
        Assert.assertEquals(decode("test\\\"test"), "test\"test");
        Assert.assertEquals(decode("test\\\\test"), "test\\test");
    }

    @Test
    public void testEncodeAndDecode() {
        String[] values = new String[]{ "test", "test\"", "test\"test", "test\\test", "test\\\\test", "test\\\\\"test", "\\\\\\\\", "\"\"\"\"", "\\\"\\\"\\\"" };
        for (String value : values) {
            String encoded = encode(value);
            String decoded = decode(encoded);
            Assert.assertEquals(decoded, value);
        }
    }

    @Test
    public void testGetMap() {
        String testText = "";
        Map<String, String> map = OStringSerializerHelper.OStringSerializerHelper.getMap(testText);
        Assert.assertNotNull(map);
        Assert.assertTrue(map.isEmpty());
        testText = "{ param1 :value1, param2 :value2}";
        // testText = "{\"param1\":\"value1\",\"param2\":\"value2\"}";
        map = OStringSerializerHelper.OStringSerializerHelper.getMap(testText);
        Assert.assertNotNull(map);
        Assert.assertFalse(map.isEmpty());
        System.out.println(map);
        System.out.println(map.keySet());
        System.out.println(map.values());
        Assert.assertEquals(map.get("param1"), "value1");
        Assert.assertEquals(map.get("param2"), "value2");
        // Following tests will be nice to support, but currently it's not supported!
        // {param1 :value1, param2 :value2}
        // {param1 : value1, param2 : value2}
        // {param1 : "value1", param2 : "value2"}
        // {"param1" : "value1", "param2" : "value2"}
        // {param1 : "value1\\value1", param2 : "value2\\value2"}
    }

    @Test
    public void testIndexOf() {
        String testString = "This is my test string";
        Assert.assertEquals(indexOf(testString, 0, 'T'), 0);
        Assert.assertEquals(indexOf(testString, 0, 'h'), 1);
        Assert.assertEquals(indexOf(testString, 0, 'i'), 2);
        Assert.assertEquals(indexOf(testString, 0, 'h', 'i'), 1);
        Assert.assertEquals(indexOf(testString, 2, 'i'), 2);
        Assert.assertEquals(indexOf(testString, 3, 'i'), 5);
    }

    @Test
    public void testSmartSplit() {
        String testString = "a, b, c, d";
        List<String> splitted = smartSplit(testString, ',');
        Assert.assertEquals(splitted.get(0), "a");
        Assert.assertEquals(splitted.get(1), " b");
        Assert.assertEquals(splitted.get(2), " c");
        Assert.assertEquals(splitted.get(3), " d");
        splitted = smartSplit(testString, ',', ' ');
        Assert.assertEquals(splitted.get(0), "a");
        Assert.assertEquals(splitted.get(1), "b");
        Assert.assertEquals(splitted.get(2), "c");
        Assert.assertEquals(splitted.get(3), "d");
        splitted = smartSplit(testString, ',', ' ', 'c');
        Assert.assertEquals(splitted.get(0), "a");
        Assert.assertEquals(splitted.get(1), "b");
        Assert.assertEquals(splitted.get(2), "");
        Assert.assertEquals(splitted.get(3), "d");
        testString = "a test, b test, c test, d test";
        splitted = smartSplit(testString, ',', ' ');
        Assert.assertEquals(splitted.get(0), "atest");
        Assert.assertEquals(splitted.get(1), "btest");
        Assert.assertEquals(splitted.get(2), "ctest");
        Assert.assertEquals(splitted.get(3), "dtest");
    }

    @Test
    public void testGetLowerIndexOfKeywords() {
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("from", 0, "from"), 0);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select from", 0, "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select from foo", 0, "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select out[' from '] from foo", 0, "from"), 21);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select from", 7, "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select from foo", 7, "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select from", 8, "from"), (-1));
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select from foo", 8, "from"), (-1));
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select\tfrom", 0, "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select\tfrom\tfoo", 0, "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select\tout[\' from \']\tfrom\tfoo", 0, "from"), 21);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select\nfrom", 0, "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select\nfrom\nfoo", 0, "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select\nout[\' from \']\nfrom\nfoo", 0, "from"), 21);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select from", 0, "let", "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select from foo", 0, "let", "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select out[' from '] from foo", 0, "let", "from"), 21);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select from", 0, "let", "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select from foo", 0, "let", "from"), 7);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select out[' from '] from foo let a = 1", 0, "let", "from"), 21);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select out[' from '] from foo let a = 1", 0, "from", "let"), 21);
        Assert.assertEquals(OStringSerializerHelper.OStringSerializerHelper.getLowerIndexOfKeywords("select (select from foo) as bar from foo", 0, "let", "from"), 32);
    }
}

