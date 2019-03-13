/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.ajax;


import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.eclipse.jetty.util.DateCache;
import org.eclipse.jetty.util.ajax.JSON.Output;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JSONTest {
    String test = "\n\n\n\t\t    " + (((((((((((((("// ignore this ,a [ \" \n" + "/* and this \n") + "/* and * // this \n") + "*/") + "{ ") + "\"onehundred\" : 100  ,") + "\"small\":-0.2,") + "\"name\" : \"fred\"  ,") + "\"empty\" : {}  ,") + "\"map\" : {\"a\":-1.0e2}  ,") + "\"array\" : [\"a\",-1.0e2,[],null,true,false]  ,") + "\"w0\":{\"class\":\"org.eclipse.jetty.util.ajax.JSONTest$Woggle\",\"name\":\"woggle0\",\"nested\":{\"class\":\"org.eclipse.jetty.util.ajax.JSONTest$Woggle\",\"name\":\"woggle1\",\"nested\":null,\"number\":-101},\"number\":100},") + "\"NaN\": NaN,") + "\"undefined\": undefined,") + "}");

    @Test
    public void testToString() {
        JSON.registerConvertor(JSONTest.Gadget.class, new JSONObjectConvertor(false));
        HashMap map = new HashMap();
        HashMap obj6 = new HashMap();
        HashMap obj7 = new HashMap();
        JSONTest.Woggle w0 = new JSONTest.Woggle();
        JSONTest.Woggle w1 = new JSONTest.Woggle();
        w0.name = "woggle0";
        w0.nested = w1;
        w0.number = 100;
        w1.name = "woggle1";
        w1.nested = null;
        w1.number = -101;
        map.put("n1", null);
        map.put("n2", new Integer(2));
        map.put("n3", new Double((-3.0E-11)));
        map.put("n4", "4\n\r\t\"4");
        map.put("n5", new Object[]{ "a", new Character('b'), new Integer(3), new String[]{  }, null, Boolean.TRUE, Boolean.FALSE });
        map.put("n6", obj6);
        map.put("n7", obj7);
        map.put("n8", new int[]{ 1, 2, 3, 4 });
        map.put("n9", new JSON.Literal("[{},  [],  {}]"));
        map.put("w0", w0);
        obj7.put("x", "value");
        String s = JSON.toString(map);
        Assertions.assertTrue(((s.indexOf("\"n1\":null")) >= 0));
        Assertions.assertTrue(((s.indexOf("\"n2\":2")) >= 0));
        Assertions.assertTrue(((s.indexOf("\"n3\":-3.0E-11")) >= 0));
        Assertions.assertTrue(((s.indexOf("\"n4\":\"4\\n")) >= 0));
        Assertions.assertTrue(((s.indexOf("\"n5\":[\"a\",\"b\",")) >= 0));
        Assertions.assertTrue(((s.indexOf("\"n6\":{}")) >= 0));
        Assertions.assertTrue(((s.indexOf("\"n7\":{\"x\":\"value\"}")) >= 0));
        Assertions.assertTrue(((s.indexOf("\"n8\":[1,2,3,4]")) >= 0));
        Assertions.assertTrue(((s.indexOf("\"n9\":[{},  [],  {}]")) >= 0));
        Assertions.assertTrue(((s.indexOf("\"w0\":{\"class\":\"org.eclipse.jetty.util.ajax.JSONTest$Woggle\",\"name\":\"woggle0\",\"nested\":{\"class\":\"org.eclipse.jetty.util.ajax.JSONTest$Woggle\",\"name\":\"woggle1\",\"nested\":null,\"number\":-101},\"number\":100}")) >= 0));
        JSONTest.Gadget gadget = new JSONTest.Gadget();
        gadget.setShields(42);
        gadget.setWoggles(new JSONTest.Woggle[]{ w0, w1 });
        s = JSON.toString(new JSONTest.Gadget[]{ gadget });
        MatcherAssert.assertThat(s, Matchers.startsWith("["));
        MatcherAssert.assertThat(s, Matchers.containsString("\"modulated\":false"));
        MatcherAssert.assertThat(s, Matchers.containsString("\"shields\":42"));
        MatcherAssert.assertThat(s, Matchers.containsString("\"name\":\"woggle0\""));
        MatcherAssert.assertThat(s, Matchers.containsString("\"name\":\"woggle1\""));
    }

    @Test
    public void testParse() {
        Map map = ((Map) (JSON.parse(test)));
        Assertions.assertEquals(new Long(100), map.get("onehundred"));
        Assertions.assertEquals("fred", map.get("name"));
        Assertions.assertEquals((-0.2), map.get("small"));
        Assertions.assertTrue(map.get("array").getClass().isArray());
        Assertions.assertTrue(((map.get("w0")) instanceof JSONTest.Woggle));
        Assertions.assertTrue(((((JSONTest.Woggle) (map.get("w0"))).nested) instanceof JSONTest.Woggle));
        Assertions.assertEquals((-101), ((JSONTest.Woggle) (((JSONTest.Woggle) (map.get("w0"))).nested)).number);
        Assertions.assertTrue(map.containsKey("NaN"));
        Assertions.assertEquals(null, map.get("NaN"));
        Assertions.assertTrue(map.containsKey("undefined"));
        Assertions.assertEquals(null, map.get("undefined"));
        test = "{\"data\":{\"source\":\"15831407eqdaawf7\",\"widgetId\":\"Magnet_8\"},\"channel\":\"/magnets/moveStart\",\"connectionId\":null,\"clientId\":\"15831407eqdaawf7\"}";
        map = ((Map) (JSON.parse(test)));
    }

    @Test
    public void testToString_LineFeed() {
        Map<String, String> map = new HashMap<>();
        map.put("str", "line\nfeed");
        String jsonStr = JSON.toString(map);
        MatcherAssert.assertThat(jsonStr, Matchers.is("{\"str\":\"line\\nfeed\"}"));
    }

    @Test
    public void testToString_Tab() {
        Map<String, String> map = new HashMap<>();
        map.put("str", "tab\tchar");
        String jsonStr = JSON.toString(map);
        MatcherAssert.assertThat(jsonStr, Matchers.is("{\"str\":\"tab\\tchar\"}"));
    }

    @Test
    public void testToString_Bel() {
        Map<String, String> map = new HashMap<>();
        map.put("str", "ascii\u0007bel");
        String jsonStr = JSON.toString(map);
        MatcherAssert.assertThat(jsonStr, Matchers.is("{\"str\":\"ascii\\u0007bel\"}"));
    }

    @Test
    public void testToString_Utf8() {
        Map<String, String> map = new HashMap<>();
        map.put("str", "japanese: ??");
        String jsonStr = JSON.toString(map);
        MatcherAssert.assertThat(jsonStr, Matchers.is("{\"str\":\"japanese: \u685f\u6a4b\"}"));
    }

    @Test
    public void testToJson_Utf8_Encoded() {
        JSON jsonUnicode = new JSON() {
            @Override
            protected void escapeUnicode(Appendable buffer, char c) throws IOException {
                buffer.append(String.format("\\u%04x", ((int) (c))));
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("str", "japanese: ??");
        String jsonStr = jsonUnicode.toJSON(map);
        MatcherAssert.assertThat(jsonStr, Matchers.is("{\"str\":\"japanese: \\u685f\\u6a4b\"}"));
    }

    @Test
    public void testParse_Utf8_JsonEncoded() {
        String jsonStr = "{\"str\": \"japanese: \\u685f\\u6a4b\"}";
        Map map = ((Map) (JSON.parse(jsonStr)));
        MatcherAssert.assertThat(map.get("str"), Matchers.is("japanese: ??"));
    }

    @Test
    public void testParse_Utf8_JavaEncoded() {
        String jsonStr = "{\"str\": \"japanese: \u685f\u6a4b\"}";
        Map map = ((Map) (JSON.parse(jsonStr)));
        MatcherAssert.assertThat(map.get("str"), Matchers.is("japanese: ??"));
    }

    @Test
    public void testParse_Utf8_Raw() {
        String jsonStr = "{\"str\": \"japanese: \u685f\u6a4b\"}";
        Map map = ((Map) (JSON.parse(jsonStr)));
        MatcherAssert.assertThat(map.get("str"), Matchers.is("japanese: ??"));
    }

    @Test
    public void testParseReader() throws Exception {
        Map map = ((Map) (JSON.parse(new StringReader(test))));
        Assertions.assertEquals(new Long(100), map.get("onehundred"));
        Assertions.assertEquals("fred", map.get("name"));
        Assertions.assertTrue(map.get("array").getClass().isArray());
        Assertions.assertTrue(((map.get("w0")) instanceof JSONTest.Woggle));
        Assertions.assertTrue(((((JSONTest.Woggle) (map.get("w0"))).nested) instanceof JSONTest.Woggle));
        test = "{\"data\":{\"source\":\"15831407eqdaawf7\",\"widgetId\":\"Magnet_8\"},\"channel\":\"/magnets/moveStart\",\"connectionId\":null,\"clientId\":\"15831407eqdaawf7\"}";
        map = ((Map) (JSON.parse(test)));
    }

    @Test
    public void testStripComment() {
        String test = "\n\n\n\t\t    " + (((((((("// ignore this ,a [ \" \n" + "/* ") + "{ ") + "\"onehundred\" : 100  ,") + "\"name\" : \"fred\"  ,") + "\"empty\" : {}  ,") + "\"map\" : {\"a\":-1.0e2}  ,") + "\"array\" : [\"a\",-1.0e2,[],null,true,false]  ,") + "} */");
        Object o = JSON.parse(test, false);
        Assertions.assertTrue((o == null));
        o = JSON.parse(test, true);
        Assertions.assertTrue((o instanceof Map));
        Assertions.assertEquals("fred", ((Map) (o)).get("name"));
    }

    @Test
    public void testQuote() {
        String test = "\"abc123|\\\"|\\\\|\\/|\\b|\\f|\\n|\\r|\\t|\\uaaaa|\"";
        String result = ((String) (JSON.parse(test, false)));
        Assertions.assertEquals("abc123|\"|\\|/|\b|\f|\n|\r|\t|\uaaaa|", result);
    }

    @Test
    public void testBigDecimal() {
        Object obj = JSON.parse("1.0E7");
        Assertions.assertTrue((obj instanceof Double));
        BigDecimal bd = BigDecimal.valueOf(1.0E7);
        String string = JSON.toString(new Object[]{ bd });
        obj = Array.get(JSON.parse(string), 0);
        Assertions.assertTrue((obj instanceof Double));
    }

    @Test
    public void testZeroByte() {
        String withzero = "\u0000";
        JSON.toString(withzero);
    }

    public static class Gadget {
        private boolean modulated;

        private long shields;

        private JSONTest.Woggle[] woggles;

        /**
         *
         *
         * @return the modulated
         */
        public boolean isModulated() {
            return modulated;
        }

        /**
         *
         *
         * @param modulated
         * 		the modulated to set
         */
        public void setModulated(boolean modulated) {
            this.modulated = modulated;
        }

        /**
         *
         *
         * @return the shields
         */
        public long getShields() {
            return shields;
        }

        /**
         *
         *
         * @param shields
         * 		the shields to set
         */
        public void setShields(long shields) {
            this.shields = shields;
        }

        /**
         *
         *
         * @return the woggles
         */
        public JSONTest.Woggle[] getWoggles() {
            return woggles;
        }

        /**
         *
         *
         * @param woggles
         * 		the woggles to set
         */
        public void setWoggles(JSONTest.Woggle[] woggles) {
            this.woggles = woggles;
        }
    }

    @Test
    public void testConvertor() {
        // test case#1 - force timezone to GMT
        JSON json = new JSON();
        json.addConvertor(Date.class, new JSONDateConvertor("MM/dd/yyyy HH:mm:ss zzz", TimeZone.getTimeZone("GMT"), false));
        json.addConvertor(Object.class, new JSONObjectConvertor());
        JSONTest.Woggle w0 = new JSONTest.Woggle();
        JSONTest.Gizmo g0 = new JSONTest.Gizmo();
        w0.name = "woggle0";
        w0.nested = g0;
        w0.number = 100;
        g0.name = "woggle1";
        g0.nested = null;
        g0.number = -101;
        g0.tested = true;
        HashMap map = new HashMap();
        Date dummyDate = new Date(1);
        map.put("date", dummyDate);
        map.put("w0", w0);
        StringBuffer buf = new StringBuffer();
        json.append(buf, map);
        String js = buf.toString();
        Assertions.assertTrue(((js.indexOf("\"date\":\"01/01/1970 00:00:00 GMT\"")) >= 0));
        Assertions.assertTrue(((js.indexOf("org.eclipse.jetty.util.ajax.JSONTest$Woggle")) >= 0));
        Assertions.assertTrue(((js.indexOf("org.eclipse.jetty.util.ajax.JSONTest$Gizmo")) < 0));
        Assertions.assertTrue(((js.indexOf("\"tested\":true")) >= 0));
        // test case#3
        TimeZone tzone = TimeZone.getTimeZone("JST");
        String tzone3Letter = tzone.getDisplayName(false, TimeZone.SHORT);
        String format = "EEE MMMMM dd HH:mm:ss zzz yyyy";
        Locale l = new Locale("ja", "JP");
        if (l != null) {
            json.addConvertor(Date.class, new JSONDateConvertor(format, tzone, false, l));
            buf = new StringBuffer();
            json.append(buf, map);
            js = buf.toString();
            // assertTrue(js.indexOf("\"date\":\"\u6728 1\u6708 01 09:00:00 JST 1970\"")>=0);
            Assertions.assertTrue(((js.indexOf(" 01 09:00:00 JST 1970\"")) >= 0));
            Assertions.assertTrue(((js.indexOf("org.eclipse.jetty.util.ajax.JSONTest$Woggle")) >= 0));
            Assertions.assertTrue(((js.indexOf("org.eclipse.jetty.util.ajax.JSONTest$Gizmo")) < 0));
            Assertions.assertTrue(((js.indexOf("\"tested\":true")) >= 0));
        }
        // test case#4
        json.addConvertor(Date.class, new JSONDateConvertor(true));
        w0.nested = null;
        buf = new StringBuffer();
        json.append(buf, map);
        js = buf.toString();
        Assertions.assertTrue(((js.indexOf("\"date\":\"Thu Jan 01 00:00:00 GMT 1970\"")) < 0));
        Assertions.assertTrue(((js.indexOf("org.eclipse.jetty.util.ajax.JSONTest$Woggle")) >= 0));
        Assertions.assertTrue(((js.indexOf("org.eclipse.jetty.util.ajax.JSONTest$Gizmo")) < 0));
        map = ((HashMap) (json.parse(new JSON.StringSource(js))));
        Assertions.assertTrue(((map.get("date")) instanceof Date));
        Assertions.assertTrue(((map.get("w0")) instanceof JSONTest.Woggle));
    }

    enum Color {

        Red,
        Green,
        Blue;}

    @Test
    public void testEnumConvertor() {
        JSON json = new JSON();
        Locale l = new Locale("en", "US");
        json.addConvertor(Date.class, new JSONDateConvertor(DateCache.DEFAULT_FORMAT, TimeZone.getTimeZone("GMT"), false, l));
        json.addConvertor(Enum.class, new JSONEnumConvertor(false));
        json.addConvertor(Object.class, new JSONObjectConvertor());
        JSONTest.Woggle w0 = new JSONTest.Woggle();
        JSONTest.Gizmo g0 = new JSONTest.Gizmo();
        w0.name = "woggle0";
        w0.nested = g0;
        w0.number = 100;
        w0.other = JSONTest.Color.Blue;
        g0.name = "woggle1";
        g0.nested = null;
        g0.number = -101;
        g0.tested = true;
        g0.other = JSONTest.Color.Green;
        HashMap map = new HashMap();
        map.put("date", new Date(1));
        map.put("w0", w0);
        map.put("g0", g0);
        StringBuffer buf = new StringBuffer();
        json.append(((Appendable) (buf)), map);
        String js = buf.toString();
        Assertions.assertTrue(((js.indexOf("\"date\":\"Thu Jan 01 00:00:00 GMT 1970\"")) >= 0));
        Assertions.assertTrue(((js.indexOf("org.eclipse.jetty.util.ajax.JSONTest$Woggle")) >= 0));
        Assertions.assertTrue(((js.indexOf("org.eclipse.jetty.util.ajax.JSONTest$Gizmo")) < 0));
        Assertions.assertTrue(((js.indexOf("\"tested\":true")) >= 0));
        Assertions.assertTrue(((js.indexOf("\"Green\"")) >= 0));
        Assertions.assertTrue(((js.indexOf("\"Blue\"")) < 0));
        json.addConvertor(Date.class, new JSONDateConvertor(DateCache.DEFAULT_FORMAT, TimeZone.getTimeZone("GMT"), true, l));
        json.addConvertor(Enum.class, new JSONEnumConvertor(false));
        w0.nested = null;
        buf = new StringBuffer();
        json.append(((Appendable) (buf)), map);
        js = buf.toString();
        Assertions.assertTrue(((js.indexOf("\"date\":\"Thu Jan 01 00:00:00 GMT 1970\"")) < 0));
        Assertions.assertTrue(((js.indexOf("org.eclipse.jetty.util.ajax.JSONTest$Woggle")) >= 0));
        Assertions.assertTrue(((js.indexOf("org.eclipse.jetty.util.ajax.JSONTest$Gizmo")) < 0));
        Map map2 = ((HashMap) (json.parse(new JSON.StringSource(js))));
        Assertions.assertTrue(((map2.get("date")) instanceof Date));
        Assertions.assertTrue(((map2.get("w0")) instanceof JSONTest.Woggle));
        Assertions.assertEquals(null, ((JSONTest.Woggle) (map2.get("w0"))).getOther());
        Assertions.assertEquals(JSONTest.Color.Green.toString(), ((Map) (map2.get("g0"))).get("other"));
        json.addConvertor(Date.class, new JSONDateConvertor(DateCache.DEFAULT_FORMAT, TimeZone.getTimeZone("GMT"), true, l));
        json.addConvertor(Enum.class, new JSONEnumConvertor(true));
        buf = new StringBuffer();
        json.append(((Appendable) (buf)), map);
        js = buf.toString();
        map2 = ((HashMap) (json.parse(new JSON.StringSource(js))));
        Assertions.assertTrue(((map2.get("date")) instanceof Date));
        Assertions.assertTrue(((map2.get("w0")) instanceof JSONTest.Woggle));
        Assertions.assertEquals(null, ((JSONTest.Woggle) (map2.get("w0"))).getOther());
        Object o = ((Map) (map2.get("g0"))).get("other");
        Assertions.assertEquals(JSONTest.Color.Green, o);
    }

    public static class Gizmo {
        String name;

        JSONTest.Gizmo nested;

        long number;

        boolean tested;

        Object other;

        public String getName() {
            return name;
        }

        public JSONTest.Gizmo getNested() {
            return nested;
        }

        public long getNumber() {
            return number;
        }

        public boolean isTested() {
            return tested;
        }

        public Object getOther() {
            return other;
        }
    }

    public static class Woggle extends JSONTest.Gizmo implements JSON.Convertible {
        public Woggle() {
        }

        @Override
        public void fromJSON(Map object) {
            name = ((String) (object.get("name")));
            nested = ((JSONTest.Gizmo) (object.get("nested")));
            number = ((Number) (object.get("number"))).intValue();
        }

        @Override
        public void toJSON(Output out) {
            out.addClass(JSONTest.Woggle.class);
            out.add("name", name);
            out.add("nested", nested);
            out.add("number", number);
        }

        @Override
        public String toString() {
            return ((((name) + "<<") + (nested)) + ">>") + (number);
        }
    }
}

