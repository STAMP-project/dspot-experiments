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

    public void testDateSerialization_add6347_add6374null6717_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347_add6374__8 = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347_add6374null6717 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6348_add6379null6745_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6348_add6379__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6348__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6348__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6348_add6379null6745 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6348_add6379null6746_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6348_add6379__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6348__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6348__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6348_add6379null6746 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347_add6374null6718_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347_add6374__8 = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347_add6374null6718 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6397_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347null6397 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6398_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347null6398 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347_add6376null6709_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6347_add6376__13 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347_add6376null6709 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6398_failAssert0_add6588_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6347null6398 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6347null6398_failAssert0_add6588 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347_add6375null6713_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6347_add6375__11 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347_add6375null6713 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347_add6375null6714_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6347_add6375__11 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347_add6375null6714 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347_add6375null6715_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6347_add6375__11 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347_add6375null6715 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6389_failAssert0_add6700_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat.getDateInstance(dateStyle, Locale.US);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6389 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6389_failAssert0_add6700 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347_add6374null6719_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347_add6374__8 = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347_add6374null6719 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6392_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6392 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6391_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6391 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6393_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6393 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6390_failAssert0_add6689_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    dateTypeAdapter.toJson(null);
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6390 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6390_failAssert0_add6689 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6348_add6382null6749_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6348__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6348_add6382__14 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6348__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6348_add6382null6749 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6397_failAssert0_add6587_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(currentDate);
                String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6347null6397 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6347null6397_failAssert0_add6587 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6348_add6383null6741_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6348__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6348_add6383__14 = formatter.format(currentDate);
            String o_testDateSerialization_add6348__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6348_add6383null6741 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6390_failAssert0_add6688_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    dateTypeAdapter.toJson(null);
                    dateTypeAdapter.toJson(null);
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6390 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6390_failAssert0_add6688 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6349_add6371null6737_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6349__10 = formatter.format(currentDate);
            String o_testDateSerialization_add6349_add6371__13 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6349__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6349_add6371null6737 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347_add6376null6710_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6347_add6376__13 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347_add6376null6710 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6392_failAssert0_add6707_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    formatter.format(currentDate);
                    formatter.format(currentDate);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6392 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6392_failAssert0_add6707 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6391_failAssert0_add6685_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6391 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6391_failAssert0_add6685 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6391_failAssert0_add6684_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    formatter.format(currentDate);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6391 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6391_failAssert0_add6684 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6392_failAssert0_add6704_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    dateTypeAdapter.toJson(null);
                    String dateString = dateTypeAdapter.toJson(null);
                    formatter.format(currentDate);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6392 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6392_failAssert0_add6704 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6392_failAssert0_add6703_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat.getDateInstance(dateStyle, Locale.US);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    formatter.format(currentDate);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6392 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6392_failAssert0_add6703 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6397_failAssert0_add6586_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(currentDate);
                formatter.format(currentDate);
                String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6347null6397 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6347null6397_failAssert0_add6586 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6398_failAssert0_add6589_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(currentDate);
                String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6347null6398 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6347null6398_failAssert0_add6589 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6349null6394_failAssert0_add6606_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add6349__10 = formatter.format(currentDate);
                String o_testDateSerialization_add6349__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6349null6394 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6349null6394_failAssert0_add6606 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6389_failAssert0_add6698_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat.getDateInstance(dateStyle, Locale.US);
                    DateFormat.getDateInstance(dateStyle, Locale.US);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6389 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6389_failAssert0_add6698 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6349_add6370null6733_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6349_add6370__10 = formatter.format(currentDate);
            String o_testDateSerialization_add6349__10 = formatter.format(currentDate);
            String o_testDateSerialization_add6349__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6349_add6370null6733 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6397_failAssert0_add6584_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(null);
                dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(currentDate);
                String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6347null6397 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6347null6397_failAssert0_add6584 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6392_failAssert0_add6706_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    formatter.format(currentDate);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6392 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6392_failAssert0_add6706 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6389_failAssert0_add6699_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat.getDateInstance(dateStyle, Locale.US);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    dateTypeAdapter.toJson(null);
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6389 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6389_failAssert0_add6699 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6349null6394_failAssert0_add6609_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add6349__10 = formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                String o_testDateSerialization_add6349__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6349null6394 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6349null6394_failAssert0_add6609 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6390_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6390 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6397_failAssert0_add6583_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(currentDate);
                String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6347null6397 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6347null6397_failAssert0_add6583 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6348null6400_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6348__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6348__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6348null6400 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6389_failAssert0_add6697_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat.getDateInstance(dateStyle, Locale.US);
                    DateFormat.getDateInstance(dateStyle, Locale.US);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6389 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6389_failAssert0_add6697 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6393_failAssert0_add6692_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat.getDateInstance(dateStyle, Locale.US);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6393 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6393_failAssert0_add6692 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347_add6377null6722_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6347_add6377__13 = formatter.format(currentDate);
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347_add6377null6722 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347_add6377null6721_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6347_add6377__13 = formatter.format(currentDate);
            String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6347_add6377null6721 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6389_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6389 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6391_failAssert0_add6682_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    formatter.format(currentDate);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6391 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6391_failAssert0_add6682 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6393_failAssert0_add6696_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6393 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6393_failAssert0_add6696 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6393_failAssert0_add6695_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    formatter.format(currentDate);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6393 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6393_failAssert0_add6695 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6393_failAssert0_add6694_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6393 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6393_failAssert0_add6694 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0_add6391_failAssert0_add6680_failAssert0() throws Exception {
        try {
            {
                {
                    int dateStyle = DateFormat.LONG;
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                    Date currentDate = new Date();
                    dateTypeAdapter.toJson(null);
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6391 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull6350_failAssert0_add6391_failAssert0_add6680 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6349_add6369null6726_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6349_add6369__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6349__10 = formatter.format(currentDate);
            String o_testDateSerialization_add6349__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6349_add6369null6726 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6349_add6369null6725_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add6349_add6369__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add6349__10 = formatter.format(currentDate);
            String o_testDateSerialization_add6349__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6349_add6369null6725 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6349_add6372null6729_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6349__10 = formatter.format(currentDate);
            String o_testDateSerialization_add6349_add6372__13 = formatter.format(currentDate);
            String o_testDateSerialization_add6349__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6349_add6372null6729 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6348null6400_failAssert0_add6631_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add6348__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                String o_testDateSerialization_add6348__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6348null6400 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6348null6400_failAssert0_add6631 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6348_add6380null6753_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6348_add6380__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6348__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6348__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6348_add6380null6753 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6348null6400_failAssert0_add6630_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add6348__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                formatter.format(currentDate);
                String o_testDateSerialization_add6348__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6348null6400 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6348null6400_failAssert0_add6630 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6398_failAssert0null6763_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6347null6398 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6347null6398_failAssert0null6763 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6398_failAssert0_add6590_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(currentDate);
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6347null6398 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6347null6398_failAssert0_add6590 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6398_failAssert0_add6591_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6347null6398 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6347null6398_failAssert0_add6591 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6349null6394_failAssert0_add6610_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add6349__10 = formatter.format(currentDate);
                formatter.format(currentDate);
                String o_testDateSerialization_add6349__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6349null6394 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6349null6394_failAssert0_add6610 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6347null6397_failAssert0null6761_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add6347__8 = dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add6347__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add6347null6397 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add6347null6397_failAssert0null6761 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull6350_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerializationnull6350 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6349null6394_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6349__10 = formatter.format(currentDate);
            String o_testDateSerialization_add6349__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6349null6394 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add6348_add6381null6757_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add6348_add6381__10 = formatter.format(currentDate);
            String o_testDateSerialization_add6348__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add6348__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add6348_add6381null6757 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString4null162_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-M<-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString4__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString4null162 should have thrown NullPointerException");
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

    public void testDatePatternnull11_failAssert0_add131_failAssert0_add1614_failAssert0() throws Exception {
        try {
            {
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
                junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add131 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add131_failAssert0_add1614 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString5_literalMutationString75null1956_failAssert0() throws Exception {
        try {
            String pattern = "  ";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString5__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString5_literalMutationString75null1956 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString4_literalMutationString66null1950_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-M<-d";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString4__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString4_literalMutationString66null1950 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_add9null150_failAssert0_add1473_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDatePattern_add9__10 = formatter.format(currentDate);
                String o_testDatePattern_add9__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePattern_add9null150 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePattern_add9null150_failAssert0_add1473 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add131_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add131 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add132_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add132 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString5_literalMutationString77null1953_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-&-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString5__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString5_literalMutationString77null1953 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString1_add120null1828_failAssert0() throws Exception {
        try {
            String pattern = "";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_literalMutationString1_add120__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDatePattern_literalMutationString1__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString1_add120null1828 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_literalMutationString83_failAssert0_literalMutationString1072_failAssert0() throws Exception {
        try {
            {
                {
                    String pattern = "yyy/y-MMdd";
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                    DateFormat formatter = new SimpleDateFormat(pattern);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_literalMutationString83 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_literalMutationString83_failAssert0_literalMutationString1072 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add131_failAssert0_literalMutationString946_failAssert0() throws Exception {
        try {
            {
                {
                    String pattern = "  ";
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                    DateFormat formatter = new SimpleDateFormat(pattern);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    formatter.format(currentDate);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add131 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add131_failAssert0_literalMutationString946 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString4null162_failAssert0_add1490_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-M<-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDatePattern_literalMutationString4__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePattern_literalMutationString4null162 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePattern_literalMutationString4null162_failAssert0_add1490 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString4null162_failAssert0_add1491_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-M<-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                String o_testDatePattern_literalMutationString4__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePattern_literalMutationString4null162 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePattern_literalMutationString4null162_failAssert0_add1491 should have thrown NullPointerException");
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

    public void testDatePattern_literalMutationString4_add118null1880_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-M<-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString4_add118__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDatePattern_literalMutationString4__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString4_add118null1880 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString1null165_failAssert0() throws Exception {
        try {
            String pattern = "";
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

    public void testDatePatternnull11_failAssert0_literalMutationString85_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-M-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_literalMutationString85 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_literalMutationString83_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyy/y-MM-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_literalMutationString83 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString4_add117null1872_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-M<-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_literalMutationString4_add117__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDatePattern_literalMutationString4__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString4_add117null1872 should have thrown NullPointerException");
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

    public void testDatePatternnull11_failAssert0_literalMutationString82_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyy_-MM-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_literalMutationString82 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString2null168_failAssert0() throws Exception {
        try {
            String pattern = "  ";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString2__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString2null168 should have thrown NullPointerException");
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

    public void testDatePattern_literalMutationString5null171_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-M-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString5__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString5null171 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_literalMutationString81_failAssert0() throws Exception {
        try {
            {
                String pattern = "  ";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_literalMutationString81 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add129_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add129 should have thrown NullPointerException");
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

    public void testDatePatternnull11_failAssert0_literalMutationString80_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_literalMutationString80 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString4_literalMutationString64null1944_failAssert0() throws Exception {
        try {
            String pattern = "  ";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString4__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString4_literalMutationString64null1944 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add130_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add130 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString4_add119null1876_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-M<-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString4_add119__10 = formatter.format(currentDate);
            String o_testDatePattern_literalMutationString4__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString4_add119null1876 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_add9null150_failAssert0_add1469_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDatePattern_add9__10 = formatter.format(currentDate);
                String o_testDatePattern_add9__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePattern_add9null150 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePattern_add9null150_failAssert0_add1469 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString4null162_failAssert0_literalMutationString843_failAssert0() throws Exception {
        try {
            {
                String pattern = "";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDatePattern_literalMutationString4__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePattern_literalMutationString4null162 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePattern_literalMutationString4null162_failAssert0_literalMutationString843 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add132_failAssert0_add1616_failAssert0() throws Exception {
        try {
            {
                {
                    String pattern = "yyyy-MM-dd";
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                    DateFormat formatter = new SimpleDateFormat(pattern);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add132 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add132_failAssert0_add1616 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_add9null150_failAssert0_literalMutationString834_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyy-MM-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDatePattern_add9__10 = formatter.format(currentDate);
                String o_testDatePattern_add9__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePattern_add9null150 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePattern_add9null150_failAssert0_literalMutationString834 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString1_add122null1832_failAssert0() throws Exception {
        try {
            String pattern = "";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString1_add122__10 = formatter.format(currentDate);
            String o_testDatePattern_literalMutationString1__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString1_add122null1832 should have thrown NullPointerException");
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

