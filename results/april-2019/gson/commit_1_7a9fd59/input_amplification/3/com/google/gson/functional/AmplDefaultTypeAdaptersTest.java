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
    public void testDateDeserializationWithPattern_literalMutationNumber26431null27360_failAssert0_add29766_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
                new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
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
                junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber26431null27360 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber26431null27360_failAssert0_add29766 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add26437null27306_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add26437null27306 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull26450_failAssert0_add27131_failAssert0() throws Exception {
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
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull26450 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull26450_failAssert0_add27131 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add26447_remove27199null30463_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add26447_remove27199null30463 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber26435_remove27223null30479_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(2631613806206L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            now.getDay();
            extracted.getDay();
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber26435_remove27223null30479 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber26431null27360_failAssert0_literalMutationString28674_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyMyy-MM-dd";
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
                junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber26431null27360 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber26431null27360_failAssert0_literalMutationString28674 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber26431null27360_failAssert0_literalMutationString28673_failAssert0() throws Exception {
        try {
            {
                String pattern = "{\'name3\':\'v3\'}";
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
                junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber26431null27360 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber26431null27360_failAssert0_literalMutationString28673 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber26434null27368_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber26434null27368 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull26450_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull26450 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber26431null27360_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber26431null27360 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull26450_failAssert0_literalMutationNumber26827_failAssert0() throws Exception {
        try {
            {
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
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull26450 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull26450_failAssert0_literalMutationNumber26827 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add26439_add26865null30306_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            new GsonBuilder().setDateFormat(DateFormat.FULL);
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add26439_add26865null30306 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationString26428null27348_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-d";
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationString26428null27348 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber26432null27372_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber26432null27372 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_add2941null4617_failAssert0_literalMutationString6841_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("BqO"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_add2941null4617 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add2941null4617_failAssert0_literalMutationString6841 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString2932_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString2932 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString2932_failAssert0_add4462_failAssert0() throws Exception {
        try {
            {
                TimeZone.getDefault();
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
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString2932 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString2932_failAssert0_add4462 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_add2940_literalMutationString3093_failAssert0_add7719_failAssert0() throws Exception {
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
                    String json = gson.toJson(timestamp, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_add2940_literalMutationString3093 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add2940_literalMutationString3093_failAssert0_add7719 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_add2940_literalMutationString3093_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add2940_literalMutationString3093 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_add2944_remove4513null8146_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String o_testTimestampSerialization_add2944__15 = gson.toJson(null, Timestamp.class);
                String json = gson.toJson(timestamp, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add2944_remove4513null8146 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add2941null4617_failAssert0_add7786_failAssert0() throws Exception {
        try {
            {
                TimeZone.getDefault();
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_add2941null4617 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add2941null4617_failAssert0_add7786 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString2919null4694_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("U]TC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString2919null4694 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add2941null4617_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add2941null4617 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString2918null4726_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString2918null4726 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString2915_literalMutationString3708_literalMutationString5971_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone(""));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("y$yyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString2915_literalMutationString3708_literalMutationString5971 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString2932_failAssert0_literalMutationString3964_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("U}TC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(timestamp, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString2932 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString2932_failAssert0_literalMutationString3964 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerializationnull2953_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerializationnull2953 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add2940_add4037null8078_failAssert0() throws Exception {
        try {
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
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add2940_add4037null8078 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerializationnull2955null4741_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerializationnull2955null4741 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString2915_literalMutationString3708null8182_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone(""));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("y$yyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString2915_literalMutationString3708null8182 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber8600null10374_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber8600null10374 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber8600_remove10248null14087_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber8600_remove10248null14087 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add8622_remove10199null13990_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_add8622_remove10199null13990 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_remove8627null10322_failAssert0_add13632_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale.getDefault();
                Locale defaultLocale = Locale.getDefault();
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_remove8627null10322 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_remove8627null10322_failAssert0_add13632 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add8621null10309_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String o_testSqlDateSerialization_add8621__15 = gson.toJson(null, Timestamp.class);
                String json = gson.toJson(sqlDate, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_add8621null10309 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_remove8627_remove10210null13994_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_remove8627_remove10210null13994 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString8609_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString8609 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_remove8627null10322_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_remove8627null10322 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add8625null10273_failAssert0_add13561_failAssert0() throws Exception {
        try {
            {
                TimeZone.getDefault();
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
                junit.framework.TestCase.fail("testSqlDateSerialization_add8625null10273 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_add8625null10273_failAssert0_add13561 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_remove8627null10322_failAssert0_literalMutationString12594_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone(""));
                Locale defaultLocale = Locale.getDefault();
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_remove8627null10322 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_remove8627null10322_failAssert0_literalMutationString12594 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber8597_literalMutationNumber9355null14066_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber8597_literalMutationNumber9355null14066 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString8608_failAssert0_literalMutationString9571_failAssert0() throws Exception {
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
                    gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString8608 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString8608_failAssert0_literalMutationString9571 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add8625null10273_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_add8625null10273 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull8630_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerializationnull8630 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString8607null10382_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString8607null10382 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString8609_failAssert0_add10134_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                Locale.getDefault();
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    java.sql.Date sqlDate = new java.sql.Date(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(sqlDate, Timestamp.class);
                    gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString8609 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString8609_failAssert0_add10134 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationNumber8598_add9933null13922_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(-1L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                java.sql.Date o_testSqlDateSerialization_literalMutationNumber8598_add9933__18 = gson.fromJson("\"1970-01-01\"", java.sql.Date.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationNumber8598_add9933null13922 should have thrown NullPointerException");
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

