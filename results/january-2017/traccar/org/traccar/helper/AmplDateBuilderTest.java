

package org.traccar.helper;


public class AmplDateBuilderTest {
    @org.junit.Test
    public void testDateBuilder() throws java.text.ParseException {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        org.traccar.helper.DateBuilder dateBuilder = new org.traccar.helper.DateBuilder().setDate(2015, 10, 20).setTime(1, 21, 11);
        org.junit.Assert.assertEquals(dateFormat.parse("2015-10-20 01:21:11"), dateBuilder.getDate());
    }

    /* amplification of org.traccar.helper.DateBuilderTest#testDateBuilder */
    @org.junit.Test(timeout = 1000)
    public void testDateBuilder_cf18() throws java.text.ParseException {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        org.traccar.helper.DateBuilder dateBuilder = new org.traccar.helper.DateBuilder().setDate(2015, 10, 20).setTime(1, 21, 11);
        // StatementAdderOnAssert create random local variable
        int vc_7 = 995075168;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_7, 995075168);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.DateBuilder vc_6 = new org.traccar.helper.DateBuilder();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_6).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_6).setCurrentDate().equals(vc_6));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_6).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_6).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_6));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_6).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_6).setCurrentDate()).setCurrentDate().equals(vc_6));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_6).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_6).getDate()).getMinutes(), 0);
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf18__13 = // StatementAdderMethod cloned existing statement
vc_6.addMinute(vc_7);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).getDate()).getMinutes(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf18__13.equals(vc_6));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 10:08:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).getDate()).getHours(), 11);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).toGMTString(), "8 Jan 2017 10:08:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).getHours(), 11);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate().equals(o_testDateBuilder_cf18__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).getDate()).getTime(), 1483870080000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate().equals(vc_6));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 11:08:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_6));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf18__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).setCurrentDate().equals(vc_6));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).toLocaleString(), "Jan 8, 2017 11:08:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).getMinutes(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).toInstant()).getEpochSecond(), 1483870080L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf18__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).getTime(), 1483870080000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).getDate()).toInstant()).toEpochMilli(), 1483870080000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf18__13).setCurrentDate()).getDate()).getMonth(), 0);
        org.junit.Assert.assertEquals(dateFormat.parse("2015-10-20 01:21:11"), dateBuilder.getDate());
    }

    /* amplification of org.traccar.helper.DateBuilderTest#testDateBuilder */
    @org.junit.Test(timeout = 1000)
    public void testDateBuilder_cf96() throws java.text.ParseException {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        org.traccar.helper.DateBuilder dateBuilder = new org.traccar.helper.DateBuilder().setDate(2015, 10, 20).setTime(1, 21, 11);
        // StatementAdderOnAssert create random local variable
        int vc_48 = -374629420;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_48, -374629420);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.DateBuilder vc_47 = new org.traccar.helper.DateBuilder();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_47).setCurrentDate().equals(vc_47));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_47));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_47).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_47).setCurrentDate()).setCurrentDate().equals(vc_47));
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf96__13 = // StatementAdderMethod cloned existing statement
vc_47.setYear(vc_48);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf96__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf96__13.equals(vc_47));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_47));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).setCurrentDate().equals(vc_47));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf96__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate().equals(o_testDateBuilder_cf96__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).setCurrentDate().equals(vc_47));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf96__13).getDate()).getHours(), 1);
        org.junit.Assert.assertEquals(dateFormat.parse("2015-10-20 01:21:11"), dateBuilder.getDate());
    }

    /* amplification of org.traccar.helper.DateBuilderTest#testDateBuilder */
    @org.junit.Test(timeout = 1000)
    public void testDateBuilder_cf54_cf754() throws java.text.ParseException {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        org.traccar.helper.DateBuilder dateBuilder = new org.traccar.helper.DateBuilder().setDate(2015, 10, 20).setTime(1, 21, 11);
        // StatementAdderOnAssert create random local variable
        int vc_27 = 8524288;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_27, 8524288);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_27, 8524288);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.DateBuilder vc_26 = new org.traccar.helper.DateBuilder();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_26).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_26).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_26).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_26).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf54__13 = // StatementAdderMethod cloned existing statement
