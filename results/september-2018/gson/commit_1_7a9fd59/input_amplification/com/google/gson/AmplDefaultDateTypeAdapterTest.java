package com.google.gson;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class AmplDefaultDateTypeAdapterTest {
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

    @Test(timeout = 10000)
    public void testDateSerialization_add5136_add5165null5512_failAssert151() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5136__10 = formatter.format(currentDate);
            String o_testDateSerialization_add5136_add5165__13 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5136__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5136_add5165null5512 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5135null5186_failAssert125() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5135__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5135__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5135null5186 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5135_add5182null5362_failAssert154() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5135__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5135_add5182__14 = formatter.format(currentDate);
            String o_testDateSerialization_add5135__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5135_add5182null5362 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5135_add5177null5503_failAssert133() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5135__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5135_add5177__14 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5135__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5135_add5177null5503 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5134_add5180null5407_failAssert144() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add5134__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add5134_add5180__13 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5134__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5134_add5180null5407 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5135_add5172null5529_failAssert135() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5135_add5172__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5135__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5135__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5135_add5172null5529 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5134null5190_failAssert123_add5387() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add5134__8 = dateTypeAdapter.toJson(currentDate);
            Assert.assertEquals("\"October 10, 2018\"", o_testDateSerialization_add5134__8);
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5134__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5134null5190 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5134null5190_failAssert123_add5367() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add5134null5190_failAssert123_add5367__10 = dateTypeAdapter.toJson(currentDate);
            Assert.assertEquals("\"October 10, 2018\"", o_testDateSerialization_add5134null5190_failAssert123_add5367__10);
            String o_testDateSerialization_add5134__8 = dateTypeAdapter.toJson(currentDate);
            Assert.assertEquals("\"October 10, 2018\"", o_testDateSerialization_add5134__8);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5134__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5134null5190 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5134null5190_failAssert123_add5400() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add5134__8 = dateTypeAdapter.toJson(currentDate);
            Assert.assertEquals("\"October 10, 2018\"", o_testDateSerialization_add5134__8);
            String dateString = dateTypeAdapter.toJson(null);
            formatter.format(currentDate);
            String o_testDateSerialization_add5134__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5134null5190 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5134null5190_failAssert123() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add5134__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5134__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5134null5190 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5134null5189_failAssert122() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add5134__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add5134__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5134null5189 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5135_add5167null5389_failAssert140() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add5135_add5167__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add5135__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5135__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5135_add5167null5389 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerializationnull5137_failAssert120() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerializationnull5137 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5135_add5174null5526_failAssert157() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5135_add5174__10 = formatter.format(currentDate);
            String o_testDateSerialization_add5135__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5135__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5135_add5174null5526 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5134null5190_failAssert123_add5408() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add5134__8 = dateTypeAdapter.toJson(currentDate);
            Assert.assertEquals("\"October 10, 2018\"", o_testDateSerialization_add5134__8);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5134__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5134null5190 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            expected.getMessage();
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5135_add5167null5399_failAssert139() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add5135_add5167__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5135__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5135__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5135_add5167null5399 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5134null5190_failAssert123_add5377() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add5134__8 = dateTypeAdapter.toJson(currentDate);
            Assert.assertEquals("\"October 10, 2018\"", o_testDateSerialization_add5134__8);
            dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5134__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5134null5190 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5136null5178_failAssert128() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5136__10 = formatter.format(currentDate);
            String o_testDateSerialization_add5136__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5136null5178 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDateSerialization_add5134_add5180null5415_failAssert145() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add5134__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add5134_add5180__13 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add5134__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDateSerialization_add5134_add5180null5415 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_literalMutationString4null177_failAssert20() throws Exception {
        try {
            String pattern = "yy[y-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString4__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_literalMutationString4null177 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_literalMutationString1null138_failAssert6() throws Exception {
        try {
            String pattern = "yyyy-MM-%dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString1__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_literalMutationString1null138 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePatternnull11_failAssert2_add149() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Assert.assertEquals("yyyy-MM-dd", pattern);
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            formatter.format(currentDate);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePatternnull11 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add8null181_failAssert36() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add8__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add8__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add8null181 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add8_add174null1596_failAssert88() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add8_add174__10 = formatter.format(currentDate);
            String o_testDatePattern_add8__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add8__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add8_add174null1596 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_literalMutationString3null141_failAssert15() throws Exception {
        try {
            String pattern = "  ";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString3__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_literalMutationString3null141 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePatternnull11_failAssert2_add140() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Assert.assertEquals("yyyy-MM-dd", pattern);
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePatternnull11 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add8_add172null1817_failAssert60() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add8_add172__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add8__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add8__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add8_add172null1817 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePatternnull11_failAssert2_literalMutationString84() throws Exception {
        try {
            String pattern = "  ";
            Assert.assertEquals("  ", pattern);
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePatternnull11 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add8_add170null827_failAssert79() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_add8_add170__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDatePattern_add8__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add8__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add8_add170null827 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add8_add170null841_failAssert80() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_add8_add170__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add8__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add8__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add8_add170null841 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add9null142_failAssert42() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add9__10 = formatter.format(currentDate);
            String o_testDatePattern_add9__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add9null142 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePatternnull11_failAssert2() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePatternnull11 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add7_add119null1189_failAssert73() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_add7__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add7_add119__13 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add7__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add7_add119null1189 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePatternnull11_failAssert2_add156() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Assert.assertEquals("yyyy-MM-dd", pattern);
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePatternnull11 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            expected.getMessage();
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add7_add119null1166_failAssert74() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_add7__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDatePattern_add7_add119__13 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add7__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add7_add119null1166 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add7null153_failAssert30() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_add7__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDatePattern_add7__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add7null153 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePatternnull11_failAssert2_add136() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            Assert.assertEquals("yyyy-MM-dd", pattern);
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePatternnull11 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add9_add113null861_failAssert95() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add9__10 = formatter.format(currentDate);
            String o_testDatePattern_add9_add113__13 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add9__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add9_add113null861 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePatternnull11_failAssert2_literalMutationString111() throws Exception {
        try {
            String pattern = "yyyy-MM-d";
            Assert.assertEquals("yyyy-MM-d", pattern);
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePatternnull11 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_literalMutationString1_literalMutationString86null1962_failAssert101() throws Exception {
        try {
            String pattern = "yyyyS-MM-%dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString1__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_literalMutationString1_literalMutationString86null1962 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add7null158_failAssert31() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_add7__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add7__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add7null158 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePatternnull11_failAssert2_literalMutationString73() throws Exception {
        try {
            String pattern = "";
            Assert.assertEquals("", pattern);
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePatternnull11 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add8_add178null1103_failAssert86() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add8__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add8_add178__14 = formatter.format(currentDate);
            String o_testDatePattern_add8__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add8_add178null1103 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePatternnull11_failAssert2_literalMutationString99() throws Exception {
        try {
            String pattern = "yy4yy-MM-dd";
            Assert.assertEquals("yy4yy-MM-dd", pattern);
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePatternnull11 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_add8_add175null1736_failAssert58() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add8__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add8_add175__14 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_add8__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_add8_add175null1736 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_literalMutationString5null122_failAssert26() throws Exception {
        try {
            String pattern = "yyyy-MMdd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString5__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_literalMutationString5null122 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDatePattern_literalMutationString2null110_failAssert10() throws Exception {
        try {
            String pattern = "";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString2__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            org.junit.Assert.fail("testDatePattern_literalMutationString2null110 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    private void assertFormatted(String formatted, DefaultDateTypeAdapter adapter) {
        Assert.assertEquals(AmplDefaultDateTypeAdapterTest.toLiteral(formatted), adapter.toJson(new Date(0)));
    }

    private void assertParsed(String date, DefaultDateTypeAdapter adapter) throws IOException {
        Assert.assertEquals(date, new Date(0), adapter.fromJson(AmplDefaultDateTypeAdapterTest.toLiteral(date)));
        Assert.assertEquals("ISO 8601", new Date(0), adapter.fromJson(AmplDefaultDateTypeAdapterTest.toLiteral("1970-01-01T00:00:00Z")));
    }

    private static String toLiteral(String s) {
        return ('"' + s) + '"';
    }
}

