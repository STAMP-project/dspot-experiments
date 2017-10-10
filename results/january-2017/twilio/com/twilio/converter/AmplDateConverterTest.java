

package com.twilio.converter;


/**
 * Test Class for {@link DateConverter}.
 */
public class AmplDateConverterTest {
    @org.junit.Test
    public void testRfc2822() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("Tue, 29 Mar 2016 13:00:05 +0000");
        org.junit.Assert.assertEquals(2, dt.getDayOfWeek());
        org.junit.Assert.assertEquals(29, dt.getDayOfMonth());
        org.junit.Assert.assertEquals(3, dt.getMonthOfYear());
        org.junit.Assert.assertEquals(2016, dt.getYear());
        org.junit.Assert.assertEquals(13, dt.getHourOfDay());
        org.junit.Assert.assertEquals(0, dt.getMinuteOfHour());
        org.junit.Assert.assertEquals(5, dt.getSecondOfMinute());
    }

    @org.junit.Test
    public void testInvalidRfc2822() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("gibberish");
        org.junit.Assert.assertNull(dt);
    }

    @org.junit.Test
    public void testIso8601() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.iso8601DateTimeFromString("2016-01-15T21:49:24Z");
        org.junit.Assert.assertEquals(15, dt.getDayOfMonth());
        org.junit.Assert.assertEquals(1, dt.getMonthOfYear());
        org.junit.Assert.assertEquals(2016, dt.getYear());
        org.junit.Assert.assertEquals(21, dt.getHourOfDay());
        org.junit.Assert.assertEquals(49, dt.getMinuteOfHour());
        org.junit.Assert.assertEquals(24, dt.getSecondOfMinute());
    }

    @org.junit.Test
    public void testInvalidIso8601() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.iso8601DateTimeFromString("blanks");
        org.junit.Assert.assertNull(dt);
    }

    @org.junit.Test
    public void testLocalDate() {
        org.joda.time.LocalDate ld = com.twilio.converter.DateConverter.localDateFromString("2016-11-11");
        org.junit.Assert.assertEquals(2016, ld.getYear());
        org.junit.Assert.assertEquals(11, ld.getMonthOfYear());
        org.junit.Assert.assertEquals(11, ld.getDayOfMonth());
    }

    @org.junit.Test
    public void testInvalidLocalDate() {
        org.joda.time.LocalDate date = com.twilio.converter.DateConverter.localDateFromString("bad");
        org.junit.Assert.assertNull(date);
    }

    @org.junit.Test
    public void testLocalDateToString() {
        java.lang.String date = com.twilio.converter.DateConverter.dateStringFromLocalDate(new org.joda.time.LocalDate(2016, 9, 21));
        org.junit.Assert.assertEquals("2016-09-21", date);
    }

    @org.junit.Test
    public void testDifferentLocaleRFC2822() {
        java.util.Locale.setDefault(new java.util.Locale("fr", "CA"));
        org.joda.time.DateTime dateTime = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("Tue, 29 Mar 2016 13:00:05 +0000");
        org.junit.Assert.assertNotNull(dateTime);
    }

    @org.junit.Test
    public void testDifferentLocaleISO8601() {
        java.util.Locale.setDefault(new java.util.Locale("fr", "CA"));
        org.joda.time.DateTime dateTime = com.twilio.converter.DateConverter.iso8601DateTimeFromString("2016-01-15T21:49:24Z");
        org.junit.Assert.assertNotNull(dateTime);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testDifferentLocaleISO8601 */
    @org.junit.Test(timeout = 10000)
    public void testDifferentLocaleISO8601_cf8() {
        java.util.Locale.setDefault(new java.util.Locale("fr", "CA"));
        org.joda.time.DateTime dateTime = com.twilio.converter.DateConverter.iso8601DateTimeFromString("2016-01-15T21:49:24Z");
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_2 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_1 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testDifferentLocaleISO8601_cf8__9 = // StatementAdderMethod cloned existing statement
vc_1.dateStringFromLocalDate(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testDifferentLocaleISO8601_cf8__9);
        org.junit.Assert.assertNotNull(dateTime);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testDifferentLocaleISO8601 */
    @org.junit.Test(timeout = 10000)
    public void testDifferentLocaleISO8601_cf22_literalMutation683_cf8493() {
        java.util.Locale.setDefault(new java.util.Locale("hi", "CA"));
        org.joda.time.DateTime dateTime = com.twilio.converter.DateConverter.iso8601DateTimeFromString("2016-01-15T21:49:24Z");
        // StatementAdderOnAssert create null value
        java.lang.String vc_14 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14);
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_12 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_12);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_12);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_12);
        // AssertGenerator replace invocation
        org.joda.time.LocalDate o_testDifferentLocaleISO8601_cf22__9 = // StatementAdderMethod cloned existing statement
