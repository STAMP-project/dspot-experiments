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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AmplDefaultTypeAdaptersTest {
    private Gson gson;

    private TimeZone oldTimeZone;

    @Before
    public void setUp() throws Exception {
        this.oldTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
        Locale.setDefault(Locale.US);
        gson = new Gson();
    }

    @After
    public void tearDown() throws Exception {
        TimeZone.setDefault(oldTimeZone);
    }

    private static class ClassWithUrlField {
        URL url;
    }

    private void testNullSerializationAndDeserialization(Class<?> c) {
        Assert.assertEquals("null", gson.toJson(null, c));
        Assert.assertEquals(null, gson.fromJson("null", c));
    }

    @SuppressWarnings("deprecation")
    private void assertEqualsDate(Date date, int year, int month, int day) {
        Assert.assertEquals((year - 1900), date.getYear());
        Assert.assertEquals(month, date.getMonth());
        Assert.assertEquals(day, date.getDate());
    }

    @SuppressWarnings("deprecation")
    private void assertEqualsTime(Date date, int hours, int minutes, int seconds) {
        Assert.assertEquals(hours, date.getHours());
        Assert.assertEquals(minutes, date.getMinutes());
        Assert.assertEquals(seconds, date.getSeconds());
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDateDeserializationWithPattern_add48530null49046_failAssert286() throws Exception {
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
            org.junit.Assert.fail("testDateDeserializationWithPattern_add48530null49046 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDateDeserializationWithPattern_literalMutationString48513null48973_failAssert287() throws Exception {
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
            org.junit.Assert.fail("testDateDeserializationWithPattern_literalMutationString48513null48973 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDateDeserializationWithPattern_literalMutationNumber48517null49314_failAssert282() throws Exception {
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
            org.junit.Assert.fail("testDateDeserializationWithPattern_literalMutationNumber48517null49314 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDateDeserializationWithPattern_literalMutationNumber48518null48986_failAssert283() throws Exception {
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
            org.junit.Assert.fail("testDateDeserializationWithPattern_literalMutationNumber48518null48986 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDateDeserializationWithPattern_add48528null49113_failAssert284() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            org.junit.Assert.fail("testDateDeserializationWithPattern_add48528null49113 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDateDeserializationWithPattern_add48532null49422_failAssert281() throws Exception {
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
            now.getDay();
            extracted.getDay();
            org.junit.Assert.fail("testDateDeserializationWithPattern_add48532null49422 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDateDeserializationWithPattern_add48527_add49325null54901_failAssert294() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String o_testDateDeserializationWithPattern_add48527_add49325__9 = gson.toJson(now);
            String json = gson.toJson(null);
            Date o_testDateDeserializationWithPattern_add48527__11 = gson.fromJson(json, Date.class);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            org.junit.Assert.fail("testDateDeserializationWithPattern_add48527_add49325null54901 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDateDeserializationWithPattern_add48527_add49338null54947_failAssert291() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date o_testDateDeserializationWithPattern_add48527_add49338__11 = gson.fromJson(json, Date.class);
            Date o_testDateDeserializationWithPattern_add48527__11 = gson.fromJson(json, Date.class);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            org.junit.Assert.fail("testDateDeserializationWithPattern_add48527_add49338null54947 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDateDeserializationWithPatternnull48536_failAssert276() throws Exception {
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
            org.junit.Assert.fail("testDateDeserializationWithPatternnull48536 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDateDeserializationWithPattern_add48527_add49428null54962_failAssert290() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date o_testDateDeserializationWithPattern_add48527__11 = gson.fromJson(json, Date.class);
            Date o_testDateDeserializationWithPattern_add48527_add49428__14 = gson.fromJson(json, Date.class);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            org.junit.Assert.fail("testDateDeserializationWithPattern_add48527_add49428null54962 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testDateDeserializationWithPattern_add48527null49456_failAssert279() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
            Date now = new Date(1315806903103L);
            String json = gson.toJson(null);
            Date o_testDateDeserializationWithPattern_add48527__11 = gson.fromJson(json, Date.class);
            Date extracted = gson.fromJson(json, Date.class);
            now.getYear();
            extracted.getYear();
            now.getMonth();
            extracted.getMonth();
            now.getDay();
            extracted.getDay();
            org.junit.Assert.fail("testDateDeserializationWithPattern_add48527null49456 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5128_add7014_literalMutationString9277_failAssert89() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testTimestampSerialization_add5128__3 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5128_add7014__21 = gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5128_add7014_literalMutationString9277 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5135_add7056_literalMutationString13459_failAssert68() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135__18 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135_add7056__21 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                gson.fromJson("{Spt.!:w]ya`", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5135_add7056_literalMutationString13459 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_remove5138null6663_failAssert50() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_remove5138null6663 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5135_add7056_literalMutationString12339_failAssert65() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135__18 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135_add7056__21 = gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5135_add7056_literalMutationString12339 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_literalMutationString5121_failAssert26() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_literalMutationString5121 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_remove5138_literalMutationString6066_failAssert59() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_remove5138_literalMutationString6066 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5128_literalMutationString6940_failAssert42() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testTimestampSerialization_add5128__3 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5128_literalMutationString6940 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5135_literalMutationString7001_failAssert33() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135__18 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5135_literalMutationString7001 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5128_add7014_literalMutationString9738_failAssert88() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testTimestampSerialization_add5128__3 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5128_add7014__21 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5128_add7014_literalMutationString9738 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerializationnull5142_failAssert30() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerializationnull5142 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5135null7075_failAssert40() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135__18 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5135null7075 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5135_add7056_literalMutationString13129_failAssert66() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135__18 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135_add7056__21 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5135_add7056_literalMutationString13129 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5135_add7056_literalMutationString11497_failAssert64() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135__18 = gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135_add7056__21 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5135_add7056_literalMutationString11497 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5135_add7035_literalMutationString14915_failAssert62() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135_add7035__18 = gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135__18 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5135_add7035_literalMutationString14915 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5128null7043_failAssert47() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testTimestampSerialization_add5128__3 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5128null7043 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5135_add7035_literalMutationString14987_failAssert61() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135_add7035__18 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135__18 = gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5135_add7035_literalMutationString14987 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_remove5139null6833_failAssert53() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_remove5139null6833 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5135_literalMutationString6967_failAssert32() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135__18 = gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class);
                gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5135_literalMutationString6967 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testTimestampSerialization_add5135_add7035_literalMutationString15017_failAssert63() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                Timestamp timestamp = new Timestamp(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(timestamp, Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135_add7035__18 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                Timestamp o_testTimestampSerialization_add5135__18 = gson.fromJson("\"1970-01-01\"", Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", Timestamp.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testTimestampSerialization_add5135_add7035_literalMutationString15017 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_remove15572_literalMutationString16875_failAssert144() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_remove15572_literalMutationString16875 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_add15562_add17433_literalMutationString23063_failAssert153() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testSqlDateSerialization_add15562_add17433__3 = TimeZone.getTimeZone("UTC");
            TimeZone o_testSqlDateSerialization_add15562__3 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_add15562_add17433_literalMutationString23063 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_add15562null17498_failAssert114() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testSqlDateSerialization_add15562__3 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_add15562null17498 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_add15562_add17456_literalMutationString24541_failAssert151() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testSqlDateSerialization_add15562__3 = TimeZone.getTimeZone("UTC");
            TimeZone o_testSqlDateSerialization_add15562_add17456__6 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_add15562_add17456_literalMutationString24541 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_add15569null17490_failAssert128() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                java.sql.Date o_testSqlDateSerialization_add15569__18 = gson.fromJson("\"1970-01-01\"", java.sql.Date.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_add15569null17490 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_add15569_add17392_literalMutationString18944_failAssert148() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testSqlDateSerialization_add15569_add17392__3 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                java.sql.Date o_testSqlDateSerialization_add15569__18 = gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_add15569_add17392_literalMutationString18944 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_remove15572null17249_failAssert133() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_remove15572null17249 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_add15569_literalMutationString17180_failAssert118() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                java.sql.Date o_testSqlDateSerialization_add15569__18 = gson.fromJson("\"1970-01-01\"", java.sql.Date.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_add15569_literalMutationString17180 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_add15562_literalMutationString17382_failAssert105() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testSqlDateSerialization_add15562__3 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_add15562_literalMutationString17382 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_add15569_literalMutationString16850_failAssert117() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                java.sql.Date o_testSqlDateSerialization_add15569__18 = gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_add15569_literalMutationString16850 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_literalMutationString15555_failAssert98() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_literalMutationString15555 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerializationnull15576_failAssert103() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerializationnull15576 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_add15569_add17392_literalMutationString19319_failAssert149() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testSqlDateSerialization_add15569_add17392__3 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(sqlDate, Timestamp.class);
                java.sql.Date o_testSqlDateSerialization_add15569__18 = gson.fromJson("\"1970-01-01\"", java.sql.Date.class);
                gson.fromJson("{\'name3\':\'v3\'}", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_add15569_add17392_literalMutationString19319 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            Assert.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_remove15573null17156_failAssert138() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_remove15573null17156 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testSqlDateSerialization_add15569_add17392null21475_failAssert176() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone o_testSqlDateSerialization_add15569_add17392__3 = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            try {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                java.sql.Date o_testSqlDateSerialization_add15569__18 = gson.fromJson("\"1970-01-01\"", java.sql.Date.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            } finally {
                TimeZone.setDefault(defaultTimeZone);
                Locale.setDefault(defaultLocale);
            }
            org.junit.Assert.fail("testSqlDateSerialization_add15569_add17392null21475 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
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

