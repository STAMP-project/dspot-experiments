package com.google.gson.functional;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;
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

    public void testDefaultGregorianCalendarSerialization_literalMutationString1503() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":11,\"minute\":26,\"second\":20}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1503__8 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1503__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1503__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1503__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1503__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1503__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1503__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1503__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1503__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1503__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1503__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1503__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":11,\"minute\":26,\"second\":20}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1503__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1503__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1503__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1503__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1503__12);
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