vc_12.localDateFromString(vc_14);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testDifferentLocaleISO8601_cf22__9);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_2802 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2802);
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_2800 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2800);
        // AssertGenerator replace invocation
        java.lang.String o_testDifferentLocaleISO8601_cf22_literalMutation683_cf8493__25 = // StatementAdderMethod cloned existing statement
vc_2800.dateStringFromLocalDate(vc_2802);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testDifferentLocaleISO8601_cf22_literalMutation683_cf8493__25);
        org.junit.Assert.assertNotNull(dateTime);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testDifferentLocaleRFC2822 */
    @org.junit.Test(timeout = 10000)
    public void testDifferentLocaleRFC2822_cf9870() {
        java.util.Locale.setDefault(new java.util.Locale("fr", "CA"));
        org.joda.time.DateTime dateTime = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("Tue, 29 Mar 2016 13:00:05 +0000");
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_3250 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3250);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_3249 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testDifferentLocaleRFC2822_cf9870__9 = // StatementAdderMethod cloned existing statement
vc_3249.dateStringFromLocalDate(vc_3250);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testDifferentLocaleRFC2822_cf9870__9);
        org.junit.Assert.assertNotNull(dateTime);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testDifferentLocaleRFC2822 */
    @org.junit.Test(timeout = 10000)
    public void testDifferentLocaleRFC2822_cf9883_cf10406() {
        java.util.Locale.setDefault(new java.util.Locale("fr", "CA"));
        org.joda.time.DateTime dateTime = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("Tue, 29 Mar 2016 13:00:05 +0000");
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_3259 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_3259, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_3259, "");
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_3257 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testDifferentLocaleRFC2822_cf9883__9 = // StatementAdderMethod cloned existing statement
vc_3257.rfc2822DateTimeFromString(vc_3259);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testDifferentLocaleRFC2822_cf9883__9);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_3442 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3442);
        // AssertGenerator replace invocation
        java.lang.String o_testDifferentLocaleRFC2822_cf9883_cf10406__17 = // StatementAdderMethod cloned existing statement
vc_3257.dateStringFromLocalDate(vc_3442);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testDifferentLocaleRFC2822_cf9883_cf10406__17);
        org.junit.Assert.assertNotNull(dateTime);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testDifferentLocaleRFC2822 */
    @org.junit.Test(timeout = 10000)
    public void testDifferentLocaleRFC2822_cf9874_cf10086_cf14364() {
        java.util.Locale.setDefault(new java.util.Locale("fr", "CA"));
        org.joda.time.DateTime dateTime = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("Tue, 29 Mar 2016 13:00:05 +0000");
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_3255 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_3255, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_3255, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_3255, "");
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_3252 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3252);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3252);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3252);
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testDifferentLocaleRFC2822_cf9874__9 = // StatementAdderMethod cloned existing statement
vc_3252.iso8601DateTimeFromString(vc_3255);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testDifferentLocaleRFC2822_cf9874__9);
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_3335 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_3335, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_3335, "");
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testDifferentLocaleRFC2822_cf9874_cf10086__19 = // StatementAdderMethod cloned existing statement
vc_3252.iso8601DateTimeFromString(vc_3335);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testDifferentLocaleRFC2822_cf9874_cf10086__19);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_4754 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4754);
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_4752 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4752);
        // AssertGenerator replace invocation
        java.lang.String o_testDifferentLocaleRFC2822_cf9874_cf10086_cf14364__33 = // StatementAdderMethod cloned existing statement
