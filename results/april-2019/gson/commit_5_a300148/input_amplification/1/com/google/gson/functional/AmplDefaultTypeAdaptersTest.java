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

    public void testDefaultCalendarSerialization_literalMutationString1567() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1567__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1567__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1567__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1567__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1567__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1567__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1567__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1567__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1567__11 = json.contains("^.mW=M");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1567__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1567__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1567__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1567__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1567__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1567__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1567__10);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1567__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1566() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1566__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1566__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1566__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1566__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1566__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1566__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1566__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1566__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1566__11 = json.contains("mnute");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1566__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1566__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1566__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1566__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1566__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1566__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1566__10);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1566__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1565() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1565__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1565__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1565__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1565__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1565__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1565__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1565__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1565__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1565__11 = json.contains("minu%te");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1565__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1565__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1565__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1565__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1565__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1565__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1565__10);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1565__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1564() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1564__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1564__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1564__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1564__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1564__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1564__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1564__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1564__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1564__11 = json.contains("m9nute");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1564__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1564__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1564__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1564__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1564__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1564__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1564__10);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1564__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1569() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1569__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1569__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1569__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1569__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1569__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1569__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1569__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1569__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1569__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1569__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1569__12 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1569__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1569__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1569__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1569__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1569__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1569__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1568() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1568__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1568__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1568__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1568__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1568__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1568__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1568__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1568__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1568__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1568__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1568__12 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1568__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1568__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1568__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1568__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1568__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1568__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1563() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1563__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1563__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1563__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1563__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1563__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1563__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1563__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1563__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1563__11 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1563__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1563__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1563__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1563__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1563__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1563__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1563__10);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1563__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1562() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1562__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1562__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1562__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1562__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1562__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1562__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1562__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1562__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1562__11 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1562__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1562__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1562__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1562__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1562__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1562__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1562__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1562__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1561() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1561__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1561__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1561__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1561__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1561__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1561__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1561__10 = json.contains(" wxrA(AH*");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1561__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1561__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1561__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1561__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1561__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1561__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1561__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1561__9);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1561__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1561__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1560() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1560__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1560__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1560__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1560__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1560__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1560__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1560__10 = json.contains("hou{OfDay");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1560__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1560__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1560__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1560__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1560__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1560__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1560__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1560__9);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1560__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1560__11);
    }

    public void testDefaultCalendarSerialization_add1577() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_add1577__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__7);
        boolean o_testDefaultCalendarSerialization_add1577__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__8);
        boolean o_testDefaultCalendarSerialization_add1577__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__9);
        boolean o_testDefaultCalendarSerialization_add1577__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__10);
        boolean o_testDefaultCalendarSerialization_add1577__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__11);
        boolean o_testDefaultCalendarSerialization_add1577__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__12);
        boolean o_testDefaultCalendarSerialization_add1577__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1577__12);
    }

    public void testDefaultCalendarSerialization_add1578() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_add1578__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__7);
        boolean o_testDefaultCalendarSerialization_add1578__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__8);
        boolean o_testDefaultCalendarSerialization_add1578__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__9);
        boolean o_testDefaultCalendarSerialization_add1578__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__10);
        boolean o_testDefaultCalendarSerialization_add1578__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__11);
        boolean o_testDefaultCalendarSerialization_add1578__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__12);
        boolean o_testDefaultCalendarSerialization_add1578__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1578__12);
    }

    public void testDefaultCalendarSerialization_add1575() throws Exception {
        Gson gson = new GsonBuilder().create();
        String o_testDefaultCalendarSerialization_add1575__4 = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", o_testDefaultCalendarSerialization_add1575__4);
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_add1575__9 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1575__9);
        boolean o_testDefaultCalendarSerialization_add1575__10 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1575__10);
        boolean o_testDefaultCalendarSerialization_add1575__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1575__11);
        boolean o_testDefaultCalendarSerialization_add1575__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1575__12);
        boolean o_testDefaultCalendarSerialization_add1575__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1575__13);
        boolean o_testDefaultCalendarSerialization_add1575__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1575__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", o_testDefaultCalendarSerialization_add1575__4);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1575__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1575__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1575__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1575__12);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1575__13);
    }

    public void testDefaultCalendarSerialization_literalMutationString1539() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1539__7 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1539__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1539__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1539__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1539__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1539__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1539__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1539__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1539__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1539__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1539__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1539__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1539__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1539__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1539__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1539__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1539__11);
    }

    public void testDefaultCalendarSerialization_add1576() throws Exception {
        Gson gson = new GsonBuilder().create();
        Calendar.getInstance();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_add1576__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1576__8);
        boolean o_testDefaultCalendarSerialization_add1576__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1576__9);
        boolean o_testDefaultCalendarSerialization_add1576__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1576__10);
        boolean o_testDefaultCalendarSerialization_add1576__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1576__11);
        boolean o_testDefaultCalendarSerialization_add1576__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1576__12);
        boolean o_testDefaultCalendarSerialization_add1576__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1576__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1576__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1576__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1576__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1576__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1576__12);
    }

    public void testDefaultCalendarSerialization_add1579() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_add1579__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__7);
        boolean o_testDefaultCalendarSerialization_add1579__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__8);
        boolean o_testDefaultCalendarSerialization_add1579__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__9);
        boolean o_testDefaultCalendarSerialization_add1579__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__10);
        boolean o_testDefaultCalendarSerialization_add1579__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__11);
        boolean o_testDefaultCalendarSerialization_add1579__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__12);
        boolean o_testDefaultCalendarSerialization_add1579__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1579__12);
    }

    public void testDefaultCalendarSerialization_literalMutationString1538() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1538__7 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1538__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1538__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1538__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1538__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1538__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1538__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1538__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1538__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1538__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1538__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1538__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1538__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1538__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1538__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1538__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1538__11);
    }

    public void testDefaultCalendarSerialization_add1574() throws Exception {
        new GsonBuilder().create();
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_add1574__9 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1574__9);
        boolean o_testDefaultCalendarSerialization_add1574__10 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1574__10);
        boolean o_testDefaultCalendarSerialization_add1574__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1574__11);
        boolean o_testDefaultCalendarSerialization_add1574__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1574__12);
        boolean o_testDefaultCalendarSerialization_add1574__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1574__13);
        boolean o_testDefaultCalendarSerialization_add1574__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1574__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1574__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1574__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1574__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1574__12);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1574__13);
    }

    public void testDefaultCalendarSerialization_literalMutationString1570() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1570__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1570__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1570__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1570__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1570__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1570__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1570__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1570__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1570__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1570__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1570__12 = json.contains("{ZJy+V");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1570__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1570__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1570__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1570__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1570__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1570__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1573() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1573__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1573__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1573__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1573__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1573__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1573__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1573__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1573__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1573__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1573__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1573__12 = json.contains("sexond");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1573__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1573__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1573__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1573__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1573__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1573__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1572() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1572__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1572__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1572__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1572__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1572__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1572__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1572__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1572__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1572__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1572__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1572__12 = json.contains("secoond");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1572__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1572__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1572__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1572__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1572__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1572__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1571() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1571__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1571__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1571__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1571__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1571__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1571__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1571__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1571__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1571__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1571__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1571__12 = json.contains("secod");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1571__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1571__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1571__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1571__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1571__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1571__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1545() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1545__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1545__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1545__8 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1545__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1545__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1545__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1545__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1545__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1545__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1545__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1545__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1545__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1545__7);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1545__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1545__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1545__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1545__11);
    }

    public void testDefaultCalendarSerialization_add1580() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_add1580__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__7);
        boolean o_testDefaultCalendarSerialization_add1580__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__8);
        boolean o_testDefaultCalendarSerialization_add1580__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__9);
        boolean o_testDefaultCalendarSerialization_add1580__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__10);
        boolean o_testDefaultCalendarSerialization_add1580__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__11);
        boolean o_testDefaultCalendarSerialization_add1580__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__12);
        boolean o_testDefaultCalendarSerialization_add1580__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1580__12);
    }

    public void testDefaultCalendarSerialization_literalMutationString1544() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1544__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1544__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1544__8 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1544__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1544__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1544__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1544__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1544__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1544__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1544__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1544__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1544__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1544__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1544__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1544__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1544__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1544__11);
    }

    public void testDefaultCalendarSerialization_add1581() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_add1581__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__7);
        boolean o_testDefaultCalendarSerialization_add1581__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__8);
        boolean o_testDefaultCalendarSerialization_add1581__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__9);
        boolean o_testDefaultCalendarSerialization_add1581__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__10);
        boolean o_testDefaultCalendarSerialization_add1581__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__11);
        boolean o_testDefaultCalendarSerialization_add1581__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__12);
        boolean o_testDefaultCalendarSerialization_add1581__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1581__12);
    }

    public void testDefaultCalendarSerialization_literalMutationString1543() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1543__7 = json.contains("DG}h");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1543__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1543__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1543__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1543__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1543__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1543__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1543__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1543__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1543__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1543__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1543__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1543__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1543__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1543__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1543__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1543__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1542() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1542__7 = json.contains("yar");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1542__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1542__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1542__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1542__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1542__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1542__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1542__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1542__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1542__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1542__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1542__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1542__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1542__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1542__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1542__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1542__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1549() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1549__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1549__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1549__8 = json.contains("monh");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1549__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1549__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1549__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1549__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1549__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1549__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1549__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1549__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1549__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1549__7);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1549__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1549__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1549__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1549__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1548() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1548__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1548__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1548__8 = json.contains("mtonth");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1548__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1548__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1548__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1548__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1548__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1548__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1548__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1548__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1548__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1548__7);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1548__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1548__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1548__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1548__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1547() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1547__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1547__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1547__8 = json.contains(";onth");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1547__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1547__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1547__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1547__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1547__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1547__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1547__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1547__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1547__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1547__7);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1547__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1547__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1547__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1547__11);
    }

    public void testDefaultCalendarSerialization_add1582() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_add1582__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__7);
        boolean o_testDefaultCalendarSerialization_add1582__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__8);
        boolean o_testDefaultCalendarSerialization_add1582__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__9);
        boolean o_testDefaultCalendarSerialization_add1582__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__10);
        boolean o_testDefaultCalendarSerialization_add1582__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__11);
        boolean o_testDefaultCalendarSerialization_add1582__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__12);
        boolean o_testDefaultCalendarSerialization_add1582__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__11);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_add1582__12);
    }

    public void testDefaultCalendarSerialization_literalMutationString1546() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1546__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1546__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1546__8 = json.contains("Dm[bb");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1546__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1546__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1546__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1546__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1546__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1546__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1546__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1546__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1546__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1546__7);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1546__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1546__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1546__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1546__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1541() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1541__7 = json.contains("ye]ar");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1541__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1541__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1541__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1541__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1541__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1541__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1541__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1541__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1541__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1541__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1541__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1541__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1541__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1541__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1541__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1541__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1540() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1540__7 = json.contains("7ear");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1540__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1540__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1540__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1540__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1540__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1540__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1540__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1540__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1540__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1540__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1540__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1540__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1540__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1540__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1540__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1540__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1556() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1556__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1556__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1556__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1556__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1556__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1556__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1556__10 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1556__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1556__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1556__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1556__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1556__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1556__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1556__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1556__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1556__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1556__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1555() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1555__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1555__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1555__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1555__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1555__9 = json.contains("dayIOfMonth");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1555__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1555__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1555__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1555__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1555__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1555__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1555__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1555__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1555__8);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1555__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1555__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1555__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1554() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1554__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1554__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1554__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1554__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1554__9 = json.contains("dayO<Month");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1554__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1554__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1554__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1554__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1554__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1554__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1554__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1554__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1554__8);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1554__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1554__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1554__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1553() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1553__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1553__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1553__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1553__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1553__9 = json.contains("uDJw6h3yF,");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1553__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1553__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1553__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1553__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1553__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1553__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1553__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1553__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1553__8);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1553__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1553__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1553__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1559() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1559__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1559__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1559__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1559__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1559__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1559__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1559__10 = json.contains("hourOfEDay");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1559__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1559__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1559__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1559__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1559__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1559__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1559__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1559__9);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1559__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1559__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1558() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1558__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1558__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1558__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1558__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1558__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1558__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1558__10 = json.contains("hurOfDay");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1558__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1558__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1558__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1558__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1558__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1558__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1558__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1558__9);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1558__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1558__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1557() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1557__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1557__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1557__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1557__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1557__9 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1557__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1557__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1557__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1557__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1557__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1557__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1557__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1557__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1557__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1557__9);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1557__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1557__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1552() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1552__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1552__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1552__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1552__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1552__9 = json.contains("daOfMonth");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1552__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1552__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1552__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1552__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1552__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1552__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1552__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1552__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1552__8);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1552__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1552__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1552__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1551() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1551__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1551__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1551__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1551__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1551__9 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1551__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1551__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1551__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1551__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1551__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1551__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1551__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1551__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1551__8);
        TestCase.assertFalse(o_testDefaultCalendarSerialization_literalMutationString1551__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1551__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1551__11);
    }

    public void testDefaultCalendarSerialization_literalMutationString1550() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(Calendar.getInstance());
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        boolean o_testDefaultCalendarSerialization_literalMutationString1550__7 = json.contains("year");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1550__7);
        boolean o_testDefaultCalendarSerialization_literalMutationString1550__8 = json.contains("month");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1550__8);
        boolean o_testDefaultCalendarSerialization_literalMutationString1550__9 = json.contains("");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1550__9);
        boolean o_testDefaultCalendarSerialization_literalMutationString1550__10 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1550__10);
        boolean o_testDefaultCalendarSerialization_literalMutationString1550__11 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1550__11);
        boolean o_testDefaultCalendarSerialization_literalMutationString1550__12 = json.contains("second");
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1550__12);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":3}", json);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1550__7);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1550__8);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1550__9);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1550__10);
        TestCase.assertTrue(o_testDefaultCalendarSerialization_literalMutationString1550__11);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1972() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1972__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1972__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1972__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1972__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1972__10 = json.contains("dayOMonth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1972__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1972__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1972__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1972__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1972__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1972__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1972__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1972__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1972__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1972__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1972__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1972__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1971() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1971__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1971__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1971__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1971__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1971__10 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1971__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1971__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1971__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1971__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1971__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1971__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1971__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1971__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1971__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1971__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1971__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1971__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1974() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1974__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1974__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1974__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1974__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1974__10 = json.contains("daryOfMonth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1974__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1974__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1974__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1974__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1974__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1974__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1974__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1974__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1974__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1974__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1974__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1974__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1973() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1973__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1973__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1973__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1973__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1973__10 = json.contains("A%;R?&,0.F");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1973__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1973__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1973__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1973__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1973__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1973__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1973__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1973__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1973__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1973__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1973__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1973__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1976() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1976__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1976__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1976__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1976__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1976__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1976__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1976__11 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1976__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1976__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1976__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1976__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1976__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1976__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1976__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1976__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1976__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1976__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1975() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1975__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1975__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1975__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1975__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1975__10 = json.contains("dakOfMonth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1975__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1975__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1975__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1975__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1975__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1975__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1975__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1975__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1975__9);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1975__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1975__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1975__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1978() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1978__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1978__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1978__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1978__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1978__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1978__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1978__11 = json.contains("hoRurOfDay");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1978__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1978__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1978__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1978__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1978__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1978__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1978__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1978__10);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1978__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1978__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1977() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1977__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1977__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1977__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1977__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1977__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1977__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1977__11 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1977__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1977__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1977__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1977__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1977__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1977__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1977__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1977__10);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1977__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1977__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1970() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1970__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1970__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1970__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1970__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1970__10 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1970__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1970__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1970__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1970__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1970__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1970__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1970__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1970__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1970__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1970__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1970__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1970__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1979() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1979__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1979__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1979__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1979__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1979__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1979__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1979__11 = json.contains("<DFMF%&a%");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1979__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1979__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1979__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1979__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1979__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1979__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1979__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1979__10);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1979__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1979__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1961() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1961__8 = json.contains("yar");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1961__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1961__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1961__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1961__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1961__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1961__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1961__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1961__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1961__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1961__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1961__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1961__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1961__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1961__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1961__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1961__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1960() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1960__8 = json.contains("yetar");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1960__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1960__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1960__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1960__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1960__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1960__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1960__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1960__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1960__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1960__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1960__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1960__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1960__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1960__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1960__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1960__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1963() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1963__8 = json.contains(".vzK");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1963__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1963__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1963__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1963__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1963__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1963__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1963__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1963__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1963__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1963__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1963__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1963__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1963__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1963__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1963__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1963__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1962() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1962__8 = json.contains("-ear");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1962__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1962__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1962__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1962__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1962__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1962__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1962__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1962__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1962__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1962__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1962__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1962__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1962__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1962__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1962__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1962__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1965() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1965__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1965__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1965__9 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1965__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1965__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1965__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1965__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1965__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1965__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1965__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1965__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1965__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1965__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1965__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1965__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1965__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1965__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1964() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1964__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1964__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1964__9 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1964__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1964__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1964__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1964__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1964__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1964__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1964__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1964__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1964__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1964__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1964__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1964__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1964__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1964__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1967() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1967__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1967__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1967__9 = json.contains("moth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1967__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1967__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1967__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1967__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1967__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1967__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1967__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1967__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1967__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1967__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1967__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1967__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1967__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1967__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1966() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1966__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1966__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1966__9 = json.contains("moeth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1966__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1966__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1966__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1966__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1966__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1966__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1966__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1966__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1966__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1966__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1966__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1966__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1966__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1966__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1969() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1969__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1969__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1969__9 = json.contains("vH@7c");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1969__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1969__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1969__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1969__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1969__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1969__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1969__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1969__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1969__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1969__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1969__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1969__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1969__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1969__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1968() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1968__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1968__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1968__9 = json.contains("m#onth");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1968__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1968__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1968__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1968__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1968__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1968__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1968__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1968__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1968__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1968__8);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1968__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1968__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1968__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1968__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1993() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1993__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1993__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1993__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1993__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1993__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1993__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1993__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1993__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1993__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1993__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1993__13 = json.contains("sefond");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1993__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1993__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1993__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1993__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1993__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1993__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1990() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1990__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1990__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1990__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1990__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1990__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1990__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1990__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1990__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1990__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1990__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1990__13 = json.contains("secoQnd");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1990__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1990__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1990__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1990__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1990__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1990__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1992() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1992__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1992__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1992__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1992__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1992__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1992__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1992__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1992__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1992__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1992__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1992__13 = json.contains("seond");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1992__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1992__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1992__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1992__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1992__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1992__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1991() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1991__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1991__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1991__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1991__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1991__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1991__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1991__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1991__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1991__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1991__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1991__13 = json.contains("#lU1E7");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1991__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1991__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1991__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1991__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1991__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1991__12);
    }

    public void testDefaultGregorianCalendarSerialization_add2001() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add2001__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__8);
        boolean o_testDefaultGregorianCalendarSerialization_add2001__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__9);
        boolean o_testDefaultGregorianCalendarSerialization_add2001__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__10);
        boolean o_testDefaultGregorianCalendarSerialization_add2001__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__11);
        boolean o_testDefaultGregorianCalendarSerialization_add2001__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__12);
        boolean o_testDefaultGregorianCalendarSerialization_add2001__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__13);
        boolean o_testDefaultGregorianCalendarSerialization_add2001__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2001__13);
    }

    public void testDefaultGregorianCalendarSerialization_add2000() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add2000__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__8);
        boolean o_testDefaultGregorianCalendarSerialization_add2000__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__9);
        boolean o_testDefaultGregorianCalendarSerialization_add2000__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__10);
        boolean o_testDefaultGregorianCalendarSerialization_add2000__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__11);
        boolean o_testDefaultGregorianCalendarSerialization_add2000__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__12);
        boolean o_testDefaultGregorianCalendarSerialization_add2000__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__13);
        boolean o_testDefaultGregorianCalendarSerialization_add2000__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add2000__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1958() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1958__8 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1958__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1958__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1958__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1958__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1958__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1958__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1958__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1958__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1958__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1958__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1958__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1958__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1958__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1958__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1958__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1958__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1959() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1959__8 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1959__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1959__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1959__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1959__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1959__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1959__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1959__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1959__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1959__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1959__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1959__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1959__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1959__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1959__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1959__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1959__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1983() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1983__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1983__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1983__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1983__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1983__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1983__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1983__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1983__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1983__12 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1983__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1983__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1983__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1983__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1983__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1983__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1983__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1983__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1982() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1982__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1982__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1982__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1982__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1982__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1982__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1982__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1982__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1982__12 = json.contains("mi^ute");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1982__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1982__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1982__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1982__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1982__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1982__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1982__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1982__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1985() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1985__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1985__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1985__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1985__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1985__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1985__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1985__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1985__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1985__12 = json.contains("minue");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1985__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1985__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1985__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1985__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1985__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1985__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1985__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1985__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1984() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1984__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1984__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1984__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1984__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1984__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1984__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1984__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1984__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1984__12 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1984__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1984__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1984__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1984__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1984__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1984__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1984__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1984__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1987() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1987__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1987__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1987__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1987__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1987__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1987__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1987__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1987__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1987__12 = json.contains("LGF}>o");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1987__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1987__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1987__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1987__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1987__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1987__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1987__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1987__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1986() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1986__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1986__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1986__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1986__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1986__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1986__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1986__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1986__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1986__12 = json.contains("miBnute");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1986__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1986__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1986__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1986__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1986__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1986__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1986__11);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1986__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1989() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1989__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1989__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1989__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1989__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1989__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1989__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1989__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1989__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1989__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1989__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1989__13 = json.contains("{\'name3\':\'v3\'}");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1989__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1989__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1989__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1989__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1989__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1989__12);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1988() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1988__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1988__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1988__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1988__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1988__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1988__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1988__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1988__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1988__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1988__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1988__13 = json.contains("");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1988__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1988__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1988__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1988__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1988__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1988__12);
    }

    public void testDefaultGregorianCalendarSerialization_add1999() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add1999__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__8);
        boolean o_testDefaultGregorianCalendarSerialization_add1999__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__9);
        boolean o_testDefaultGregorianCalendarSerialization_add1999__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__10);
        boolean o_testDefaultGregorianCalendarSerialization_add1999__11 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__11);
        boolean o_testDefaultGregorianCalendarSerialization_add1999__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__12);
        boolean o_testDefaultGregorianCalendarSerialization_add1999__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__13);
        boolean o_testDefaultGregorianCalendarSerialization_add1999__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1999__13);
    }

    public void testDefaultGregorianCalendarSerialization_add1998() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add1998__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__8);
        boolean o_testDefaultGregorianCalendarSerialization_add1998__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__9);
        boolean o_testDefaultGregorianCalendarSerialization_add1998__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__10);
        boolean o_testDefaultGregorianCalendarSerialization_add1998__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__11);
        boolean o_testDefaultGregorianCalendarSerialization_add1998__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__12);
        boolean o_testDefaultGregorianCalendarSerialization_add1998__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__13);
        boolean o_testDefaultGregorianCalendarSerialization_add1998__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1998__13);
    }

    public void testDefaultGregorianCalendarSerialization_add1997() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add1997__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__8);
        boolean o_testDefaultGregorianCalendarSerialization_add1997__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__9);
        boolean o_testDefaultGregorianCalendarSerialization_add1997__10 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__10);
        boolean o_testDefaultGregorianCalendarSerialization_add1997__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__11);
        boolean o_testDefaultGregorianCalendarSerialization_add1997__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__12);
        boolean o_testDefaultGregorianCalendarSerialization_add1997__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__13);
        boolean o_testDefaultGregorianCalendarSerialization_add1997__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1997__13);
    }

    public void testDefaultGregorianCalendarSerialization_add1996() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add1996__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__8);
        boolean o_testDefaultGregorianCalendarSerialization_add1996__9 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__9);
        boolean o_testDefaultGregorianCalendarSerialization_add1996__10 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__10);
        boolean o_testDefaultGregorianCalendarSerialization_add1996__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__11);
        boolean o_testDefaultGregorianCalendarSerialization_add1996__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__12);
        boolean o_testDefaultGregorianCalendarSerialization_add1996__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__13);
        boolean o_testDefaultGregorianCalendarSerialization_add1996__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1996__13);
    }

    public void testDefaultGregorianCalendarSerialization_add1995() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String o_testDefaultGregorianCalendarSerialization_add1995__6 = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", o_testDefaultGregorianCalendarSerialization_add1995__6);
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add1995__9 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1995__9);
        boolean o_testDefaultGregorianCalendarSerialization_add1995__10 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1995__10);
        boolean o_testDefaultGregorianCalendarSerialization_add1995__11 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1995__11);
        boolean o_testDefaultGregorianCalendarSerialization_add1995__12 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1995__12);
        boolean o_testDefaultGregorianCalendarSerialization_add1995__13 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1995__13);
        boolean o_testDefaultGregorianCalendarSerialization_add1995__14 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1995__14);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", o_testDefaultGregorianCalendarSerialization_add1995__6);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1995__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1995__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1995__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1995__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1995__13);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1981() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1981__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1981__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1981__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1981__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1981__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1981__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1981__11 = json.contains("houOfDay");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1981__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1981__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1981__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1981__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1981__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1981__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1981__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1981__10);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1981__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1981__12);
    }

    public void testDefaultGregorianCalendarSerialization_add1994() throws Exception {
        new GsonBuilder().create();
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_add1994__10 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1994__10);
        boolean o_testDefaultGregorianCalendarSerialization_add1994__11 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1994__11);
        boolean o_testDefaultGregorianCalendarSerialization_add1994__12 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1994__12);
        boolean o_testDefaultGregorianCalendarSerialization_add1994__13 = json.contains("hourOfDay");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1994__13);
        boolean o_testDefaultGregorianCalendarSerialization_add1994__14 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1994__14);
        boolean o_testDefaultGregorianCalendarSerialization_add1994__15 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1994__15);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1994__10);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1994__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1994__12);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1994__13);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_add1994__14);
    }

    public void testDefaultGregorianCalendarSerialization_literalMutationString1980() throws Exception {
        Gson gson = new GsonBuilder().create();
        GregorianCalendar cal = new GregorianCalendar();
        String json = gson.toJson(cal);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1980__8 = json.contains("year");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1980__8);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1980__9 = json.contains("month");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1980__9);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1980__10 = json.contains("dayOfMonth");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1980__10);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1980__11 = json.contains("hourOQDay");
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1980__11);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1980__12 = json.contains("minute");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1980__12);
        boolean o_testDefaultGregorianCalendarSerialization_literalMutationString1980__13 = json.contains("second");
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1980__13);
        TestCase.assertEquals("{\"year\":2019,\"month\":4,\"dayOfMonth\":9,\"hourOfDay\":15,\"minute\":38,\"second\":15}", json);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1980__8);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1980__9);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1980__10);
        TestCase.assertFalse(o_testDefaultGregorianCalendarSerialization_literalMutationString1980__11);
        TestCase.assertTrue(o_testDefaultGregorianCalendarSerialization_literalMutationString1980__12);
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

