/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows.kvp;


import KvpUtils.CQL_DELIMITER;
import KvpUtils.INNER_DELIMETER;
import KvpUtils.KEYWORD_DELIMITER;
import KvpUtils.OUTER_DELIMETER;
import KvpUtils.VALUE_DELIMITER;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.geoserver.ows.util.KvpUtils;


public class KvpUtilsTest extends TestCase {
    public void testEmptyString() {
        TestCase.assertEquals(0, KvpUtils.readFlat("").size());
    }

    public void testTrailingEmtpyStrings() {
        TestCase.assertEquals(Arrays.asList(new String[]{ "x", "", "x", "", "" }), KvpUtils.readFlat("x,,x,,"));
    }

    public void testEmtpyNestedString() {
        List result = KvpUtils.readNested("");
        TestCase.assertEquals(1, result.size());
        TestCase.assertEquals(0, ((List) (result.get(0))).size());
    }

    public void testStarNestedString() {
        List result = KvpUtils.readNested("*");
        TestCase.assertEquals(1, result.size());
        TestCase.assertEquals(0, ((List) (result.get(0))).size());
    }

    public void testWellKnownTokenizers() {
        String[] expected;
        List actual;
        expected = new String[]{ "1", "2", "3", "" };
        actual = KvpUtils.readFlat("1,2,3,", INNER_DELIMETER);
        assertKvp(expected, actual);
        expected = new String[]{ "abc", "def", "" };
        actual = KvpUtils.readFlat("(abc)(def)()", OUTER_DELIMETER);
        assertKvp(expected, actual);
        expected = new String[]{ "abc" };
        actual = KvpUtils.readFlat("(abc)", OUTER_DELIMETER);
        assertKvp(expected, actual);
        expected = new String[]{ "" };
        actual = KvpUtils.readFlat("()", OUTER_DELIMETER);
        assertKvp(expected, actual);
        expected = new String[]{ "", "A=1", "B=2", "" };
        actual = KvpUtils.readFlat(";A=1;B=2;", CQL_DELIMITER);
        assertKvp(expected, actual);
        expected = new String[]{ "ab", "cd", "ef", "" };
        actual = KvpUtils.readFlat("ab&cd&ef&", KEYWORD_DELIMITER);
        assertKvp(expected, actual);
        expected = new String[]{ "A", "1 " };
        actual = KvpUtils.readFlat("A=1 ", VALUE_DELIMITER);
        assertKvp(expected, actual);
    }

    public void testRadFlatUnkownDelimiter() {
        List actual;
        final String[] expected = new String[]{ "1", "2", "3", "" };
        actual = KvpUtils.readFlat("1^2^3^", "\\^");
        assertKvp(expected, actual);
        actual = KvpUtils.readFlat("1-2-3-", "-");
        assertKvp(expected, actual);
    }

    public void testEscapedTokens() {
        // test trivial scenarios
        List<String> actual = KvpUtils.escapedTokens("", ',');
        TestCase.assertEquals(Arrays.asList(""), actual);
        actual = KvpUtils.escapedTokens(",", ',');
        TestCase.assertEquals(Arrays.asList("", ""), actual);
        actual = KvpUtils.escapedTokens("a,b", ',');
        TestCase.assertEquals(Arrays.asList("a", "b"), actual);
        actual = KvpUtils.escapedTokens("a,b,c", ',');
        TestCase.assertEquals(Arrays.asList("a", "b", "c"), actual);
        actual = KvpUtils.escapedTokens("a,b,c", ',', 2);
        TestCase.assertEquals(Arrays.asList("a", "b,c"), actual);
        actual = KvpUtils.escapedTokens("a,b,c", ',', 1);
        TestCase.assertEquals(Arrays.asList("a,b,c"), actual);
        actual = KvpUtils.escapedTokens("a,b,c", ',', 0);
        TestCase.assertEquals(Arrays.asList("a", "b", "c"), actual);
        actual = KvpUtils.escapedTokens("a,b,c", ',', 1000);
        TestCase.assertEquals(Arrays.asList("a", "b", "c"), actual);
        // test escaped data
        actual = KvpUtils.escapedTokens("\\\\,\\\\", ',');
        TestCase.assertEquals(Arrays.asList("\\\\", "\\\\"), actual);
        actual = KvpUtils.escapedTokens("a\\,b,c", ',');
        TestCase.assertEquals(Arrays.asList("a\\,b", "c"), actual);
        actual = KvpUtils.escapedTokens("a\\,b,c,d", ',', 2);
        TestCase.assertEquals(Arrays.asList("a\\,b", "c,d"), actual);
        // test error conditions
        try {
            KvpUtils.escapedTokens(null, ',');
            TestCase.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
        }
        try {
            KvpUtils.escapedTokens("", '\\');
            TestCase.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
        }
        try {
            KvpUtils.escapedTokens("\\", '\\');
            TestCase.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
        }
    }

    public static void testUnescape() {
        // test trivial scenarios
        String actual = KvpUtils.unescape("abc");
        TestCase.assertEquals("abc", actual);
        // test escape sequences
        actual = KvpUtils.unescape("abc\\\\");
        TestCase.assertEquals("abc\\", actual);
        actual = KvpUtils.unescape("abc\\d");
        TestCase.assertEquals("abcd", actual);
        // test error conditions
        try {
            KvpUtils.unescape(null);
            TestCase.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
        }
        try {
            KvpUtils.unescape("\\");
            TestCase.fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
        }
    }

    public static void testParseQueryString() {
        Map<String, Object> kvp = KvpUtils.parseQueryString("geoserver?request=WMS&version=1.0.0&CQL_FILTER=NAME='geoserver'");
        TestCase.assertEquals(3, kvp.size());
        TestCase.assertEquals("WMS", kvp.get("request"));
        TestCase.assertEquals("1.0.0", kvp.get("version"));
        TestCase.assertEquals("NAME='geoserver'", kvp.get("CQL_FILTER"));
    }

    public static void testParseQueryStringRepeated() {
        Map<String, Object> kvp = KvpUtils.parseQueryString("geoserver?request=WMS&version=1.0.0&version=2.0.0&CQL_FILTER=NAME='geoserver'");
        TestCase.assertEquals(3, kvp.size());
        TestCase.assertEquals("WMS", kvp.get("request"));
        TestCase.assertTrue(Arrays.equals(new String[]{ "1.0.0", "2.0.0" }, ((String[]) (kvp.get("version")))));
        TestCase.assertEquals("NAME='geoserver'", kvp.get("CQL_FILTER"));
    }
}