vc_26.setMillis(vc_27);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 3:22:04 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf54__13.equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate().equals(o_testDateBuilder_cf54__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).toInstant()).getNano(), 288000000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).getSeconds(), 4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).getDate()).getTime(), 1483842124288L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).getMinutes(), 22);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).toLocaleString(), "Jan 8, 2017 3:22:04 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).getHours(), 3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).getDate()).getSeconds(), 4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).getDate()).getMinutes(), 22);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).toInstant()).toEpochMilli(), 1483842124288L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).toGMTString(), "8 Jan 2017 02:22:04 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).getDate()).getHours(), 3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf54__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf54__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).getTime(), 1483842124288L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).toInstant()).getEpochSecond(), 1483842124L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 02:22:04 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).setCurrentDate()).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54__13).getDate()).getTimezoneOffset(), -60);
        // StatementAdderOnAssert create random local variable
        int vc_252 = -1199334510;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_252, -1199334510);
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf54_cf754__139 = // StatementAdderMethod cloned existing statement
vc_26.addMinute(vc_252);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).getDate()).getMinutes(), 52);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).getDate()).getSeconds(), 4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate().equals(o_testDateBuilder_cf54_cf754__139));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).getTime(), -125786830075712L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).getHours(), 22);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).getDate()).getHours(), 22);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).getDay(), 4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).toInstant()).getEpochSecond(), -125786830076L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).getDate()).getTime(), -125786830075712L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).getSeconds(), 4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 21:52:04 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).getMinutes(), 52);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf54_cf754__139));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).getDate()).getDay(), 4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).toInstant()).getNano(), 288000000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).toLocaleString(), "Jan 8, 2017 10:52:04 PM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 10:52:04 PM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate().equals(vc_26));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).toInstant()).toEpochMilli(), -125786830075712L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).getDate()).toGMTString(), "8 Jan 2017 21:52:04 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf54_cf754__139).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf54_cf754__139));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf54_cf754__139.equals(vc_26));
        org.junit.Assert.assertEquals(dateFormat.parse("2015-10-20 01:21:11"), dateBuilder.getDate());
    }

    /* amplification of org.traccar.helper.DateBuilderTest#testDateBuilder */
    @org.junit.Test(timeout = 1000)
    public void testDateBuilder_cf66_cf1119() throws java.text.ParseException {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        org.traccar.helper.DateBuilder dateBuilder = new org.traccar.helper.DateBuilder().setDate(2015, 10, 20).setTime(1, 21, 11);
        // StatementAdderOnAssert create random local variable
        int vc_33 = 443792523;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_33, 443792523);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_33, 443792523);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.DateBuilder vc_32 = new org.traccar.helper.DateBuilder();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_32).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_32).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf66__13 = // StatementAdderMethod cloned existing statement
vc_32.setMonth(vc_33);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf66__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf66__13.equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate().equals(o_testDateBuilder_cf66__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf66__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getHours(), 1);
        // StatementAdderOnAssert create random local variable
        long vc_353 = 6308045893176125681L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_353, 6308045893176125681L);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.DateBuilder vc_352 = new org.traccar.helper.DateBuilder();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_352).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf66_cf1119__141 = // StatementAdderMethod cloned existing statement
vc_352.addSeconds(vc_353);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).toLocaleString(), "Jan 8, 2017 3:10:28 PM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getTime(), -125786857771672L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 3:10:28 PM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getDay(), 4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).toInstant()).getNano(), 328000000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).toInstant()).getEpochSecond(), -125786857772L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getMinutes(), 10);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate().equals(o_testDateBuilder_cf66_cf1119__141));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getSeconds(), 28);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).toInstant()).toEpochMilli(), -125786857771672L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getSeconds(), 28);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getMinutes(), 10);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getHours(), 15);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf66_cf1119__141.equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf66_cf1119__141));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getDay(), 4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).toGMTString(), "8 Jan 2017 14:10:28 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 14:10:28 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getHours(), 15);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf66_cf1119__141));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getTime(), -125786857771672L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getMonth(), 0);
        org.junit.Assert.assertEquals(dateFormat.parse("2015-10-20 01:21:11"), dateBuilder.getDate());
    }

    /* amplification of org.traccar.helper.DateBuilderTest#testDateBuilder */
    @org.junit.Test(timeout = 1000)
    public void testDateBuilder_cf65_cf921() throws java.text.ParseException {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        org.traccar.helper.DateBuilder dateBuilder = new org.traccar.helper.DateBuilder().setDate(2015, 10, 20).setTime(1, 21, 11);
        // StatementAdderOnAssert create literal from method
        int int_vc_7 = 20;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_7, 20);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_7, 20);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.DateBuilder vc_32 = new org.traccar.helper.DateBuilder();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_32).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_32).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf65__13 = // StatementAdderMethod cloned existing statement
vc_32.setMonth(int_vc_7);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate().equals(o_testDateBuilder_cf65__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf65__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf65__13.equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf65__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf65__13).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator replace invocation
        java.util.Date o_testDateBuilder_cf65_cf921__137 = // StatementAdderMethod cloned existing statement
