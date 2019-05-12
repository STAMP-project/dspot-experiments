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

    public void testDefaultGregorianCalendarSerialization_add101957() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add101957__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__8);
        boolean o_testDefaultGregorianCalendarSerialization_add101957__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__9);
        boolean o_testDefaultGregorianCalendarSerialization_add101957__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__10);
        boolean o_testDefaultGregorianCalendarSerialization_add101957__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__11);
        boolean o_testDefaultGregorianCalendarSerialization_add101957__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__12);
        boolean o_testDefaultGregorianCalendarSerialization_add101957__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__13);
        boolean o_testDefaultGregorianCalendarSerialization_add101957__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101957__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101927() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101927__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101927__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101927__9 = json.contains("m nth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101927__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101927__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101927__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101927__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101927__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101927__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101927__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101927__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101927__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101927__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101927__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101927__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101927__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101927__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101949() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101949__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101949__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101949__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101949__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101949__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101949__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101949__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101949__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101949__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101949__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101949__13 = json.contains("mT*3[M");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101949__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101949__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101949__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101949__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101949__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101949__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101928() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101928__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101928__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101928__9 = json.contains("monh");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101928__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101928__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101928__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101928__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101928__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101928__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101928__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101928__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101928__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101928__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101928__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101928__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101928__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101928__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101925() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101925__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101925__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101925__9 = json.contains("mognth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101925__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101925__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101925__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101925__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101925__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101925__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101925__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101925__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101925__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101925__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101925__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101925__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101925__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101925__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101947() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101947__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101947__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101947__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101947__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101947__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101947__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101947__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101947__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101947__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101947__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101947__13 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101947__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101947__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101947__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101947__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101947__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101947__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101926() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101926__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101926__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101926__9 = json.contains("og2hs");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101926__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101926__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101926__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101926__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101926__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101926__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101926__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101926__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101926__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101926__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101926__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101926__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101926__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101926__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101948() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101948__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101948__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101948__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101948__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101948__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101948__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101948__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101948__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101948__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101948__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101948__13 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101948__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101948__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101948__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101948__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101948__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101948__12);
    }

    public void testDefaultGregorianCalendarSerialization_add101959() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add101959__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__8);
        boolean o_testDefaultGregorianCalendarSerialization_add101959__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__9);
        boolean o_testDefaultGregorianCalendarSerialization_add101959__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__10);
        boolean o_testDefaultGregorianCalendarSerialization_add101959__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__11);
        boolean o_testDefaultGregorianCalendarSerialization_add101959__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__12);
        boolean o_testDefaultGregorianCalendarSerialization_add101959__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__13);
        boolean o_testDefaultGregorianCalendarSerialization_add101959__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101959__13);
    }

    public void testDefaultGregorianCalendarSerialization_add101958() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add101958__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__8);
        boolean o_testDefaultGregorianCalendarSerialization_add101958__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__9);
        boolean o_testDefaultGregorianCalendarSerialization_add101958__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__10);
        boolean o_testDefaultGregorianCalendarSerialization_add101958__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__11);
        boolean o_testDefaultGregorianCalendarSerialization_add101958__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__12);
        boolean o_testDefaultGregorianCalendarSerialization_add101958__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__13);
        boolean o_testDefaultGregorianCalendarSerialization_add101958__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101958__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101929() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101929__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101929__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101929__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101929__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101929__10 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101929__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101929__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101929__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101929__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101929__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101929__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101929__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101929__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101929__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101929__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101929__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101929__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101920() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101920__8 = json.contains("yer");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101920__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101920__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101920__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101920__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101920__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101920__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101920__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101920__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101920__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101920__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101920__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101920__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101920__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101920__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101920__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101920__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101923() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101923__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101923__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101923__9 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101923__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101923__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101923__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101923__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101923__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101923__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101923__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101923__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101923__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101923__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101923__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101923__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101923__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101923__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101945() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101945__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101945__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101945__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101945__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101945__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101945__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101945__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101945__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101945__12 = json.contains("miute");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101945__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101945__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101945__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101945__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101945__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101945__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101945__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101945__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101924() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101924__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101924__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101924__9 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101924__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101924__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101924__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101924__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101924__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101924__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101924__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101924__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101924__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101924__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101924__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101924__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101924__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101924__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101946() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101946__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101946__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101946__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101946__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101946__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101946__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101946__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101946__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101946__12 = json.contains("mpinute");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101946__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101946__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101946__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101946__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101946__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101946__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101946__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101946__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101921() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101921__8 = json.contains("bear");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101921__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101921__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101921__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101921__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101921__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101921__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101921__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101921__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101921__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101921__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101921__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101921__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101921__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101921__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101921__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101921__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101922() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101922__8 = json.contains("b]c8");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101922__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101922__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101922__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101922__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101922__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101922__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101922__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101922__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101922__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101922__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101922__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101922__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101922__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101922__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101922__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101922__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101944() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101944__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101944__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101944__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101944__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101944__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101944__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101944__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101944__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101944__12 = json.contains("mcnute");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101944__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101944__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101944__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101944__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101944__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101944__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101944__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101944__12);
    }

    public void testDefaultGregorianCalendarSerialization_add101960() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add101960__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__8);
        boolean o_testDefaultGregorianCalendarSerialization_add101960__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__9);
        boolean o_testDefaultGregorianCalendarSerialization_add101960__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__10);
        boolean o_testDefaultGregorianCalendarSerialization_add101960__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__11);
        boolean o_testDefaultGregorianCalendarSerialization_add101960__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__12);
        boolean o_testDefaultGregorianCalendarSerialization_add101960__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__13);
        boolean o_testDefaultGregorianCalendarSerialization_add101960__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add101960__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101936() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101936__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101936__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101936__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101936__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101936__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101936__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101936__11 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101936__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101936__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101936__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101936__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101936__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101936__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101936__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101936__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101936__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101936__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101919() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101919__8 = json.contains("yevar");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101919__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101919__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101919__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101919__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101919__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101919__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101919__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101919__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101919__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101919__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101919__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101919__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101919__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101919__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101919__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101919__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101930() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101930__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101930__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101930__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101930__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101930__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101930__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101930__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101930__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101930__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101930__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101930__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101930__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101930__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101930__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101930__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101930__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101930__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101952() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101952__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101952__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101952__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101952__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101952__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101952__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101952__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101952__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101952__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101952__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101952__13 = json.contains("seond");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101952__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101952__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101952__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101952__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101952__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101952__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101931() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101931__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101931__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101931__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101931__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101931__10 = json.contains("oayOfMonth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101931__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101931__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101931__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101931__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101931__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101931__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101931__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101931__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101931__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101931__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101931__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101931__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101950() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101950__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101950__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101950__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101950__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101950__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101950__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101950__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101950__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101950__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101950__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101950__13 = json.contains("+econd");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101950__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101950__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101950__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101950__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101950__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101950__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101951() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101951__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101951__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101951__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101951__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101951__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101951__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101951__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101951__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101951__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101951__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101951__13 = json.contains("seVcond");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101951__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101951__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101951__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101951__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101951__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101951__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101934() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101934__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101934__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101934__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101934__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101934__10 = json.contains("dayOfMonh");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101934__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101934__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101934__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101934__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101934__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101934__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101934__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101934__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101934__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101934__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101934__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101934__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101935() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101935__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101935__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101935__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101935__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101935__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101935__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101935__11 = json.contains("ho[rOfDay");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101935__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101935__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101935__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101935__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101935__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101935__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101935__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101935__10);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101935__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101935__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101932() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101932__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101932__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101932__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101932__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101932__10 = json.contains("dayOfEMonth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101932__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101932__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101932__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101932__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101932__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101932__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101932__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101932__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101932__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101932__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101932__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101932__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString101933() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101933__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101933__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101933__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101933__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101933__10 = json.contains("_?XX0arN7j");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101933__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101933__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101933__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101933__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101933__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString101933__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101933__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":12,\"hourOfDay\":4,\"minute\":29,\"second\":28}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101933__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101933__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString101933__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101933__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString101933__12);
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

