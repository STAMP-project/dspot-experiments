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
    public void testDateDeserializationWithPatternnull719_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDateDeserializationWithPatternnull719 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testTimestampSerializationnull115_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerializationnull115 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testTimestampSerialization_literalMutationString94_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testTimestampSerialization_literalMutationString94 should have thrown JsonParseException");
        } catch (JsonParseException expected) {
            TestCase.assertEquals("The date should be a string value", expected.getMessage());
        }
    }

    public void testSqlDateSerializationnull275_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerializationnull275 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testSqlDateSerialization_literalMutationString254_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testSqlDateSerialization_literalMutationString254 should have thrown JsonParseException");
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

