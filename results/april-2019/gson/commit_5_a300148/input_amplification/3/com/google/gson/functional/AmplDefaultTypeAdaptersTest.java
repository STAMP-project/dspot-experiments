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

    public void testDefaultGregorianCalendarSerialization_literalMutationString104280() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__10 = json.contains("dayOfMonh");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104280__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104280__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104273_add106380() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__9 = json.contains("mo]nth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273_add106380__17 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104273_add106380__17);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104273_add106380__17);
    }

    public void testDefaultGregorianCalendarSerialization_add104305_literalMutationString104619() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104305__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__13 = json.contains("");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__14 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104284() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104284__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104284__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104284__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__11 = json.contains("4jHClHv5j");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104284__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104284__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104284__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104284__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104284__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104284__10);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104284__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104284__12);
    }

    public void testDefaultGregorianCalendarSerialization_add104306() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__8);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__9);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__10);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__11);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__12);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__13);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104283() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104283__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104283__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104283__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__11 = json.contains("hourOfD ay");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104283__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104283__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104283__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104283__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104283__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104283__10);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104283__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104283__12);
    }

    public void testDefaultGregorianCalendarSerialization_add104305() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104305__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__8);
        boolean o_testDefaultGregorianCalendarSerialization_add104305__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__9);
        boolean o_testDefaultGregorianCalendarSerialization_add104305__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__10);
        boolean o_testDefaultGregorianCalendarSerialization_add104305__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__11);
        boolean o_testDefaultGregorianCalendarSerialization_add104305__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__12);
        boolean o_testDefaultGregorianCalendarSerialization_add104305__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__13);
        boolean o_testDefaultGregorianCalendarSerialization_add104305__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104282() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104282__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104282__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104282__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__11 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104282__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104282__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104282__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104282__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104282__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104282__10);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104282__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104282__12);
    }

    public void testDefaultGregorianCalendarSerialization_add104304() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104304__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__8);
        boolean o_testDefaultGregorianCalendarSerialization_add104304__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__9);
        boolean o_testDefaultGregorianCalendarSerialization_add104304__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__10);
        boolean o_testDefaultGregorianCalendarSerialization_add104304__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__11);
        boolean o_testDefaultGregorianCalendarSerialization_add104304__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__12);
        boolean o_testDefaultGregorianCalendarSerialization_add104304__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__13);
        boolean o_testDefaultGregorianCalendarSerialization_add104304__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104281() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104281__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104281__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104281__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__11 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104281__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104281__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104281__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104281__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104281__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104281__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104281__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104281__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104265_add106612() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__8 = json.contains("yzear");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265_add106612__17 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104265_add106612__17);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104265_add106612__17);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104280_add106371() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280_add106371__14 = json.contains("dayOfMonh");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104280_add106371__14);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__10 = json.contains("dayOfMonh");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104280_add106371__14);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104280_add106372() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__10 = json.contains("dayOfMonh");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280_add106372__17 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280_add106372__17);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280_add106372__17);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104280_add106374() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__10 = json.contains("dayOfMonh");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280_add106374__23 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280_add106374__23);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104280_add106374__23);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104281_literalMutationString105065() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__11 = json.contains("");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__13 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104279_add106365() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__10 = json.contains("dayOfMo+th");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279_add106365__20 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104279_add106365__20);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104279_add106365__20);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104266_literalMutationString105979() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__8 = json.contains("yer");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__12 = json.contains("");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104277() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104277__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104277__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104277__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104277__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104277__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104277__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104277__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104277__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104277__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104277__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104277__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104276() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104276__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104276__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__10 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104276__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104276__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104276__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104276__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104276__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104276__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104276__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104276__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104276__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104275() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104275__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104275__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__10 = json.contains("vv1!2UzV/5");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104275__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104275__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104275__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104275__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104275__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104275__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104275__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104275__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104275__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104274() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104274__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__9 = json.contains("monh");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104274__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104274__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104274__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104274__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104274__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104274__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104274__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104274__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104274__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104274__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104282_literalMutationString105593() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__11 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__13 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104279() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104279__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104279__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__10 = json.contains("dayOfMo+th");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104279__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104279__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104279__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104279__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104279__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104279__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104279__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104279__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104279__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104278() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104278__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104278__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__10 = json.contains("daNyOfMonth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104278__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104278__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104278__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104278__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104278__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104278__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104278__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104278__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104278__12);
    }

    public void testDefaultGregorianCalendarSerialization_add104305_add106308() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104305_add106308__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305_add106308__8);
        boolean o_testDefaultGregorianCalendarSerialization_add104305__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__13 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_add104305__14 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104305_add106308__8);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104271_literalMutationString105882() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__13 = json.contains("secod");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104273() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104273__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__9 = json.contains("mo]nth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104273__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104273__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104273__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104273__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104273__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104273__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104273__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104273__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104273__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104273__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104274_add106388() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__9 = json.contains("monh");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274_add106388__17 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104274_add106388__17);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104274_add106388__17);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104272() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104272__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__9 = json.contains("moVth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104272__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104272__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104272__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104272__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104272__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104272__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104272__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104272__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104272__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104272__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104271() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104271__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__9 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104271__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104271__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104271__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104271__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104271__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104271__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104271__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104271__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104271__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104271__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104270() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104270__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__9 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104270__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104270__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104270__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104270__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104270__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104270__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104270__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104270__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104270__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104270__12);
    }

    public void testDefaultGregorianCalendarSerialization_add104304_literalMutationString104502() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104304__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_add104304__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_add104304__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_add104304__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_add104304__12 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_add104304__13 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_add104304__14 = json.contains("0?#]&g");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104272_add106485() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__9 = json.contains("moVth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272_add106485__20 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104272_add106485__20);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104272_add106485__20);
    }

    public void testDefaultGregorianCalendarSerialization_add104299_remove106701() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104299__10 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_add104299__11 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_add104299__12 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_add104299__13 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_add104299__14 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_add104299__15 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104275_literalMutationString105669() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__10 = json.contains("vv1!2UzV/5");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__13 = json.contains("Fn(2}^");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104283_literalMutationString105099() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__11 = json.contains("hourOfD ay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__12 = json.contains("dDqR#[");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104273_literalMutationString104907() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__9 = json.contains("mo]nth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__10 = json.contains("pjy7xrgPxF");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104273__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_add104304_add106282() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104304__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_add104304_add106282__11 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304_add106282__11);
        boolean o_testDefaultGregorianCalendarSerialization_add104304__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_add104304__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_add104304__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_add104304__12 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_add104304__13 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_add104304__14 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104304_add106282__11);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104266() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__8 = json.contains("yer");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104266__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104266__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104266__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104266__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104266__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104266__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104266__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104266__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104266__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104266__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104266__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104276_add106471() throws Exception {
        new GsonBuilder().create();
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__10 = json.contains("");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104265() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__8 = json.contains("yzear");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104265__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104265__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104265__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104265__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104265__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104265__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104265__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104265__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104265__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104265__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104265__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104264() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104264__8 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104264__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104264__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104264__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104264__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104264__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104264__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104264__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104264__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104264__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104264__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104264__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104264__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104264__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104264__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104264__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104264__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104284_literalMutationString105994() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__8 = json.contains("yeKar");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__11 = json.contains("4jHClHv5j");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104263() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104263__8 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104263__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104263__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104263__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104263__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104263__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104263__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104263__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104263__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104263__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104263__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104263__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104263__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104263__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104263__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104263__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104263__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104280_literalMutationString104886() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__10 = json.contains("dayOfMonh");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__12 = json.contains("miqute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104283_add106421() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__11 = json.contains("hourOfD ay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283_add106421__20 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104283_add106421__20);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104283_add106421__20);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104281_add106407() throws Exception {
        new GsonBuilder().create();
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__11 = json.contains("");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104281__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104283_add106420() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283_add106420__17 = json.contains("hourOfD ay");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104283_add106420__17);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__11 = json.contains("hourOfD ay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104283_add106420__17);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104269() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104269__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104269__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104269__9 = json.contains("qp@yt");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104269__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104269__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104269__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104269__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104269__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104269__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104269__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104269__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104269__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104269__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104269__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104269__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104269__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104269__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104268() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104268__8 = json.contains("(^D}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104268__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104268__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104268__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104268__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104268__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104268__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104268__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104268__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104268__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104268__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104268__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104268__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104268__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104268__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104268__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104268__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104267() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104267__8 = json.contains("yIar");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104267__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104267__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104267__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104267__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104267__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104267__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104267__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104267__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104267__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104267__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104267__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104267__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104267__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104267__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104267__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104267__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104265_literalMutationString105927() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__8 = json.contains("yzear");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__9 = json.contains("moth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104265__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_add104306_add106346() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_add104306_add106346__14 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306_add106346__14);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__13 = json.contains("second");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__14 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306_add106346__14);
    }

    public void testDefaultGregorianCalendarSerialization_add104306_add106344() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104306_add106344__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306_add106344__8);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__13 = json.contains("second");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__14 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104306_add106344__8);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104279_literalMutationString104856() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__10 = json.contains("dayOfMo+th");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104279__13 = json.contains("secpnd");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104277_add106645() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__10 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277_add106645__20 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104277_add106645__20);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104277_add106645__20);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104283_literalMutationString105096() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__11 = json.contains("hourOfD ay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__12 = json.contains("minte");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104283__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104278_add106461() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__10 = json.contains("daNyOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278_add106461__20 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104278_add106461__20);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104278_add106461__20);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104270_literalMutationString105202() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__9 = json.contains("");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__12 = json.contains("8inute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104272_literalMutationString105379() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__9 = json.contains("moVth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__12 = json.contains("m,inute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104272__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104298() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104298__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104298__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104298__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104298__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104298__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104298__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104298__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104298__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104298__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104298__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104298__13 = json.contains("sAcond");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104298__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104298__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104298__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104298__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104298__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104298__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104280_literalMutationString104873() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__10 = json.contains("dayOfMoh");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104297() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104297__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104297__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104297__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104297__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104297__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104297__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104297__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104297__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104297__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104297__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104297__13 = json.contains("seco,nd");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104297__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104297__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104297__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104297__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104297__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104297__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104296() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104296__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104296__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104296__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104296__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104296__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104296__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104296__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104296__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104296__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104296__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104296__13 = json.contains("secnd");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104296__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104296__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104296__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104296__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104296__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104296__12);
    }

    public void testDefaultGregorianCalendarSerialization_add104299() throws Exception {
        new GsonBuilder().create();
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104299__10 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104299__10);
        boolean o_testDefaultGregorianCalendarSerialization_add104299__11 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104299__11);
        boolean o_testDefaultGregorianCalendarSerialization_add104299__12 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104299__12);
        boolean o_testDefaultGregorianCalendarSerialization_add104299__13 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104299__13);
        boolean o_testDefaultGregorianCalendarSerialization_add104299__14 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104299__14);
        boolean o_testDefaultGregorianCalendarSerialization_add104299__15 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104299__15);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104299__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104299__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104299__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104299__13);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104299__14);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104274_literalMutationString104931() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__8 = json.contains("yevar");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__9 = json.contains("monh");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104274__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104291() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104291__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104291__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104291__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104291__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104291__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104291__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104291__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104291__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104291__12 = json.contains("miute");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104291__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104291__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104291__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104291__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104291__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104291__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104291__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104291__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104290() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104290__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104290__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104290__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104290__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104290__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104290__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104290__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104290__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104290__12 = json.contains("mifute");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104290__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104290__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104290__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104290__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104290__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104290__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104290__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104290__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104295() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104295__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104295__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104295__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104295__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104295__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104295__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104295__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104295__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104295__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104295__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104295__13 = json.contains("rFtfRD");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104295__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104295__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104295__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104295__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104295__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104295__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104294() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104294__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104294__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104294__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104294__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104294__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104294__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104294__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104294__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104294__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104294__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104294__13 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104294__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104294__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104294__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104294__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104294__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104294__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104277_literalMutationString106087() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__10 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__12 = json.contains("minu:te");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104277__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104293() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104293__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104293__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104293__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104293__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104293__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104293__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104293__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104293__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104293__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104293__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104293__13 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104293__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104293__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104293__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104293__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104293__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104293__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104270_add106439() throws Exception {
        new GsonBuilder().create();
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__9 = json.contains("");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104270__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104282_add106529() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282_add106529__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104282_add106529__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__11 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104282__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104282_add106529__8);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104292() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104292__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104292__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104292__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104292__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104292__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104292__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104292__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104292__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104292__12 = json.contains("-<zNB=");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104292__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104292__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104292__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104292__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104292__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104292__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104292__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104292__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104284_add106624() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String o_testDefaultGregorianCalendarSerialization_literalMutationString104284_add106624__6 = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", o_testDefaultGregorianCalendarSerialization_literalMutationString104284_add106624__6);
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__11 = json.contains("4jHClHv5j");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", o_testDefaultGregorianCalendarSerialization_literalMutationString104284_add106624__6);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104284_add106625() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284_add106625__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104284_add106625__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__11 = json.contains("4jHClHv5j");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104284_add106625__8);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104271_add106594() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271_add106594__11 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104271_add106594__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__9 = json.contains("{\'name3\':\'v3\'}");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104271__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104271_add106594__11);
    }

    public void testDefaultGregorianCalendarSerialization_add104306_literalMutationString104757() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__10 = json.contains("dyOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__13 = json.contains("second");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__14 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_add104306_literalMutationString104758() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104306__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__10 = json.contains("dayOfgMonth");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__13 = json.contains("second");
        boolean o_testDefaultGregorianCalendarSerialization_add104306__14 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_add104303() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104303__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__8);
        boolean o_testDefaultGregorianCalendarSerialization_add104303__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__9);
        boolean o_testDefaultGregorianCalendarSerialization_add104303__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__10);
        boolean o_testDefaultGregorianCalendarSerialization_add104303__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__11);
        boolean o_testDefaultGregorianCalendarSerialization_add104303__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__12);
        boolean o_testDefaultGregorianCalendarSerialization_add104303__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__13);
        boolean o_testDefaultGregorianCalendarSerialization_add104303__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104303__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104288() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104288__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104288__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104288__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104288__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104288__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104288__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104288__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104288__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104288__12 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104288__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104288__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104288__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104288__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104288__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104288__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104288__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104288__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104276_literalMutationString105343() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__10 = json.contains("");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__12 = json.contains("?hgT4=");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104276__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104278_literalMutationString105253() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__9 = json.contains("");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__10 = json.contains("daNyOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104278__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_add104302() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104302__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__8);
        boolean o_testDefaultGregorianCalendarSerialization_add104302__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__9);
        boolean o_testDefaultGregorianCalendarSerialization_add104302__10 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__10);
        boolean o_testDefaultGregorianCalendarSerialization_add104302__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__11);
        boolean o_testDefaultGregorianCalendarSerialization_add104302__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__12);
        boolean o_testDefaultGregorianCalendarSerialization_add104302__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__13);
        boolean o_testDefaultGregorianCalendarSerialization_add104302__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104302__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104287() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104287__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104287__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104287__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104287__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104287__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104287__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104287__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104287__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104287__12 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104287__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104287__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104287__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104287__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104287__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104287__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104287__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104287__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104284_literalMutationString106024() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__11 = json.contains("4jHClHv5j");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104284__13 = json.contains("scond");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104280_literalMutationString104862() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__8 = json.contains("e6_E");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__10 = json.contains("dayOfMonh");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104280__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_add104301() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104301__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__8);
        boolean o_testDefaultGregorianCalendarSerialization_add104301__9 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__9);
        boolean o_testDefaultGregorianCalendarSerialization_add104301__10 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__10);
        boolean o_testDefaultGregorianCalendarSerialization_add104301__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__11);
        boolean o_testDefaultGregorianCalendarSerialization_add104301__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__12);
        boolean o_testDefaultGregorianCalendarSerialization_add104301__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__13);
        boolean o_testDefaultGregorianCalendarSerialization_add104301__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104301__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104286() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104286__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104286__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104286__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104286__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104286__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104286__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104286__11 = json.contains("hou9OfDay");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104286__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104286__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104286__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104286__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104286__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104286__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104286__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104286__10);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104286__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104286__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104266_add106615() throws Exception {
        new GsonBuilder().create();
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__8 = json.contains("yer");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__10 = json.contains("dayOfMonth");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104266__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_add104300() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String o_testDefaultGregorianCalendarSerialization_add104300__6 = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", o_testDefaultGregorianCalendarSerialization_add104300__6);
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add104300__9 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104300__9);
        boolean o_testDefaultGregorianCalendarSerialization_add104300__10 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104300__10);
        boolean o_testDefaultGregorianCalendarSerialization_add104300__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104300__11);
        boolean o_testDefaultGregorianCalendarSerialization_add104300__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104300__12);
        boolean o_testDefaultGregorianCalendarSerialization_add104300__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104300__13);
        boolean o_testDefaultGregorianCalendarSerialization_add104300__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104300__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", o_testDefaultGregorianCalendarSerialization_add104300__6);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104300__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104300__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104300__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104300__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add104300__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104285() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104285__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104285__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104285__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104285__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104285__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104285__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104285__11 = json.contains("houOfDay");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104285__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104285__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104285__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104285__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104285__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104285__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104285__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104285__10);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104285__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104285__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104275_add106544() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String o_testDefaultGregorianCalendarSerialization_literalMutationString104275_add106544__6 = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", o_testDefaultGregorianCalendarSerialization_literalMutationString104275_add106544__6);
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__8 = json.contains("year");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__9 = json.contains("month");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__10 = json.contains("vv1!2UzV/5");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__11 = json.contains("hourOfDay");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__12 = json.contains("minute");
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104275__13 = json.contains("second");
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", o_testDefaultGregorianCalendarSerialization_literalMutationString104275_add106544__6);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":1,\"second\":3}", json);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString104289() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104289__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104289__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104289__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104289__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104289__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104289__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104289__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104289__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104289__12 = json.contains("mi6nute");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104289__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString104289__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104289__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":20,\"minute\":0,\"second\":58}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104289__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104289__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104289__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString104289__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString104289__12);
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