vc_4752.dateStringFromLocalDate(vc_4754);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testDifferentLocaleRFC2822_cf9874_cf10086_cf14364__33);
        org.junit.Assert.assertNotNull(dateTime);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testInvalidIso8601 */
    @org.junit.Test(timeout = 10000)
    public void testInvalidIso8601_cf19405() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.iso8601DateTimeFromString("blanks");
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_6418 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_6418);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_6417 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testInvalidIso8601_cf19405__7 = // StatementAdderMethod cloned existing statement
vc_6417.dateStringFromLocalDate(vc_6418);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidIso8601_cf19405__7);
        org.junit.Assert.assertNull(dt);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testInvalidIso8601 */
    @org.junit.Test(timeout = 10000)
    public void testInvalidIso8601_cf19413_cf19759() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.iso8601DateTimeFromString("blanks");
        // StatementAdderOnAssert create null value
        java.lang.String vc_6426 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_6426);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_6426);
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_6424 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_6424);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_6424);
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testInvalidIso8601_cf19413__7 = // StatementAdderMethod cloned existing statement
vc_6424.rfc2822DateTimeFromString(vc_6426);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidIso8601_cf19413__7);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_6562 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_6562);
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_6560 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_6560);
        // AssertGenerator replace invocation
        java.lang.String o_testInvalidIso8601_cf19413_cf19759__19 = // StatementAdderMethod cloned existing statement
vc_6560.dateStringFromLocalDate(vc_6562);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidIso8601_cf19413_cf19759__19);
        org.junit.Assert.assertNull(dt);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testInvalidIso8601 */
    @org.junit.Test(timeout = 10000)
    public void testInvalidIso8601_cf19418_cf19983_cf21221() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.iso8601DateTimeFromString("blanks");
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_6427 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_6427, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_6427, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_6427, "");
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_6425 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testInvalidIso8601_cf19418__7 = // StatementAdderMethod cloned existing statement
vc_6425.rfc2822DateTimeFromString(vc_6427);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidIso8601_cf19418__7);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_6642 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_6642);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_6642);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_6641 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testInvalidIso8601_cf19418_cf19983__17 = // StatementAdderMethod cloned existing statement
vc_6641.dateStringFromLocalDate(vc_6642);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidIso8601_cf19418_cf19983__17);
        // StatementAdderOnAssert create null value
        java.lang.String vc_7082 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_7082);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_7081 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testInvalidIso8601_cf19418_cf19983_cf21221__29 = // StatementAdderMethod cloned existing statement
vc_7081.rfc2822DateTimeFromString(vc_7082);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidIso8601_cf19418_cf19983_cf21221__29);
        org.junit.Assert.assertNull(dt);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testInvalidLocalDate */
    @org.junit.Test(timeout = 10000)
    public void testInvalidLocalDate_cf28644() {
        org.joda.time.LocalDate date = com.twilio.converter.DateConverter.localDateFromString("bad");
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_9728 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9728);
        // AssertGenerator replace invocation
        java.lang.String o_testInvalidLocalDate_cf28644__5 = // StatementAdderMethod cloned existing statement
