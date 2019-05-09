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
    public void testDateDeserializationWithPattern_literalMutationNumber9126null10039_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber9126null10039 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationString9120null10027_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MMdd";
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationString9120null10027 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add9134null10023_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add9134null10023 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add9128null10011_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add9128null10011 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull9141_failAssert0_add9817_failAssert0() throws Exception {
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
                junit.framework.TestCase.fail("testDateDeserializationWithPatternnull9141 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull9141_failAssert0_add9817 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPatternnull9141_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull9141 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add9135null9996_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add9135null9996 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_add9132null10000_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_add9132null10000 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void testDateDeserializationWithPattern_literalMutationNumber9123null10043_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPattern_literalMutationNumber9123null10043 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerializationnull767_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerializationnull767 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString728null2509_failAssert0() throws Exception {
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
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString728null2509 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString746_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString746 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString746_failAssert0_add2277_failAssert0() throws Exception {
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
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString746 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString746_failAssert0_add2277 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerializationnull767_failAssert0_add2305_failAssert0() throws Exception {
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
                junit.framework.TestCase.fail("testTimestampSerializationnull767 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerializationnull767_failAssert0_add2305 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerializationnull767_failAssert0_literalMutationString1821_failAssert0() throws Exception {
        try {
            {
                TimeZone defaultTimeZone = TimeZone.getDefault();
                TimeZone.setDefault(TimeZone.getTimeZone("UTTC"));
                Locale defaultLocale = Locale.getDefault();
                Locale.setDefault(Locale.US);
                {
                    Timestamp timestamp = new Timestamp(0L);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    String json = gson.toJson(null, Timestamp.class);
                    gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerializationnull767 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testTimestampSerializationnull767_failAssert0_literalMutationString1821 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString746_failAssert0null2572_failAssert0() throws Exception {
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
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString746 should have thrown JsonParseException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString746_failAssert0null2572 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString750_failAssert0null2560_failAssert0() throws Exception {
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
                    gson.fromJson("_{{l>^r@)C1R", Timestamp.class).getTime();
                }
                junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString750 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString750_failAssert0null2560 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_add754null2461_failAssert0() throws Exception {
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
            }
            junit.framework.TestCase.fail("testTimestampSerialization_add754null2461 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString2835null4609_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("7TC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString2835null4609 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_add2866null4539_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_add2866null4539 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString2852_failAssert0null4653_failAssert0() throws Exception {
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
                    gson.fromJson(":<e2op^cl&xZ", java.sql.Date.class).getTime();
                }
                junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString2852 should have thrown JsonSyntaxException");
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString2852_failAssert0null4653 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString2848null4633_failAssert0() throws Exception {
        try {
            TimeZone defaultTimeZone = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
            Locale defaultLocale = Locale.getDefault();
            Locale.setDefault(Locale.US);
            {
                java.sql.Date sqlDate = new java.sql.Date(0L);
                Gson gson = new GsonBuilder().setDateFormat("yy;y-MM-dd").create();
                String json = gson.toJson(null, Timestamp.class);
                gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime();
            }
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString2848null4633 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString2850_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString2850 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull2871_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerializationnull2871 should have thrown NullPointerException");
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

