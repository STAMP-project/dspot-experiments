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

    public void testDateSerializationnull1410_failAssert0_add1447_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull1410 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull1410_failAssert0_add1447 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull1410_failAssert0_add1446_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull1410 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull1410_failAssert0_add1446 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add1407null1461_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add1407__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add1407__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add1407null1461 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add1407null1460_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add1407__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add1407__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add1407null1460 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add1408null1457_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add1408__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add1408__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add1408null1457 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull1410_failAssert0_add1445_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull1410 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull1410_failAssert0_add1445 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull1410_failAssert0_add1444_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull1410 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull1410_failAssert0_add1444 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add1409null1454_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add1409__10 = formatter.format(currentDate);
            String o_testDateSerialization_add1409__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add1409null1454 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull1410_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerializationnull1410 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull1410_failAssert0_add1448_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull1410 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull1410_failAssert0_add1448 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_literalMutationString95_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM>-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_literalMutationString95 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString3null162_failAssert0() throws Exception {
        try {
            String pattern = "  ";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString3__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString3null162 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_add9null150_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add9__10 = formatter.format(currentDate);
            String o_testDatePattern_add9__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_add9null150 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString4null174_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-.M-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString4__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString4null174 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_add7null154_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_add7__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDatePattern_add7__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_add7null154 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_literalMutationString93_failAssert0() throws Exception {
        try {
            {
                String pattern = "";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_literalMutationString93 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add139_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add139 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add140_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add140 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString2null171_failAssert0() throws Exception {
        try {
            String pattern = "";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString2__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString2null171 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_add7null155_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_add7__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add7__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_add7null155 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString6null168_failAssert0() throws Exception {
        try {
            String pattern = "yyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString6__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString6null168 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_add8null158_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add8__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add8__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_add8null158 should have thrown NullPointerException");
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

    public void testDatePattern_literalMutationString1null165_failAssert0() throws Exception {
        try {
            String pattern = "yyyy7-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString1__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString1null165 should have thrown NullPointerException");
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

