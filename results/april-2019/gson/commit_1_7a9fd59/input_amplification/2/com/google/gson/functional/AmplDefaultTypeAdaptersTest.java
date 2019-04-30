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
    public void testDateDeserializationWithPattern_literalMutationString10449null11364_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationString10449null11364 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add10457null11321_failAssert0() throws Exception {
        try {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add10457null11321 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull10470_failAssert0_literalMutationNumber10820_failAssert0() throws Exception {
        try {
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
                extracted.getDay();
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull10470 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull10470_failAssert0_literalMutationNumber10820 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber10455null11372_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber10455null11372 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add10458null11336_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add10458null11336 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber10453null11376_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903102L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber10453null11376 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull10470_failAssert0_add11116_failAssert0() throws Exception {
        try {
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
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull10470 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull10470_failAssert0_add11116 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add10467null11316_failAssert0() throws Exception {
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
            extracted.getDay();
            extracted.getDay();
            now.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add10467null11316 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationString10450_failAssert0null11403_failAssert0() throws Exception {
        try {
            {
                String pattern = "yy9y-MM-dd";
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
                junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationString10450 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationString10450_failAssert0null11403 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull10470_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull10470 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull10470_failAssert0_add11120_failAssert0() throws Exception {
        try {
            {
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
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull10470 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull10470_failAssert0_add11120 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull10470_failAssert0_add11122_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
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
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull10470 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull10470_failAssert0_add11122 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber10456null11356_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber10456null11356 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationString10447null11384_failAssert0() throws Exception {
        try {
            String pattern = "yyyhy-MM-dd";
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationString10447null11384 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString1027_literalMutationString1707_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone(">DO"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1027_literalMutationString1707 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerializationnull1062_failAssert0_literalMutationString2021_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerializationnull1062 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerializationnull1062_failAssert0_literalMutationString2021 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add1055_literalMutationString1446_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add1055__17 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add1055_literalMutationString1446 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_add1053null2754_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String o_testTimestampSerialization_add1053__15 = gson.toJson(timestamp, Timestamp.class);
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add1053null2754 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString1041_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1041 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationNumber1031null2817_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationNumber1031null2817 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString1042_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                gson.fromJson("{l>^r@)C1RND", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1042 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString1042_failAssert0_literalMutationString2119_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("U@TC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(timestamp, Timestamp.class);
                    gson.fromJson("{l>^r@)C1RND", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1042 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1042_failAssert0_literalMutationString2119 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString1042_failAssert0_add2603_failAssert0() throws Exception {
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
                    gson.fromJson("{l>^r@)C1RND", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1042 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1042_failAssert0_add2603 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString1042_failAssert0null2880_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("{l>^r@)C1RND", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1042 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1042_failAssert0null2880 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString1041_failAssert0null2864_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1041 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1041_failAssert0null2864 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerializationnull1062_failAssert0_add2548_failAssert0() throws Exception {
        try {
            {
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
                junit.framework.TestCase.fail("testTimestampSerializationnull1062 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerializationnull1062_failAssert0_add2548 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add1056null2734_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerialization_add1056null2734 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString1041_failAssert0_add2554_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(timestamp, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1041 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1041_failAssert0_add2554 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_add1057_literalMutationString1596_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerialization_add1057_literalMutationString1596 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString1045null2797_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-0_1\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1045null2797 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationNumber1033null2821_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(2L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationNumber1033null2821 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_remove1058null2730_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerialization_remove1058null2730 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerializationnull1062_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerializationnull1062 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString1028null2789_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString1028null2789 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add3422null5120_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_add3422null5120 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_remove3424null5066_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_remove3424null5066 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull3428_failAssert0_literalMutationString4297_failAssert0() throws Exception {
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
                    gson.fromJson("\"1970-0d-01\"", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerializationnull3428 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerializationnull3428_failAssert0_literalMutationString4297 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add3419null5097_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String o_testSqlDateSerialization_add3419__15 = gson.toJson(sqlDate, Timestamp.class);
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_add3419null5097 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString3408_failAssert0_literalMutationString4366_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(sqlDate, Timestamp.class);
                    gson.fromJson("{\'nme3\':\'v3\'}", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString3408 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString3408_failAssert0_literalMutationString4366 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull3428_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerializationnull3428 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString3394_literalMutationString4139_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString3394_literalMutationString4139 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString3393null5137_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("zTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString3393null5137 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add3416null5106_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_add3416null5106 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString3405null5133_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-M-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString3405null5133 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString3408_failAssert0_add4891_failAssert0() throws Exception {
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
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString3408 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString3408_failAssert0_add4891 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add3415null5082_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_add3415null5082 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull3428_failAssert0_add4856_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerializationnull3428 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerializationnull3428_failAssert0_add4856 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber3398null5177_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber3398null5177 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString3408_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString3408 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull3431_literalMutationString3564_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerializationnull3431_literalMutationString3564 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
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