vc_9728.dateStringFromLocalDate(date);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidLocalDate_cf28644__5);
        org.junit.Assert.assertNull(date);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testInvalidLocalDate */
    @org.junit.Test(timeout = 10000)
    public void testInvalidLocalDate_cf28662_cf29425() {
        org.joda.time.LocalDate date = com.twilio.converter.DateConverter.localDateFromString("bad");
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1826 = "bad";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1826, "bad");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_1826, "bad");
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_9740 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9740);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9740);
        // AssertGenerator replace invocation
        org.joda.time.LocalDate o_testInvalidLocalDate_cf28662__7 = // StatementAdderMethod cloned existing statement
vc_9740.localDateFromString(String_vc_1826);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidLocalDate_cf28662__7);
        // AssertGenerator replace invocation
        java.lang.String o_testInvalidLocalDate_cf28662_cf29425__15 = // StatementAdderMethod cloned existing statement
vc_9740.dateStringFromLocalDate(date);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidLocalDate_cf28662_cf29425__15);
        org.junit.Assert.assertNull(date);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testInvalidLocalDate */
    @org.junit.Test(timeout = 10000)
    public void testInvalidLocalDate_cf28652_cf28964_cf33076() {
        org.joda.time.LocalDate date = com.twilio.converter.DateConverter.localDateFromString("bad");
        // StatementAdderOnAssert create null value
        java.lang.String vc_9734 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9734);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9734);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9734);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_9733 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testInvalidLocalDate_cf28652__7 = // StatementAdderMethod cloned existing statement
vc_9733.iso8601DateTimeFromString(vc_9734);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidLocalDate_cf28652__7);
        // StatementAdderOnAssert create null value
        java.lang.String vc_9862 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9862);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_9862);
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testInvalidLocalDate_cf28652_cf28964__15 = // StatementAdderMethod cloned existing statement
vc_9733.iso8601DateTimeFromString(vc_9862);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidLocalDate_cf28652_cf28964__15);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_11233 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testInvalidLocalDate_cf28652_cf28964_cf33076__25 = // StatementAdderMethod cloned existing statement
vc_11233.dateStringFromLocalDate(date);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidLocalDate_cf28652_cf28964_cf33076__25);
        org.junit.Assert.assertNull(date);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testInvalidRfc2822 */
    @org.junit.Test(timeout = 10000)
    public void testInvalidRfc2822_cf38675() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("gibberish");
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_13122 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13122);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_13121 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testInvalidRfc2822_cf38675__7 = // StatementAdderMethod cloned existing statement
vc_13121.dateStringFromLocalDate(vc_13122);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidRfc2822_cf38675__7);
        org.junit.Assert.assertNull(dt);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testInvalidRfc2822 */
    @org.junit.Test(timeout = 10000)
    public void testInvalidRfc2822_cf38688_cf39251() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("gibberish");
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_13131 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_13131, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_13131, "");
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_13129 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testInvalidRfc2822_cf38688__7 = // StatementAdderMethod cloned existing statement
vc_13129.rfc2822DateTimeFromString(vc_13131);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidRfc2822_cf38688__7);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_13346 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13346);
        // AssertGenerator replace invocation
        java.lang.String o_testInvalidRfc2822_cf38688_cf39251__15 = // StatementAdderMethod cloned existing statement
vc_13129.dateStringFromLocalDate(vc_13346);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidRfc2822_cf38688_cf39251__15);
        org.junit.Assert.assertNull(dt);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testInvalidRfc2822 */
    @org.junit.Test(timeout = 10000)
    public void testInvalidRfc2822_cf38673_cf38725_cf41227() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("gibberish");
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_13122 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13122);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13122);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13122);
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_13120 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13120);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13120);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13120);
        // AssertGenerator replace invocation
        java.lang.String o_testInvalidRfc2822_cf38673__7 = // StatementAdderMethod cloned existing statement
vc_13120.dateStringFromLocalDate(vc_13122);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidRfc2822_cf38673__7);
        // StatementAdderOnAssert create null value
        java.lang.String vc_13150 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13150);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_13150);
        // AssertGenerator replace invocation
        org.joda.time.LocalDate o_testInvalidRfc2822_cf38673_cf38725__17 = // StatementAdderMethod cloned existing statement
