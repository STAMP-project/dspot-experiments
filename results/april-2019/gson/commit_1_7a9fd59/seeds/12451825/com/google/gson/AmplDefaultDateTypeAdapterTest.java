package com.google.gson;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;


public class AmplDefaultDateTypeAdapterTest extends TestCase {
    private void assertFormattingAlwaysEmitsUsLocale(Locale locale) {
        TimeZone defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(locale);
        try {
            assertFormatted("Jan 1, 1970 12:00:00 AM", new DefaultDateTypeAdapter(Date.class));
            assertFormatted("1/1/70", new DefaultDateTypeAdapter(Date.class, DateFormat.SHORT));
            assertFormatted("Jan 1, 1970", new DefaultDateTypeAdapter(Date.class, DateFormat.MEDIUM));
            assertFormatted("January 1, 1970", new DefaultDateTypeAdapter(Date.class, DateFormat.LONG));
            assertFormatted("1/1/70 12:00 AM", new DefaultDateTypeAdapter(DateFormat.SHORT, DateFormat.SHORT));
            assertFormatted("Jan 1, 1970 12:00:00 AM", new DefaultDateTypeAdapter(DateFormat.MEDIUM, DateFormat.MEDIUM));
            assertFormatted("January 1, 1970 12:00:00 AM UTC", new DefaultDateTypeAdapter(DateFormat.LONG, DateFormat.LONG));
            assertFormatted("Thursday, January 1, 1970 12:00:00 AM UTC", new DefaultDateTypeAdapter(DateFormat.FULL, DateFormat.FULL));
        } finally {
            TimeZone.setDefault(defaultTimeZone);
            Locale.setDefault(defaultLocale);
        }
    }

    public void testDateSerializationnull258_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerializationnull258 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    private void assertFormatted(String formatted, DefaultDateTypeAdapter adapter) {
        TestCase.assertEquals(AmplDefaultDateTypeAdapterTest.toLiteral(formatted), adapter.toJson(new Date(0)));
    }

    private void assertParsed(String date, DefaultDateTypeAdapter adapter) throws IOException {
        TestCase.assertEquals(date, new Date(0), adapter.fromJson(AmplDefaultDateTypeAdapterTest.toLiteral(date)));
        TestCase.assertEquals("ISO 8601", new Date(0), adapter.fromJson(AmplDefaultDateTypeAdapterTest.toLiteral("1970-01-01T00:00:00Z")));
    }

    private static String toLiteral(String s) {
        return ('"' + s) + '"';
    }
}