dateBuilder.getDate();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)o_testDateBuilder_cf65_cf921__137).getSeconds(), 11);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)o_testDateBuilder_cf65_cf921__137).getMinutes(), 21);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)o_testDateBuilder_cf65_cf921__137).toInstant()).toEpochMilli(), 1445304071000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)o_testDateBuilder_cf65_cf921__137).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)o_testDateBuilder_cf65_cf921__137).toGMTString(), "20 Oct 2015 01:21:11 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)o_testDateBuilder_cf65_cf921__137).getDay(), 2);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)o_testDateBuilder_cf65_cf921__137).toInstant()).getEpochSecond(), 1445304071L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)o_testDateBuilder_cf65_cf921__137).getTimezoneOffset(), -120);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)o_testDateBuilder_cf65_cf921__137).getHours(), 3);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)o_testDateBuilder_cf65_cf921__137).getDate(), 20);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)o_testDateBuilder_cf65_cf921__137).getTime(), 1445304071000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)o_testDateBuilder_cf65_cf921__137).getYear(), 115);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)o_testDateBuilder_cf65_cf921__137).toLocaleString(), "Oct 20, 2015 3:21:11 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)o_testDateBuilder_cf65_cf921__137).getMonth(), 9);
        org.junit.Assert.assertEquals(dateFormat.parse("2015-10-20 01:21:11"), dateBuilder.getDate());
    }

    /* amplification of org.traccar.helper.DateBuilderTest#testDateBuilder */
    @org.junit.Test(timeout = 1000)
    public void testDateBuilder_cf71_cf1298_cf1717() throws java.text.ParseException {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        org.traccar.helper.DateBuilder dateBuilder = new org.traccar.helper.DateBuilder().setDate(2015, 10, 20).setTime(1, 21, 11);
        // StatementAdderOnAssert create literal from method
        int int_vc_8 = 11;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_8, 11);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_8, 11);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_8, 11);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.DateBuilder vc_35 = new org.traccar.helper.DateBuilder();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_35).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_35).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_35).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_35).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_35).getDate()).getHours(), 1);
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf71__13 = // StatementAdderMethod cloned existing statement
vc_35.setSecond(int_vc_8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).getDate()).getTime(), 1483833611000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).toLocaleString(), "Jan 8, 2017 1:00:11 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:11 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).toGMTString(), "8 Jan 2017 00:00:11 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf71__13.equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).toInstant()).toEpochMilli(), 1483833611000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf71__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:11 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).toInstant()).getEpochSecond(), 1483833611L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_35));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).getDate()).getSeconds(), 11);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate().equals(o_testDateBuilder_cf71__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).getTime(), 1483833611000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).getSeconds(), 11);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf71__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71__13).setCurrentDate()).getDate()).getDate(), 8);
        // StatementAdderOnAssert create random local variable
        long vc_402 = 1591424048664920695L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_402, 1591424048664920695L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_402, 1591424048664920695L);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.DateBuilder vc_401 = new org.traccar.helper.DateBuilder();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_401));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).setCurrentDate().equals(vc_401));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_401).setCurrentDate().equals(vc_401));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_401));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).setCurrentDate().equals(vc_401));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_401).setCurrentDate().equals(vc_401));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_401).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_401).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf71_cf1298__141 = // StatementAdderMethod cloned existing statement
vc_401.addSeconds(vc_402);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf71_cf1298__141));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).getDate()).getHours(), 23);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).toGMTString(), "8 Jan 2017 22:40:56 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate().equals(o_testDateBuilder_cf71_cf1298__141));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).toInstant()).getNano(), 24000000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).getDate()).getTime(), 1483915256024L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf71_cf1298__141));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).getHours(), 23);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf71_cf1298__141.equals(vc_401));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).getSeconds(), 56);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).getMinutes(), 40);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).toInstant()).toEpochMilli(), 1483915256024L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 11:40:56 PM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).setCurrentDate().equals(vc_401));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).getDate()).getSeconds(), 56);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).getDate()).getMinutes(), 40);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_401));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).toInstant()).getEpochSecond(), 1483915256L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).getTime(), 1483915256024L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).getDate()).toLocaleString(), "Jan 8, 2017 11:40:56 PM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 22:40:56 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298__141).setCurrentDate().equals(vc_401));
        // StatementAdderOnAssert create random local variable
        int vc_511 = -1710851429;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_511, -1710851429);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.DateBuilder vc_510 = new org.traccar.helper.DateBuilder();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_510));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).setCurrentDate().equals(vc_510));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_510).setCurrentDate().equals(vc_510));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_510).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_510).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf71_cf1298_cf1717__327 = // StatementAdderMethod cloned existing statement