vc_13120.localDateFromString(vc_13150);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidRfc2822_cf38673_cf38725__17);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_14045 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        org.joda.time.LocalDate o_testInvalidRfc2822_cf38673_cf38725_cf41227__29 = // StatementAdderMethod cloned existing statement
vc_14045.localDateFromString(vc_13150);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testInvalidRfc2822_cf38673_cf38725_cf41227__29);
        org.junit.Assert.assertNull(dt);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testIso8601 */
    @org.junit.Test(timeout = 10000)
    public void testIso8601_cf48002() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.iso8601DateTimeFromString("2016-01-15T21:49:24Z");
        org.junit.Assert.assertEquals(15, dt.getDayOfMonth());
        org.junit.Assert.assertEquals(1, dt.getMonthOfYear());
        org.junit.Assert.assertEquals(2016, dt.getYear());
        org.junit.Assert.assertEquals(21, dt.getHourOfDay());
        org.junit.Assert.assertEquals(49, dt.getMinuteOfHour());
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_16450 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16450);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_16449 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testIso8601_cf48002__17 = // StatementAdderMethod cloned existing statement
vc_16449.dateStringFromLocalDate(vc_16450);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testIso8601_cf48002__17);
        org.junit.Assert.assertEquals(24, dt.getSecondOfMinute());
    }

    /* amplification of com.twilio.converter.DateConverterTest#testIso8601 */
    @org.junit.Test(timeout = 10000)
    public void testIso8601_cf48011_cf48495() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.iso8601DateTimeFromString("2016-01-15T21:49:24Z");
        org.junit.Assert.assertEquals(15, dt.getDayOfMonth());
        org.junit.Assert.assertEquals(1, dt.getMonthOfYear());
        org.junit.Assert.assertEquals(2016, dt.getYear());
        org.junit.Assert.assertEquals(21, dt.getHourOfDay());
        org.junit.Assert.assertEquals(49, dt.getMinuteOfHour());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_3085 = "2016-01-15T21:49:24Z";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3085, "2016-01-15T21:49:24Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3085, "2016-01-15T21:49:24Z");
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_16456 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16456);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16456);
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testIso8601_cf48011__17 = // StatementAdderMethod cloned existing statement
vc_16456.rfc2822DateTimeFromString(String_vc_3085);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testIso8601_cf48011__17);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_16578 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16578);
        // AssertGenerator replace invocation
        java.lang.String o_testIso8601_cf48011_cf48495__27 = // StatementAdderMethod cloned existing statement
