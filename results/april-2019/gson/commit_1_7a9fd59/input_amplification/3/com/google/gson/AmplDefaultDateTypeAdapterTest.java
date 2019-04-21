package com.google.gson;


import java.io.IOException;
import java.text.DateFormat;
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

    public void testDateSerialization_add7498null7519_failAssert0_add7611_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7519 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7519_failAssert0_add7611 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7519_failAssert0_add7612_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7519 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7519_failAssert0_add7612 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7519_failAssert0_add7610_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(currentDate);
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7519 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7519_failAssert0_add7610 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7519_failAssert0_add7613_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7519 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7519_failAssert0_add7613 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7499null7521_failAssert0_add7560_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7499null7521 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7499null7521_failAssert0_add7560 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7497null7516_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat.getDateInstance(dateStyle, Locale.US);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add7497null7516 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7518_failAssert0_add7625_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7518 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7518_failAssert0_add7625 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7500null7513_failAssert0_add7587_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7500null7513 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7500null7513_failAssert0_add7587 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7519_failAssert0_add7609_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(currentDate);
                dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7519 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7519_failAssert0_add7609 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7499null7521_failAssert0_add7563_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7499null7521 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7499null7521_failAssert0_add7563 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7518_failAssert0_add7623_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7518 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7518_failAssert0_add7623 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7500null7513_failAssert0_add7589_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7500null7513 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7500null7513_failAssert0_add7589 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7518_failAssert0_add7622_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7518 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7518_failAssert0_add7622 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7500null7513_failAssert0_add7588_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7500null7513 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7500null7513_failAssert0_add7588 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7497null7516_failAssert0_add7606_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7497null7516 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7497null7516_failAssert0_add7606 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7499null7521_failAssert0_add7561_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7499null7521 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7499null7521_failAssert0_add7561 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7499null7521_failAssert0_add7562_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7499null7521 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7499null7521_failAssert0_add7562 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7518_failAssert0_add7624_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(currentDate);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7518 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7518_failAssert0_add7624 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7497null7516_failAssert0_add7607_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7497null7516 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7497null7516_failAssert0_add7607 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7497null7516_failAssert0_add7602_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7497null7516 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7497null7516_failAssert0_add7602 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7518_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            dateTypeAdapter.toJson(null);
            String dateString = dateTypeAdapter.toJson(currentDate);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add7498null7518 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7497null7516_failAssert0_add7605_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7497null7516 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7497null7516_failAssert0_add7605 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7519_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            dateTypeAdapter.toJson(currentDate);
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add7498null7519 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7518_failAssert0_add7621_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7518 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7518_failAssert0_add7621 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7499null7521_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add7499null7521 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7497null7516_failAssert0_add7604_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7497null7516 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7497null7516_failAssert0_add7604 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7497null7516_failAssert0_add7603_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7497null7516 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7497null7516_failAssert0_add7603 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7518_failAssert0_add7620_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7518 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7518_failAssert0_add7620 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7500null7513_failAssert0_add7586_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7500null7513 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7500null7513_failAssert0_add7586 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7519_failAssert0_add7608_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(currentDate);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7519 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7519_failAssert0_add7608 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7500null7513_failAssert0_add7585_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7500null7513 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7500null7513_failAssert0_add7585 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7500null7513_failAssert0_add7584_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                formatter.format(currentDate);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7500null7513 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7500null7513_failAssert0_add7584 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7500null7513_failAssert0() throws Exception {
        try {
            int dateStyle = DateFormat.LONG;
            DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
            DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
            Date currentDate = new Date();
            String dateString = dateTypeAdapter.toJson(null);
            formatter.format(currentDate);
            AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
            junit.framework.TestCase.fail("testDateSerialization_add7500null7513 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7499null7521_failAssert0_add7557_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat.getDateInstance(dateStyle, Locale.US);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7499null7521 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7499null7521_failAssert0_add7557 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7518_failAssert0null7649_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7518 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7518_failAssert0null7649 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7499null7521_failAssert0_add7558_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7499null7521 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7499null7521_failAssert0_add7558 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7498null7519_failAssert0null7645_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                dateTypeAdapter.toJson(null);
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7498null7519 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7498null7519_failAssert0null7645 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            TestCase.assertEquals(null, expected.getMessage());
        }
    }

    public void testDateSerialization_add7499null7521_failAssert0_add7559_failAssert0() throws Exception {
        try {
            {
                int dateStyle = DateFormat.LONG;
                DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
                DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
                Date currentDate = new Date();
                String dateString = dateTypeAdapter.toJson(null);
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                AmplDefaultDateTypeAdapterTest.toLiteral(formatter.format(currentDate));
                junit.framework.TestCase.fail("testDateSerialization_add7499null7521 should have thrown NullPointerException");
            }
            junit.framework.TestCase.fail("testDateSerialization_add7499null7521_failAssert0_add7559 should have thrown NullPointerException");
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

