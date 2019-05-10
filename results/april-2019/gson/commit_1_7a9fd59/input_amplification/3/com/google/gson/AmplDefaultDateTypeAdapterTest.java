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

    public void testDateSerialization_add4546null4591_failAssert0_add4892_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add4546__10 = formatter.format(currentDate);
                formatter.format(currentDate);
                String o_testDateSerialization_add4546__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add4546null4591 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add4546null4591_failAssert0_add4892 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4546null4591_failAssert0_add4890_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                String o_testDateSerialization_add4546__10 = formatter.format(currentDate);
                String o_testDateSerialization_add4546__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add4546null4591 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add4546null4591_failAssert0_add4890 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4546_add4568null4946_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add4546__10 = formatter.format(currentDate);
            String o_testDateSerialization_add4546_add4568__13 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add4546__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4546_add4568null4946 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544_add4572null4915_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add4544_add4572__11 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4544_add4572null4915 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull4547_failAssert0_add4581_failAssert0_add4808_failAssert0() throws Exception {
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
                    junit.framework.TestCase.fail("testDateSerializationnull4547 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4581 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4581_failAssert0_add4808 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544null4594_failAssert0null4973_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add4544null4594 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add4544null4594_failAssert0null4973 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544_add4572null4916_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add4544_add4572__11 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4544_add4572null4916 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull4547_failAssert0_add4582_failAssert0_add4783_failAssert0() throws Exception {
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
                    junit.framework.TestCase.fail("testDateSerializationnull4547 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4582 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4582_failAssert0_add4783 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544null4595_failAssert0_add4861_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add4544null4595 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add4544null4595_failAssert0_add4861 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544_add4571null4911_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add4544_add4571__8 = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4544_add4571null4911 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4545_add4579null4922_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add4545__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add4545_add4579__14 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add4545__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4545_add4579null4922 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544null4595_failAssert0null4975_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add4544null4595 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add4544null4595_failAssert0null4975 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4545null4597_failAssert0_add4869_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                String o_testDateSerialization_add4545__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                String o_testDateSerialization_add4545__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add4545null4597 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add4545null4597_failAssert0_add4869 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull4547_failAssert0_add4583_failAssert0_add4798_failAssert0() throws Exception {
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
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDateSerializationnull4547 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4583 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4583_failAssert0_add4798 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544null4594_failAssert0_add4852_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(null);
                dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(currentDate);
                String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add4544null4594 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add4544null4594_failAssert0_add4852 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4546null4591_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add4546__10 = formatter.format(currentDate);
            String o_testDateSerialization_add4546__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4546null4591 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4545null4597_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add4545__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add4545__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4545null4597 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544_add4574null4919_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add4544_add4574__13 = formatter.format(currentDate);
            String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4544_add4574null4919 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544_add4571null4910_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add4544_add4571__8 = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4544_add4571null4910 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544null4595_failAssert0_add4859_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add4544null4595 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add4544null4595_failAssert0_add4859 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4546_add4567null4938_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add4546_add4567__10 = formatter.format(currentDate);
            String o_testDateSerialization_add4546__10 = formatter.format(currentDate);
            String o_testDateSerialization_add4546__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4546_add4567null4938 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544null4594_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4544null4594 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull4547_failAssert0_add4582_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull4547 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4582 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull4547_failAssert0_add4583_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull4547 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4583 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544null4595_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4544null4595 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull4547_failAssert0_add4582_failAssert0_add4782_failAssert0() throws Exception {
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
                    junit.framework.TestCase.fail("testDateSerializationnull4547 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4582 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4582_failAssert0_add4782 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4545_add4576null4931_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String o_testDateSerialization_add4545_add4576__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDateSerialization_add4545__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            String o_testDateSerialization_add4545__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add4545_add4576null4931 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4545null4597_failAssert0_add4871_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDateSerialization_add4545__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                String o_testDateSerialization_add4545__12 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add4545null4597 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add4545null4597_failAssert0_add4871 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull4547_failAssert0_add4585_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull4547 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4585 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull4547_failAssert0_add4584_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull4547 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4584 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull4547_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerializationnull4547 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add4544null4594_failAssert0_add4853_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String o_testDateSerialization_add4544__8 = dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                String o_testDateSerialization_add4544__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add4544null4594 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add4544null4594_failAssert0_add4853 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull4547_failAssert0_add4581_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerializationnull4547 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4581 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerializationnull4547_failAssert0_add4583_failAssert0_add4796_failAssert0() throws Exception {
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
                    junit.framework.TestCase.fail("testDateSerializationnull4547 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4583 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerializationnull4547_failAssert0_add4583_failAssert0_add4796 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_add7null151_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_add7__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add7__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_add7null151 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_add7null150_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_add7__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDatePattern_add7__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_add7null150 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add137_failAssert0_add1081_failAssert0() throws Exception {
        try {
            {
                {
                    String pattern = "yyyy-MM-dd";
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                    DateFormat formatter = new SimpleDateFormat(pattern);
                    Date currentDate = new Date();
                    dateTypeAdapter.toJson(null);
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add137 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add137_failAssert0_add1081 should have thrown NullPointerException");
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

    public void testDatePattern_literalMutationString3null162_failAssert0_add1199_failAssert0() throws Exception {
        try {
            {
                String pattern = "Xyyy-MM-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDatePattern_literalMutationString3__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePattern_literalMutationString3null162 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePattern_literalMutationString3null162_failAssert0_add1199 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add137_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add137 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString1null168_failAssert0_add1168_failAssert0() throws Exception {
        try {
            {
                String pattern = "";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDatePattern_literalMutationString1__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePattern_literalMutationString1null168 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePattern_literalMutationString1null168_failAssert0_add1168 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add139_failAssert0_literalMutationString664_failAssert0() throws Exception {
        try {
            {
                {
                    String pattern = "yyyy-M@-dd";
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                    DateFormat formatter = new SimpleDateFormat(pattern);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    formatter.format(currentDate);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add139 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add139_failAssert0_literalMutationString664 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString1null168_failAssert0_literalMutationString752_failAssert0() throws Exception {
        try {
            {
                String pattern = "  ";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDatePattern_literalMutationString1__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePattern_literalMutationString1null168 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePattern_literalMutationString1null168_failAssert0_literalMutationString752 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add139_failAssert0_literalMutationString662_failAssert0() throws Exception {
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
                junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add139 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add139_failAssert0_literalMutationString662 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString5_failAssert0_literalMutationString88_failAssert0null1503_failAssert0() throws Exception {
        try {
            {
                {
                    String pattern = "";
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                    DateFormat formatter = new SimpleDateFormat(pattern);
                    Date currentDate = new Date();
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDatePattern_literalMutationString5 should have thrown IllegalArgumentException");
                }
                junit.framework.TestCase.fail("testDatePattern_literalMutationString5_failAssert0_literalMutationString88 should have thrown AssertionFailedError");
            }
            junit.framework.TestCase.fail("testDatePattern_literalMutationString5_failAssert0_literalMutationString88_failAssert0null1503 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_literalMutationString95_failAssert0() throws Exception {
        try {
            {
                String pattern = "yyyy-MM;dd";
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
            String pattern = "Xyyy-MM-dd";
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

    public void testDatePatternnull11_failAssert0_literalMutationString94_failAssert0() throws Exception {
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
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_literalMutationString94 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString4null174_failAssert0() throws Exception {
        try {
            String pattern = "yyy-MM-dd";
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

    public void testDatePattern_literalMutationString3null162_failAssert0_literalMutationString777_failAssert0() throws Exception {
        try {
            {
                String pattern = "X$yyy-MM-dd";
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                DateFormat formatter = new SimpleDateFormat(pattern);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                String o_testDatePattern_literalMutationString3__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDatePattern_literalMutationString3null162 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePattern_literalMutationString3null162_failAssert0_literalMutationString777 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString1null168_failAssert0() throws Exception {
        try {
            String pattern = "";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString1__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString1null168 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString6null171_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-M;M-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString6__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString6null171 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add139_failAssert0_add1088_failAssert0() throws Exception {
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
                junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add139 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add139_failAssert0_add1088 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString3_add118null1357_failAssert0() throws Exception {
        try {
            String pattern = "Xyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_literalMutationString3_add118__8 = dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString3__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString3_add118null1357 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString3_literalMutationString66null1408_failAssert0() throws Exception {
        try {
            String pattern = "Xyyy-]M-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString3__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString3_literalMutationString66null1408 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add139_failAssert0_add1084_failAssert0() throws Exception {
        try {
            {
                {
                    String pattern = "yyyy-MM-dd";
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                    DateFormat formatter = new SimpleDateFormat(pattern);
                    Date currentDate = new Date();
                    dateTypeAdapter.toJson(null);
                    String dateString = dateTypeAdapter.toJson(null);
                    formatter.format(currentDate);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add139 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add139_failAssert0_add1084 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString1_add124null1330_failAssert0() throws Exception {
        try {
            String pattern = "";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String o_testDatePattern_literalMutationString1_add124__8 = dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            String o_testDatePattern_literalMutationString1__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString1_add124null1330 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_add9null158_failAssert0() throws Exception {
        try {
            String pattern = "yyyy-MM-dd";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_add9__10 = formatter.format(currentDate);
            String o_testDatePattern_add9__11 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_add9null158 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePatternnull11_failAssert0_add137_failAssert0_add1079_failAssert0() throws Exception {
        try {
            {
                {
                    String pattern = "yyyy-MM-dd";
                    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
                    DateFormat formatter = new SimpleDateFormat(pattern);
                    Date currentDate = new Date();
                    dateTypeAdapter.toJson(null);
                    dateTypeAdapter.toJson(null);
                    String dateString = dateTypeAdapter.toJson(null);
                    AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                    junit.framework.TestCase.fail("testDatePatternnull11 should have thrown NullPointerException");
                }
                junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add137 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDatePatternnull11_failAssert0_add137_failAssert0_add1079 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDatePattern_literalMutationString2null165_failAssert0() throws Exception {
        try {
            String pattern = "  ";
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
            DateFormat formatter = new SimpleDateFormat(pattern);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            String o_testDatePattern_literalMutationString2__10 = AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDatePattern_literalMutationString2null165 should have thrown NullPointerException");
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