vc_16456.dateStringFromLocalDate(vc_16578);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testIso8601_cf48011_cf48495__27);
        org.junit.Assert.assertEquals(24, dt.getSecondOfMinute());
    }

    /* amplification of com.twilio.converter.DateConverterTest#testIso8601 */
    @org.junit.Test(timeout = 10000)
    public void testIso8601_cf48011_cf48512_cf56495() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.iso8601DateTimeFromString("2016-01-15T21:49:24Z");
        org.junit.Assert.assertEquals(15, dt.getDayOfMonth());
        org.junit.Assert.assertEquals(1, dt.getMonthOfYear());
        org.junit.Assert.assertEquals(2016, dt.getYear());
        org.junit.Assert.assertEquals(21, dt.getHourOfDay());
        org.junit.Assert.assertEquals(49, dt.getMinuteOfHour());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_3085 = "2016-01-15T21:49:24Z";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3085, "2016-01-15T21:49:24Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3085, "2016-01-15T21:49:24Z");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3085, "2016-01-15T21:49:24Z");
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_16456 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16456);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16456);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16456);
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testIso8601_cf48011__17 = // StatementAdderMethod cloned existing statement
vc_16456.rfc2822DateTimeFromString(String_vc_3085);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testIso8601_cf48011__17);
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_16584 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16584);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16584);
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testIso8601_cf48011_cf48512__27 = // StatementAdderMethod cloned existing statement
vc_16584.rfc2822DateTimeFromString(String_vc_3085);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testIso8601_cf48011_cf48512__27);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_18450 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18450);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_18449 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testIso8601_cf48011_cf48512_cf56495__41 = // StatementAdderMethod cloned existing statement
vc_18449.dateStringFromLocalDate(vc_18450);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testIso8601_cf48011_cf48512_cf56495__41);
        org.junit.Assert.assertEquals(24, dt.getSecondOfMinute());
    }

    /* amplification of com.twilio.converter.DateConverterTest#testLocalDate */
    @org.junit.Test(timeout = 10000)
    public void testLocalDate_cf56548() {
        org.joda.time.LocalDate ld = com.twilio.converter.DateConverter.localDateFromString("2016-11-11");
        org.junit.Assert.assertEquals(2016, ld.getYear());
        org.junit.Assert.assertEquals(11, ld.getMonthOfYear());
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_18466 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18466);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_18465 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testLocalDate_cf56548__11 = // StatementAdderMethod cloned existing statement
vc_18465.dateStringFromLocalDate(vc_18466);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDate_cf56548__11);
        org.junit.Assert.assertEquals(11, ld.getDayOfMonth());
    }

    /* amplification of com.twilio.converter.DateConverterTest#testLocalDate */
    @org.junit.Test(timeout = 10000)
    public void testLocalDate_cf56561_cf57369() {
        org.joda.time.LocalDate ld = com.twilio.converter.DateConverter.localDateFromString("2016-11-11");
        org.junit.Assert.assertEquals(2016, ld.getYear());
        org.junit.Assert.assertEquals(11, ld.getMonthOfYear());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_3463 = "2016-11-11";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3463, "2016-11-11");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3463, "2016-11-11");
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_18473 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testLocalDate_cf56561__11 = // StatementAdderMethod cloned existing statement
vc_18473.rfc2822DateTimeFromString(String_vc_3463);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDate_cf56561__11);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_18706 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18706);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_18705 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testLocalDate_cf56561_cf57369__21 = // StatementAdderMethod cloned existing statement
vc_18705.dateStringFromLocalDate(vc_18706);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDate_cf56561_cf57369__21);
        org.junit.Assert.assertEquals(11, ld.getDayOfMonth());
    }

    /* amplification of com.twilio.converter.DateConverterTest#testLocalDate */
    @org.junit.Test(timeout = 10000)
    public void testLocalDate_cf56565_cf57534_cf59833() {
        org.joda.time.LocalDate ld = com.twilio.converter.DateConverter.localDateFromString("2016-11-11");
        org.junit.Assert.assertEquals(2016, ld.getYear());
        org.junit.Assert.assertEquals(11, ld.getMonthOfYear());
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_18479 = new java.lang.String();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_18479, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_18479, "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_18479, "");
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_18476 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18476);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18476);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18476);
        // AssertGenerator replace invocation
        org.joda.time.LocalDate o_testLocalDate_cf56565__11 = // StatementAdderMethod cloned existing statement
vc_18476.localDateFromString(vc_18479);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDate_cf56565__11);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_18754 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18754);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18754);
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_18752 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18752);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18752);
        // AssertGenerator replace invocation
        java.lang.String o_testLocalDate_cf56565_cf57534__23 = // StatementAdderMethod cloned existing statement