vc_510.setDay(vc_511);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate().equals(o_testDateBuilder_cf71_cf1298_cf1717__327));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).setCurrentDate().equals(vc_510));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate().equals(vc_510));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_510));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf71_cf1298_cf1717__327.equals(vc_510));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf71_cf1298_cf1717__327));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf71_cf1298_cf1717__327));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf71_cf1298_cf1717__327).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        org.junit.Assert.assertEquals(dateFormat.parse("2015-10-20 01:21:11"), dateBuilder.getDate());
    }

    /* amplification of org.traccar.helper.DateBuilderTest#testDateBuilder */
    @org.junit.Test(timeout = 1000)
    public void testDateBuilder_cf66_cf1119_cf3129() throws java.text.ParseException {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        org.traccar.helper.DateBuilder dateBuilder = new org.traccar.helper.DateBuilder().setDate(2015, 10, 20).setTime(1, 21, 11);
        // StatementAdderOnAssert create random local variable
        int vc_33 = 443792523;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_33, 443792523);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_33, 443792523);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_33, 443792523);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.DateBuilder vc_32 = new org.traccar.helper.DateBuilder();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_32).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_32).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_32).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_32).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_32).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf66__13 = // StatementAdderMethod cloned existing statement
vc_32.setMonth(vc_33);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf66__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf66__13.equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate().equals(o_testDateBuilder_cf66__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf66__13));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66__13).setCurrentDate()).getDate()).getHours(), 1);
        // StatementAdderOnAssert create random local variable
        long vc_353 = 6308045893176125681L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_353, 6308045893176125681L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_353, 6308045893176125681L);
        // StatementAdderOnAssert create random local variable
        org.traccar.helper.DateBuilder vc_352 = new org.traccar.helper.DateBuilder();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_352).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toInstant()).toEpochMilli(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getMinutes(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toInstant()).getEpochSecond(), 1483833600L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toLocaleString(), "Jan 8, 2017 1:00:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)vc_352).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 00:00:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getHours(), 1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getTime(), 1483833600000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)vc_352).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)vc_352).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf66_cf1119__141 = // StatementAdderMethod cloned existing statement
vc_352.addSeconds(vc_353);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).toLocaleString(), "Jan 8, 2017 3:10:28 PM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getTime(), -125786857771672L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 3:10:28 PM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getDay(), 4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).toInstant()).getNano(), 328000000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).toInstant()).getEpochSecond(), -125786857772L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getMinutes(), 10);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate().equals(o_testDateBuilder_cf66_cf1119__141));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getSeconds(), 28);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).toInstant()).toEpochMilli(), -125786857771672L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getSeconds(), 28);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getMinutes(), 10);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getHours(), 15);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf66_cf1119__141.equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf66_cf1119__141));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getDay(), 4);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).toGMTString(), "8 Jan 2017 14:10:28 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 14:10:28 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).getDate()).getHours(), 15);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf66_cf1119__141));
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_352));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getTime(), -125786857771672L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119__141).getDate()).getMonth(), 0);
        // StatementAdderOnAssert create random local variable
        int vc_889 = 1186353209;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_889, 1186353209);
        // AssertGenerator replace invocation
        org.traccar.helper.DateBuilder o_testDateBuilder_cf66_cf1119_cf3129__325 = // StatementAdderMethod cloned existing statement
vc_32.addMinute(vc_889);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).getTime(), 1483867740000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).getTimezoneOffset(), -60);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).getHours(), 10);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(o_testDateBuilder_cf66_cf1119_cf3129__325.equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).getDay(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).toInstant()).getEpochSecond(), 1483867740L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).toInstant()).toEpochMilli(), 1483867740000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf66_cf1119_cf3129__325));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).getDate()).getTime(), 1483867740000L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).getDate()).toGMTString(), "8 Jan 2017 09:29:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate().equals(o_testDateBuilder_cf66_cf1119_cf3129__325));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).toLocaleString(), "Jan 8, 2017 10:29:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).toGMTString(), "8 Jan 2017 09:29:00 GMT");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).getDate()).getMinutes(), 29);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).setCurrentDate().equals(vc_32));
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).getDate()).toLocaleString(), "Jan 8, 2017 10:29:00 AM");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).getMonth(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).getYear(), 117);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.time.Instant)((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).toInstant()).getNano(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).getDate()).getDate(), 8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).getDate()).getHours(), 10);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).getSeconds(), 0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(((java.util.Date)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).getDate()).getMinutes(), 29);
        // AssertGenerator add assertion
        junit.framework.Assert.assertTrue(((org.traccar.helper.DateBuilder)((org.traccar.helper.DateBuilder)o_testDateBuilder_cf66_cf1119_cf3129__325).setCurrentDate()).setCurrentDate().equals(o_testDateBuilder_cf66_cf1119_cf3129__325));
        org.junit.Assert.assertEquals(dateFormat.parse("2015-10-20 01:21:11"), dateBuilder.getDate());
    }
}

