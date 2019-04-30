package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;


public class AmplDefaultTypeAdaptersTest extends TestCase {
    private Gson gson;

    private TimeZone oldTimeZone;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.oldTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
        Locale.setDefault(Locale.US);
        gson = new Gson();
    }

    @Override
    protected void tearDown() throws Exception {
        TimeZone.setDefault(oldTimeZone);
        super.tearDown();
    }

    private static class ClassWithUrlField {
        URL url;
    }

    private void testNullSerializationAndDeserialization(Class<?> c) {
        TestCase.assertEquals("null", gson.toJson(null, c));
        TestCase.assertEquals(null, gson.fromJson("null", c));
    }

    @SuppressWarnings("deprecation")
    private void assertEqualsDate(Date date, int year, int month, int day) {
        TestCase.assertEquals((year - 1900), date.getYear());
        TestCase.assertEquals(month, date.getMonth());
        TestCase.assertEquals(day, date.getDate());
    }

    @SuppressWarnings("deprecation")
    private void assertEqualsTime(Date date, int hours, int minutes, int seconds) {
        TestCase.assertEquals(hours, date.getHours());
        TestCase.assertEquals(minutes, date.getMinutes());
        TestCase.assertEquals(seconds, date.getSeconds());
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add44928null45775_failAssert0_literalMutationString48379_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-M-dd";
                Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
                Date now = new Date(1315806903103L);
                String json = gson.toJson(null);
                gson.fromJson(json, Date.class);
                Date extracted = gson.fromJson(json, Date.class);
                now.getYear();
                extracted.getYear();
                now.getMonth();
                extracted.getMonth();
                now.getDay();
                extracted.getDay();
                junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44928null45775 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44928null45775_failAssert0_literalMutationString48379 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add44924_remove45655null51741_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44924_remove45655null51741 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber44922null45823_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(2631613806206L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber44922null45823 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull44937_failAssert0_add45593_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
                Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
                Date now = new Date(1315806903103L);
                String json = gson.toJson(null);
                Date extracted = gson.fromJson(json, Date.class);
                now.getYear();
                extracted.getYear();
                now.getMonth();
                extracted.getMonth();
                now.getDay();
                extracted.getDay();
                extracted.getDay();
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937_failAssert0_add45593 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull44937_failAssert0_add45592_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
                Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
                Date now = new Date(1315806903103L);
                String json = gson.toJson(null);
                Date extracted = gson.fromJson(json, Date.class);
                now.getYear();
                extracted.getYear();
                now.getMonth();
                extracted.getMonth();
                now.getDay();
                now.getDay();
                extracted.getDay();
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937_failAssert0_add45592 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add44930_remove45666null51781_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44930_remove45666null51781 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add44928null45775_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            gson.fromJson(json, Date.class);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44928null45775 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationString44916_remove45727null51745_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-!MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationString44916_remove45727null51745 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationString44913_add45556null51852_failAssert0() throws Exception {
        try {
            String pattern = "{\'name3\':\'v3\'}";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationString44913_add45556null51852 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull44937_failAssert0_add45593_failAssert0_add50645_failAssert0() throws Exception {
        try {
            {
                {
                    String pattern = "yyyy-MM-dd";
                    new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
                    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
                    Date now = new Date(1315806903103L);
                    String json = gson.toJson(null);
                    Date extracted = gson.fromJson(json, Date.class);
                    now.getYear();
                    extracted.getYear();
                    now.getMonth();
                    extracted.getMonth();
                    now.getDay();
                    extracted.getDay();
                    extracted.getDay();
                    junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937 should have thrown JsonParseException");
                }
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937_failAssert0_add45593 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937_failAssert0_add45593_failAssert0_add50645 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add44930null45798_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            extracted.getYear();
            extracted.getYear();
            now.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44930null45798 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber44919null45831_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(0L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber44919null45831 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationString44916null45835_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-!MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationString44916null45835 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull44937_failAssert0_add45592_failAssert0_add50418_failAssert0() throws Exception {
        try {
            {
                {
                    String pattern = "yyyy-MM-dd";
                    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
                    Date now = new Date(1315806903103L);
                    String json = gson.toJson(null);
                    Date extracted = gson.fromJson(json, Date.class);
                    now.getYear();
                    extracted.getYear();
                    now.getMonth();
                    extracted.getMonth();
                    now.getDay();
                    now.getDay();
                    extracted.getDay();
                    junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937 should have thrown JsonParseException");
                }
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937_failAssert0_add45592 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937_failAssert0_add45592_failAssert0_add50418 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber44921null45847_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(657903451551L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber44921null45847 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add44928null45775_failAssert0_add50596_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
                Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
                Date now = new Date(1315806903103L);
                String json = gson.toJson(null);
                gson.fromJson(json, Date.class);
                gson.fromJson(json, Date.class);
                Date extracted = gson.fromJson(json, Date.class);
                now.getYear();
                extracted.getYear();
                now.getMonth();
                extracted.getMonth();
                now.getDay();
                extracted.getDay();
                junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44928null45775 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44928null45775_failAssert0_add50596 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber44922null45823_failAssert0_add50611_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
                Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
                Date now = new Date(2631613806206L);
                String json = gson.toJson(null);
                Date extracted = gson.fromJson(json, Date.class);
                now.getYear();
                extracted.getYear();
                extracted.getYear();
                now.getMonth();
                extracted.getMonth();
                now.getDay();
                extracted.getDay();
                junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber44922null45823 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber44922null45823_failAssert0_add50611 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber44923null45855_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903104L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber44923null45855 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add44931_remove45695null51773_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            now.getMonth();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44931_remove45695null51773 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationString44917_add45485null51844_failAssert0() throws Exception {
        try {
            String pattern = "yyy-MM-dd";
            new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern);
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationString44917_add45485null51844 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull44937_failAssert0_literalMutationString45282_failAssert0() throws Exception {
        try {
            {
                String pattern = "yy:yy-MM-dd";
                Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
                Date now = new Date(1315806903103L);
                String json = gson.toJson(null);
                Date extracted = gson.fromJson(json, Date.class);
                now.getYear();
                extracted.getYear();
                now.getMonth();
                extracted.getMonth();
                now.getDay();
                extracted.getDay();
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937_failAssert0_literalMutationString45282 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull44937_failAssert0_literalMutationString45281_failAssert0() throws Exception {
        try {
            {
                String pattern = "{\'name3\':\'v3\'}";
                Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
                Date now = new Date(1315806903103L);
                String json = gson.toJson(null);
                Date extracted = gson.fromJson(json, Date.class);
                now.getYear();
                extracted.getYear();
                now.getMonth();
                extracted.getMonth();
                now.getDay();
                extracted.getDay();
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937_failAssert0_literalMutationString45281 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull44937_failAssert0_add45592_failAssert0_literalMutationNumber48216_failAssert0() throws Exception {
        try {
            {
                {
                    String pattern = "yyyy-MM-dd";
                    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
                    Date now = new Date(0L);
                    String json = gson.toJson(null);
                    Date extracted = gson.fromJson(json, Date.class);
                    now.getYear();
                    extracted.getYear();
                    now.getMonth();
                    extracted.getMonth();
                    now.getDay();
                    now.getDay();
                    extracted.getDay();
                    junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937 should have thrown JsonParseException");
                }
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937_failAssert0_add45592 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937_failAssert0_add45592_failAssert0_literalMutationNumber48216 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add44931null45814_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44931null45814 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationString44917null45827_failAssert0() throws Exception {
        try {
            String pattern = "yyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationString44917null45827 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add44925_add45394null52223_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern);
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            gson.fromJson(json, Date.class);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44925_add45394null52223 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull44937_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull44937 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add44932null45810_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            extracted.getMonth();
            extracted.getMonth();
            now.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add44932null45810 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5491_failAssert0_literalMutationString6539_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(timestamp, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5491 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5491_failAssert0_literalMutationString6539 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationNumber5482null7242_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationNumber5482null7242 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationNumber5482null7242_failAssert0_literalMutationString10583_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationNumber5482null7242 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationNumber5482null7242_failAssert0_literalMutationString10583 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5491_failAssert0null7317_failAssert0_literalMutationString10567_failAssert0() throws Exception {
        try {
            {
                {
                    TimeZone defaultTimeZone = TimeZone.getDefault();
                    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                    Locale defaultLocale = Locale.getDefault();
                    Locale.setDefault(Locale.US);
                    {
                        Timestamp timestamp = new Timestamp(0L);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                        String json = gson.toJson(null, Timestamp.class);
                        gson.fromJson("j", Timestamp.class).getTime();
                    }
                    junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5491 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5491_failAssert0null7317 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5491_failAssert0null7317_failAssert0_literalMutationString10567 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add5502_literalMutationString5838null14434_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("ZTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add5502_literalMutationString5838null14434 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add5501_add6644_literalMutationString9423_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5501_add6644__18 = gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add5501_add6644_literalMutationString9423 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5489_literalMutationString6313_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5489_literalMutationString6313 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5491_failAssert0_literalMutationString6539_failAssert0_literalMutationString10962_failAssert0() throws Exception {
        try {
            {
                {
                    TimeZone defaultTimeZone = TimeZone.getDefault();
                    TimeZone.setDefault(TimeZone.getTimeZone("U TC"));
                    Locale defaultLocale = Locale.getDefault();
                    Locale.setDefault(Locale.US);
                    {
                        Timestamp timestamp = new Timestamp(0L);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                        String json = gson.toJson(timestamp, Timestamp.class);
                        gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
                    }
                    junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5491 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5491_failAssert0_literalMutationString6539 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5491_failAssert0_literalMutationString6539_failAssert0_literalMutationString10962 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5489_literalMutationString6313_failAssert0_add13543_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyy-MM-dd").create();
                    gson.toJson(timestamp, Timestamp.class);
                    String json = gson.toJson(timestamp, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5489_literalMutationString6313 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5489_literalMutationString6313_failAssert0_add13543 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5486null7270_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("{\'name3\':\'v3\'}").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5486null7270 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerializationnull5513_failAssert0_literalMutationString6421_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy^MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerializationnull5513 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerializationnull5513_failAssert0_literalMutationString6421 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationNumber5481_add6915null14206_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(-1L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationNumber5481_add6915null14206 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerializationnull5513_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerializationnull5513 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5492_failAssert0_literalMutationString6477_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("{\'name3\':\'v3\'}"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(timestamp, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5492 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5492_failAssert0_literalMutationString6477 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerializationnull5513_failAssert0_add6970_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale.getDefault();
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerializationnull5513 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerializationnull5513_failAssert0_add6970 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add5497null7205_failAssert0() throws Exception {
        try {
            TimeZone.getDefault();
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add5497null7205 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add5505_literalMutationString5939null14423_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone(""));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add5505_literalMutationString5939null14423 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5478_remove7154null14135_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("lTC"));
            Locale defaultLocale = Locale.getDefault();
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5478_remove7154null14135 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add5501_remove7068null14187_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add5501_remove7068null14187 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add5505null7214_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add5505null7214 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add5508_remove7079null14154_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add5508_remove7079null14154 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationNumber5482null7242_failAssert0_add13154_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    gson.toJson(null, Timestamp.class);
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationNumber5482null7242 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationNumber5482null7242_failAssert0_add13154 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5479_literalMutationString6244_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5479_literalMutationString6244 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_add5503null7208null14078_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                new GsonBuilder().setDateFormat(null);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add5503null7208null14078 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5491_failAssert0_literalMutationString6539_failAssert0_add13347_failAssert0() throws Exception {
        try {
            {
                {
                    TimeZone defaultTimeZone = TimeZone.getDefault();
                    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                    Locale defaultLocale = Locale.getDefault();
                    Locale.setDefault(Locale.US);
                    {
                        Timestamp timestamp = new Timestamp(0L);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                        String json = gson.toJson(timestamp, Timestamp.class);
                        gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
                    }
                    junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5491 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5491_failAssert0_literalMutationString6539 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5491_failAssert0_literalMutationString6539_failAssert0_add13347 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5490_add6960null14518_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM.dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5490_add6960null14518 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5476_remove7115null14127_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("/j."));
            Locale defaultLocale = Locale.getDefault();
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5476_remove7115null14127 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_remove5510null7175_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_remove5510null7175 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5490null7294_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM.dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5490null7294 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5486null7270_failAssert0_add13234_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("{\'name3\':\'v3\'}").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                    gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5486null7270 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5486null7270_failAssert0_add13234 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5492_failAssert0_add7001_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(timestamp, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5492 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5492_failAssert0_add7001 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5486null7270_failAssert0_literalMutationNumber10725_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("{\'name3\':\'v3\'}").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5486null7270 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5486null7270_failAssert0_literalMutationNumber10725 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString5492_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString5492 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15236_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15236 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15222null16985_failAssert0_add23275_failAssert0() throws Exception {
        try {
            {
                TimeZone.getDefault();
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15222null16985 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15222null16985_failAssert0_add23275 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15233null16997_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-Md-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15233null16997 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15223_remove16885null23884_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("k4X"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15223_remove16885null23884 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber15227_add16605_literalMutationString19835_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber15227_add16605_literalMutationString19835 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber15224_remove16855null23791_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(1L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber15224_remove16855null23791 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15222null16985_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15222null16985 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add15247_literalMutationString15636null24085_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                new GsonBuilder().setDateFormat("yyyy-MM-dd");
                Gson gson = new GsonBuilder().setDateFormat("{\'name3\':\'v3\'}").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_add15247_literalMutationString15636null24085 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15231_literalMutationString15797null24102_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone(""));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("{\'name3\':\'v3\'}").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15231_literalMutationString15797null24102 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15240_failAssert0null17027_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("z ;=x;JRVa?b", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15240 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15240_failAssert0null17027 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber15225null17009_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(-1L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber15225null17009 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber15227null16989_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber15227null16989 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15236_failAssert0_add16775_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    new GsonBuilder().setDateFormat("yyyy-MM-dd");
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(sqlDate, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15236 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15236_failAssert0_add16775 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15236_failAssert0_add16774_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(sqlDate, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15236 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15236_failAssert0_add16774 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15222null16985_failAssert0_literalMutationString21144_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("J"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15222null16985 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15222null16985_failAssert0_literalMutationString21144 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber15228null17017_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(2L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber15228null17017 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15236_failAssert0null17051_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15236 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15236_failAssert0null17051 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull15257_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerializationnull15257 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15221null16981_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("aTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15221null16981 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber15227_literalMutationNumber15939null24051_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber15227_literalMutationNumber15939null24051 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15221_literalMutationString15906_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("aTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15221_literalMutationString15906 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15233null16997_failAssert0_add22848_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    new GsonBuilder().setDateFormat("yyyy-Md-dd").create();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-Md-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15233null16997 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15233null16997_failAssert0_add22848 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15236_failAssert0_literalMutationString16298_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-2MM-dd").create();
                    String json = gson.toJson(sqlDate, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15236 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15236_failAssert0_literalMutationString16298 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add15244_remove16843null23817_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_add15244_remove16843null23817 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull15257_failAssert0_add16702_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerializationnull15257 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerializationnull15257_failAssert0_add16702 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_remove15253null16902_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_remove15253null16902 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull15257_failAssert0_literalMutationNumber16137_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerializationnull15257 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerializationnull15257_failAssert0_literalMutationNumber16137 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber15225_literalMutationString16060_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(-1L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber15225_literalMutationString16060 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber15227_add16606null23916_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber15227_add16606null23916 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull15259_add16332null24011_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerializationnull15259_add16332null24011 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15235_failAssert0_literalMutationString16259_failAssert0null24396_failAssert0() throws Exception {
        try {
            {
                {
                    TimeZone defaultTimeZone = TimeZone.getDefault();
                    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                    Locale defaultLocale = Locale.getDefault();
                    Locale.setDefault(Locale.US);
                    {
                        java.sql.Date sqlDate = new java.sql.Date(0L);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                        String json = gson.toJson(null, Timestamp.class);
                        gson.fromJson("|", java.sql.Date.class).getTime();
                    }
                    junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15235 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15235_failAssert0_literalMutationString16259 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15235_failAssert0_literalMutationString16259_failAssert0null24396 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber15226null16969_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber15226null16969 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add15247null16935_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                new GsonBuilder().setDateFormat("yyyy-MM-dd");
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_add15247null16935 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15231null16965_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("{\'name3\':\'v3\'}").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15231null16965 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add15243null16930_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testSqlDateSerialization_add15243__3 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_add15243null16930 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15233null16997_failAssert0_literalMutationNumber20296_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(1L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-Md-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15233null16997 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15233null16997_failAssert0_literalMutationNumber20296 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_remove15253_literalMutationNumber15445null24058_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(1L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_remove15253_literalMutationNumber15445null24058 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString15220null17005_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("{\'name3\':\'v3\'}"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString15220null17005 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber15226_remove16851null24135_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber15226_remove16851null24135 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull15259null16891_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerializationnull15259null16891 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    private static class ClassWithBigDecimal {
        BigDecimal value;

        ClassWithBigDecimal(String value) {
            this.value = new BigDecimal(value);
        }

        String getExpectedJson() {
            return ("{\"value\":" + (value.toEngineeringString())) + "}";
        }
    }

    private static class ClassWithBigInteger {
        BigInteger value;

        ClassWithBigInteger(String value) {
            this.value = new BigInteger(value);
        }

        String getExpectedJson() {
            return ("{\"value\":" + (value)) + "}";
        }
    }

    @SuppressWarnings("rawtypes")
    private static class MyClassTypeAdapter extends TypeAdapter<Class> {
        @Override
        public void write(JsonWriter out, Class value) throws IOException {
            out.value(value.getName());
        }

        @Override
        public Class read(JsonReader in) throws IOException {
            String className = in.nextString();
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
        }
    }

    static class NumberAsStringAdapter extends TypeAdapter<Number> {
        private final Constructor<? extends Number> constructor;

        NumberAsStringAdapter(Class<? extends Number> type) throws Exception {
            this.constructor = type.getConstructor(String.class);
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value.toString());
        }

        @Override
        public Number read(JsonReader in) throws IOException {
            try {
                return constructor.newInstance(in.nextString());
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
    }
}