vc_18752.dateStringFromLocalDate(vc_18754);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDate_cf56565_cf57534__23);
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_19380 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_19380);
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testLocalDate_cf56565_cf57534_cf59833__37 = // StatementAdderMethod cloned existing statement
vc_19380.iso8601DateTimeFromString(vc_18479);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDate_cf56565_cf57534_cf59833__37);
        org.junit.Assert.assertEquals(11, ld.getDayOfMonth());
    }

    /* amplification of com.twilio.converter.DateConverterTest#testLocalDateToString */
    @org.junit.Test(timeout = 10000)
    public void testLocalDateToString_cf65766() {
        java.lang.String date = com.twilio.converter.DateConverter.dateStringFromLocalDate(new org.joda.time.LocalDate(2016, 9, 21));
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_20978 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_20978);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_20977 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testLocalDateToString_cf65766__8 = // StatementAdderMethod cloned existing statement
vc_20977.dateStringFromLocalDate(vc_20978);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDateToString_cf65766__8);
        org.junit.Assert.assertEquals("2016-09-21", date);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testLocalDateToString */
    @org.junit.Test(timeout = 10000)
    public void testLocalDateToString_cf65773_cf66441() {
        java.lang.String date = com.twilio.converter.DateConverter.dateStringFromLocalDate(new org.joda.time.LocalDate(2016, 9, 21));
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_20981 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testLocalDateToString_cf65773__6 = // StatementAdderMethod cloned existing statement
vc_20981.iso8601DateTimeFromString(date);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDateToString_cf65773__6);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_21106 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_21106);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_21105 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testLocalDateToString_cf65773_cf66441__14 = // StatementAdderMethod cloned existing statement
vc_21105.dateStringFromLocalDate(vc_21106);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDateToString_cf65773_cf66441__14);
        org.junit.Assert.assertEquals("2016-09-21", date);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testLocalDateToString */
    @org.junit.Test(timeout = 10000)
    public void testLocalDateToString_cf65782_cf66985_cf68995() {
        java.lang.String date = com.twilio.converter.DateConverter.dateStringFromLocalDate(new org.joda.time.LocalDate(2016, 9, 21));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_3934 = "2016-09-21";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3934, "2016-09-21");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3934, "2016-09-21");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_3934, "2016-09-21");
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_20985 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testLocalDateToString_cf65782__8 = // StatementAdderMethod cloned existing statement
vc_20985.rfc2822DateTimeFromString(String_vc_3934);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDateToString_cf65782__8);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_21250 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_21250);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_21250);
        // AssertGenerator replace invocation
        java.lang.String o_testLocalDateToString_cf65782_cf66985__16 = // StatementAdderMethod cloned existing statement
vc_20985.dateStringFromLocalDate(vc_21250);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDateToString_cf65782_cf66985__16);
        // StatementAdderOnAssert create null value
        java.lang.String vc_21706 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_21706);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_21705 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testLocalDateToString_cf65782_cf66985_cf68995__28 = // StatementAdderMethod cloned existing statement
vc_21705.rfc2822DateTimeFromString(vc_21706);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testLocalDateToString_cf65782_cf66985_cf68995__28);
        org.junit.Assert.assertEquals("2016-09-21", date);
    }

    /* amplification of com.twilio.converter.DateConverterTest#testRfc2822 */
    @org.junit.Test(timeout = 10000)
    public void testRfc2822_cf74489() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("Tue, 29 Mar 2016 13:00:05 +0000");
        org.junit.Assert.assertEquals(2, dt.getDayOfWeek());
        org.junit.Assert.assertEquals(29, dt.getDayOfMonth());
        org.junit.Assert.assertEquals(3, dt.getMonthOfYear());
        org.junit.Assert.assertEquals(2016, dt.getYear());
        org.junit.Assert.assertEquals(13, dt.getHourOfDay());
        org.junit.Assert.assertEquals(0, dt.getMinuteOfHour());
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_22978 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22978);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_22977 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testRfc2822_cf74489__19 = // StatementAdderMethod cloned existing statement
vc_22977.dateStringFromLocalDate(vc_22978);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testRfc2822_cf74489__19);
        org.junit.Assert.assertEquals(5, dt.getSecondOfMinute());
    }

    /* amplification of com.twilio.converter.DateConverterTest#testRfc2822 */
    @org.junit.Test(timeout = 10000)
    public void testRfc2822_cf74497_cf75077() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("Tue, 29 Mar 2016 13:00:05 +0000");
        org.junit.Assert.assertEquals(2, dt.getDayOfWeek());
        org.junit.Assert.assertEquals(29, dt.getDayOfMonth());
        org.junit.Assert.assertEquals(3, dt.getMonthOfYear());
        org.junit.Assert.assertEquals(2016, dt.getYear());
        org.junit.Assert.assertEquals(13, dt.getHourOfDay());
        org.junit.Assert.assertEquals(0, dt.getMinuteOfHour());
        // StatementAdderOnAssert create null value
        java.lang.String vc_22986 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22986);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22986);
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_22984 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22984);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22984);
        // AssertGenerator replace invocation
        org.joda.time.DateTime o_testRfc2822_cf74497__19 = // StatementAdderMethod cloned existing statement
