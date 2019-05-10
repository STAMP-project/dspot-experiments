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
import java.util.Calendar;
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

    public void testDefaultCalendarSerialization_literalMutationString10213() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10213__7 = json.contains("yePar");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10213__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10213__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10213__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10213__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10213__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10213__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10213__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10213__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10213__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10213__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10213__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10213__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10213__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10213__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10213__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10213__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10235() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10235__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10235__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10235__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10235__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10235__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10235__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10235__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10235__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10235__11 = json.contains(")ahC8}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10235__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10235__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10235__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10235__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10235__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10235__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10235__10);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10235__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10212() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10212__7 = json.contains("M!ov");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10212__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10212__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10212__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10212__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10212__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10212__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10212__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10212__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10212__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10212__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10212__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10212__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10212__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10212__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10212__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10212__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10234() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10234__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10234__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10234__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10234__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10234__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10234__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10234__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10234__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10234__11 = json.contains("m&inute");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10234__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10234__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10234__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10234__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10234__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10234__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10234__10);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10234__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10215() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10215__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10215__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10215__8 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10215__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10215__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10215__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10215__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10215__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10215__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10215__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10215__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10215__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10215__7);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10215__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10215__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10215__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10215__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10237() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10237__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10237__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10237__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10237__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10237__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10237__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10237__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10237__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10237__11 = json.contains("Pinute");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10237__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10237__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10237__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10237__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10237__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10237__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10237__10);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10237__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10214() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10214__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10214__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10214__8 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10214__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10214__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10214__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10214__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10214__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10214__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10214__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10214__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10214__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10214__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10214__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10214__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10214__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10214__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10236() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10236__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10236__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10236__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10236__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10236__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10236__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10236__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10236__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10236__11 = json.contains("mnute");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10236__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10236__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10236__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10236__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10236__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10236__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10236__10);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10236__11);
    }

    public void testDefaultCalendarSerialization_add10249() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_add10249__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__7);
        boolean o_testDefaultCalendarSerialization_add10249__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__8);
        boolean o_testDefaultCalendarSerialization_add10249__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__9);
        boolean o_testDefaultCalendarSerialization_add10249__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__10);
        boolean o_testDefaultCalendarSerialization_add10249__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__11);
        boolean o_testDefaultCalendarSerialization_add10249__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__12);
        boolean o_testDefaultCalendarSerialization_add10249__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10249__12);
    }

    public void testDefaultCalendarSerialization_literalMutationString10217() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10217__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10217__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10217__8 = json.contains("moth");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10217__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10217__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10217__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10217__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10217__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10217__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10217__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10217__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10217__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10217__7);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10217__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10217__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10217__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10217__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10239() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10239__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10239__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10239__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10239__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10239__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10239__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10239__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10239__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10239__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10239__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10239__12 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10239__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10239__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10239__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10239__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10239__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10239__11);
    }

    public void testDefaultCalendarSerialization_add10248() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_add10248__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__7);
        boolean o_testDefaultCalendarSerialization_add10248__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__8);
        boolean o_testDefaultCalendarSerialization_add10248__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__9);
        boolean o_testDefaultCalendarSerialization_add10248__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__10);
        boolean o_testDefaultCalendarSerialization_add10248__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__11);
        boolean o_testDefaultCalendarSerialization_add10248__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__12);
        boolean o_testDefaultCalendarSerialization_add10248__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10248__12);
    }

    public void testDefaultCalendarSerialization_literalMutationString10216() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10216__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10216__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10216__8 = json.contains("mo@th");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10216__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10216__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10216__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10216__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10216__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10216__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10216__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10216__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10216__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10216__7);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10216__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10216__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10216__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10216__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10238() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10238__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10238__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10238__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10238__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10238__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10238__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10238__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10238__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10238__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10238__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10238__12 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10238__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10238__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10238__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10238__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10238__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10238__11);
    }

    public void testDefaultCalendarSerialization_add10247() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_add10247__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__7);
        boolean o_testDefaultCalendarSerialization_add10247__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__8);
        boolean o_testDefaultCalendarSerialization_add10247__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__9);
        boolean o_testDefaultCalendarSerialization_add10247__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__10);
        boolean o_testDefaultCalendarSerialization_add10247__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__11);
        boolean o_testDefaultCalendarSerialization_add10247__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__12);
        boolean o_testDefaultCalendarSerialization_add10247__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10247__12);
    }

    public void testDefaultCalendarSerialization_literalMutationString10219() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10219__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10219__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10219__8 = json.contains("monXth");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10219__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10219__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10219__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10219__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10219__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10219__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10219__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10219__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10219__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10219__7);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10219__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10219__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10219__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10219__11);
    }

    public void testDefaultCalendarSerialization_add10246() throws Exception {
        Gson gson = new GsonBuilder().create();
        Calendar.getInstance();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_add10246__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10246__8);
        boolean o_testDefaultCalendarSerialization_add10246__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10246__9);
        boolean o_testDefaultCalendarSerialization_add10246__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10246__10);
        boolean o_testDefaultCalendarSerialization_add10246__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10246__11);
        boolean o_testDefaultCalendarSerialization_add10246__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10246__12);
        boolean o_testDefaultCalendarSerialization_add10246__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10246__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10246__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10246__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10246__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10246__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10246__12);
    }

    public void testDefaultCalendarSerialization_literalMutationString10218() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10218__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10218__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10218__8 = json.contains("y7XSa");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10218__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10218__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10218__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10218__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10218__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10218__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10218__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10218__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10218__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10218__7);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10218__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10218__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10218__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10218__11);
    }

    public void testDefaultCalendarSerialization_add10245() throws Exception {
        Gson gson = new GsonBuilder().create();
        String o_testDefaultCalendarSerialization_add10245__4 = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", o_testDefaultCalendarSerialization_add10245__4);
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_add10245__9 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10245__9);
        boolean o_testDefaultCalendarSerialization_add10245__10 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10245__10);
        boolean o_testDefaultCalendarSerialization_add10245__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10245__11);
        boolean o_testDefaultCalendarSerialization_add10245__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10245__12);
        boolean o_testDefaultCalendarSerialization_add10245__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10245__13);
        boolean o_testDefaultCalendarSerialization_add10245__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10245__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", o_testDefaultCalendarSerialization_add10245__4);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10245__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10245__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10245__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10245__12);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10245__13);
    }

    public void testDefaultCalendarSerialization_literalMutationString10231() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10231__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10231__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10231__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10231__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10231__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10231__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10231__10 = json.contains("FQ:Sk7X;:");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10231__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10231__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10231__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10231__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10231__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10231__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10231__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10231__9);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10231__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10231__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10230() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10230__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10230__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10230__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10230__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10230__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10230__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10230__10 = json.contains("hour-fDay");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10230__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10230__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10230__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10230__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10230__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10230__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10230__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10230__9);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10230__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10230__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10211() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10211__7 = json.contains("yer");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10211__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10211__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10211__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10211__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10211__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10211__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10211__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10211__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10211__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10211__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10211__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10211__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10211__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10211__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10211__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10211__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10233() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10233__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10233__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10233__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10233__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10233__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10233__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10233__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10233__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10233__11 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10233__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10233__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10233__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10233__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10233__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10233__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10233__10);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10233__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10210() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10210__7 = json.contains("y=ar");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10210__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10210__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10210__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10210__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10210__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10210__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10210__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10210__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10210__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10210__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10210__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10210__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10210__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10210__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10210__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10210__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10232() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10232__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10232__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10232__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10232__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10232__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10232__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10232__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10232__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10232__11 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10232__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10232__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10232__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10232__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10232__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10232__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10232__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10232__11);
    }

    public void testDefaultCalendarSerialization_add10244() throws Exception {
        new GsonBuilder().create();
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_add10244__9 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10244__9);
        boolean o_testDefaultCalendarSerialization_add10244__10 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10244__10);
        boolean o_testDefaultCalendarSerialization_add10244__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10244__11);
        boolean o_testDefaultCalendarSerialization_add10244__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10244__12);
        boolean o_testDefaultCalendarSerialization_add10244__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10244__13);
        boolean o_testDefaultCalendarSerialization_add10244__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10244__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10244__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10244__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10244__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10244__12);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10244__13);
    }

    public void testDefaultCalendarSerialization_literalMutationString10224() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10224__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10224__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10224__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10224__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10224__9 = json.contains("doyOfMonth");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10224__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10224__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10224__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10224__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10224__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10224__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10224__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10224__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10224__8);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10224__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10224__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10224__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10223() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10223__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10223__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10223__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10223__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10223__9 = json.contains("(tiK bu_20");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10223__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10223__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10223__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10223__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10223__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10223__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10223__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10223__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10223__8);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10223__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10223__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10223__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10226() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10226__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10226__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10226__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10226__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10226__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10226__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10226__10 = json.contains("hourOfDy");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10226__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10226__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10226__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10226__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10226__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10226__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10226__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10226__9);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10226__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10226__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10225() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10225__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10225__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10225__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10225__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10225__9 = json.contains("dayOfMonvth");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10225__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10225__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10225__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10225__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10225__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10225__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10225__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10225__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10225__8);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10225__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10225__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10225__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10228() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10228__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10228__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10228__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10228__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10228__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10228__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10228__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10228__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10228__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10228__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10228__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10228__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10228__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10228__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10228__9);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10228__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10228__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10227() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10227__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10227__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10227__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10227__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10227__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10227__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10227__10 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10227__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10227__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10227__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10227__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10227__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10227__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10227__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10227__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10227__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10227__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10208() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10208__7 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10208__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10208__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10208__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10208__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10208__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10208__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10208__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10208__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10208__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10208__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10208__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10208__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10208__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10208__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10208__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10208__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10229() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10229__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10229__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10229__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10229__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10229__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10229__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10229__10 = json.contains("hourOfDMay");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10229__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10229__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10229__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10229__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10229__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10229__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10229__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10229__9);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10229__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10229__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10240() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10240__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10240__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10240__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10240__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10240__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10240__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10240__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10240__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10240__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10240__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10240__12 = json.contains("secnd");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10240__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10240__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10240__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10240__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10240__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10240__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10220() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10220__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10220__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10220__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10220__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10220__9 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10220__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10220__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10220__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10220__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10220__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10220__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10220__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10220__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10220__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10220__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10220__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10220__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10242() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10242__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10242__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10242__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10242__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10242__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10242__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10242__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10242__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10242__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10242__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10242__12 = json.contains("secNond");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10242__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10242__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10242__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10242__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10242__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10242__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10241() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10241__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10241__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10241__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10241__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10241__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10241__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10241__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10241__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10241__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10241__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10241__12 = json.contains("Pecond");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10241__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10241__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10241__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10241__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10241__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10241__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10222() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10222__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10222__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10222__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10222__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10222__9 = json.contains("daOfMonth");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10222__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10222__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10222__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10222__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10222__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10222__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10222__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10222__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10222__8);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10222__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10222__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10222__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10221() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10221__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10221__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10221__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10221__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10221__9 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10221__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10221__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10221__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10221__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10221__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10221__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10221__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10221__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10221__8);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10221__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10221__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10221__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10243() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10243__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10243__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10243__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10243__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10243__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10243__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10243__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10243__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10243__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10243__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10243__12 = json.contains("j9St<a");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10243__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10243__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10243__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10243__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10243__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10243__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString10209() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString10209__7 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10209__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString10209__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10209__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString10209__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10209__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString10209__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10209__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString10209__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10209__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString10209__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10209__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString10209__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10209__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10209__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10209__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString10209__11);
    }

    public void testDefaultCalendarSerialization_add10252() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_add10252__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__7);
        boolean o_testDefaultCalendarSerialization_add10252__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__8);
        boolean o_testDefaultCalendarSerialization_add10252__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__9);
        boolean o_testDefaultCalendarSerialization_add10252__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__10);
        boolean o_testDefaultCalendarSerialization_add10252__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__11);
        boolean o_testDefaultCalendarSerialization_add10252__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__12);
        boolean o_testDefaultCalendarSerialization_add10252__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10252__12);
    }

    public void testDefaultCalendarSerialization_add10251() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_add10251__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__7);
        boolean o_testDefaultCalendarSerialization_add10251__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__8);
        boolean o_testDefaultCalendarSerialization_add10251__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__9);
        boolean o_testDefaultCalendarSerialization_add10251__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__10);
        boolean o_testDefaultCalendarSerialization_add10251__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__11);
        boolean o_testDefaultCalendarSerialization_add10251__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__12);
        boolean o_testDefaultCalendarSerialization_add10251__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10251__12);
    }

    public void testDefaultCalendarSerialization_add10250() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        boolean o_testDefaultCalendarSerialization_add10250__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__7);
        boolean o_testDefaultCalendarSerialization_add10250__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__8);
        boolean o_testDefaultCalendarSerialization_add10250__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__9);
        boolean o_testDefaultCalendarSerialization_add10250__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__10);
        boolean o_testDefaultCalendarSerialization_add10250__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__11);
        boolean o_testDefaultCalendarSerialization_add10250__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__12);
        boolean o_testDefaultCalendarSerialization_add10250__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":12,\"minute\":6,\"second\":1}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add10250__12);
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