vc_22984.rfc2822DateTimeFromString(vc_22986);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testRfc2822_cf74497__19);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_23122 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_23122);
        // StatementAdderOnAssert create null value
        com.twilio.converter.DateConverter vc_23120 = (com.twilio.converter.DateConverter)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_23120);
        // AssertGenerator replace invocation
        java.lang.String o_testRfc2822_cf74497_cf75077__31 = // StatementAdderMethod cloned existing statement
vc_23120.dateStringFromLocalDate(vc_23122);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testRfc2822_cf74497_cf75077__31);
        org.junit.Assert.assertEquals(5, dt.getSecondOfMinute());
    }

    /* amplification of com.twilio.converter.DateConverterTest#testRfc2822 */
    @org.junit.Test(timeout = 10000)
    public void testRfc2822_cf74506_cf75562_cf81539() {
        org.joda.time.DateTime dt = com.twilio.converter.DateConverter.rfc2822DateTimeFromString("Tue, 29 Mar 2016 13:00:05 +0000");
        org.junit.Assert.assertEquals(2, dt.getDayOfWeek());
        org.junit.Assert.assertEquals(29, dt.getDayOfMonth());
        org.junit.Assert.assertEquals(3, dt.getMonthOfYear());
        org.junit.Assert.assertEquals(2016, dt.getYear());
        org.junit.Assert.assertEquals(13, dt.getHourOfDay());
        org.junit.Assert.assertEquals(0, dt.getMinuteOfHour());
        // StatementAdderOnAssert create null value
        java.lang.String vc_22990 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22990);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22990);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_22990);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_22989 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        org.joda.time.LocalDate o_testRfc2822_cf74506__19 = // StatementAdderMethod cloned existing statement
vc_22989.localDateFromString(vc_22990);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testRfc2822_cf74506__19);
        // StatementAdderOnAssert create null value
        org.joda.time.LocalDate vc_23234 = (org.joda.time.LocalDate)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_23234);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_23234);
        // StatementAdderOnAssert create random local variable
        com.twilio.converter.DateConverter vc_23233 = new com.twilio.converter.DateConverter();
        // AssertGenerator replace invocation
        java.lang.String o_testRfc2822_cf74506_cf75562__29 = // StatementAdderMethod cloned existing statement
vc_23233.dateStringFromLocalDate(vc_23234);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testRfc2822_cf74506_cf75562__29);
        // StatementAdderOnAssert create null value
        java.lang.String vc_24574 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_24574);
        // AssertGenerator replace invocation
        org.joda.time.LocalDate o_testRfc2822_cf74506_cf75562_cf81539__39 = // StatementAdderMethod cloned existing statement
vc_22989.localDateFromString(vc_24574);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testRfc2822_cf74506_cf75562_cf81539__39);
        org.junit.Assert.assertEquals(5, dt.getSecondOfMinute());
    }
}

